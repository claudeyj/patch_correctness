import os, sys
from os.path import *
import csv, json, copy
import pandas as pd
import numpy as np
import matplotlib
matplotlib.use("agg")
from matplotlib import pyplot as plt
import random
from sklearn.metrics import roc_auc_score, precision_recall_curve, auc
from scipy import stats
from typing import List, Dict

# get scores of ssfix, s3, capgen, opad

def add_patch_property(bugs_dict, bug_id, patch_name, property_name, property_value):
    if not bug_id in bugs_dict.keys(): bugs_dict[bug_id] = dict()
    patches_dict = bugs_dict[bug_id]
    if not patch_name in patches_dict.keys(): patches_dict[patch_name] = dict()
    patch_dict = patches_dict[patch_name]
    patch_dict[property_name] = property_value

def rank_correct_patch(patches_dict, tool):
    # the patches should contain at least one correct patch and one overfitting patch
    
    reverse = tool in ['s3', 'sum_entropy', 'mean_entropy']
    correct_patches_dict = {k: v for k, v in patches_dict.items() if v['label'] == 'correct'}
    overfitting_patches_dict = {k: v for k, v in patches_dict.items() if v['label'] == 'overfitting'}
    if len(correct_patches_dict) > 0 and len(overfitting_patches_dict) == 0: return None
    if len(correct_patches_dict) == 0 and len(overfitting_patches_dict) > 0: return None
    correct_patch_score = max([float(patch_dict[tool]) for patch_dict in correct_patches_dict.values()])
    if reverse: correct_patch_score = min([float(patch_dict[tool]) for patch_dict in correct_patches_dict.values()])
    rank = 1
    for patch_name in patches_dict.keys():
        score = float(patches_dict[patch_name][tool])
        if not reverse:
            if score > correct_patch_score: rank += 1
        else:
            if score < correct_patch_score: rank += 1
    return rank
    
def rank_patches_per_bug(bugs_dict:Dict, tool):
    rank_dict = dict()
    for bug_id in bugs_dict:
        patches_dict = bugs_dict[bug_id]
        rank = rank_correct_patch(patches_dict, tool)
        if rank == None: continue
        # print(bug_id + ': ' + str(rank) + ' out of ' + str(len(patches_dict)))
        rank_dict[bug_id] = (rank, len(patches_dict), tool)
        
    return rank_dict

def rank_patches_per_bug_subdataset(dataset:List, bugs_dict:Dict, tool):
    subset_bugs_dict = dict()
    for item in dataset:
        score, label, patch_full_name = item
        bug_id, patch_name = patch_full_name.split('_')
        subset_bugs_dict[bug_id] = subset_bugs_dict.get(bug_id, dict())
        subset_bugs_dict[bug_id][patch_name] = bugs_dict[bug_id][patch_name]

    return rank_patches_per_bug(subset_bugs_dict, tool)
    
def get_top_N(score_label_list, correct_num, tool):
    top_N = list()
    others = list()
    tied = list()
    if tool == 'capgen' or tool == 'ssfix': reverse = True
    if tool in ['s3', 'sum_entropy', 'mean_entropy']: reverse = False
    sorted_score_label_list = sorted(score_label_list, key=lambda x: x[0], reverse=reverse)
    threshold = sorted_score_label_list[correct_num - 1][0]
    for pair in sorted_score_label_list:
        if pair[0] > threshold: 
            if reverse: top_N.append(pair)
            else: others.append(pair)
        if pair[0] < threshold: 
            if reverse: others.append(pair)
            else: top_N.append(pair)
        if pair[0] == threshold: tied.append(pair)
    
    if len(top_N) < correct_num and len(top_N) + len(tied) > correct_num:
        random.seed(1)
        sampled = random.sample(sorted(tied), correct_num - len(top_N))
        top_N = top_N + sampled
        for data in sampled:
            tied.remove(data)
        others = others + tied
    elif len(top_N) + len(tied) == correct_num:
        top_N = top_N + tied
    assert len(top_N) == correct_num
    return top_N, others

def print_confusion_matrix_from_patches(patches, tool):
    score_label_list = list()
    correct_num = 0
    for bug_id in patches:
        patch_dict = patches[bug_id]
        for patch_name in patch_dict:
            score = float(patch_dict[patch_name][tool])
            label = patch_dict[patch_name]['label']
            score_label_list.append((score, label))
            if label == 'correct': correct_num += 1       
    
    print_confusion_matrix(score_label_list, correct_num, tool)  
    return score_label_list   
            
