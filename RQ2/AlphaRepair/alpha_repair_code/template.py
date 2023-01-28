import re
import sys, os
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

def match_conditional_expression(code):
    ret = []
    if re.match(r"if\s?\(.+\)\s?{$", code):
        s_code = code.split(")")
        pre_code = ")".join(s_code[:-1])
        post_code = ")" + s_code[-1]
        ret.append((pre_code + ' &&', post_code)) # Java version
        ret.append((pre_code + ' ||', post_code))

    return ret


def match_calling_function(code):
    ret = []
    matches = re.finditer(r"[^)(\s]+\([^)(]+\)", code)
    for match in matches:
        matched_code = match.group()
        sc = code.split(matched_code)
        assert (len(sc) == 2)
        matched_code.split("(")
        ret.append((sc[0], "("+"".join(matched_code.split("(")[1:])+sc[1]))
        # print(matched_code)

    return ret


def match_function_api_call(code):  # lowest level
    ret = []
    matches = re.finditer(r"\([^)(]+\)", code)
    for match in matches:  # Match single function api print(abc)
        matched_code = match.group()
        sc = code.split(matched_code)

        t_prefix = sc[0]+"("
        t_suffix = ")" + sc[1]
        if t_prefix not in [v[0] for v in ret] and t_suffix not in [v[1] for v in ret]:
            ret.append((t_prefix, t_suffix))

    return ret


def _match_function_multi_input_api_call_generate_template(matched_code):
    ret = []
    parameters = matched_code.split(",")

    # ret.append("(<mask>"+",<mask>"*(len(parameters)-1)+")") # Replace all variables.
    ret.append(("(", "," + matched_code + ")")) # add variable in beginning
    ret.append(("("+matched_code + ",", ")")) # add variable in end

    for index, parameter in enumerate(parameters): # Replace each variable with mask while keeping others
        new_code = "("
        for jindex in range(len(parameters)):
            add_code = "<mask>"
            if index != jindex:
                add_code = parameters[jindex]

            if jindex != 0:
                new_code += ","+add_code
            else:
                new_code += add_code
        new_code += ")"
        sc = new_code.split("<mask>")
        ret.append((sc[0], sc[1]))

    return ret


def match_function_multi_input_api_call(code): # lowest level

    ret = []
    matches = re.finditer(r"\([^)(]+,[^)(]+\)", code)
    for match in matches:  # Match single function api print(abc)
        matched_code = match.group()
        sc = code.split(matched_code)
        assert (len(sc) == 2)
        matched_code = matched_code[1:-1]
        for t_prefix, t_suffix in _match_function_multi_input_api_call_generate_template(matched_code):
            ret.append((sc[0] + t_prefix, t_suffix+sc[1]))

    return ret


def generate_match_template(code):

    ret = []

    # Not very smart templates
    ret.extend(match_conditional_expression(code))
    ret.extend(match_function_api_call(code))
    ret.extend(match_function_multi_input_api_call(code))
    ret.extend(match_calling_function(code))

    return ret




