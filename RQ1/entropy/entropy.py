import os, sys, json, math
from alpha_repair_code.model import *
from os.path import *
import subprocess
sys.path.append(abspath(join(dirname(__file__), '..', '..', 'pylib')))
from utils import find_files_recursive

def extract_common_prefix_tokens(tokens_1:torch, tokens_2:torch):
    tokens_1 = tokens_1.to(lm.model.device)
    tokens_2 = tokens_2.to(lm.model.device)
    for i in range(min(len(tokens_1), len(tokens_2))):
        if tokens_1[i] != tokens_2[i]:
            return tokens_1[:i]
        
def extract_common_prefix_str(output_str, target_str):
    # target_str should have been stripped
    print('DEBUG: output_str: ', output_str)
    print('DEBUG: target_str: ', target_str)
    striped_output_str = output_str.lstrip() # remove leading spaces, tabs, newlines
    striped_str = output_str[:len(output_str) - len(striped_output_str)]
    for i in range(min(len(striped_output_str), len(target_str))):
        if striped_output_str[i] != target_str[i]:
            return output_str[:len(striped_str) + i], striped_str
    return output_str[:len(striped_str) + min(len(striped_output_str), len(target_str))], striped_str

def str_2_tokens(str:str):
    return lm.tokenizer.encode(str, return_tensors='pt', add_special_tokens=False)[0]

def tokens_2_str(tokens:torch):
    return lm.tokenizer.decode(tokens, skip_special_tokens=True)

def load_vocab(vocab_path):
    with open(vocab_path, 'r') as f:
        vocab = json.load(f)
    return vocab

def concat_encoded_tokens(token_tensor_1, token_tensor_2):
    # shape of token_tensor_1 and token_tensor_2: (1, seq_len)
    token_tensor_1 = token_tensor_1.to(lm.model.device)
    token_tensor_2 = token_tensor_2.to(lm.model.device)
    return torch.cat((token_tensor_1[0][:], token_tensor_2[0][:])).unsqueeze(0)

def concat_encoded_tokens_list(token_tensor_list):
    # shape of element in token_tensor_list: (1, seq_len)
    if len(token_tensor_list) == 1:
        return token_tensor_list[0]
    else:
        return concat_encoded_tokens(token_tensor_list[0], concat_encoded_tokens_list(token_tensor_list[1:]))

def build_input(prefix: str, suffix: str) -> str:
    return prefix + infill_ph + suffix
    
def get_scores(prefix_tokens:torch, suffix_tokens:torch, target_tokens:torch, scores:list):
    # prefix and suffix are stripped encoded tokens
    # target_tokens should also be stripped encoded tokens
    if len(target_tokens) == 0:
        return
    lst = []
    # torch.tensor[[1]], prefix_tokens, torch.tensor[[32099]], suffix_tokens, torch.tensor[[2]]
    lst.append(torch.tensor([[1]]))
    lst.append(prefix_tokens)
    lst.append(torch.tensor([[32099]]))
    lst.append(suffix_tokens)
    lst.append(torch.tensor([[2]]))
    input_tokens = concat_encoded_tokens_list(lst) # shape: (1, seq_len)
    target_tokens = target_tokens.to(lm.model.device)
    
    with torch.no_grad():
        lm.model.reinit(lm.tokenizer, False, set(), 'java', '', '')
        raw_o = lm.model.generate(input_tokens,
                                     max_length=50,
                                     do_sample=True,
                                     output_scores=True,
                                     return_dict_in_generate=True,
                                     temperature=1,
                                     top_k=200,
                                     top_p=1,
                                     use_cache=True)
        t_outputs = lm.model.tokenizer.batch_decode(raw_o.sequences, skip_special_tokens=False)
        t_output = t_outputs[0]
        assert infill_ph in t_output, 'infill_ph not in output' + raw_o.sequences[0]
        next_target_token = target_tokens[0]
        min_index = raw_o.sequences[0, 1:].tolist().index(token_2_id_vocab[infill_ph])
        next_target_token_score_dist = raw_o.scores[min_index + 1][0].softmax(dim=0) # 0 here means the first batch
        score_next_target_token = next_target_token_score_dist[next_target_token] 
    
    scores.append(score_next_target_token.item())
    get_scores(concat_encoded_tokens(prefix_tokens, torch.tensor([[next_target_token]])), suffix_tokens, target_tokens[1:], scores)
     