def print_confusion_matrix(score_label_list, correct_num, tool): 
    TP = 0
    TN = 0
    FP = 0
    FN = 0
    scores = []
    facts = []
    top_N, others = get_top_N(score_label_list, correct_num, tool)
    for element in top_N:
        if element[1] == 'correct':
            TN += 1
            facts.append(1)
        else:
            FN += 1
            facts.append(0)
        scores.append(element[0])
    for element in others:
        if element[1] == 'correct':
            FP += 1
            facts.append(1)
        else:
            TP += 1
            facts.append(0)
        scores.append(element[0])
    print("TN: " + str(TN))
    print("FN: " + str(FN))
    print("TP: " + str(TP))
    print("FP: " + str(FP))

    # print("average score: " + str(sum(scores) / len(scores)))
    correct_scores = [element[0] for element in score_label_list if element[1] == 'correct']
    overfitting_scores = [element[0] for element in score_label_list if element[1] == 'overfitting']
    print('correct average: ' + str(sum(correct_scores) / len(correct_scores)))
    if not len(overfitting_scores) == 0:
        print('overfitting average: ' + str(sum(overfitting_scores) / len(overfitting_scores)))
    try:
        print("precision: " + str(TP / (TP + FP)))
        print("recall: " + str(TP / (TP + FN)))
        print("correct recall: " + str(TN / (TN + FP)))
        print("F1: " + str(TP / (TP + 1/2 * (FP + FN))))
        if scores[0] < scores[-1]: scores = [score * (-1) for score in scores] # for s3 only
        print("ROC-AUC: " + str(roc_auc_score(facts, scores)))
        precision, recall, thresholds = precision_recall_curve(facts, scores)
        print("PR-AUC: " + str(auc(recall, precision)))
    except:
        pass
    
def rank_dev_patches(dev_patches, tool_patches, tool):
    reverse = tool == 's3'
    rank_dict = dict()
    for bug_id in tool_patches:
        dev_patch_dict = list(dev_patches[bug_id].values())[0]
        dev_score = float(dev_patch_dict[tool])
        overfitting_patches_dict = {k: v for k, v in tool_patches[bug_id].items() if v['label'] == 'overfitting'}
        rank = 1
        if len(overfitting_patches_dict) == 0: continue
        for _, patch_dict in overfitting_patches_dict.items():
            overfit_patch_score = float(patch_dict[tool])
            if not reverse:
                if overfit_patch_score > dev_score: rank += 1
            else:
                if overfit_patch_score < dev_score: rank += 1
        rank_dict[bug_id] = (rank, len(overfitting_patches_dict) + 1, tool)
        # print('%s: developer patches ranking: %d out of %d' % (bug_id, ranking, len(overfitting_patches_dict) + 1))
    return rank_dict

@DeprecationWarning
def parse_ase_result(patch_list_path):
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
        
        dlabel = 'Doverfitting'
        if patch_id == '0': subdir = 'Patches_ICSE'
        else: subdir = 'Patches_others'
        patch_dir = join(ASE_patch_dir, subdir, dlabel, tool, project, id)
        if patch_id != '0': patch_dir = join(patch_dir, patch_id)
        if not isdir(patch_dir): 
            patch_dir = patch_dir.replace(dlabel, 'Dcorrect')
            dlabel = 'Dcorrect'
        assert isdir(patch_dir)
        patch_set.append((project, id, tool, patch_id, dlabel))
    return patch_set
    
def get_average(score_string):
    if score_string.startswith('['):
        result = 0
        count = 0
        score_string = score_string[1:-1]
        for score_item in score_string.split(', '):
            result += float(score_item)
            count += 1
        return result / count
    else: return float(score_string)

