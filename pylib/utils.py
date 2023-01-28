import os
from os.path import *
import sys
from shutil import copy
from shutil import copyfile
from shutil import copytree
from shutil import rmtree
import subprocess

def get_root_dirs(project_dir, dataset):
    project, id = project_dir.split('/')[-2:]
    assert dataset == '1.2' or dataset == '2.0', 'illegal dataset input!'
    property_file = join(project_dir, 'defects4j.build.properties')

    if dataset == '1.2':
        info_file = join('../d4j1.2_src_path', project, id + '.txt')
        assert isfile(info_file)
        with open(info_file) as f:
            lines = f.readlines()
        src_dir = join(project_dir, lines[0].strip()[1:])
        bin_dir = join(project_dir, lines[1].strip()[1:])
        test_src_dir = join(project_dir, lines[2].strip()[1:])
        test_bin_dir = join(project_dir, lines[3].strip()[1:])

    if dataset == '2.0':
        with open(property_file) as f:
            lines = f.readlines()
        for line in lines:
            if line.startswith('d4j.dir.src.classes'): src_dir = join(project_dir, line.strip().split('=')[1])
            if line.startswith('d4j.dir.src.tests'): test_src_dir = join(project_dir, line.strip().split('=')[1])
            bin_dir = join(project_dir, 'target/classes')
            test_bin_dir = join(project_dir, 'target/test-classes')
            if project_dir.endswith('Gson'):
                bin_dir = join(project_dir, 'gson/target/classes')
                test_bin_dir = join(project_dir, 'gson/target/test-classes')
            
    return src_dir, bin_dir, test_src_dir, test_bin_dir


def parse_log_report(report_dir):
    log_report_path = report_dir + '/fix-report.log'
    thick_seperator = '================================================\n'
    thin_seperator = '------------------------------------------------\n'
    mutant_info_list = list()
    with open(log_report_path) as f:
        content = f.read()
        mutants_content = content.split(thick_seperator)[1]
        mutant_content_list = mutants_content.split(thin_seperator)[0:-1]
        for mutant_content in mutant_content_list:
            mutant_info = dict()
            for line in mutant_content.split('\n'):
                line = line.strip()
                if line.startswith('Mutator'):
                    mutant_info['mutator'] = line.split(': ')[1]
                if line.startswith('File Name'):
                    mutant_info['src_path'] = line.split(': ')[1]
                    mutant_info['file_class_name'] = line.split(': ')[1].split('/')[-1].split('.')[0]
                    mutant_info['package'] = line.split(': ')[1][0:mutant_info['src_path'].rindex('/')]
                if line.startswith('Line Number'):
                    mutant_info['line_number'] = line.split(': ')[1]
                if line.startswith('Dump'):
                    mutant_info['mutant_name'] = line.split(': ')[1]
                if line.startswith('Description'):
                    mutant_info['description'] = line.split(': ')[1]
            mutant_info_list.append(mutant_info)

    return mutant_info_list

def get_mutant_class_name(mutant_class_path):
    assert os.path.exists(mutant_class_path)
    output = subprocess.check_output("javap " + mutant_class_path, shell=True)
    str_output = output.decode('UTF-8')
    first_line = str_output.split('\n')[1]
    class_name = ''
    words = first_line.split(' ')
    for i in range(1, len(words)):
        if words[i - 1] == 'class':
            class_name = words[i].split('.')[-1]
    return class_name.split('<')[0]

def mkdir_if_not_exist(dir):
    if not os.path.isdir(dir):
        os.makedirs(dir)

def rmdir(dir):
    os.system('rm -rf ' + dir)

def backup_dir(src_dir, backup_dir):
    assert os.path.isdir(src_dir)
    mkdir_if_not_exist(os.path.dirname(backup_dir))
    copytree(src_dir, backup_dir)

def backup_file(src_file, backup_file):
    assert os.path.exists(src_file), src_file + '-' + backup_file
    mkdir_if_not_exist(os.path.dirname(backup_file))
    copyfile(src_file, backup_file)

def find_file(dir, prefix, postfix):
    for file in os.listdir(dir):
        if file.startswith(prefix) and file.endswith(postfix):
            return join(dir, file)

def find_files(dir, prefix, postfix):
    file_paths = []
    for file in os.listdir(dir):
        if file.startswith(prefix) and file.endswith(postfix):
            file_paths.append(join(dir, file))
    
    return file_paths

def find_files_recursive(dir, prefix, postfix):
    file_paths = []
    for root, dirs, files in os.walk(dir):
        for file in files:
            if file.startswith(prefix) and file.endswith(postfix):
                file_paths.append(join(root, file))
    
    return file_paths

def get_package_name(mutant_dir):
    with open(os.path.join(mutant_dir, 'mutant-info.log')) as f:
        lines = f.readlines()
    for line in lines:
        line = line.strip()
        if line.startswith('File Name'):
            file_path = line.split(': ')[1]
            package_name = file_path[:file_path.rindex('/')]
            return package_name

def get_all_mutant_dirs(src_patch_root_dir, exclude_no_diff=False, exclude_cant_fix=False):
    mutant_dirs = []
    for project in os.listdir(src_patch_root_dir):
        proj_dir = join(src_patch_root_dir, project)
        for id in os.listdir(proj_dir):
            proj_id_dir = join(proj_dir, id)
            for mutant in os.listdir(proj_id_dir):
                mutant_dir = join(proj_id_dir, mutant)
                if not isdir(mutant_dir): continue
                if exclude_cant_fix and isfile(join(mutant_dir, 'CANT_FIX')): continue
                if exclude_no_diff and isfile(join(mutant_dir, 'NO_DIFF')): continue
                mutant_dirs.append(mutant_dir)

    return mutant_dirs

def get_hunk_list(patch_path):
    assert os.path.isfile(patch_path), patch_path
    with open(patch_path) as f:
        lines = f.readlines()
        if len(lines) == 0:
            return list()
    hunk_loc = list()
    for i in range(len(lines)):
        if lines[i].startswith('@@'):
            hunk_loc.append(i)
    
    hunk_list = list()
    for i in range(len(hunk_loc) - 1):
        hunk_list.append(lines[hunk_loc[i] : hunk_loc[i+1]])

    hunk_list.append(lines[hunk_loc[len(hunk_loc) - 1] : ])

    return hunk_list

def get_test_result(log_path):
    if not isfile(log_path): return False
    with open(log_path) as f:
        content = f.read()
    return content.strip() == 'Failing tests: 0'

def get_modified_relative_path(mutant_info_log_path):
    with open(mutant_info_log_path) as f:
        lines = f.readlines()
    relative_path = lines[3].strip().split(': ')[1]
    assert relative_path.endswith('.java')
    return relative_path

def check_d4j_test_result(mutant_dir):
    test_result_dir = join(mutant_dir, 'd4j-result')
    compile_log_path = join(test_result_dir, 'patched-d4j-compile.log')
    test_log_path = join(test_result_dir, 'patched-d4j-test.log')
    if isfile(test_log_path):
        with open(test_log_path) as f:
            content = f.read()
        return content.strip() == 'Failing tests: 0'
    else: return False


def run_d4j_test(d4j_proj_dir, d4j_result_dir):
    if not os.path.exists(d4j_result_dir):
        os.mkdir(d4j_result_dir)
    os.chdir(d4j_proj_dir)
    os.system('defects4j compile 2> ' + os.path.join(d4j_result_dir, 'patched-d4j-compile.log'))
    os.system('defects4j test > ' + os.path.join(d4j_result_dir, 'patched-d4j-test.log'))