def get_entropy(prefix:str, suffix:str, patch:str):
    prefix_tokens = lm.tokenizer.encode(prefix, return_tensors='pt', add_special_tokens=False)[0].unsqueeze(0)
    suffix_tokens = lm.tokenizer.encode(suffix, return_tensors='pt', add_special_tokens=False)[0].unsqueeze(0)
    target_tokens = lm.tokenizer.encode(patch, return_tensors='pt', add_special_tokens=False)[0]
    
    torch.cuda.empty_cache()
    print('DEBUG: prefix_tokens: ', prefix_tokens)
    print('DEBUG: prefix_tokens shape: ', prefix_tokens.shape)
    print('DEBUG: suffix_tokens: ', suffix_tokens)
    print('DEBUG: suffix_tokens shape: ', suffix_tokens.shape)
    print('DEBUG: target_tokens: ', target_tokens)
    print('DEBUG: target_tokens shape: ', target_tokens.shape)
    scores = []
    get_scores(prefix_tokens, suffix_tokens, target_tokens, scores)
    scores = [score if score > 0 else MIN_SCORE for score in scores]
    print('DEBUG: scores: ', scores)
    
    neg_logs = [-math.log(score) for score in scores]
    sum_entropy = sum(neg_logs)
    mean_entropy = sum_entropy / len(neg_logs)
    return sum_entropy, mean_entropy

def extract_context_and_patch(buggy_file_path, patched_file_path, max_tokens_length=512):
    # only work for single hunk patches
    assert isfile(buggy_file_path) and isfile(patched_file_path)
    stdout = diff_output = subprocess.run(['diff', '--unified=100', buggy_file_path, patched_file_path], stdout=subprocess.PIPE).stdout
    try:
        diff_output = stdout.decode('utf-8')
    except UnicodeDecodeError:
        diff_output = stdout.decode('iso-8859-1')
    print('buggy_file_path: ', buggy_file_path)
    print('patched_file_path: ', patched_file_path)
    print('DEBUG: diff_output: \n', diff_output)
    lines = diff_output.split('\n')
    info_line = lines[2]
    assert info_line.startswith('@@')
    buggy_lines = []
    patched_lines = []
    prefix_context_lines = []
    suffix_context_lines = []
    prefix_coverd = False
    
    line_counter = 0
    buggy_line_counter = 0
    patched_line_counter = 0
    for line in lines[3:]:
        line_counter += 1
        if line.startswith('@@'): break
        if line.startswith('-'):
            if buggy_line_counter == 0 or line_counter == buggy_line_counter + 1:
                buggy_lines.append(line[1:])
                prefix_coverd = True
                buggy_line_counter = line_counter
        elif line.startswith('+'):
            if patched_line_counter == 0 or line_counter == patched_line_counter + 1:
                if not line[1:].strip().startswith('//'):
                    patched_lines.append(line[1:])
                prefix_coverd = True
                patched_line_counter = line_counter
        elif line.startswith(' ') and not prefix_coverd:
            prefix_context_lines.append(line[1:])
        elif line.startswith(' ') and prefix_coverd:
            suffix_context_lines.append(line[1:])
        elif line.startswith('\\ No newline at end of file') or line == '':
            continue
        else:
            raise Exception('Unexpected line: ' + line)
    assert len(prefix_context_lines) > 0 and len(suffix_context_lines) > 0
    
    tokens = lm.tokenizer.encode(build_input('\n'.join(prefix_context_lines) + '\n', '\n' + '\n'.join(suffix_context_lines)),\
        return_tensors='pt')
    
    while(len(tokens[0]) + 50 >= max_tokens_length):
        if len(prefix_context_lines) > len(suffix_context_lines):
            prefix_context_lines.pop(0)
        else:
            suffix_context_lines.pop()
        tokens = lm.tokenizer.encode(build_input('\n'.join(prefix_context_lines) + '\n', '\n' + '\n'.join(suffix_context_lines)),\
            return_tensors='pt')
    
    assert len(prefix_context_lines) > 0 and len(suffix_context_lines) > 0
    if patched_lines == []:
        patched_lines = [' ']
    elif ''.join(patched_lines).strip() == '':
        patched_lines = [' ']
    return '\n'.join(prefix_context_lines) + '\n', '\n' + '\n'.join(suffix_context_lines), ''.join(patched_lines)

