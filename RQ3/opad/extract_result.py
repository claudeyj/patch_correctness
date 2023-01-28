import os
import sys
from os.path import exists, join, isfile, isdir

def record(project, id, mutant_id, overfitting, result_file):
    label = 'overfitting' if overfitting else 'correct'
    with open(result_file, 'a') as f:
        f.write(','.join([project, id, mutant_id, label]) + '\n')

def compare_failed_tests(failed_tests_patched, failed_tests_buggy):
    if len(failed_tests_patched) > len(failed_tests_buggy):
        return True
    for test in failed_tests_patched:
        if not test in failed_tests_buggy:
            return True
    
    return False

def extract_log(log_path):
    exception_tests = set()
    with open(log_path) as f:
        lines = f.readlines()
    for i in range(len(lines)):
        line = lines[i]
        if line.startswith('---'):
            if lines[i + 1].strip().split(': ')[0].endswith('Exception'):
                exception_tests.add(line.strip().split(' ')[1])
    
    return exception_tests

def compare_log(proj_id_mutant_id_dir, result_log, test_suite):
    project, id, mutant_id = proj_id_mutant_id_dir.split('/')[-3:]
    if not mutant_id.startswith('mutant'): 
        project, id = proj_id_mutant_id_dir.split('/')[-2:]
    patched_log_path = join(proj_id_mutant_id_dir, result_log)
    failed_tests_patched = extract_log(patched_log_path)
    print(join(proj_id_mutant_id_dir, result_log))
    print('patched tests failed: ' + str(len(failed_tests_patched)))
    non_flaky_failed_test = failed_tests_patched
    if len(failed_tests_patched) == 0: return True
    for turn in range(5):
        buggy_log_path = join(buggy_test_report_root_dir, 'flaky_report_' + str(turn), test_suite, project, id, result_log)
        assert isfile(buggy_log_path), buggy_log_path
        failed_tests_buggy = extract_log(buggy_log_path)
        print('ori tests failed: ' + str(len(failed_tests_buggy)))
        non_flaky_failed_test -= failed_tests_buggy
    if len(non_flaky_failed_test) < len(failed_tests_patched): print('some flaky tests or false positive tests removed')

    print('non flaky tests failed: ' + str(len(non_flaky_failed_test)))
    return len(non_flaky_failed_test) == 0


if __name__ == '__main__':
    test_result_root_dir = 'test_report'
    dataset = sys.argv[1]
    test_suite = sys.argv[2]
    assert dataset == '1.2' or dataset == '2.0'
    assert test_suite == 'evosuite' or test_suite == 'randoop'
    
    result_file = test_suite + '_opad_result_' + dataset + '.csv'
    patch_result_dir = '../../prapr_src_patches_' + dataset
    patched_test_report_dir = join(test_result_root_dir, 'patched_test_report_' + dataset)
    buggy_test_report_root_dir = join(test_result_root_dir, 'flaky_check_report_' + dataset)
    fixed_test_report_dir = join(test_result_root_dir, 'fixed_test_report_' + dataset)
    all_projects = 'all_projects_' + dataset + '.txt'

    patched_test_suite_dir = join(patched_test_report_dir, test_suite)
    # for project in os.listdir(patched_test_suite_dir):
    for project in os.listdir(patch_result_dir):
        # proj_dir = join(patched_test_suite_dir, project)
        proj_dir = join(patch_result_dir, project)
        for id in os.listdir(proj_dir):
            proj_id_dir = join(proj_dir, id)
            for mutant_id in os.listdir(proj_id_dir):
                if mutant_id == 'fix-report.log': continue
                # proj_id_mutant_id_dir = join(proj_id_dir, mutant_id)
                patch_test_dir = join(patched_test_suite_dir, project, id, mutant_id)
                mutant_dir = join(proj_id_dir, mutant_id)
                if isfile(join(mutant_dir, 'CANT_FIX')): continue
                if isfile(join(mutant_dir, 'NO_DIFF')): continue
                overfitting = False
                if isdir(patch_test_dir):
                    for result_log in os.listdir(patch_test_dir):
                        correct = compare_log(patch_test_dir, result_log, test_suite)
                        if not correct:
                            overfitting = True
                            break
                record(project, id, mutant_id, overfitting, test_suite + '-' + result_file)

    # calculate developer patch
    fixed_test_suite_dir = join(fixed_test_report_dir, test_suite)
    with open(all_projects) as f:
        lines = f.readlines()
    all_projects = []
    for line in lines:
        all_projects.append(line.strip())
    for proj_id in all_projects:
        project, id = proj_id.split('-')
        proj_dir = join(patch_result_dir, project)
        proj_id_dir = join(fixed_test_suite_dir, project, id)
        overfitting = False
        if isdir(proj_id_dir):
            for result_log in os.listdir(proj_id_dir):
                correct = compare_log(proj_id_dir, result_log, test_suite)
                if not correct:
                    overfitting = True
                    break
        record(project, id, 'mutant-0', overfitting, test_suite + '-' + result_file)
