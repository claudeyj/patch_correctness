import os
from os.path import *
import sys
from shutil import copy
from shutil import copyfile
from shutil import copytree
from shutil import rmtree
from pathlib import Path
import subprocess
from pylib.utils import parse_log_report, get_mutant_class_name, backup_dir, backup_file, mkdir_if_not_exist, get_root_dirs, rmdir

def get_all_bin_patches(bin_patch_root_dir):
    bin_patch_dirs = []
    for project in os.listdir(bin_patch_root_dir):
        proj_dir = os.path.join(bin_patch_root_dir, project)
        for id in os.listdir(proj_dir):
            proj_id_dir = os.path.join(proj_dir, id)
            bin_patch_dirs.append(proj_id_dir)
    
    return bin_patch_dirs

# decompile specific class file
def decompile(class_internal_name, result_path):
    arg = "\'" + class_internal_name + " " + result_path + "\'"
    mkdir_if_not_exist(os.path.dirname(result_path))
    status = os.system("./gradlew run --args="+ arg)
    if not status == 0:
        sys.exit("class decompile error: " + class_internal_name + " " + result_path)

# decompile original class file and mutated class file
def decompile_mutant_and_ori(project, id, classes_dir, mutant_info, pool_path):
    # get mutant class name
    mutant_class_path = pool_path + '/' + mutant_info['mutant_name']
    class_name = get_mutant_class_name(mutant_class_path)
    assert class_name.startswith(mutant_info['file_class_name']), pool_path + ': ' + mutant_info['mutant_name'] + ' ' + class_name + ' ' + mutant_info['src_path'] + ' ' + mutant_info['file_class_name']

    # get mutant file path
    target_file_name = mutant_info['file_class_name'] + '.java'
    target_class_file_path = classes_dir + mutant_info['package'] + '/' + class_name + '.class'
    target_whole_file_path = mutant_info['src_path'].replace('.java', '')
    
    # backup the origianl class file
    backup_file(target_class_file_path, ori_class_back_up_dir + class_name + '.class')
    # decompile original class file
    # decompile(mutant_info['package'] + '/' + class_name, result_dir + project + '/' + id + '/' + mutant_info['mutant_name'].split('.')[0] + '/a-' + target_file_name)
    decompile(target_whole_file_path, result_dir + project + '/' + id + '/' + mutant_info['mutant_name'].split('.')[0] + '/a-' + target_file_name)
    # replace original class file with its mutant
    assert os.path.exists(target_class_file_path)
    parent = os.path.dirname(target_class_file_path)
    os.remove(target_class_file_path)
    copy(mutant_class_path, parent)
    os.rename(parent + '/' + mutant_info['mutant_name'], target_class_file_path)
    # decompile the file with the same name again
    # decompile(mutant_info['package'] + '/' + class_name, result_dir + project + '/' + id + '/' + mutant_info['mutant_name'].split('.')[0] + '/b-' + target_file_name)
    decompile(target_whole_file_path, result_dir + project + '/' + id + '/' + mutant_info['mutant_name'].split('.')[0] + '/b-' + target_file_name)
    os.remove(target_class_file_path)
    backup_file(ori_class_back_up_dir + class_name + '.class', target_class_file_path)
    os.remove(ori_class_back_up_dir + class_name + '.class')

def prepare_project(repo_root_dir, project, id):
    proj_id_dir = join(repo_root_dir, project, id)
    if not isdir(proj_id_dir):
        os.makedirs(proj_id_dir)
        os.system('defects4j checkout -p ' + project + ' -v ' + str(id) + 'b -w ' + proj_id_dir)
        os.system('cd ' + proj_id_dir + ' && ' + 'defects4j compile')
    src_dir, bin_dir, test_src_dir, test_bin_dir = get_root_dirs(proj_id_dir, dataset)
    assert isdir(src_dir) and isdir(bin_dir) and isdir(test_src_dir) and isdir(test_bin_dir)

    return src_dir, bin_dir, test_src_dir, test_bin_dir
        
if __name__ == '__main__':
    dataset = sys.argv[1] # dataset can be either 1.2 or 2.0
    bin_patch_root_dir = '../prapr_bin_patches_' + dataset
    bin_patch_dirs = get_all_bin_patches(bin_patch_root_dir)
    repo_root_dir = '../d4jrepos'

    result_dir = '../prapr_src_patches_' + dataset
    jd_classes_main_dir = './jd-core/build/classes/java/main/'
    jd_classes_back_up_dir = './jd-core/backup/classes-bk'
    ori_class_back_up_dir = './jd-core/backup/ori-class/'
    cur_dir = os.getcwd()
    rmdir(jd_classes_back_up_dir)
    backup_dir(jd_classes_main_dir, jd_classes_back_up_dir)
    for dir in bin_patch_dirs:
        project, id = dir.split('/')[-2:]
        pool_dir = join(dir, 'pool')
        mutant_info_list = parse_log_report(dir)
        src_dir, bin_dir, test_src_dir, test_bin_dir = prepare_project(repo_root_dir, project, id)
        os.system('cp -r ' + bin_dir + '/* ' + jd_classes_main_dir)

        # try different mutant and decompile both original class file and mutant                    
        for mutant_info in mutant_info_list:
            decompile_mutant_and_ori(project, str(id), jd_classes_main_dir, mutant_info, pool_dir)
        # recover backup of jd class files
        rmtree(jd_classes_main_dir)
        backup_dir(jd_classes_back_up_dir, jd_classes_main_dir)