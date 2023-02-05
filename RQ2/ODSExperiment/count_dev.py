
def print_statistic(res):
    TN = 0
    FP = 0
    for bug_id in res:
        label = res[bug_id]
        if label == '0':
            TN += 1
        else:
            FP += 1
            
    print(TN, FP)

def count_csv(csv_path):
    res = dict()
    with open(csv_path) as f:
        lines = f.readlines()
    for line in lines[1:]:
        patch_name = line.split(',')[1]
        patch_label = line.strip().split(',')[2]
        proj_name, id = patch_name.split('_')[1:3]
        seq = patch_name.split('_')[3]
        if not seq.endswith('-0'): continue
        res[proj_name + '_' + id] = patch_label
    return res
        
dev_1_res = count_csv('prediction_dev_1.2.csv')
dev_2_res = count_csv('prediction_dev_2.0.csv')

print(len(dev_1_res), len(dev_2_res))
print_statistic(dev_1_res)
print_statistic(dev_2_res)