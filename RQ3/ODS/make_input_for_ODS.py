import os,sys
from os.path import *
from shutil import *
import subprocess

def make_input_for_prapr(prapr_patch_dir, target_dir):
    for proj in os.listdir(prapr_patch_dir):
        proj_dir = join(prapr_patch_dir, proj)
        for id in os.listdir(proj_dir):
            proj_id_dir = join(proj_dir, id)
            for mutant_id in os.listdir(proj_id_dir):
                patch_dir = join(proj_id_dir, mutant_id)
                if isfile(join(patch_dir, 'NO_DIFF')): continue
                if isfile(join(patch_dir, 'CANT_FIX')): continue
                
                patch_id = '_'.join(['prapr', proj, id, mutant_id])
                man_patched = False
                fixed_patched = False
                patched_file_path = None
                ori_file_path = None
                file_name = None                
                for f in os.listdir(patch_dir):
                    if f.startswith('man-patched-'):
                        man_patched = True
                        patched_file_path = join(patch_dir, f)
                        break
                if not man_patched:
                    for f in os.listdir(patch_dir):
                        if f.startswith('fixed-patched-'):
                            fixed_patched = True
                            patched_file_path = join(patch_dir, f)
                            break
                for f in os.listdir(patch_dir):
                    if f.startswith('patched-') and not fixed_patched and not man_patched:
                        patched_file_path = join(patch_dir, f)
                    if f.startswith('ori-'):
                        ori_file_path = join(patch_dir, f)
                        file_name = f[4:-5]
                assert isfile(patched_file_path) and isfile(ori_file_path)
                target_diff_dir = join(target_dir, patch_id, file_name)
                os.makedirs(target_diff_dir, exist_ok=True)
                file_prefix = patch_id + '_' + file_name
                copyfile(ori_file_path, join(target_diff_dir, file_prefix + '_s.java'))
                copyfile(patched_file_path, join(target_diff_dir, file_prefix + '_t.java'))

# def make_input_for_ASE(ASE_patch_dir, target_dir):
#     patch_path_list = subprocess.check_output(['find', ASE_patch_dir, '-name', 'src.patch']).decode('utf-8').split()
#     for patch_path in patch_path_list:
#         patch_dir = dirname(patch_path)
        

def make_input_for_dev(dev_patch_dir, target_dir):
    for proj in os.listdir(dev_patch_dir):
        proj_dir = join(dev_patch_dir, proj)
        for id in os.listdir(proj_dir):
            proj_id_dir = join(proj_dir, id)
            patch_dir = join(proj_id_dir, 'mutant-0')
            patch_tuple_list = []
            patch_id = '_'.join(['dev', proj, id, 'mutant-0'])
            
            file_name_list = []
            for file in os.listdir(patch_dir):
                if file.startswith('patched-'):
                    file_name = file[8:-5]
                    file_name_list.append(file_name)
            file_name_list.sort() # ascending order
            counter = 0
            for file_name in file_name_list:
                patched_file_path = join(patch_dir, 'patched-' + file_name + '.java')
                buggy_file_path = join(patch_dir, 'buggy-' + file_name + '.java')
                target_diff_dir = join(target_dir, patch_id + '-' + str(counter), file_name)
                os.makedirs(target_diff_dir, exist_ok=True)
                file_prefix = patch_id + '-' + str(counter) + '_' + file_name
                counter += 1
                if isfile(buggy_file_path):
                    copyfile(buggy_file_path, join(target_diff_dir, file_prefix + '_s.java'))
                else:
                    with open(join(target_diff_dir, file_prefix + '_s.java'), 'w') as f:
                        print('pass', file=f) # pass
                        pass
                copyfile(patched_file_path, join(target_diff_dir, file_prefix + '_t.java'))

if __name__ == "__main__":
    root_dir_path = dirname(dirname(dirname(abspath(__file__))))
    prapr_patch_1_root_dir = join(root_dir_path, 'prapr_src_patches_1.2')
    prapr_patch_2_root_dir = join(root_dir_path, 'prapr_src_patches_2.0')
    ASE_patch_root_dir = join(root_dir_path, 'ASE_Patches')
    dev_patch_1_root_dir = join(root_dir_path, 'developer_patches_1.2')
    dev_patch_2_root_dir = join(root_dir_path, 'developer_patches_2.0')
    target_root_dir = join(root_dir_path, 'RQ3/ODS/pairs')
    
    make_input_for_prapr(prapr_patch_1_root_dir, join(target_root_dir, 'prapr_1.2'))
    make_input_for_prapr(prapr_patch_2_root_dir, join(target_root_dir, 'prapr_2.0'))
    make_input_for_dev(dev_patch_1_root_dir, join(target_root_dir, 'dev_1.2'))
    make_input_for_dev(dev_patch_2_root_dir, join(target_root_dir, 'dev_2.0'))