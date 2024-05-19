import os
mutators_path = './final_anti_20'
mutators_list = os.listdir(mutators_path)
print(mutators_list)

# 1 for the not anti-pattern while 0 for the anti-pattern
mutator_pattern ={'ArgumentPropagationMutator':1, 'ArgumentsListMutator':1, 'ArgumentsListMutatorSecondPhase':1,
                  'BooleanFalseReturnValsMutator':0, 'BooleanTrueReturnValsMutator':0,
                  'ConditionalsBoundaryMutator':1, 'ConstructorCallMutator':1,
                  'EmptyObjectReturnValsMutator':0, 'FieldAccessToMethodCallMutator':1, 'FieldNameMutator':1,
                  'FieldToLocalAccessMutator':1, 'InlineConstantMutator':1, 'LocalNameMutator':1,
                  'LocalToFieldAccessMutator':1, 'LocalToMethodCallMutator':1, 'MathMutator':1,
                  'MemberVariableMutator':1, 'MethodNameMutator':1, 'NakedReceiverMutator':1,
                  'NegateConditionalsMutator':1, 'NonVoidMethodCallGuardMutator':1, 'NonVoidMethodCallMutator':0,
                  'NullReturnValsMutator':0, 'RemoveConditionalMutator_EQUAL_ELSE':0,
                  'RemoveConditionalMutator_EQUAL_IF':0, 'RemoveConditionalMutator_ORDER_ELSE':0,
                  'RemoveConditionalMutator_ORDER_IF':0, 'RemoveIncrementsMutator':0, 'ReturningDereferenceGuardMutator':1,
                  'ReturningMethodCallGuardMutator':0, 'ReturnValsMutator':0, 'VoidMethodCallGuardMutator':1, 'VoidMethodCallMutator':1}
# possible anti: ‘BooleanTrueReturnValsMutator’ ‘DereferenceGuardMutator’ ‘MemberVariableMutator’ ‘NakedReceiverMutator’ ‘NullReturnValsMutator’ ‘VoidMethodCallMutator’

mixed_pattern = {'FieldAccessToMethodCallMutator', 'FieldNameMutator', 'InlineConstantMutator', 'LocalNameMutator', 'LocalToMethodCallMutator',  'ReturningMethodCallGuardMutator'}

cnt_tp = 0
cnt_tn = 0
cnt_fp = 0
cnt_fn = 0

for mutator in mutators_list:
    mutants = os.listdir('./final_anti_20/'+mutator)
    for mutant in mutants:
        # print('./final_anti_2_0/'+mutator+'/'+mutant)
        for root, dirs, files in os.walk('./final_anti_20/'+mutator+'/'+mutant):
            if 'CANT_FIX' not in files and 'NO_DIFF' not in files:
                if 'correct' in files:
                    if mutator_pattern[mutator] == 1:
                        cnt_tn += 1
                        f = open('TN_20.txt', 'a')
                        f.write(mutant + '\n')
                        f.close()
                    else:
                        cnt_fp += 1
                        f = open('FP_20.txt', 'a')
                        print("***FP:" + mutant+ " " + mutator)
                        f.write(mutant + '\n')
                        f.close()
                elif 'correct' not in files:
                    if mutator_pattern[mutator] == 1:
                        cnt_fn += 1
                        f = open('FN_20.txt', 'a')
                        f.write(mutant + '\n')
                        print("***FN:" + mutant + " " + mutator)
                        f.close()
                    else:
                        cnt_tp += 1
                        f = open('TP_20.txt', 'a')
                        f.write(mutant + '\n')
                        f.close()
            break
print("All patches tested successfully: " + str(cnt_tp+cnt_tn+cnt_fn+cnt_fp))
print("TP: " + str(cnt_tp))
print("FP: " + str(cnt_fp))
print("TN: " + str(cnt_tn))
print("FN: " + str(cnt_fn))
print("Precision: " + str(cnt_tp/(cnt_tp+cnt_fp)))
print("Recall: " + str(cnt_tp/(cnt_tp+cnt_fn)))
print("Correct Recall: " + str(cnt_tn/(cnt_tn+cnt_fp)))
