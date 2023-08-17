from typing import List, Dict
import matplotlib.pyplot as plt
from pathlib import Path
import json

# def ranking_box_plot(rank_dict_list:List[Dict], file_name:str):
#     # plot the box plot of the ranking of the correct patches
#     rank_list_list = list()
#     patch_num_list_list = list()
#     for rank_dict in rank_dict_list:
#         rank_list = list()
#         patch_num_list = list()
#         for bug_id in rank_dict:
#             rank, patch_num, tool = rank_dict[bug_id]
#             assert patch_num > 1
#             rank_list.append(rank)
#             patch_num_list.append(patch_num)
#         rank_list_list.append(rank_list)
#         patch_num_list_list.append(patch_num_list)
        
#     figure, axes = plt.subplots(nrows=1, ncols=2, figsize=(10, 5))
#     box_plot_0 = axes[0].boxplot(rank_list_list, vert=True, patch_artist=True, showfliers=False)
#     box_plot_1 = axes[1].boxplot(patch_num_list_list, vert=True, patch_artist=True, showfliers=False)
#     plt.savefig(file_name)

if __name__ == "__main__":
    rank_dict_dir = Path(__file__).resolve().parent / "rank_dict"
    all_rank_dict = dict()
    for file in rank_dict_dir.glob("*.json"):
        rank_dict = json.load(file.open())
        tool, dataset = file.name.split("_dict.")[0].split("_rank_")
        all_rank_dict[tool] = all_rank_dict.get(tool, dict())
        all_rank_dict[tool][dataset] = rank_dict
        
    figure, axes = plt.subplots(nrows=4, ncols=1, figsize=(20, 20))
    box_plot_list = list()
    counter = 0
    dataset_name_dict = {'ase': 'Wang v1.2', 'prapr': 'PraPR v1.2', 'prapr_new': 'PraPR v2.0', 'merged': 'Merge v2.0'}
    tool_name_dict = {'sum_entropy': 'Sum Entropy', 'mean_entropy': 'Mean Entropy', 'ssfix': 'ssFix', 's3': 's3', 'capgen': 'CapGen'}
    for dataset in dataset_name_dict.keys():
        rank_list_list = list()
        # patch_num_list_list = list()
        for tool in tool_name_dict:
            rank_dict = all_rank_dict[tool][dataset]
            rank_list = list()
            patch_num_list = list()
            for bug_id in rank_dict:
                rank, patch_num, _ = rank_dict[bug_id]
                assert patch_num > 1
                rank_list.append(rank)
                patch_num_list.append(patch_num)
            rank_list_list.append(rank_list)
            # patch_num_list_list.append(patch_num_list)
        rank_list_list.append(patch_num_list)
        # box_plot = axes[counter // 2][counter % 2]
        box_plot = axes[counter]
        box_plot.boxplot(rank_list_list, vert=True, patch_artist=True, showfliers=False)
        box_plot.set_title(dataset_name_dict[dataset], fontsize=20, style='italic')
        box_plot.set_xticklabels(list(tool_name_dict.values()) + ["Patch Number"], fontsize=20, style='italic')
        box_plot_list.append(box_plot)
        counter += 1
    
    plt.savefig("rank_box_plot.png")