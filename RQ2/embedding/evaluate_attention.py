import pickle
from sklearn.model_selection import KFold, StratifiedKFold
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import precision_score
from sklearn.svm import SVC, LinearSVC
from sklearn.naive_bayes import GaussianNB
from sklearn.tree import DecisionTreeClassifier
from sklearn.metrics import roc_curve, auc, accuracy_score, recall_score
from sklearn.preprocessing import StandardScaler,MinMaxScaler
from sklearn.metrics.pairwise import *


def evaluation_metrics(y_true, y_pred_prob):
    fpr, tpr, thresholds = roc_curve(y_true=y_true, y_score=y_pred_prob, pos_label=1)

    auc_ = auc(fpr, tpr)

    y_pred = [1 if p >= 0.5 else 0 for p in y_pred_prob]
    cnt_tp = 0
    cnt_tn = 0
    cnt_fp = 0
    cnt_fn = 0
    for i in range(len(y_pred)):
        if y_true[i] == 0 and y_pred[i] == 0:
            cnt_tp += 1
        elif y_true[i] == 1 and y_pred[i] == 0:
            cnt_fp += 1
        elif y_true[i] == 0 and y_pred[i] == 1:
            cnt_fn += 1
        elif y_true[i] == 1 and y_pred[i] == 1:
            cnt_tn += 1
    print("TP: " + str(cnt_tp))
    print("FP: " + str(cnt_fp))
    print("TN: " + str(cnt_tn))
    print("FN: " + str(cnt_fn))
    print("Precision: " + str(cnt_tp / (cnt_tp + cnt_fp)))
    print("Recall: " + str(cnt_tp / (cnt_tp + cnt_fn)))
    print("Correct Recall: " + str(cnt_tn / (cnt_tn + cnt_fp)))
    print("AUC:"+str(auc_))
    # filter incorrect patch by adjusting threshold
    # y_pred = [1 if p >= 0.039 else 0 for p in y_pred_prob]
    # print('real positive: {}, real negative: {}'.format(list(y_true).count(1),list(y_true).count(0)))
    # print('positive: {}, negative: {}'.format(y_pred.count(1),y_pred.count(0)))
    # acc = accuracy_score(y_true=y_true, y_pred=y_pred)
    # prc = precision_score(y_true=y_true, y_pred=y_pred)
    # rc = recall_score(y_true=y_true, y_pred=y_pred)
    # f1 = 2 * prc * rc / (prc + rc)
    # print('Accuracy: %f -- Precision: %f -- Recall: %f -- F1: %f -- AUC: %f' % (acc, prc, rc, f1, auc_))
    # return acc, prc, rc, f1, auc_

def bfp_clf_results_cv(train_data, predict_data, label, label_p, algorithm=None, kfold=5):
    # scaler = MinMaxScaler()
    scaler = StandardScaler()
    scaler.fit_transform(train_data)

    skf = StratifiedKFold(n_splits=kfold,shuffle=True)
    print('Algorithm results:', algorithm)
    accs, prcs, rcs, f1s, aucs = list(), list(), list(), list(), list()
    x_test, y_test = predict_data, label_p
    x_train, y_train = train_data, label
    # weight = sample_weight[train_index]
    clf = None
    if algorithm == 'lr':
        clf = LogisticRegression(solver='lbfgs', max_iter=10000).fit(X=x_train, y=y_train)
    elif algorithm == 'svm':
        clf = SVC(gamma='auto', probability=True, kernel='linear',class_weight='balanced',max_iter=1000,
                  tol=0.1).fit(X=x_train, y=y_train)
        # clf = LinearSVC(max_iter=1000).fit(X=x_train, y=y_train)
    elif algorithm == 'nb':
        clf = GaussianNB().fit(X=x_train, y=y_train)
    elif algorithm == 'dt':
        clf = DecisionTreeClassifier().fit(X=x_train, y=y_train,sample_weight=None)
    y_pred = clf.predict_proba(x_test)[:, 1]
    evaluation_metrics(y_true=y_test, y_pred_prob=y_pred)
    # accs.append(acc)
    # prcs.append(prc)
    # rcs.append(rc)
    # f1s.append(f1)
    # aucs.append(auc_)
    # print('------------------------------------------------------------------------')
    # print('Accuracy: %f -- Precision: %f -- Recall: %f -- F1: %f -- AUC: %f' % (
    # np.array(accs).mean(), np.array(prcs).mean(), np.array(rcs).mean(), np.array(f1s).mean(), np.array(aucs).mean()))


def get_feature(buggy, patched):
    return subtraction(buggy, patched)

def get_features(buggy, patched):
    subtract = subtraction(buggy, patched)
    multiple = multiplication(buggy, patched)
    cos = cosine_similarity(buggy, patched).reshape((-1,1))
    euc = euclidean_similarity(buggy, patched).reshape((-1,1))

    fe = np.hstack((subtract, multiple, cos, euc))
    return fe

def subtraction(buggy, patched):
    return buggy - patched

def multiplication(buggy, patched):
    return buggy * patched

def cosine_similarity(buggy, patched):
    return paired_cosine_distances(buggy, patched)

def euclidean_similarity(buggy, patched):
    return paired_euclidean_distances(buggy, patched)


if __name__ == '__main__':

    model = 'bert'
    # model = 'doc'
    # model = 'cc2vec_premodel'

    # algorithm
    # algorithm, kfold = 'dt', 5
    algorithm, kfold = 'lr', 5
    # algorithm, kfold = 'nb', 5

    # algorithm, kfold = 'svm', 5

    print('model: {}'.format(model))
    
    #replace the path of training dataset and predict dataset here
    path_train = '../data/experiment3/new_kui_data_for_' + model + '-last_870.pickle'
    # path_predict = '../data/experiment3/new_kui_data_for_' + model + '-final_1_2.pickle'
    path_predict = '../data/experiment3/new_kui_data_for_' + model + '-last_2_2.pickle'
    # path = '../data/experiment3/kui_data_for_' + model + '.pickle'
    # path_predict = '../data/experiment3/139_test_data_for_bert.pickle'
    # path_test = '../data/experiment3/139_test_data_for_' + model + '.pickle'

    with open(path_train, 'rb') as input:
        data = pickle.load(input)
    label, buggy, patched = data

    with open(path_predict, 'rb') as input:
        data_predict = pickle.load(input)
    label_p, buggy_p, patched_p = data_predict


    # same size with positive
    index_p = list(np.where(label == 1)[0])
    index_n = list(np.where(label == 0)[0])
    index_pp = list(np.where(label_p == 1)[0])
    index_pn = list(np.where(label_p == 0)[0])

    print('the number of train dataset: {}'.format(len(label)))
    print('positive: {}, negative: {}'.format(len(index_p), (len(label)-len(index_p))))
    print('the number of predict dataset: {}'.format(len(label_p)))
    print('positive: {}, negative: {}'.format(len(index_pp), (len(label_p) - len(index_pp))))

    # data
    train_data = get_features(buggy, patched)
    predict_data = get_features(buggy_p, patched_p)

    bfp_clf_results_cv(train_data=train_data, predict_data = predict_data, label = label, label_p = label_p, algorithm=algorithm, kfold=kfold)