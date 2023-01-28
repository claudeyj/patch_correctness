import os
import csv
import sys
from os.path import join, exists, isfile, isdir

def in_balanced(balanced_dataset_file, patch_tuple):
    with open(balanced_dataset_file) as f:
        patches = f.read().strip().split('\n')

    if len(patch_tuple) == 6:
        # ASE patch
        bug_id = patch_tuple[0] + '-' + patch_tuple[1]
        patch_name = bug_id + '_' + '-'.join([patch_tuple[2], patch_tuple[3], patch_tuple[4]])
        return patch_name in patches
    
    if len(patch_tuple) == 4:
        # prapr patch
        bug_id = patch_tuple[0] + '-' + patch_tuple[1]
        patch_name = bug_id + '_' + patch_tuple[2]
        return patch_name in patches

def is_patch_overlap(patch_tuple):
    project, id, tool, patch_id, dlabel, label = patch_tuple
    # patch_dir = join(ASE_patch_root_dir, dlabel, tool, project, id)
    if patch_id != '0': 
        patch_dir = join(ASE_patch_root_dir, 'Patches_others', dlabel, tool, project, id, patch_id)
    else: patch_dir = join(ASE_patch_root_dir, 'Patches_ICSE', dlabel, tool, project, id)
    assert isdir(patch_dir), patch_dir
    return not isfile(join(patch_dir, 'NOT_OVERLAP'))
    
def check_prapr_patch(project, id, mutant_id, patch_root_dir):
    if mutant_id == 'mutant-0': return True
    patch_dir = join(patch_root_dir, project, id, mutant_id)
    correct_path = join(patch_dir, 'correct')
    return isfile(correct_path)

def parse_parpr_result(result_file, patch_root_dir):
    # TP = TN = FN = FP = 0
    patch_set = set()
    with open(result_file) as f:
        reader = csv.reader(f)
        for row in reader:
            [project, id, mutant_id, predict]  = row
            if mutant_id == 'mutant-0': continue
            fact = check_prapr_patch(project, id, mutant_id, patch_root_dir)
            if predict == 'overfitting':
                if not fact:
                    patch_set.add((project, id, mutant_id, 'TP'))
                else:
                    patch_set.add((project, id, mutant_id, 'FP'))
            if predict == 'correct':
                if not fact:
                    patch_set.add((project, id, mutant_id, 'FN'))
                else:
                    patch_set.add((project, id, mutant_id, 'TN'))
    return patch_set

def parse_ase_patch_set(patch_list_path):
    # the patch list includes TPs, i.e., overfitting patches identified as overfitting patches
    patch_set = set()
    with open(patch_list_path) as f:
        patches = f.read().splitlines()
    for patch_name in patches:
        if patch_name.endswith('-plausible'): patch_name = patch_name.replace('-plausible', '')
        if patch_name.startswith('patch'):
            # this patch is from the five tools
            patch_id = patch_name.split('-')[0][5:]
            project, id, tool = patch_name.split('-')[1:4]
        else: 
            patch_id = '0'
            tool, project, id = patch_name.split('-')
        
        if patch_list_path.find('overfitting') != -1: dlabel = 'Doverfitting'
        else: dlabel = 'Dcorrect'
        if patch_id == '0': subdir = 'Patches_ICSE'
        else: subdir = 'Patches_others'
        patch_dir = join(ASE_patch_root_dir, subdir, dlabel, tool, project, id)
        if patch_id != '0': patch_dir = join(patch_dir, patch_id)
        if not isdir(patch_dir): 
            patch_dir = patch_dir.replace(dlabel, 'Dcorrect')
            dlabel = 'Dcorrect'
        assert isdir(patch_dir), patch_dir
        if (project, id, tool, patch_id, dlabel) in patch_set:
            if dlabel == 'Dcorrect': dlabel = 'Doverfitting'
            else: dlabel = 'Dcorrect'
        patch_set.add((project, id, tool, patch_id, dlabel))
    return patch_set

