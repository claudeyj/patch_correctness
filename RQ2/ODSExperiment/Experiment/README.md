* The training and testing dataset in the folder data.
* The scripts are named with RQs.


Thanks Prof. Dongsum Kim reported in RQ2 there are 7 patches are different from the original PatchSim pape.

ODS cannot generate features for 2 patches, and the remaining 5 patches lables are as follows and the details are 
under data/miss7patches. To reproduce the results, their feature vectors exist in AddPSMissed.csv/P4JPSMissed.csv/S4RPSMissed.csv with RQ2 script.



|patch|groundtruth|ODS-label|
|---|---|---|
|PatchHDRepair1_Lang57|            correct|      correct|
|patch26_Lang58|            correct|     overfitting|
|patch91_chart21|  overfitting| overfitting|
|patch172_Math80|            overfitting|      overfitting|
|patch177_Math105|            overfitting|      overfitting|
|PatchHDRepair5_Math50| overfitting|No results|
|patch34_Math32| overfitting|No results|
