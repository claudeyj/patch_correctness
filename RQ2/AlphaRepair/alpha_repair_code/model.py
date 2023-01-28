import torch
from transformers import AutoTokenizer, T5ForConditionalGeneration
from transformers import NoBadWordsLogitsProcessor, LogitsProcessorList
import sys, os
sys.path.append(os.path.dirname(os.path.abspath(__file__)))
from generation import CAT5


class SpanLM(object):
    def __init__(self, pretrained: str = "", weight=None, batch_size=1):
        print("Initializing a SpanLM based model: {} ...".format(pretrained))
        self.pretrained = pretrained
        self.device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
        self.model = T5ForConditionalGeneration.from_pretrained(pretrained)
        self.max_length = self.model.config.to_dict()['n_positions']
        self.infill_ph = "<extra_id_0>"
        self.infill_ph_1 = "<extra_id_1>"
        self.END_ID = 2
        self.infill_ID = 32099
        print("Max length: {}".format(self.max_length))
        self.model.__class__ = CAT5
        self.model = self.model.to(self.device)
        self.tokenizer = AutoTokenizer.from_pretrained("Salesforce/codet5-large")
        self.batch_size = batch_size

    def check_size(self, prefix: str, suffix: str) -> bool:
        input_tokens = self.tokenizer.encode(self.build_input(prefix, suffix), return_tensors='pt')
        if len(input_tokens[0]) + 50 >= self.max_length:
            print("Context size is too large ... ")
            return False
        return True

    def encode(self, string: str) -> torch.LongTensor:
        return self.tokenizer.encode(string, return_tensors='pt', add_special_tokens=False)[0]

    def decode(self, tokens: torch.LongTensor) -> str:
        return self.tokenizer.decode(tokens)

    def build_input(self, prefix: str, suffix: str) -> str:
        return prefix + self.infill_ph + suffix

    def predict(self, prefix: str, suffix: str, t_prefix: str, t_suffix: str,
                num_samples: int = 1, s_limit: bool = False, vn: set = None,
                language: str = "py"):
        input_tokens = self.tokenizer.encode(self.build_input(prefix, suffix), return_tensors='pt') \
            .repeat(min(num_samples, self.batch_size), 1)
        input_tokens = input_tokens.to(self.device)

        with torch.no_grad():
            entropies = []
            self.model.reinit(self.tokenizer, s_limit, vn, language, t_prefix, t_suffix)
            raw_o = self.model.generate(input_tokens,
                                        max_length=50,
                                        do_sample=True,
                                        output_scores=True,
                                        return_dict_in_generate=True,
                                        temperature=1,
                                        top_k=200,
                                        top_p=1,
                                        use_cache=True)
            t_outputs = self.tokenizer.batch_decode(raw_o.sequences, skip_special_tokens=False)
            # print("raw_o.sequences.shape:", raw_o.sequences.shape)
            # print("t_outputs:")
            # print(t_outputs)
            # print("raw_o.scores:", raw_o.scores)
            # for i in range(len(raw_o.scores)):
            #     print(i, ":", raw_o.scores[i].shape)
            # TODO: entropy stuff
            neg_logs = -torch.log(torch.stack(raw_o.scores, dim=1).softmax(-1))
            neg_logs = torch.gather(neg_logs, 2, raw_o.sequences[:, 1:, None]).squeeze(-1)
            outputs = []
            for index, output in enumerate(t_outputs):
                if self.infill_ph in output and self.infill_ph_1 in output:
                    outputs.append(output.split(self.infill_ph)[1].split(self.infill_ph_1)[0].strip("\n"))
                    # print(repr(output))
                    # print(index, ":", neg_logs[index].shape, ", mean:", torch.mean(neg_logs[index]).cpu().numpy().tolist())
                    # exit()
                    entropies.append(torch.mean(neg_logs[index]).cpu().numpy().tolist())  # TODO this later
                    # print(output.split(self.infill_ph)[1].split(self.infill_ph_1)[0].strip("\n"))
        #  TODO: compare against previous patches to increase diversity?
        return outputs, entropies