def store_ASE_patches(ase_patches, alpha_result_csv_file):
    with open(join(s3_capgen_dir, 'ASE_patch_score_full.txt')) as f:
        lines = f.readlines()
        for line in lines[1:]:
            # bug, s3, capgen = line.strip().split('\t')
            bug, s3, s3_tool_dev, ast_tool_buggy, ast_tool_dev, cos_tool_buggy, cos_tool_dev, s3var_tool_buggy, \
                s3var_tool_dev, capgen, capgen_dev, var_tool_buggy, var_tool_dev, syntax_tool_buggy, syntax_tool_dev, \
                    sem_tool_buggy, sem_tool_dev = line.strip().split('\t')
            bug = bug.split('Dataset_Overfitting2.D')[1]
            if len(bug.split('.')) == 5: label, tool, project, id, patch_id = bug.split('.')
            else:
                assert len(bug.split('.')) == 4
                label, tool, project, id = bug.split('.')
                patch_id = '0'
            if patch_id == '0': sub_dir = 'Patches_ICSE'
            else: sub_dir = 'Patches_others'
            patch_dir = join(ASE_patch_dir, sub_dir, 'D' + label, tool, project, id)
            if patch_id != '0': patch_dir = join(patch_dir, patch_id)
            assert isdir(patch_dir), patch_dir
            bug_id = '-'.join([project, id])
            patch_name = '-'.join([tool, patch_id, 'D' + label])
            if isfile(join(patch_dir, 'NOT_PLAUSIBLE')): continue
            if isfile(join(patch_dir, 'MISLABEL')):
                if label == 'correct': label = 'overfitting'
                elif label == 'overfitting': label = 'correct'
            add_patch_property(ase_patches, bug_id, patch_name, 'capgen', capgen)
            add_patch_property(ase_patches, bug_id, patch_name, 's3', s3)
            add_patch_property(ase_patches, bug_id, patch_name, 'label', label)
            add_patch_property(ase_patches, bug_id, patch_name, 'overlapping', not isfile(join(patch_dir, 'NOT_OVERLAP')))

            add_patch_property(ase_patches, bug_id, patch_name, 'ASTDist', ast_tool_buggy)
            add_patch_property(ase_patches, bug_id, patch_name, 'ASTCosDist', cos_tool_buggy)
            add_patch_property(ase_patches, bug_id, patch_name, 'VariableDist', s3var_tool_buggy)

            add_patch_property(ase_patches, bug_id, patch_name, 'VariableSimi', var_tool_buggy)
            add_patch_property(ase_patches, bug_id, patch_name, 'SyntaxSimi', syntax_tool_buggy)
            add_patch_property(ase_patches, bug_id, patch_name, 'SemanticSimi', sem_tool_buggy)
    
    with open(join(ssfix_dir, 'result_ASE_patches_full.csv'), newline='') as f:
        # lines = f.readlines()
        reader = csv.reader(f, delimiter=',')
        for row in reader:
            tool, project, id, patch_id, line_in_buggy, line_in_patched, structural_score, conceptual_score, ssfix, correct = row
            if tool == 'tool': continue
            if correct == 'True': label = 'correct'
            else: label = 'overfitting'
            if patch_id == '0': sub_dir = 'Patches_ICSE'
            else: sub_dir = 'Patches_others'
            patch_dir = join(ASE_patch_dir, sub_dir, 'D' + label, tool, project, id)
            if patch_id != '0': patch_dir = join(patch_dir, patch_id)
            assert isdir(patch_dir), patch_dir
            bug_id = '-'.join([project, id])
            patch_name = '-'.join([tool, patch_id, 'D' + label])
            if isfile(join(patch_dir, 'NOT_PLAUSIBLE')): continue
            if isfile(join(patch_dir, 'MISLABEL')):
                if label == 'correct': 
                    label = 'overfitting'
                elif label == 'overfitting': label = 'correct'
            add_patch_property(ase_patches, bug_id, patch_name, 'ssfix', ssfix)
            add_patch_property(ase_patches, bug_id, patch_name, 'TokenStrct', get_average(structural_score))
            add_patch_property(ase_patches, bug_id, patch_name, 'TokenConpt', get_average(conceptual_score))
            assert ase_patches[bug_id][patch_name]['label'] == label, ase_patches[bug_id]
            
    with open(join(alpha_result_csv_file)) as f:
        lines = f.readlines()
        for line in lines[1:]:
            patch_id, file_name, sum_entropy, mean_entropy = line.strip().split(',')
            if patch_id.startswith('Patches_ICSE'):
                mutant_id = '0'
                dlabel, tool, project, id = patch_id.split('_')[-4:]
                sub_dir = 'Patches_ICSE'
            else:
                dlabel, tool, project, id, mutant_id = patch_id.split('_')
                sub_dir = 'Patches_others'
            patch_dir = join(ASE_patch_dir, sub_dir, dlabel, tool, project, id)
            if mutant_id != '0': patch_dir = join(patch_dir, mutant_id)
            assert isdir(patch_dir), patch_dir
            bug_id = '-'.join([project, id])
            patch_name = '-'.join([tool, mutant_id, dlabel])
            if isfile(join(patch_dir, 'NOT_PLAUSIBLE')): continue
            add_patch_property(ase_patches, bug_id, patch_name, 'sum_entropy', sum_entropy)
            add_patch_property(ase_patches, bug_id, patch_name, 'mean_entropy', mean_entropy)
    # store_ASE_opad_result(ase_patches)

