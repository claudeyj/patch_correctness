# build dataset
mkdir -p Dataset
python build_dataset.py --data_path=input_for_alpha_repair/example
# generate patches based on the previous dataset
# folder: save path of patch files
# chances: number of times model try to generate patches. The number of final patches is not the same as this, because identical patches will be only saved once.
# batch size: batch size
mkdir -p Results/example
CUDA_VISIBLE_DEVICES=3 python repair.py --model_name=Salesforce/codet5-large \
                                        --folder=Results/example \
                                        --chances=5000 \
                                        --batch_size=256 1> Results/example/repair.log