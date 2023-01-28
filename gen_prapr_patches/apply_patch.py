import os, sys
from os.path import *
from pylib.utils import check_d4j_test_result, get_hunk_list, get_all_mutant_dirs, get_test_result, find_file, get_modified_relative_path, get_root_dirs, run_d4j_test
from shutil import copyfile

def merge_to_dict(lines:dict, line_number:int, content:str):
    if not line_number in lines.keys():
        lines[line_number] = list()
    lines[line_number].append(content)

def get_diff_lines(hunk):
    insert_lines = dict()
    delete_lines = dict()
    for i in range(1, len(hunk)):
        line = hunk[i]
        state, line_number, content = purify_line(line)
        if state == '+':
            merge_to_dict(insert_lines, line_number, content)
        if state == '-':
            merge_to_dict(delete_lines, line_number, content)
    
    return insert_lines, delete_lines

def get_ori_ori_file_path(project, id, src_root_dir, rela_path):
    proj_id_repo_dir = os.path.join(repo_root_dir, project, id)

def apply_patch(ori_java_file, patched_java_file, patch_path):
    hunk_list = get_hunk_list(patch_path)
    if len(hunk_list) > 1:
        print('multiple hunks found, please mannually fix the patch: ' + patched_java_file)
        sys.exit(0)
    else: assert len(hunk_list) == 1
    hunk = hunk_list[0]
    insert_lines, delete_lines = get_diff_lines(hunk)
    if 0 in insert_lines.keys() or 0 in delete_lines.keys():
        print('wrong line number found, please mannually fix the patch: ' + patched_java_file)
        sys.exit(0)
    if len(insert_lines) != 1 or len(delete_lines) != 1:
        print('insert lines or delete lines not equal to 1: ' + patched_java_file)
        sys.exit(0)
    assert insert_lines[0].keys()[0] == delete_lines[0].keys()[0]
    replace_line_number = delete_lines.keys()[0]
    replace_content = ''
    for line in insert_lines.values()[0]:
        replace_content += line
    with open(ori_java_file) as f:
        ori_lines = f.readlines()
    patched_lines = ori_lines
    patched_lines[replace_line_number - 1] = replace_content
    with open(patched_java_file, 'w') as f:
        f.writelines(patched_lines)

def purify_line(line):
    state = line[0]
    line_number = int(line[3:7])
    content = line[9:]
    return state, line_number, content

if __name__ == '__main__':
    dataset = sys.argv[1]
    cur_dir = os.getcwd()
    repo_root_dir = '../d4jrepos'
    src_patch_root_dir = '../prapr_src_patches_' + dataset
    mutant_dirs = get_all_mutant_dirs(src_patch_root_dir, exclude_cant_diff=True, exclude_no_fix=True)
    for mutant_dir in mutant_dirs:
        if get_test_result(join(mutant_dir, 'd4j-result', 'patched-d4j-test.log')): continue
        file_name = basename(find_file(mutant_dir, 'a-', '.java'))[2:]
        ori_java_file = join(mutant_dir, 'ori-' + file_name)
        patched_java_file = join(mutant_dir, 'patched-' + file_name)
        man_patched_java_file = join(mutant_dir, 'man-patched-' + file_name)
        project, id, mutant_id = mutant_dir.split('/')[-3:]
        patch_path = join(mutant_dir, project + '-' + id + '-' + mutant_id + '.patch')
        if not isfile(ori_java_file):
            relative_path = get_modified_relative_path(os.path.join(mutant_dir, 'mutant-info.log'))
            src_dir, bin_dir, test_src_dir, test_bin_dir = get_root_dirs(join(repo_root_dir, project, id), dataset)
            ori_ori_java_file = join(src_dir, relative_path)
            copyfile(ori_ori_java_file, ori_java_file)
        assert isfile(ori_java_file)
        
        if not isfile(patched_java_file):
            apply_patch(ori_java_file, patched_java_file, patch_path)
        assert isfile(patched_java_file)
        if check_d4j_test_result(mutant_dir): continue
        if isfile(man_patched_java_file): top_patch_java_file = man_patched_java_file
        else: top_patch_java_file = patched_java_file

        copyfile(top_patch_java_file, ori_ori_java_file)
        run_d4j_test(join(repo_root_dir, project, id), join(mutant_dir, 'd4j-result'))
        copyfile(ori_java_file, ori_ori_java_file)
        if not check_d4j_test_result(mutant_dir):
            print('compilation fails or tests fail, please manually fix this patch: ' + patch_path)
            sys.exit(0)