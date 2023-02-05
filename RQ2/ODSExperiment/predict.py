#!/usr/bin/python
from pandas import DataFrame, read_excel
import pandas as pd
import numpy as np
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.model_selection import GridSearchCV
from sklearn.metrics import confusion_matrix
from sklearn.linear_model import LogisticRegression
from sklearn.ensemble import RandomForestClassifier
from sklearn.ensemble import AdaBoostClassifier
from sklearn import svm
from sklearn.metrics import roc_auc_score
from sklearn.metrics import accuracy_score
from sklearn.metrics import fbeta_score
from sklearn.metrics import recall_score
from sklearn.model_selection import GridSearchCV
from sklearn.decomposition import PCA
from sklearn.metrics import recall_score
from sklearn.metrics import f1_score
from sklearn.metrics import precision_score
from sklearn.metrics import accuracy_score
from imblearn.over_sampling import SMOTE
import matplotlib.pyplot as plt
import itertools
from sklearn.utils import shuffle
from sklearn.impute import SimpleImputer
from imblearn.over_sampling import SMOTE
from sklearn.model_selection import train_test_split, RandomizedSearchCV
from imblearn.pipeline import make_pipeline as imbalanced_make_pipeline
from sklearn.model_selection import GridSearchCV, cross_val_score, StratifiedKFold, learning_curve
from sklearn import svm
from sklearn.utils import shuffle
import xgboost as xgb
import sys

    
    
if __name__ == '__main__':     
        
        training_list= "./train.csv"
        testing_list= sys.argv[1]
        # testing_list= "./test_example.csv"

        training = pd.read_csv(training_list, encoding='latin1',index_col=False)
        testing = pd.read_csv(testing_list, encoding='latin1',index_col=False)
  
        X_train = training.iloc[:,2:]
        Y_train = training.iloc[:,1] 
        X_test = testing.iloc[:,1:]
        id_test = testing.iloc[:,0]

        X_train, Y_train = shuffle(X_train, Y_train, random_state=0)
        model = xgb.XGBClassifier(random_state=42, max_depth=6, gamma=0.5)
        eval_set=[(X_train,Y_train)]
        model.fit(X_train,Y_train, early_stopping_rounds=30, eval_metric="mae", eval_set=eval_set)
        Y_pred = model.predict(X_test)
        result={'patch':id_test,'prediction_label':Y_pred}
        resultDF = pd.DataFrame(result)
        # resultDF.to_csv('./prediction.csv')
        resultDF.to_csv(sys.argv[2])

    