def parse_ase_result():
    ase_patch_set = set()
    all_correct_file = join(ASE_patch_root_dir, 'correct_patches.txt')
    all_overfitting_file = join(ASE_patch_root_dir, 'overfitting_patches.txt')
    opad_overfitting_file = join(ASE_patch_root_dir, test_suite + '_opad_overfitting.txt')
    tp_file = join(ASE_patch_root_dir, test_suite + '_opad_true_overfitting.txt')
    opad_overfitting_patch_set = parse_ase_patch_set(opad_overfitting_file)
    opad_tp_patch_set = parse_ase_patch_set(tp_file)
    opad_fp_patch_set = opad_overfitting_patch_set - opad_tp_patch_set
    all_correct_patch_set = parse_ase_patch_set(all_correct_file)
    all_overfitting_patch_set = parse_ase_patch_set(all_overfitting_file)
    opad_tn_patch_set = all_correct_patch_set - opad_fp_patch_set
    opad_fn_patch_set = all_overfitting_patch_set - opad_tp_patch_set
    labels = ['TP', 'FP', 'FN', 'TN']
    count = 0
    for patch_set in opad_tp_patch_set, opad_fp_patch_set, opad_fn_patch_set, opad_tn_patch_set:
        for patch in patch_set:
            ase_patch_set.add((patch[0], patch[1], patch[2], patch[3], patch[4], labels[count]))
        count += 1
        
    return ase_patch_set
   
def get_confusion_matrix(patches):
    return [len([patch for patch in patches if patch[-1] == label]) for label in ['TP', 'FP', 'FN', 'TN']]
        
if __name__ == '__main__':
    ASE_patch_root_dir = '../../ASE_Patches'
    test_suite = sys.argv[1]
    dataset = sys.argv[2]
    assert test_suite == 'evosuite' or test_suite == 'randoop'
    prapr_patch_root_dir = '../../prapr_src_patches_' + '1.2'
    prapr_result_file = test_suite + '_opad_result_' + '1.2' + '.csv'
    prapr_patch_add_root_dir = '../../prapr_src_patches_' + '2.0'
    prapr_add_result_file = test_suite + '_opad_result_' + '2.0' + '.csv'

    result_prapr = parse_parpr_result(prapr_result_file, prapr_patch_root_dir)
    result_prapr_add = parse_parpr_result(prapr_add_result_file, prapr_patch_add_root_dir)
    result_ase = parse_ase_result()
    
    TP, FP, FN, TN = 0, 0, 0, 0
    if dataset == 'ASE':
        tp, fp, fn, tn = get_confusion_matrix(result_ase)
    if dataset == 'prapr':
        tp, fp, fn, tn = get_confusion_matrix(result_prapr)
    if dataset == 'prapr_new':
        TP, FP, FN, TN = get_confusion_matrix(result_prapr)
        tp, fp, fn, tn = get_confusion_matrix(result_prapr_add)
    if dataset == 'merge':
        TP, FP, FN, TN = get_confusion_matrix(result_prapr | result_prapr_add)
        tp, fp, fn, tn = get_confusion_matrix([patch for patch in result_ase if not is_patch_overlap(patch)])
    if dataset == 'balance':
        tp, fp, fn, tn = 0, 0, 0, 0
        for count in range(10):
            balanced_dataset_file = '../../balanced_dataset/balanced_dataset_patches-' + str(count + 1) + '.txt'
            TP_0, FP_0, FN_0, TN_0 = get_confusion_matrix([patch for patch in result_prapr | result_prapr_add if in_balanced(balanced_dataset_file, patch)])
            tp_0, fp_0, fn_0, tn_0 = get_confusion_matrix([patch for patch in result_ase if (not is_patch_overlap(patch) and in_balanced(balanced_dataset_file, patch))])
            TP += TP_0
            FP += FP_0
            FN += FN_0
            TN += TN_0
            tp += tp_0
            fp += fp_0
            fn += fn_0
            tn += tn_0
    TP += tp
    FP += fp
    FN += fn
    TN += tn
    print('test suite: ' + test_suite)
    print('TP: ' + str(TP))
    print('FP: ' + str(FP))
    print('TN: ' + str(TN))
    print('FN: ' + str(FN))
    precision = TP / float(TP + FP)
    recall = TP / float(TP + FN)
    f1 = 2 * precision * recall /(precision + recall)
    print('precision: ' + str(precision))
    print('recall: ' + str(recall))
    print("F1: " + str(f1))