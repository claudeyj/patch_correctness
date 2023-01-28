import os, sys
from os.path import *
from pylib.utils import find_file

def tag_no_diff_patch(patch_path):
    # for very few patches, when decompiling there is no difference between a-*.java and b-*.java
    with open(patch_path) as f:
        content = f.read()
    if content == '': os.system('touch ' + join(dirname(patch_path), 'NO_DIFF'))

if __name__ == '__main__':
    dataset = sys.argv[1]
    assert dataset == '1.2' or dataset == '2.0'
    result_dir = '../prapr_src_patches_' + dataset
    for project in os.listdir(result_dir):
        proj_dir = join(result_dir, project)
        for id in os.listdir(proj_dir):
            proj_id_dir = join(proj_dir, id)
            for mutant in os.listdir(proj_id_dir):
                mutant_dir = join(proj_id_dir, mutant)
                a_java = find_file(mutant_dir, 'a-', '.java')
                b_java = find_file(mutant_dir, 'b-', '.java')
                patch_path = join(mutant_dir, '-'.join([project, id, mutant]) + '.patch')
                os.system('diff -u -b ' + a_java.replace('$', '\$') + ' ' + b_java.replace('$', '\$') + ' > ' + patch_path)
                tag_no_diff_patch(patch_path)