def merge_prapr_ase_patches(prapr_patches, ase_patches):
    merged_patches = copy.deepcopy(prapr_patches)
    ase_patches_filtered = dict()
    for bug_id in ase_patches:
        patches_dict = ase_patches[bug_id]
        for patch_name in patches_dict:
            patch_dict = patches_dict[patch_name]
            # Overlapping patches will be excluded when merging
            if patch_dict['overlapping']: continue
            for k, v in patch_dict.items():
                add_patch_property(merged_patches, bug_id, patch_name, k, v)
                add_patch_property(ase_patches_filtered, bug_id, patch_name, k, v)
                
    return merged_patches
    
def store_prapr_or_dev_patches(patches, csv_file, patch_root_dir):
    with open(join(s3_capgen_dir, csv_file), newline='') as f:
        reader = csv.reader(f, delimiter=',')
        for row in reader:
        # for line in lines[1:]:
            project, id, mutant_id, toolASTDifferencing, toolCosine, toolStringDistance, toolVariable, toolSyntax, \
                toolSemantic, s3, capgen, correct = row
            if project == 'project': continue
            if correct == 'TRUE': label = 'correct'
            else: label = 'overfitting'
            patch_dir = join(patch_root_dir, project, id, mutant_id)
            assert isdir(patch_dir), patch_dir
            bug_id = '-'.join([project, id])
            if isfile(join(patch_dir, 'CANT_FIX')): continue
            add_patch_property(patches, bug_id, mutant_id, 's3', s3)
            add_patch_property(patches, bug_id, mutant_id, 'capgen', capgen)
            add_patch_property(patches, bug_id, mutant_id, 'label', label)
            add_patch_property(patches, bug_id, mutant_id, 'overlapping', False)

            add_patch_property(patches, bug_id, mutant_id, 'ASTDist', toolASTDifferencing)
            add_patch_property(patches, bug_id, mutant_id, 'ASTCosDist', toolCosine)
            add_patch_property(patches, bug_id, mutant_id, 'VariableDist', toolStringDistance)

            add_patch_property(patches, bug_id, mutant_id, 'VariableSimi', toolVariable)
            add_patch_property(patches, bug_id, mutant_id, 'SyntaxSimi', toolSyntax)
            add_patch_property(patches, bug_id, mutant_id, 'SemanticSimi', toolSemantic)
    
    with open(join(ssfix_dir, csv_file), newline='') as f:
        # lines = f.readlines()
        reader = csv.reader(f, delimiter=',')
        # for line in lines[1:]:
        for row in reader:
            project, id, mutant_id, line_in_buggy, line_in_patched, structural_score, conceptual_score, ssfix, correct = row
            if project == 'project': continue
            if correct == 'True': label = 'correct'
            else: label = 'overfitting'
            patch_dir = join(patch_root_dir, project, id, mutant_id)
            assert isdir(patch_dir), patch_dir
            bug_id = '-'.join([project, id])
            if isfile(join(patch_dir, 'CANT_FIX')): continue
            add_patch_property(patches, bug_id, mutant_id, 'ssfix', ssfix)
            add_patch_property(patches, bug_id, mutant_id, 'TokenStrct', get_average(structural_score))
            add_patch_property(patches, bug_id, mutant_id, 'TokenConpt', get_average(conceptual_score))
            
    with open(join(alpha_repair_dir, csv_file)) as f:
        lines = f.readlines()
        for line in lines[1:]:
            patch_id, file_name, sum_entropy, mean_entropy = line.strip().split(',')
            patch_dir = join(patch_root_dir, project, id, mutant_id)
            assert isdir(patch_dir), patch_dir
            if isfile(join(patch_dir, 'CANT_FIX')): continue
            _, project, id, mutant_id = patch_id.split('_')
            bug_id = '-'.join([project, id])
            add_patch_property(patches, bug_id, mutant_id, 'sum_entropy', sum_entropy)
            add_patch_property(patches, bug_id, mutant_id, 'mean_entropy', mean_entropy)
            

