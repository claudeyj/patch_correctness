# Average feature scores of three static techniques and eight features from them
-----------
| Patches      | Defects4J 2.0 Correct | Defects4J 2.0 Overfitting | ASE Patches Correct | ASE Patches Overfitting | Developer Patches |
|---|---:|---:|---:|---:|---:|
| ssFix        | 1.52                  | 1.56                      | 1.49                | 1.35                    | 1.16              |
| S3           | 10.60                 | 15.73                     | 11.71               | 26.35                   | 40.64             |
| CapGen       | 0.37                  | 0.41                      | 0.44                | 0.25                    | 0.26              |
| TokenStrct   | 0.71                  | 0.76                      | 0.73                | 0.65                    | 0.55              |
| TokenConpt   | 0.81                  | 0.79                      | 0.76                | 0.70                    | 0.60              |
| ASTDist      | 6.94                  | 9.82                      | 8.24                | 19.19                   | 31.48             |
| ASTCosDist   | 0.12                  | 0.10                      | 0.11                | 0.20                    | 0.09              |
| VariableDist | 2.45                  | 3.16                      | 3.36                | 7.79                    | 8.47              |
| VariableSimi | 0.64                  | 0.65                      | 0.62                | 0.47                    | 0.61              |
| SyntaxSimi   | 0.68                  | 0.77                      | 0.71                | 0.57                    | 0.55              |
| SemanticSimi | 0.65                  | 0.68                      | 0.68                | 0.52                    | 0.56              |