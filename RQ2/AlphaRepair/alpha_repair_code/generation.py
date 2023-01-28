import torch
import re
import numpy as np

from typing import Any, Callable, Dict, Iterable, List, Optional, Tuple, Union
from torch import nn
from transformers import LogitsProcessorList, StoppingCriteriaList, LogitsProcessor, NoBadWordsLogitsProcessor
from transformers.generation_stopping_criteria import validate_stopping_criteria
from transformers.generation_utils import GenerationMixin, SampleOutput, SampleEncoderDecoderOutput, \
    SampleDecoderOnlyOutput
from transformers.models.t5.modeling_t5 import T5ForConditionalGeneration
from transformers.utils import ModelOutput


class NoImpossibleTokensLogitsProcessor:
    # Python version
    def __init__(self, possible_var, tokenizer, language: str, t_prefix: str, t_suffix: str):
        self.possible_var = possible_var
        self.tokenizer = tokenizer
        self.split_tokens = ['{', '}', '(', ')', ';', '[', ']', ':', '.', ',', "'", '"', '#', ' ', '\n',
                             '+', '-', '*', '/', '//', '%', '**', '|', '&', '!', '?', '^',
                             '>', '<', '==', '!=', '>=', '<=', '=']
        if language == "py":
            self.keywords = {'and', 'or', 'not', 'as', 'assert', 'break', 'class', 'continue', 'def', 'del', 'elif',
                             'else',
                             'except', 'and', 'False', 'finally', 'for', 'from', 'global', 'if', 'import', 'in', 'is',
                             'lambda', 'None', 'nonlocal', 'not', 'or', 'pass', 'raise', 'return', 'True', 'try',
                             'while',
                             'with', 'yield'}
            self.bif = {'rsplit', 'getattr', 'divmod', 'splitlines', 'isalpha', 'translate', 'union', 'bin', 'rjust',
                        'issubclass', 'round', 'items', 'setdefault', 'update', 'replace', 'remove', 'zip', 'sum',
                        'iter',
                        'help', 'input', 'super', 'filter', 'delattr', 'swapcase', 'str', 'compile', 'dir', 'range',
                        'isinstance', 'find', 'intersection', 'callable', 'tuple', 'title', 'istitle', 'property',
                        'add', 'reverse', 'isdisjoint', 'popitem', 'isupper', 'rpartition', 'extend', 'id', 'insert',
                        'len', 'upper', 'issuperset', 'strip', 'split', 'append', 'object', 'staticmethod', 'fromkeys',
                        'intersection_update', 'rfind', 'pop', 'classmethod', 'repr', 'oct', 'index',
                        'difference_update',
                        'lower', 'open', 'ord', 'min', 'reversed', 'keys', 'startswith', 'float', 'capitalize',
                        'hasattr',
                        'format_map', 'locals', 'globals', 'map', 'vars', 'format', 'max', 'encode', 'center',
                        'symmetric_difference', 'partition', 'ljust', 'all', 'maketrans', 'frozenset', 'eval',
                        'isprintable', 'casefold', 'lstrip', 'hex', 'values', 'set', 'list', 'complex', 'zfill',
                        'setattr', 'join', 'dict', 'int', 'hash', 'symmetric_difference_update', 'isnumeric',
                        'memoryview', 'pow', 'isdecimal', 'discard', 'copy', 'endswith', 'rindex', 'type', 'slice',
                        'isidentifier', 'expandtabs', 'clear', 'sort', 'any', 'exec', 'sorted', 'chr', 'count', 'get',
                        'print', 'abs', 'isspace', 'difference', 'isdigit', 'enumerate', 'issubset', 'bytearray',
                        'ascii',
                        'islower', 'rstrip', 'isalnum', 'bool', 'next', 'bytes'}
        elif language == "java":
            self.keywords = {'abstract', 'continue', 'for', 'new', 'switch', 'assert', 'default', 'goto', 'package',
                             'synchronized', 'boolean', 'do', 'if', 'private', 'this', 'break', 'double', 'implements',
                             'protected', 'throw', 'byte', 'else', 'import', 'public', 'throws', 'case', 'enum',
                             'instanceof', 'return', 'transient', 'catch', 'extends', 'int', 'short', 'try', 'char',
                             'final', 'interface', 'static', 'void', 'class', 'finally', 'long', 'strictfp', 'volatile',
                             'const', 'float', 'native', 'super', 'while'}
            self.bif = set()

        self.all = self.possible_var | self.keywords | self.bif

        self.split_pattern = re.compile(self.split(self.split_tokens))
        self.removed_tokens = set()
        self.t_prefix = t_prefix
        if t_suffix != "":
            self.t_suffix = self.split_pattern.split(t_suffix.split()[0])[0]
        else:
            self.t_suffix = ""

    # TODO: add a force token (forcing input to be exactly what we want)
    def __call__(self, input_ids: torch.LongTensor, scores: torch.FloatTensor) -> (torch.FloatTensor, set):
        temp_scores = torch.clone(scores)
        decoded_generations = self.tokenizer.batch_decode(input_ids[:, ])
        removed_tokens = set()
        # print(decoded_generations)
        # exit()
        for i, prefix in enumerate(decoded_generations):
            if "<extra_id_1>" in prefix or "<extra_id_0>" not in prefix:
                continue
            # prefix + self.tokenizer.batch_decode[len]
            token_scores = (scores[i] != -float("inf")).nonzero(as_tuple=True)[0]
            token_considers = self.tokenizer.batch_decode(token_scores)
            num_removed = 0
            prefix = prefix.split("<extra_id_0>")[0] + "<extra_id_0>" + self.t_prefix + prefix.split("<extra_id_0>")[1]
            for j, token in enumerate(token_considers):
                if not self._check_possible(prefix + token):
                    # if "by" in token:
                    # self.removed_tokens.add(re.split(self.split_pattern, prefix + token)[-1])
                    # print(token)
                    # sequence = (prefix + token).split()[-1]
                    # s = re.split(self.split_pattern, sequence)[-1]
                    # removed_tokens.add((prefix, s))
                    temp_scores[i, token_scores[j]] = -float("inf")
                    num_removed += 1
            if num_removed == len(token_considers):
                print("Removed all tokens ... Revert back to original")
            else:
                scores[i] = temp_scores[i]
        return scores, removed_tokens

    def force_to_possible_var(self, input_ids: torch.LongTensor, next_tokens):
        input_ids = torch.cat([input_ids, next_tokens[:, None]], dim=-1)
        decoded_generations = self.tokenizer.batch_decode(input_ids)  # TODO speed up this process
        new_sequences = []
        chosen = False
        for i, o_sequence in enumerate(decoded_generations):

            if "<extra_id_1>" in o_sequence or "<extra_id_0>" not in o_sequence:
                new_sequences.append(input_ids[i])
                continue

            o_sequence = o_sequence.split("<extra_id_0>")[0] + "<extra_id_0>" + \
                         self.t_prefix + o_sequence.split("<extra_id_0>")[1]
            sequence = o_sequence.split()[-1]
            s = self.split_pattern.split(sequence)[-1].strip()
            if s == "" or "extra_id" in s:
                new_sequences.append(input_ids[i])
                continue
            if any([x == s for x in self.bif | self.keywords]):
                new_sequences.append(input_ids[i])
                continue

            chosen_var = []

            for var in self.possible_var:
                if var.startswith(s) and var != s:
                    if self.t_suffix != "" and var.removeprefix(s).endswith(self.t_suffix):
                        chosen_var = []
                        break
                    chosen_var.append(var)
                elif var == s:
                    chosen_var = []
                    break
            if len(chosen_var) == 0:
                new_sequences.append(input_ids[i])
                continue
            chosen_var = np.random.choice(chosen_var, 1)[0]  # todo: can make it not uniform
            print(chosen_var)
            a = chosen_var.removeprefix(s)
            new_sequences.append(
                torch.cat(
                    [input_ids[i], self.tokenizer.encode(a, add_special_tokens=False, return_tensors='pt').cuda()[0]],
                    dim=-1)
            )
            chosen = True

        # print(new_sequences)
        # new_input_ids = self.tokenizer(new_sequences, return_tensors='pt', add_special_tokens=False, padding=True)['input_ids']
        # print(new_input_ids)
        # if chosen:
        #     decoded_generations = self.tokenizer.batch_decode(new_input_ids)
        #     for g in decoded_generations:
        #         print(g)
        #     print(new_input_ids)
        #     exit()
        if not chosen:
            return True, input_ids
        else:
            # return input_ids
            max_len = max([x.squeeze().numel() for x in new_sequences])
            new_sequences = [torch.nn.functional.pad(x, pad=(max_len - x.numel(), 0), mode='constant', value=0) for x in
                             new_sequences]
            input_ids = torch.stack(new_sequences)
            # exit()
            return False, input_ids
        # return next_tokens[:, None]

    @staticmethod
    def split(delimiters):
        pattern = '|'.join(map(re.escape, delimiters))
        return pattern

    def _check_possible(self, sequence: str):
        if "</s>" in sequence:
            return True
        sequence = sequence.split()[-1]
        s = self.split_pattern.split(sequence)[-1]
        if s.strip() == "" or "extra_id" in s:
            return True
        if s.strip().isnumeric():
            return True
        # if len(s.strip()) == 1:
        #     return any([var == s.strip() for var in self.possible_var])
        # else:
        return any([var.startswith(s.strip()) for var in self.all])