def display(dict_small, dict_merge, file_name):
    data = []
    text = []
    for bug_id in sorted(dict_merge):
        if not bug_id in dict_small.keys(): 
            data_tmp = np.nan
            text_tmp = '-'
        else: 
            data_tmp = dict_small[bug_id][0] / dict_small[bug_id][1]
            text_tmp = str(dict_small[bug_id][0]) + '/' + str(dict_small[bug_id][1])
        data.append([data_tmp, dict_merge[bug_id][0] / dict_merge[bug_id][1]])
        text.append([text_tmp, str(dict_merge[bug_id][0]) + '/' + str(dict_merge[bug_id][1])])
    
    df = pd.DataFrame(np.array(data), columns=['ASE patches', 'ASE + prapr patches'], index=sorted(dict_merge.keys()))
    fig = plt.figure(figsize=(8, 40))
    ax = fig.add_subplot(111)
    ax.axis('off')
    table = ax.table(cellText=text, rowLabels=df.index, colLabels=df.columns, loc='center', cellColours=plt.cm.Greys(df * 0.8))
    fig.savefig(file_name)

def average_correct_rank(rank_dict):
    rank_sum = 0
    for bug_id in rank_dict:
        rank, patch_num, tool = rank_dict[bug_id]
        rank_sum += rank
    
    return rank_sum / len(rank_dict.keys())

def average_num_patches(rank_dict):
    patch_sum = 0
    for bug_id in rank_dict:
        rank, patch_num, tool = rank_dict[bug_id]
        patch_sum += patch_num
    return patch_sum / len(rank_dict.keys())

def compare_correct_rank(rank_dict_small, rank_dict_merge):
    drop = 0
    for bug_id in rank_dict_small:
        rank_small, patch_num_small, tool = rank_dict_small[bug_id]
        rank_merge, patch_num_merge, tool = rank_dict_merge[bug_id]
        if rank_merge > rank_small: drop += 1
        
    print('%d out of %d bugs correct rank droped' % (drop, len(rank_dict_small)))
    
def get_balanced_dataset(prapr_ase_merged_patches, tool):
    correct_patches = list()
    ase_overfitting_patches = list()
    prapr_overfitting_patches = list()
    for bug_id in prapr_ase_merged_patches:
        patch_dict = prapr_ase_merged_patches[bug_id]
        for patch_name in patch_dict:
            patch_full_name = bug_id + '_' + patch_name
            properties = patch_dict[patch_name]
            label = properties['label']
            if label == 'correct': correct_patches.append((float(properties[tool]), label, patch_full_name))
            if label == 'overfitting': 
                if patch_name.startswith('mutant'): prapr_overfitting_patches.append((float(properties[tool]), label, patch_full_name))
                else: ase_overfitting_patches.append((float(properties[tool]), label, patch_full_name))
                
    correct_num = len(correct_patches)
    ase_overfitting_sample_num = round(584 / (584 + 1905) * correct_num)
    prapr_overfitting_sample_num = round(1905 / (584 + 1905) * correct_num)
    ase_overfitting_patches_sampled = random.sample(sorted(ase_overfitting_patches), ase_overfitting_sample_num)
    prapr_overfitting_patches_sampled = random.sample(sorted(prapr_overfitting_patches), prapr_overfitting_sample_num)
    assert len(ase_overfitting_patches_sampled + prapr_overfitting_patches_sampled) == correct_num, len(ase_overfitting_patches_sampled + prapr_overfitting_patches_sampled)
    return ase_overfitting_patches_sampled + prapr_overfitting_patches_sampled + correct_patches

def print_average_score_for_property(patches, property):
    correct_score_list = list()
    overfitting_score_list = list()
    for bug_id in patches:
        patch_dict = patches[bug_id]
        for patch_name in patch_dict:
            properties = patch_dict[patch_name]
            label = properties['label']
            if properties[property] == 'null': continue # if the value is empty, skip to the next
            score = float(properties[property])
            if label == 'correct': correct_score_list.append(score)
            else: overfitting_score_list.append(score)
    print('property ' + property + ' correct average score: ' + str(np.mean(correct_score_list)))
    print('property ' + property + ' overfitting average score: ' + str(np.mean(overfitting_score_list)))