def store_ase_result(ASE_patch_dir, output_path):
    write_head(output_path)
    existing_results = extract_existing_results(output_path)
    with open(output_path, 'a') as f_out:
        for patch_path in find_files_recursive(ASE_patch_dir, "src", '.patch'):
            patch_dir = dirname(patch_path)
            if isfile(join(patch_dir, 'NOT_PLAUSIBLE')): continue
            patch_id = '_'.join(patch_dir.split('/')[-5:])
            print('DEBUG: patch_id: ', patch_id)
            if patch_id in existing_results: continue
            ori_file_path = join(patch_dir, 'buggy1.java')
            patched_file_path = join(patch_dir, 'tool-patch1.java')
            
            assert isfile(ori_file_path) and isfile(patched_file_path)
            prefix, suffix, patch = extract_context_and_patch(ori_file_path, patched_file_path)
            file_name = 'buggy1'
            sum_entropy, mean_entropy = get_entropy(prefix, suffix, patch)
            f_out.write(','.join([patch_id, file_name, str(sum_entropy), str(mean_entropy)]) + '\n')

def extract_existing_results(output_path):
    with open(output_path, 'r') as f:
        lines = f.readlines()
    patch_id_list = []
    for line in lines:
        patch_id_list.append(line.split(',')[0])
    return patch_id_list

def store_prapr_result(prapr_patch_dir, output_path):
    write_head(output_path)
    existing_results = extract_existing_results(output_path)
    with open(output_path, 'a') as f_out:
        for proj in os.listdir(prapr_patch_dir):
            proj_dir = join(prapr_patch_dir, proj)
            for id in os.listdir(proj_dir):
                proj_id_dir = join(proj_dir, id)
                for mutant_id in os.listdir(proj_id_dir):
                    patch_dir = join(proj_id_dir, mutant_id)
                    if isfile(join(patch_dir, 'NO_DIFF')): continue
                    if isfile(join(patch_dir, 'CANT_FIX')): continue
                    
                    patch_id = '_'.join(['prapr', proj, id, mutant_id])
                    if patch_id in existing_results: continue
                    man_patched = False
                    fixed_patched = False
                    patched_file_path = None
                    ori_file_path = None
                    file_name = None                
                    for f in os.listdir(patch_dir):
                        if f.startswith('man-patched-'):
                            man_patched = True
                            patched_file_path = join(patch_dir, f)
                            break
                    if not man_patched:
                        for f in os.listdir(patch_dir):
                            if f.startswith('fixed-patched-'):
                                fixed_patched = True
                                patched_file_path = join(patch_dir, f)
                                break
                    for f in os.listdir(patch_dir):
                        if f.startswith('patched-') and not fixed_patched and not man_patched:
                            patched_file_path = join(patch_dir, f)
                        if f.startswith('ori-'):
                            ori_file_path = join(patch_dir, f)
                            file_name = f[4:-5]
                    assert isfile(patched_file_path) and isfile(ori_file_path)
                    
                    prefix, suffix, patch = extract_context_and_patch(ori_file_path, patched_file_path)
                    
                    sum_entropy, mean_entropy = get_entropy(prefix, suffix, patch)
                    f_out.write(','.join([patch_id, file_name, str(sum_entropy), str(mean_entropy)]) + '\n')

