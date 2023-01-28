import os
import shutil
# replace the route of prapr patches you want to classify according to mutator here
path_patch = './patch-result-2.0'


mutator_list = []
cnt = 0
for root, dirs, files in os.walk(path_patch):
    # print("Line 24")
    # print(root, dirs, files)
    for file in files:
        # print(os.path.join(root, file))
        if "mutant-info.log" not in files or 'CANT_FIX' in files or 'NO_DIFF' in files:
            break
        else:
            f = open(root+'/'+'mutant-info.log','r')
            # print("***Line 33")
            con = f.readlines()
            for s in con:
                # print("***Line 36")
                if s.startswith('\tMutator'):
                    mutator = s.split(' ')[1].replace('\n','')
                    if mutator not in mutator_list:
                        mutator_list.append(mutator)
                    print(root)
                    fileName = root.replace('\\','-').replace('./patch-result-2.0-','').replace('C:-Users-11497-Desktop-patches_set_combined-0-patch_correctness-patch-result-','')
                    print(fileName)
                    # print("***"+fileName)
                    # print("****Line 43")
                    if os.path.exists('./final_anti_20' + '/' + mutator + '/'+fileName):
                        shutil.rmtree('./final_anti_20' + '/' + mutator + '/'+fileName)
                    cnt += 1
                    # print("***Line 47")
                    shutil.copytree(root, './final_anti_20' + '/' + mutator + '/'+fileName)
                    # print("***"+root, './p-sorted' + '/' + mutator + '/'+fileName)
                    print(mutator, cnt)
                    break
        break
print(cnt)
print(mutator_list, len(mutator_list))

# cnt_f = 0
# dir_list = []
# for m in mutator_list:
#     for dir in os.listdir('./sorted2-0/'+m):
#         print(dir)
#         if dir in dir_list:
#             print("***dir")
#         else:
#         dir_list.append(dir)
#         cnt_f += 1
# print(cnt_f)