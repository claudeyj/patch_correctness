from genericpath import isfile
import os, sys
from os.path import *
from shutil import copyfile
from pylib.utils import get_mutant_class_name, get_package_name, get_all_mutant_dirs, get_root_dirs, get_test_result

if __name__ == '__main__':
    cwd = os.getcwd()
    dataset = sys.argv[1]
    assert dataset == '1.2' or dataset == '2.0'
    d4j_repo_root_dir = '../d4jrepos'
    bin_patch_root_dir = '../prapr_bin_patches_' + dataset
    src_patch_root_dir = '../prapr_src_patches_' + dataset
    temp_log_path = 'temp.log'
    mutant_dirs = get_all_mutant_dirs(src_patch_root_dir)
    for mutant_dir in mutant_dirs:
        os.chdir(cwd)
        print('running ' + mutant_dir)
        [project, id, mutant_id] = mutant_dir.split('/')[-3:]
        d4j_repo_dir = os.path.join(d4j_repo_root_dir, project, id)
        bin_patch_dir = os.path.join(bin_patch_root_dir, project, id)
        mutant_file_path = os.path.join(bin_patch_dir, 'pool', mutant_id + '.class')
        mutant_class_name = get_mutant_class_name(mutant_file_path)
        package_name = get_package_name(mutant_dir)
        bin_root_dir = get_root_dirs(d4j_repo_dir, dataset)
        target_file_path = os.path.join(bin_root_dir, package_name, mutant_class_name + '.class')
        target_file_bak_path = os.path.join(bin_root_dir, package_name, mutant_class_name + '.class-bak')
        if not os.path.exists(target_file_path):
            os.chdir(d4j_repo_dir)
            os.system('defects4j compile')
        assert os.path.isfile(target_file_path), target_file_path
        os.rename(target_file_path, target_file_bak_path)
        assert os.path.isfile(mutant_file_path), mutant_file_path
        assert os.path.isdir(d4j_repo_dir)
        copyfile(mutant_file_path, target_file_path)
        os.chdir(d4j_repo_dir)
        status = os.system('timeout 600 defects4j test > ' + temp_log_path)
        copyfile(target_file_bak_path, target_file_path)
        os.remove(target_file_bak_path)
        if not get_test_result(temp_log_path):
            print(mutant_dir + ' can\'t pass all tests')
            os.system('touch ' + join(mutant_dir, 'CANT_FIX'))
        os.remove(temp_log_path)