def store_dev_result(dev_patch_dir, output_path):
    write_head(output_path)
    existing_results = extract_existing_results(output_path)
    with open(output_path, 'a') as f_out:
        for proj in os.listdir(dev_patch_dir):
            proj_dir = join(dev_patch_dir, proj)
            for id in os.listdir(proj_dir):
                proj_id_dir = join(proj_dir, id)
                patch_dir = join(proj_id_dir, 'mutant-0')
                patch_id = '_'.join(['dev', proj, id, 'mutant-0'])
                if patch_id in existing_results: continue
                src_patch_path = join(patch_dir, id + '.src.patch')
                try:
                    with open(src_patch_path, 'r', encoding='utf-8') as f:
                        lines = f.readlines()
                except:
                    with open(src_patch_path, 'r', encoding='ISO-8859-1') as f:
                        lines = f.readlines()
                file_name_get = False
                for idx, line in enumerate(lines):
                    if line.startswith('diff') and not lines[idx + 1].startswith('deleted'):
                        file_name = line.strip().split('/')[-1].split('.')[0]
                        file_name_get = True
                        break
                    elif line.startswith('Index: '):
                        file_name = line.strip().split('/')[-1].split('.')[0]
                        file_name_get = True
                assert file_name_get, 'File name not found in ' + src_patch_path
                ori_file_path = join(patch_dir, 'buggy-' + file_name + '.java')
                patched_file_path = join(patch_dir, 'patched-' + file_name + '.java')
                
                prefix, suffix, patch = extract_context_and_patch(ori_file_path, patched_file_path)
                sum_entropy, mean_entropy = get_entropy(prefix, suffix, patch)
                f_out.write(','.join([patch_id, file_name.replace('.java', ''), str(sum_entropy), str(mean_entropy)]) + '\n')

def write_head(output_path):
    if not isfile(output_path):
        with open(output_path, 'w') as f_out:
            f_out.write('patch_id,file_name,sum_entropy,mean_entropy' + '\n')
if __name__ == '__main__':
    datasets = sys.argv[1:]
    torch.cuda.empty_cache()
    model_name = 'Salesforce/codet5-large'
    MIN_SCORE = 1e-10
    sample_size = 100
    lm = SpanLM(pretrained=model_name, batch_size=16)
    infill_ph = "<extra_id_0>"
    infill_ph_1 = "<extra_id_1>"
    lm.model.reinit(lm.tokenizer, False, set(), 'java', '', '')
    
    token_2_id_vocab = load_vocab('vocab.json')
    
    prapr_patch_root_dir = '../../prapr_src_patches_1.2'
    prapr_add_patch_root_dir = '../../prapr_src_patches_2.0'
    ASE_patch_dir = '../../ASE_Patches'
    dev_patch_root_dir = '../../developer_patches_1.2'
    dev_add_patch_root_dir = '../../developer_patches_2.0'
    prapr_csv = 'result_1.2.csv'
    prapr_add_csv = 'result_2.0.csv'
    ase_csv = 'result_ase.csv'
    dev_csv = 'result_dev_1.2.csv'
    dev_add_csv = 'result_dev_2.0.csv'
    
    if 'prapr' in datasets:
        store_prapr_result(prapr_patch_root_dir, prapr_csv)
    if 'prapr_add' in datasets:
        store_prapr_result(prapr_add_patch_root_dir, prapr_add_csv)
    if 'ase' in datasets:
        store_ase_result(ASE_patch_dir, ase_csv)
    if 'dev' in datasets:
        store_dev_result(dev_patch_root_dir, dev_csv)
    if 'dev_add' in datasets:
        store_dev_result(dev_add_patch_root_dir, dev_add_csv)