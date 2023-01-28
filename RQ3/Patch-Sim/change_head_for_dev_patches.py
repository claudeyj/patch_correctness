import os
dev_patches = os.listdir('dev_patches_apply')
cnt = 0
for patch in dev_patches:
    head_minus = ' '+patch.split('-')[0]+patch.split('-')[1]+'b'
    head_add = head_minus+'_'+patch+'b/'
    print(head_minus, head_add)
    print("Patch: "+patch)
    try:
        f_a = open('dev_patches_apply/'+patch,'r')
        con_a = f_a.readlines()
        f_a.close()
        f_b = open('developer_src_patches/'+patch.replace('-src','.src'),'r')
        con_b = f_b.readlines()
        f_b.close()
        cnt+=1
    except:
        continue
    # print(con_a)
    # print(con_b)
    for i in range(len(con_a)):
        if con_a[i].startswith('---'):
            print(con_a[i])
            for b in con_b:
                if b.startswith('---'):
                    b = b.replace(' a/',head_minus+'/')
                    # print("***"+b)
                    con_a[i] = b
        if con_a[i].startswith('+++'):
            print(con_a[i])
            for b in con_b:
                if b.startswith('+++'):
                    b = b.replace(' b/',head_add)
                    con_a[i] = b
    print(con_a)
    f_right = open('dev_right_head/'+patch,'w')
    f_right.writelines(con_a)
print(cnt)