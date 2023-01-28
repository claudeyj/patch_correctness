import json
from difflib import unified_diff


def get_unified_diff(source, mutant):
    output = ""
    for line in unified_diff(source.split('\n'), mutant.split('\n'), lineterm=''):
        output += line + "\n"
    return output


def parse_example():
    with open("Dataset/example.json", "r") as f:
        result = json.load(f)

    cleaned_result = {}
    for k, v_s in result.items():
        for v in v_s:
            lines = v['buggy'].splitlines()
            # leading_white_space = len(lines[0]) - len(lines[0].lstrip())
            leading_white_space = 0
            cleaned_result_v = {"buggy": "\n".join([line[leading_white_space:] for line in lines])}
            lines = v["prefix"].splitlines()
            cleaned_result_v["prefix"] = "\n".join([line[leading_white_space:] for line in lines])
            lines = v["suffix"].splitlines()
            cleaned_result_v["suffix"] = "\n".join([line[leading_white_space:] for line in lines])
            lines = v['fix'].splitlines()
            # leading_white_space = len(lines[0]) - len(lines[0].lstrip())
            leading_white_space = 0
            cleaned_result_v["fix"] = "\n".join([line[leading_white_space:] for line in lines])
            # cleaned_result[k + ".java"]["vn"] = set(v["vn"])
            # TODO if buggy line is empty (i.e. new line is to be added,
            #  find white space from previous/post lines and added it)
            buggy_line = remove_suffix(remove_prefix(cleaned_result_v["buggy"], cleaned_result_v["prefix"]), cleaned_result_v["suffix"]).replace("\n", "")
            cleaned_result_v["buggy_line"] = buggy_line
            cleaned_result[k + "-place-" +str(v_s.index(v)) + ".java"] = cleaned_result_v
            cleaned_result_v["place_num"] = len(v_s)

    result = {k: v for k, v in cleaned_result.items()}

    return result


def remove_suffix(input_string, suffix):
    if suffix and input_string.endswith(suffix):
        return input_string[:-len(suffix)]
    return input_string


def remove_prefix(input_string, prefix):
    if prefix and input_string.startswith(prefix):
        return input_string[len(prefix):]
    return input_string
