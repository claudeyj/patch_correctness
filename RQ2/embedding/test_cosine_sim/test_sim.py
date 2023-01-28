import os
import numpy as np
from bert_serving.client import BertClient
from sklearn.metrics.pairwise import cosine_similarity

def get_cos_similar_matrix(v1, v2):
    num = np.dot(v1, np.array(v2).T) 
    denom = np.linalg.norm(v1, axis=1).reshape(-1, 1) * np.linalg.norm(v2, axis=1)
    res = num / denom
    res[np.isneginf(res)] = 0
    return 0.5 + 0.5 * res

m = BertClient(check_length=False)
correct_patches =  os.listdir('Fragments_2/correct/')
plausible_patches = os.listdir('Fragments_2/plausible/')
cnt = 0

for file in correct_patches:
    if file.endswith('ori.txt'):
        try:
            f_o = open('Fragments_2/correct/'+file,'r')
            con_o = f_o.read()
            f_m = open('Fragments_2/correct/'+file.replace('ori','man'),'r')
            con_m = f_m.read()
            # f_save = open('vec/correct/'+file.replace('ori',''),'w')
            n1 = m.encode([con_o])
            n2 = m.encode([con_m])
            cnt += 1
            print(cnt)
            # f_save.writelines(n)
            # np.savetxt('vec/correct/'+file.replace('ori',''),'w')
            sim = cosine_similarity(n1,n2)
            print("Sim for correct "+file +' is: '+str(sim))
            print(sim[0][0])
            file_save_name = file.replace('_ori','')
            f_save_c = open('vec/correct/'+file_save_name,'w')
            f_save_c.write(str(sim[0][0]))
            f_save_c.close()
        except:
            print("***Correct failed! "+ file)

for file in plausible_patches:
    if file.endswith('ori.txt'):
        try:
            f_o = open('Fragments_2/plausible/'+file,'r')
            con_o = f_o.read()
            f_m = open('Fragments_2/plausible/'+file.replace('ori','man'),'r')
            con_m = f_m.read()
            # f_save = open('vec/correct/'+file.replace('ori',''),'w')
            n1 = m.encode([con_o])
            n2 = m.encode([con_m])
            cnt += 1
            print(cnt)
            # f_save.writelines(n)
            # np.savetxt('vec/correct/'+file.replace('ori',''),'w')
            sim = cosine_similarity(n1,n2)
            print("Sim for plausible "+file +' is: '+str(sim)+str(np.average(n1)))
            print(sim[0][0])
            f_save_p = open('vec/plausible/' + file.replace('_ori',''), 'w')
            f_save_p.write(str(sim[0][0]))
            f_save_p.close()
        except:
            print("***Plausible failed! "+ file)
print(n1,n2)



f_o = open('Fragments_2/plausible/'+'JacksonCore-17-mutant-12_ori.txt','r')
con_o = f_o.read()
f_m = open('Fragments_2/plausible/'+'JacksonCore-17-mutant-12_man.txt','r')
con_m = f_m.read()
# f_save = open('vec/correct/'+file.replace('ori',''),'w')
n1 = m.encode([con_o])
n2 = m.encode([con_m])
sim = cosine_similarity(n1,n2)
f_save_p = open('vec/plausible/' + 'JacksonCore-17-mutant-12.txt', 'w')
f_save_p.write(str(sim[0][0]))
f_save_p.close()