def print_average_score_all(patches):
    print("\n")
    for property in ['TokenStrct', 'TokenConpt', 'ASTDist', 'ASTCosDist', 'VariableDist', \
         'VariableSimi', 'SyntaxSimi', 'SemanticSimi', 'capgen', 'ssfix', 's3', 'mean_entropy', 'sum_entropy']:
        print_average_score_for_property(patches, property)
        
if __name__ == '__main__':
    ssfix_dir = '../RQ1/ssFix'
    s3_capgen_dir = '../RQ1/refined-scores/capgen_s3'
    alpha_repair_dir = '../RQ1/entropy'
    opad_dir = '../RQ3/opad'
    ASE_patch_dir = '../ASE_Patches'
    prapr_patch_root_dir = '../prapr_src_patches_1.2'
    prapr_add_patch_root_dir = '../prapr_src_patches_2.0'
    dev_patch_root_dir = '../developer_patches_1.2'
    dev_add_patch_root_dir = '../developer_patches_2.0'
    prapr_csv = 'result_1.2.csv'
    prapr_add_csv = 'result_2.0.csv'
    dev_csv = 'result_dev_patches_1.2.csv'
    dev_add_csv = 'result_dev_patches_2.0.csv'
    
    ase_patches = dict()
    prapr_patches = dict()
    dev_patches = dict()
    dev_add_patches = dict()
    prapr_add_patches = dict()
    tool = sys.argv[1]
    
    store_ASE_patches(ase_patches, join(alpha_repair_dir, 'result_ase.csv'))
    store_prapr_or_dev_patches(prapr_patches, prapr_csv, prapr_patch_root_dir)
    store_prapr_or_dev_patches(prapr_add_patches, prapr_add_csv, prapr_add_patch_root_dir)
    store_prapr_or_dev_patches(dev_patches, dev_csv, dev_patch_root_dir)
    store_prapr_or_dev_patches(dev_add_patches, dev_add_csv, dev_add_patch_root_dir)
    # prapr_ase_merged_patches = merge_prapr_ase_patches(prapr_patches, ase_patches)
    prapr_new_patches = prapr_patches.copy()
    prapr_new_patches.update(prapr_add_patches)
    prapr_ase_merged_patches = merge_prapr_ase_patches(prapr_new_patches, ase_patches)
    def print_bug_num_multi_patch(patches):
        print('ori bug num: ' + str(len(patches)))
        print(len([bug_id for bug_id in patches.keys() if len(patches[bug_id].keys()) > 1]))
    print_bug_num_multi_patch(ase_patches)
    print_bug_num_multi_patch(prapr_patches)
    print_bug_num_multi_patch(prapr_new_patches)
    print_bug_num_multi_patch(prapr_ase_merged_patches)
    
    rank_ase_dict = rank_patches_per_bug(ase_patches, tool)
    rank_merged_dict = rank_patches_per_bug(prapr_ase_merged_patches, tool)
    # rank_dev_dict = rank_dev_patches(dev_patches, ase_patches, tool)
    # rank_dev_merged_dict = rank_dev_patches(dev_patches, prapr_ase_merged_patches, tool)
    
    # compare_correct_rank(rank_ase_dict, rank_merged_dict)
    
    print('\nase patches:')
    ase_score_label_list = print_confusion_matrix_from_patches(ase_patches, tool)
    print('AVR / average patch num: %s(%s)' % (average_correct_rank(rank_ase_dict), average_num_patches(rank_ase_dict)))
    print("number of bugs included: " + str(len(rank_ase_dict)))
    
    print('\nprapr 1.2 patches:')
    prapr_score_label_list = print_confusion_matrix_from_patches(prapr_patches, tool)
    rank_prapr_dict = rank_patches_per_bug(prapr_patches, tool)
    print('AVR / average patch num: %s(%s)' % (average_correct_rank(rank_prapr_dict), average_num_patches(rank_prapr_dict)))
    print("number of bugs included: " + str(len(rank_prapr_dict)))
    
    print('\nprapr 2.0 patches:')
    prapr_new_score_label_list = print_confusion_matrix_from_patches(prapr_new_patches, tool)
    rank_prapr_new_dict = rank_patches_per_bug(prapr_new_patches, tool)
    print('AVR / average patch num: %s(%s)' % (average_correct_rank(rank_prapr_new_dict), average_num_patches(rank_prapr_new_dict)))
    print("number of bugs included: " + str(len(rank_prapr_new_dict)))
    
    print('\nprapr + ase merged:')
    merged_score_label_list = print_confusion_matrix_from_patches(prapr_ase_merged_patches, tool)
    print('AVR / average patch num: %s(%s)' % (average_correct_rank(rank_merged_dict), average_num_patches(rank_merged_dict)))
    print("number of bugs included: " + str(len(rank_merged_dict)))
    
    # ranking_box_plot([rank_ase_dict, rank_prapr_dict, rank_prapr_new_dict, rank_merged_dict], 'ranking_box_plot_' + tool + '.png')
    
    # dump the ranking results to json file
    for rank_dict, file_name in zip([rank_ase_dict, rank_prapr_dict, rank_prapr_new_dict, rank_merged_dict], \
        ['rank_dict/' + tool + '_' + name for name in 'rank_ase_dict.json rank_prapr_dict.json rank_prapr_new_dict.json rank_merged_dict.json'.split()]):
        with open(file_name, 'w') as f:
            json.dump(rank_dict, f)
        
    
    # calculate average of sampled balanced datasets
    count = 10
    balanced_datasets = list()
    AVR_list = list()
    patch_num_list = list()
    for i in range(count):
        random.seed(i + 1)
        balanced_dataset_file = '../balanced_dataset/balanced_dataset_patches-' + str(i + 1) + '.txt'
        balanced_dataset = get_balanced_dataset(prapr_ase_merged_patches, tool)
        
        print('\nbalanced dataset %s:' % (i + 1))
        rank_dict_subdataset = rank_patches_per_bug_subdataset(balanced_dataset, prapr_ase_merged_patches, tool)
        patch_num_subset = average_num_patches(rank_dict_subdataset)
        avr_subset = average_correct_rank(rank_dict_subdataset)
        print('AVR / average patch num: %.2f(%.2f)' % (avr_subset, patch_num_subset))
        print("number of bugs included: " + str(len(rank_dict_subdataset)))
        AVR_list.append(avr_subset)
        patch_num_list.append(patch_num_subset)
        
        if not isfile(balanced_dataset_file):
            with open(balanced_dataset_file, 'w') as f:
                f.writelines([x[-1] + '\n' for x in balanced_dataset])
        balanced_datasets += balanced_dataset
    print('\n10 balanced datasets:')
    print_confusion_matrix(balanced_datasets, int(len(balanced_datasets)/2), tool)
    
    print('average AVR / average patch num: %.2f(%.2f)' % (sum(AVR_list) / count, sum(patch_num_list) / count))
    
    print('\ndeveloper patches:')
    dev_new_patches = dev_patches.copy()
    dev_new_patches.update(dev_add_patches)
    dev_score_label_list = print_confusion_matrix_from_patches(dev_patches, tool)
    
    # significance test
    print('\nsignificance test:')
    ase_correct_scores = [element[0] for element in ase_score_label_list if element[1] == 'correct']
    ase_overfitting_scores = [element[0] for element in ase_score_label_list if element[1] == 'overfitting']
    prapr_new_correct_scores =  [element[0] for element in prapr_new_score_label_list if element[1] == 'correct']
    prapr_new_overfitting_scores =  [element[0] for element in prapr_new_score_label_list if element[1] == 'overfitting']
    dev_scores = [element[0] for element in dev_score_label_list]
    alter = 'less' if tool in ['s3', 'mean_entropy', 'sum_entropy'] else 'greater'
    print('ase correct vs overfitting: %s' % stats.mannwhitneyu(ase_correct_scores, ase_overfitting_scores, alternative=alter)[1])
    print('prapr 2.0 correct vs overfitting: %s' % stats.mannwhitneyu(prapr_new_correct_scores, prapr_new_overfitting_scores, alternative=alter)[1])
    print('ase overfitting vs developer: %s' % stats.mannwhitneyu(ase_overfitting_scores, dev_scores, alternative=alter)[1])
    print('prapr 2.0 overfitting vs developer: %s' % stats.mannwhitneyu(prapr_new_overfitting_scores, dev_scores, alternative=alter)[1])

    print_average_score_all(ase_patches)
    
    print_average_score_all(prapr_new_patches)
    print_average_score_all(dev_new_patches)