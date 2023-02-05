import sys
import os
import argparse
import json 

from transformers import AutoTokenizer

def build_full_scale_dataset(data_path):
    os.environ['TOKENIZERS_PARALLELISM'] = "false"
    tokenizer = AutoTokenizer.from_pretrained("Salesforce/codet5-large")
    full_scale_dataset = {}

    for bug_name in os.listdir(data_path):
        if bug_name == ".DS_Store":
            continue
        path_file_path = os.path.join(data_path, bug_name, "diff_buggy_fixed/src.patch")
        try:
            with open(path_file_path, encoding='utf-8') as patch_code_lines:
                patch_lines = patch_code_lines.readlines()
        except:
            with open(path_file_path, encoding='ISO-8859-1') as patch_code_lines:
                patch_lines = patch_code_lines.readlines()
        files = []
        files.append(patch_lines)

        patch_place = [] 
        for f in files:
            at_idx = []
            file_path = f[0].split("\t")[0].replace("--- ", "")
            for line in f:
                if line.startswith("@@ -"):
                    part_info = {}
                    buggy_start_idx = ""
                    buggy_length = ""
                    fixed_start_idx = ""
                    fixed_length = ""
                    i = 0
                    while i < len(line):
                        if buggy_start_idx != "" and buggy_length != "" and fixed_start_idx != "" and fixed_length == "":
                            while line[i] != " ":
                                fixed_length += line[i]
                                i += 1
                        if buggy_start_idx != "" and buggy_length != "" and fixed_start_idx == "" and line[i] == "+":
                            i += 1
                            while True:
                                fixed_start_idx += line[i]
                                i += 1
                                if line[i] == ",":
                                    break
                        if buggy_start_idx != "" and buggy_length == "":
                            while line[i] != " ":
                                buggy_length += line[i]
                                i += 1
                        if i >= 4 and buggy_start_idx == "":
                            while True:
                                buggy_start_idx += line[i]
                                i += 1
                                if line[i] == ",":
                                    break
                        i += 1
                    part_info["line_idx"] = f.index(line)
                    part_info["fix_start_idx"] = int(fixed_start_idx)
                    part_info["fix_length"] = int(fixed_length)
                    part_info["bug_start_idx"] = int(buggy_start_idx)
                    part_info["bug_length"] = int(buggy_length)
                    at_idx.append(part_info)
            at_idx.append({"line_idx": len(f)})
            for t in range(len(at_idx)):
                if t == len(at_idx)-1:
                    continue
                idx = at_idx[t]["line_idx"]+1
                fix_idx = at_idx[t]["fix_start_idx"]
                bug_idx = at_idx[t]["bug_start_idx"]
                fix_line_idx = []
                fix_line_idx_in_f = []
                bug_line_idx = []
                bug_line_idx_in_f = []
                while idx < at_idx[t+1]["line_idx"]:
                    if f[idx].startswith("+"):
                        fix_line_idx.append(fix_idx-1)
                        fix_line_idx_in_f.append(idx)
                        fix_idx += 1
                    elif f[idx].startswith("-"):
                        bug_line_idx.append(bug_idx-1)
                        bug_line_idx_in_f.append(idx)
                        bug_idx += 1
                    else:
                        if len(fix_line_idx) != 0 and len(bug_line_idx) != 0:
                            patch_place.append({"file": file_path, "fix": fix_line_idx, "fix_content": [f[kk].replace("+", "") for kk in fix_line_idx_in_f], "bug": bug_line_idx, "bug_content": [f[kk].replace("-", "") for kk in bug_line_idx_in_f], "type": "fix+bug"})
                        elif (len(fix_line_idx) != 0 and len(bug_line_idx) == 0) or (len(fix_line_idx) == 0 and len(bug_line_idx) != 0):
                            raise Exception("Wrong format!")
                        fix_line_idx = []
                        fix_line_idx_in_f = []
                        bug_line_idx = []
                        bug_line_idx_in_f = []
                        fix_idx += 1
                        bug_idx += 1
                    idx += 1
                full_scale_dataset[bug_name] = []
                for pp in patch_place:
                    bug_block_info = {}
                    pp_path = os.path.join(data_path, bug_name, pp["file"])
                    try:
                        with open(pp_path, encoding='utf-8') as source_code_lines:
                            blob_lines = source_code_lines.readlines()
                    except:
                        with open(pp_path, encoding='ISO-8859-1') as source_code_lines:
                            blob_lines = source_code_lines.readlines()
                    context_num = 0
                    prev_len = -1
                    while True:
                        context_num += 1
                        if pp["type"] == "fix+bug":
                            if pp["bug"][0]-context_num >= 0:
                                prefix_tem = "".join(blob_lines[pp["bug"][0]-context_num:pp["bug"][0]])
                            if pp["bug"][-1]+context_num < len(blob_lines):
                                suffix_tem = "".join(blob_lines[pp["bug"][-1]+1:pp["bug"][-1]+context_num+1])
                        else:
                            raise Exception("Wrong format!")
                        tokenization_result = tokenizer.encode(prefix_tem + "<extra_id_0>" + suffix_tem, return_tensors="pt")
                        if prev_len == tokenization_result.shape[1]:
                            print(tokenization_result.shape)
                            context_num = context_num - 1
                            break
                        elif tokenization_result.shape[1] + 51 < 512:
                            prev_len = tokenization_result.shape[1]
                            bug_block_info["prefix"] = prefix_tem
                            bug_block_info["suffix"] = suffix_tem
                        else:
                            print(tokenization_result.shape)
                            context_num = context_num - 1
                            break
                    if pp["type"] == "fix+bug":
                        bug_block_info["buggy"] = bug_block_info["prefix"] + "".join([blob_lines[tt] for tt in pp["bug"]]) + bug_block_info["suffix"]
                    else:
                        raise Exception("Wrong format!")
                    if pp["type"] == "fix+bug":
                        bug_block_info["fix"] = bug_block_info["prefix"] + "".join(pp["fix_content"]) + bug_block_info["suffix"]
                    else:
                        raise Exception("Wrong format!")
                    bug_block_info["start"] = pp["bug"][0]-context_num
                    bug_block_info["end"] = pp["bug"][-1]+context_num
                    bug_block_info["file"] = "/".join(file_path.split("/")[1:])
                    print(bug_block_info["file"])
                    full_scale_dataset[bug_name].append(bug_block_info)
            
    with open("Dataset/example.json", "w") as outfile:
        json.dump(full_scale_dataset, outfile)

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('--data_path', type=str, default="/home/yifeng/Project/dl/repair_plus_plus/example/input_for_alpha_repair/example",
                        help='data path')
    args = parser.parse_args()
    build_full_scale_dataset(args.data_path)