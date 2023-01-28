import os
# replace the path with prapr patches' route
patches_path = './prapr_src_patches_2.0'
cnt = 0
for root, dirs, files in os.walk(patches_path):
    for file in files:
        if 'CANT_FIX' not in files and 'NO_DIFF' not in files and len(files)>2:
            # cnt += 1
            for f_file in files:
                if f_file == 'src.patch':
                    f = open(root+'/'+'src.patch','r')
                    str = f.readlines()
                    # print(str)
                    for s in str:
                        flag = True
                        if s.startswith("@@"):
                            cnt+=1
                            flag = False
                            print(s.split())
                            ori_start = int(s.split()[1].replace('-','').split(',')[0])
                            ori_line = int(s.split()[1].replace('-','').split(',')[1])
                            ma_start = int(s.split()[2].replace('+','').split(',')[0])
                            ma_line = int(s.split()[2].replace('+','').split(',')[1])
                            print(ori_start, ori_line, ma_start,ma_line)
                            for file_ori in files:
                                if file_ori.startswith('ori-') and file_ori.endswith('.java'):
                                    f_ori = open(root+'/'+file_ori,'r')
                                    ori_str = f_ori.readlines()
                                    print(ori_str[ori_start-1:ori_start-1+ori_line])
                                    print("***")
                                    tokens = (root+'\\'+file_ori).split('\\')
                                    w_name = tokens[-4]+'-'+tokens[-3]+'-'+tokens[-2]+'_ori.txt'
                                    if 'correct' in files:
                                        print("This is a correct patch!***")
                                        f_w_ori = open('./Fragments_2/correct'+'/'+w_name,'w')
                                        f_w_ori.writelines(ori_str[ori_start-1:ori_start-1+ori_line])
                                    else:
                                        f_w_ori = open('./Fragments_2/plausible'+'/'+w_name,'w')
                                        f_w_ori.writelines(ori_str[ori_start - 1:ori_start - 1 + ori_line])

                            flag_p = False
                            for file_man in files:
                                if file_man.startswith('man') and file_man.endswith('.java'):
                                    flag_p = True
                                    f_man = open(root+'/'+file_man,'r')
                                    man_str = f_man.readlines()
                                    print(man_str[ma_start-1:ma_start-1+ma_line])
                                    print("***")
                                    tokens = (root + '\\' + file_man).split('\\')
                                    w_name = tokens[-4] + '-' + tokens[-3] + '-' + tokens[-2] + '_man.txt'
                                    if 'correct' in files:
                                        print("This is a correct patch!")
                                        f_w_man = open('./Fragments_2/correct'+'\\'+w_name,'w' + '\\' + w_name,'w')
                                        f_w_man.writelines(man_str[ma_start-1:ma_start-1+ma_line])
                                    else:
                                        f_w_man = open('./Fragments_2/plausible' + '\\' + w_name,'w')
                                        f_w_man.writelines(man_str[ma_start - 1:ma_start - 1 + ma_line])

                            for file_man in files:
                                if file_man.startswith('fixed') and file_man.endswith('.java'):
                                    if flag_p == True:
                                        break
                                    flag_p = True
                                    f_man = open(root+'/'+file_man,'r')
                                    man_str = f_man.readlines()
                                    print(man_str[ma_start-1:ma_start-1+ma_line])
                                    print("***")
                                    tokens = (root + '\\' + file_man).split('\\')
                                    w_name = tokens[-4] + '-' + tokens[-3] + '-' + tokens[-2] + '_man.txt'
                                    if 'correct' in files:
                                        print("This is a correct patch!")
                                        f_w_man = open('./Fragments_2/correct'+'\\'+w_name,'w' + '\\' + w_name,'w')
                                        f_w_man.writelines(man_str[ma_start-1:ma_start-1+ma_line])
                                    else:
                                        f_w_man = open('./Fragments_2/plausible' + '\\' + w_name,'w')
                                        f_w_man.writelines(man_str[ma_start - 1:ma_start - 1 + ma_line])
                            for file_man in files:
                                if file_man.startswith('patched') and file_man.endswith('.java'):
                                    if flag_p == True:
                                        break
                                    flag_p = True
                                    f_man = open(root+'/'+file_man,'r')
                                    man_str = f_man.readlines()
                                    print(man_str[ma_start-1:ma_start-1+ma_line])
                                    print("***")
                                    tokens = (root + '\\' + file_man).split('\\')
                                    w_name = tokens[-4] + '-' + tokens[-3] + '-' + tokens[-2] + '_man.txt'
                                    if 'correct' in files:
                                        print("This is a correct patch!")
                                        f_w_man = open('./Fragments_2/correct'+'\\'+w_name,'w' + '\\' + w_name,'w')
                                        f_w_man.writelines(man_str[ma_start-1:ma_start-1+ma_line])
                                    else:
                                        f_w_man = open('./Fragments_2/plausible' + '\\' + w_name,'w')
                                        f_w_man.writelines(man_str[ma_start - 1:ma_start - 1 + ma_line])
                            break
            print(files)
            break
print(cnt)