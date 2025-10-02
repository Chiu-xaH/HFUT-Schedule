# 基于Tessesact5训练的图片验证码识别模型


爬取1000张验证码图片，先用[基础模型](https://github.com/tesseract-ocr/tessdata_best/blob/main/eng.traineddata)粗标注，挑出350张，训练出模型，然后用次模型对剩余样本进行标注，此时准确率已经很高了，最后将1000+样本全部用于训练，得到最终模型

combine_tessdata -u /usr/share/tesseract-ocr/5/tessdata/eng.traineddata ./data/eng/eng.

make training MODEL_NAME=captcha START_MODEL=eng DATA_DIR=./data OUTPUT_DIR=./data/captcha GROUND_TRUTH_DIR=./data/captcha/ground-truth MAX_ITERATIONS=100000 PSM=8 TARGET_CHARSET=./data/captcha/captcha.charset TESSDATA=/usr/share/tesseract-ocr/5/tessdata

一定要用tesseract_best的基础模型训练，准确率比从0训练更高，生成的模型大小在10MB+

识别前请对验证码进行灰度处理，只需要img = Image.open(file_path).convert('L')即可