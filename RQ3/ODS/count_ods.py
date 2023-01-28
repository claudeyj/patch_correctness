import os, sys
from os.path import *

def print_confusion_matrix(result):
    print('TP: %d' % result['TP'])
    print('FP: %d' % result['FP'])
    print('TN: %d' % result['TN'])
    print('FN: %d' % result['FN'])
    print('Precision: %.2f' % (result['TP'] / (result['TP'] + result['FP'])))
    print('Recall: %.2f' % (result['TP'] / (result['TP'] + result['FN'])))
    print('Accuracy: %.2f' % ((result['TP'] + result['TN']) / (result['TP'] + result['TN'] + result['FP'] + result['FN'])))
    print('CPR: %.2f' % (result['TN'] / (result['TN'] + result['FN']))) # CPR = TN / (TN + FN)
    print('F1: %.2f' % (2 * result['TP'] / (2 * result['TP'] + result['FP'] + result['FN'])))
    print('Correct Recall: %.2f' % (result['TN'] / (result['TN'] + result['FP']))) # Correct Recall = TN / (TN + FP), i.e., TNR

if __name__ == '__main__':
    prediction_result_file = sys.argv[1]
    if "1.2" in prediction_result_file:
        version = "1.2"
    elif "2.0" in prediction_result_file:
        version = "2.0"
    patch_root_dir = '../../prapr_src_patches_' + version
    assert isdir(patch_root_dir)
    with open(prediction_result_file, 'r') as f:
        lines = f.readlines()
    result = {}
    for line in lines[1:]:
        seq, patch_id, label = line.strip().split(',')
        _, proj, id, mutant_id = patch_id.split('_')[:4]
        patch_dir = join(patch_root_dir, proj, id, mutant_id)
        correct = isfile(join(patch_dir, 'correct'))
        if correct:
            if label == '0':
                result['TN'] = result.get('TN', 0) + 1
            else:
                result['FP'] = result.get('FP', 0) + 1
        else:
            if label == '0':
                result['FN'] = result.get('FN', 0) + 1
            else:
                result['TP'] = result.get('TP', 0) + 1
    
    print('-----------------')
    print('prapr v{} result:'.format(version))
    print_confusion_matrix(result)
    print('-----------------')
    print('ase result:')
    result_ase = {'TP': 620, 'FP': 66, 'TN': 182, 'FN': 34}
    print_confusion_matrix(result_ase)