class CAT5(T5ForConditionalGeneration):
    # Can also be initialized the same way, although not required as python "casting" do the job
    def __init__(self, config):
        super().__init__(config)
        self.vn = None
        self.s_limit = False
        self.no_impossible_processor = None
        self.tokenizer = None

    def reinit(self, tokenizer, s_limit, vn, language, t_prefix, t_suffix):
        self.tokenizer = tokenizer
        self.vn = vn
        self.s_limit = s_limit
        if s_limit:
            self.no_impossible_processor = NoImpossibleTokensLogitsProcessor(vn, self.tokenizer, language,
                                                                             t_prefix, t_suffix)

    def sample(
            self,
            input_ids: torch.LongTensor,
            logits_processor: Optional[LogitsProcessorList] = None,
            stopping_criteria: Optional[StoppingCriteriaList] = None,
            logits_warper: Optional[LogitsProcessorList] = None,
            max_length: Optional[int] = None,
            pad_token_id: Optional[int] = None,
            eos_token_id: Optional[int] = None,
            output_attentions: Optional[bool] = None,
            output_hidden_states: Optional[bool] = None,
            output_scores: Optional[bool] = None,
            return_dict_in_generate: Optional[bool] = None,
            synced_gpus: Optional[bool] = False,
            **model_kwargs,
    ) -> Union[SampleOutput, torch.LongTensor]:
        # init values

        logits_processor = logits_processor if logits_processor is not None else LogitsProcessorList()
        stopping_criteria = stopping_criteria if stopping_criteria is not None else StoppingCriteriaList()
        if max_length is not None:
            stopping_criteria = validate_stopping_criteria(stopping_criteria, max_length)
        logits_warper = logits_warper if logits_warper is not None else LogitsProcessorList()
        pad_token_id = pad_token_id if pad_token_id is not None else self.config.pad_token_id
        eos_token_id = eos_token_id if eos_token_id is not None else self.config.eos_token_id
        output_scores = output_scores if output_scores is not None else self.config.output_scores
        output_attentions = output_attentions if output_attentions is not None else self.config.output_attentions
        output_hidden_states = (
            output_hidden_states if output_hidden_states is not None else self.config.output_hidden_states
        )
        return_dict_in_generate = (
            return_dict_in_generate if return_dict_in_generate is not None else self.config.return_dict_in_generate
        )

        # init attention / hidden states / scores tuples
        scores = () if (return_dict_in_generate and output_scores) else None
        decoder_attentions = () if (return_dict_in_generate and output_attentions) else None
        cross_attentions = () if (return_dict_in_generate and output_attentions) else None
        decoder_hidden_states = () if (return_dict_in_generate and output_hidden_states) else None

        # if model is an encoder-decoder, retrieve encoder attention weights and hidden states
        if return_dict_in_generate and self.config.is_encoder_decoder:
            encoder_attentions = model_kwargs["encoder_outputs"].get("attentions") if output_attentions else None
            encoder_hidden_states = (
                model_kwargs["encoder_outputs"].get("hidden_states") if output_hidden_states else None
            )

        # keep track of which sequences are already finished
        unfinished_sequences = input_ids.new(input_ids.shape[0]).fill_(1)
        cur_len = input_ids.shape[-1]
        previous_cache = True

        # auto-regressive generation

        while True:
            # prepare model inputs
            model_inputs = self.prepare_inputs_for_generation(input_ids, **model_kwargs, previous_cache=previous_cache)

            # forward pass to get next token
            outputs = self(
                **model_inputs,
                return_dict=True,
                output_attentions=output_attentions,
                output_hidden_states=output_hidden_states,
            )

            next_token_logits = outputs.logits[:, -1, :]

            # pre-process distribution
            next_token_scores = logits_processor(input_ids, next_token_logits)
            next_token_scores = logits_warper(input_ids, next_token_scores)
            if self.s_limit:
                next_token_scores, removed_tokens = self.no_impossible_processor(input_ids, next_token_scores)
            # Store scores, attentions and hidden_states when required
            if return_dict_in_generate:
                if output_scores:
                    scores += (next_token_scores,)
                if output_attentions:
                    decoder_attentions += (
                        (outputs.decoder_attentions,) if self.config.is_encoder_decoder else (outputs.attentions,)
                    )
                    if self.config.is_encoder_decoder:
                        cross_attentions += (outputs.cross_attentions,)

                if output_hidden_states:
                    decoder_hidden_states += (
                        (outputs.decoder_hidden_states,)
                        if self.config.is_encoder_decoder
                        else (outputs.hidden_states,)
                    )

            # sample
            # print((next_token_scores[0] != -float("inf")).nonzero(as_tuple=True))
            # print(self.tokenizer.batch_decode((next_token_scores[0] != -float("inf")).nonzero(as_tuple=True)[0]))
            # exit()
            probs = nn.functional.softmax(next_token_scores, dim=-1)
            # if len(removed_tokens) != 0:
            #     print(removed_tokens)
            # print("Shape: {}".format(probs.shape))
            try:
                next_tokens = torch.multinomial(probs, num_samples=1).squeeze(1)
            except:
                print(removed_tokens)
                print(self.tokenizer.batch_decode(input_ids))
                exit()
            if self.s_limit:
                previous_cache, input_ids = self.no_impossible_processor.force_to_possible_var(input_ids, next_tokens)
            else:
                input_ids = torch.cat([input_ids, next_tokens[:, None]], dim=-1)
            # print(next_tokens)
            # finished sentences should have their next token be a padding token
            if eos_token_id is not None:
                if pad_token_id is None:
                    raise ValueError("If `eos_token_id` is defined, make sure that `pad_token_id` is defined.")
                # next_tokens = next_tokens * unfinished_sequences + pad_token_id * (1 - unfinished_sequences)

            # update generated ids, model inputs, and length for next step
            # input_ids = torch.cat([input_ids, next_tokens[:, None]], dim=-1)
            # input_ids = torch.cat([input_ids, next_tokens], dim=-1)
            # print(self.tokenizer.batch_decode(input_ids))
            model_kwargs = self._update_model_kwargs_for_generation(
                outputs, model_kwargs, is_encoder_decoder=self.config.is_encoder_decoder
            )
            cur_len = cur_len + 1

            # if eos_token was found in one sentence, set sentence to finished
            # if eos_token_id is not None:
            #     unfinished_sequences = unfinished_sequences.mul((next_tokens != eos_token_id).long())

            # stop when each sentence is finished, or if we exceed the maximum length
            if unfinished_sequences.max() == 0 or stopping_criteria(input_ids, scores):
                break

        # if self.s_limit:
        #     print(self.no_impossible_processor.removed_tokens)

        if return_dict_in_generate:
            if self.config.is_encoder_decoder:
                return SampleEncoderDecoderOutput(
                    sequences=input_ids,
                    scores=scores,
                    encoder_attentions=encoder_attentions,
                    encoder_hidden_states=encoder_hidden_states,
                    decoder_attentions=decoder_attentions,
                    cross_attentions=cross_attentions,
                    decoder_hidden_states=decoder_hidden_states,
                )
            else:
                return SampleDecoderOnlyOutput(
                    sequences=input_ids,
                    scores=scores,
                    attentions=decoder_attentions,
                    hidden_states=decoder_hidden_states,
                )
        else:
            return input_ids

    @staticmethod
    def _update_model_kwargs_for_generation(
            outputs: ModelOutput, model_kwargs: Dict[str, Any], is_encoder_decoder: bool = False
    ) -> Dict[str, Any]:
        # update past
        if "past_key_values" in outputs:
            model_kwargs["past"] = outputs.past_key_values
            # print(model_kwargs["past"])
        elif "mems" in outputs:
            model_kwargs["past"] = outputs.mems
            # print(model_kwargs["past"])
        elif "past_buckets_states" in outputs:
            model_kwargs["past"] = outputs.past_buckets_states
            # print(model_kwargs["past"])
        else:
            model_kwargs["past"] = None

        # update token_type_ids with last value
        if "token_type_ids" in model_kwargs:
            token_type_ids = model_kwargs["token_type_ids"]
            model_kwargs["token_type_ids"] = torch.cat([token_type_ids, token_type_ids[:, -1].unsqueeze(-1)], dim=-1)
            # print(model_kwargs["token_type_ids"])
        # update attention mask
        if not is_encoder_decoder:
            if "attention_mask" in model_kwargs:
                attention_mask = model_kwargs["attention_mask"]
                model_kwargs["attention_mask"] = torch.cat(
                    [attention_mask, attention_mask.new_ones((attention_mask.shape[0], 1))], dim=-1
                )

        return model_kwargs

    def prepare_inputs_for_generation(
            self,
            input_ids,
            past=None,
            attention_mask=None,
            head_mask=None,
            decoder_head_mask=None,
            cross_attn_head_mask=None,
            use_cache=None,
            encoder_outputs=None,
            previous_cache=None,
            **kwargs
    ):

        # cut decoder_input_ids if past is used
        if past is not None:
            input_ids = input_ids[:, -1:]

        return {
            "decoder_input_ids": input_ids,
            "past_key_values": past,
            "encoder_outputs": encoder_outputs,
            "attention_mask": attention_mask,
            "head_mask": head_mask,
            "decoder_head_mask": decoder_head_mask,
            "cross_attn_head_mask": cross_attn_head_mask,
            "use_cache": use_cache and previous_cache,
        }
