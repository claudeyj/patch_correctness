import argparse
import sys, os
sys.path.append(os.path.dirname(os.path.abspath(__file__)))
import random
import time
import numpy as np
import torch
import json
import re

from model import SpanLM
from dataset import parse_example, get_unified_diff
from template import generate_match_template


def set_seed(seed: int):
    random.seed(seed)
    np.random.seed(seed)
    torch.manual_seed(seed)
    torch.cuda.manual_seed_all(seed)


def _write_to_file(patch_file, folder, patch):
    try:
        with open(folder + "/" + patch_file, 'w') as f:
            f.write(patch)
    except:
        with open(folder + "/" + patch_file, 'w') as f:
            f.write("write error ... ")
        return False


def generate_templates(prefix: str, suffix: str, buggy_line: str, model: SpanLM):
    templates = []
    leading_white_space = len(buggy_line) - len(buggy_line.lstrip())
    buggy_line = buggy_line.lstrip()

    # entire line replace
    templates.append(("lr",
                    "{}\n{}".format(prefix, " " * leading_white_space),
                    "\n{}".format(suffix),
                    "",
                    ""))

    if len(buggy_line) > 0:
        # partial before
        str_builder = ""
        tokens = model.encode(buggy_line)
        for s in tokens:
            str_builder += model.decode(s)
            templates.append(("pb",
                              "{}\n{}".format(prefix, " " * leading_white_space + str_builder),
                              "\n{}".format(suffix),
                              str_builder,
                              ""))

        # partial after
        str_builder = ""
        for s in reversed(tokens):
            str_builder = model.decode(s) + str_builder
            templates.append(("pa",
                              "{}\n{}".format(prefix, " " * leading_white_space),
                              "{}\n{}".format(str_builder, suffix),
                              "",
                              str_builder))

        template_match = generate_match_template(buggy_line)
        for t_prefix, t_suffix in template_match:
            templates.append(("tm",
                              "{}\n{}".format(prefix, " " * leading_white_space + t_prefix),
                              "{}\n{}".format(t_suffix, suffix),
                              t_prefix,
                              t_suffix))

    return templates


def remove_comments(string):
    pattern = r"(\".*?\"|\'.*?\')|(/\*.*?\*/|//[^\r\n]*$)"
    # first group captures quoted strings (double or single)
    # second group captures comments (//single-line or /* multi-line */)
    regex = re.compile(pattern, re.MULTILINE|re.DOTALL)
    def _replacer(match):
        # if the 2nd group (capturing comments) is not None,
        # it means we have captured a non-quoted (real) comment string.
        if match.group(2) is not None:
            return "" # so we will return empty to remove the comment
        else: # otherwise, we will return the 1st group
            return match.group(1) # captured quoted-string
    return regex.sub(_replacer, string)


def suffix_repair_loop(args, model: SpanLM, prefix, suffix, file_name, folder, bug, t_chances):
    start = time.time()
    repair_result = []
    p_diff = {}
    print("Repairing bug {} ... ".format(file_name.split(".")[0]))

    templates = generate_templates(prefix, suffix, bug['buggy_line'], model)
    vn = set()

    if not model.check_size(prefix=prefix, suffix=suffix):
        return 0, []

    total_times = 0
    for template_name, prefix, suffix, t_prefix, t_suffix in templates:
        print("{}{}{}".format(prefix, ">>> [INSERT] <<<", suffix))
        chances = int((t_chances / bug["place_num"]) / len(templates))
        while chances > 0:
            total_times += 1
            torch.cuda.empty_cache()
            print("Try :{}".format(total_times))
            outputs, entropies = model.predict(prefix=prefix, suffix=suffix, t_prefix=t_prefix,
                                            t_suffix=t_suffix, num_samples=chances,
                                            s_limit=args.s_limit, vn=vn, language=args.language)
            chances -= args.batch_size
            for index, output in enumerate(outputs):
                output = prefix + output + suffix
                unique_output = remove_comments(output).replace(" ", "").replace("\n", "").replace("\t", "").replace("\r", "")
                diff = get_unified_diff(bug['buggy'], output)
                if unique_output in p_diff:
                    repair_result[p_diff[unique_output]]['num'] += 1
                    continue
                p_diff[unique_output] = len(repair_result)
                print(diff)
                _write_to_file(file_name.split(".")[0] + "_" + str(len(repair_result))
                               + "." + file_name.split(".")[1],
                               folder, output)
                repair_result.append({'output': output,
                                      'diff': diff,
                                      'finish_reason': 'stop',
                                      'entropy': entropies[index],
                                      'valid': False,
                                      'type': template_name,
                                      'num': 1})
    end = time.time()
    print("{} Unique Patches Generated in {}s".format(len(repair_result), end - start))

    return total_times, repair_result


def repair(args, model: SpanLM, bugs: dict):
    if not os.path.exists(args.folder):
        os.makedirs(args.folder)
    with open(args.folder + "/args.txt", "w") as f:
        f.write(str(args))

    result = {}
    t_generated = 0
    t_unique = 0
    start_t = time.time()

    for file_name, bug in bugs.items():
        prefix = bug['prefix']
        suffix = bug['suffix']
        n_generated, result[file_name] = suffix_repair_loop(args, model, prefix, suffix, file_name,
                                                            args.folder, bug, args.chances)
        if n_generated >= 1:
            t_generated += n_generated * args.batch_size
            t_unique += len(result[file_name])

    end_t = time.time()

    with open(args.folder + "/stats.txt", "w") as f:
        f.write("Total generated: {}\n".format(t_generated))
        f.write("Total unique: {}\n".format(t_unique))
        f.write("Total time: {}\n".format(end_t - start_t))

    with open(args.folder + "/lm_repair.json", "w") as f:  # write to file
        json.dump(result, f)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--model_name", type=str, default="Salesforce/codet5-large")
    parser.add_argument("--seed", type=int, default=420)
    parser.add_argument("--folder", type=str, default="Results/test")
    parser.add_argument("--batch_size", type=int, default=1)
    parser.add_argument("--chances", type=int, default=1)
    parser.add_argument("--s_limit", action="store_true", default=False)
    args = parser.parse_args()
    set_seed(args.seed)
    print(args)
    bugs = parse_example()
    args.language = "java"

    model = SpanLM(pretrained=args.model_name, batch_size=args.batch_size)
    repair(args, model, bugs)


if __name__ == '__main__':
    main()
