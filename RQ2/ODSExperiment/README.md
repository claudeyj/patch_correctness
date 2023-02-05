
# Automated Classification of Overfitting Patches with Statically Extracted Code Features

This is the repository of [Automated Classification of Overfitting Patches with Statically Extracted Code Features](http://arxiv.org/pdf/1910.12057) ([doi:10.1109/tse.2021.3071750](https://doi.org/10.1109/tse.2021.3071750))

```
@article{ye2021ods,
 title = {Automated Classification of Overfitting Patches with Statically Extracted Code Features},
 author = {He Ye and Jian Gu and Matias Martinez and Thomas Durieux and Martin Monperrus},
 journal = {IEEE Transactions on Software Engineering},
 year = {2021},
 doi = {10.1109/tse.2021.3071750},
}
```



## Folder Structure
 ```bash
├── Experiment: csv feature data and script for reproducing our experiment
│ 
├── Features: ODS code features
│   └── Code: ODS code description features in JSON format
│   └── Patterns: ODS repair pattern features in JSON format
│   └── Context: ODS context features in JSON format 
├── Source: The source program files that can be taken input for Coming to generate ODS features
│
├── Tests: Evosuite tests generated for Bugs.jar and Bears for labeling the correctness of RepairThemAll patches
│
└── RawRepairThemAllPatches: raw patches from the experiment of RepairThemAll

```


## ODS Feature Extraction

We have integrated ODS feature extraction with an open source tool [Coming](https://github.com/SpoonLabs/coming). 
To extract code features, you can parse a pair of source and target files in Source folder. 
Use the feature mode of Coming to obtain ODS features. 

## parameters
We use the default parameters of XGBoost (i.e., learning_rate sets to 0.3 and max_depth sets to 6), only turning the gamma to 0.5. All parameters can be found in our notebooks. 


# How to use ODS to predict new and unseen patches:

## checkout Coming repository and build it with maven command. Please note the Java version is 1.8.
```
https://github.com/SpoonLabs/coming.git
mvn install -DskipTests
```

## execute the following script with the demo samples in Coming project. You will get a generated csv file called test.csv and the code features in Json format in output path.
```
java -classpath ./target/coming-0-SNAPSHOT-jar-with-dependencies.jar fr.inria.coming.main.ComingMain -input files -mode features -location ./src/main/resources/pairsD4j -output ./out
```
Please be noted that Coming project requires the specific structures of input source and target files: 
```
<location_arg>
├── <diff_folder>
│   └── <modif_file>
│       ├── <diff_folder>_<modif_file>_s.java
│       └── <diff_folder>_<modif_file>_t.java
```
## get the test.csv ready and predict it with the following code. You will find the prediction result generated in prediction.csv.
```
python3 predict.py

You may also need the dependecies:
python3 -m pip install  xgboost
python3 -m pip install scikit-learn
python3 -m pip install imblearn
python3 -m pip install matplotlib
```

