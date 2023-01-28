import os
category_list = ['Chart','Time','Math','Lang']
patches =  os.listdir('source_patch')
cnt = 0
for patch in patches:
    if 'Closure' not in patch and 'Mockito' not in patch:
        try:
            f = open('source_patch/'+patch,'r')
            str = f.readlines()
            # print(str[0], str[1])
            java_file = patch.split('-')[0]+patch.split('-')[1]
            print(java_file)
            f = open('Sim_head/'+java_file+'.txt', 'r')
            str = f.readlines()
            # line0 = str[0].replace(java_file, java_file+'_'+patch)
            line1 = str[1]
            line2 = str[1].replace(java_file, java_file+'_'+patch).replace('---','+++')
            # print(line0)
            print(line1)
            print(line2)
            f = open('source_patch/' + patch, 'r')
            str = f.readlines()
            str[0] = line1
            str[1] = line2
            print(str)
            f1 = open('patch_head/'+patch,'w')
            f1.writelines(str)
        except:
            cnt+=1
            continue

print(cnt)