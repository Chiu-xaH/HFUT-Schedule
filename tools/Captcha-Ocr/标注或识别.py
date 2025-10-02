import os
import subprocess
import re

folder_path = "captchas"
tessdata_dir = r"D:\Program Files (x86)\Tesseract\tessdata"
lang_model = "final"  # captcha.traineddata
tesseract_cmd = "tesseract"  # 如果 tesseract 不在 PATH，写完整路径

for filename in os.listdir(folder_path):
    file_path = os.path.join(folder_path, filename)
    if not os.path.isfile(file_path):
        continue
    try:
        # 输出临时文本文件
        temp_txt = os.path.join(folder_path, "temp")
        cmd = [
            tesseract_cmd,
            file_path,
            temp_txt,
            "-l", lang_model,
            "--psm", "8",
            "--tessdata-dir", tessdata_dir
        ]
        subprocess.run(cmd, check=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        
        # 读取识别结果
        with open(temp_txt + ".txt", "r") as f:
            text = f.read().strip()
        
        if text:
            # 转小写并只保留英文和数字
            text = text.lower()
            text = re.sub(r'[^a-z0-9]', '', text)

            # z 改成 2
            # text = text.replace('z', '2')
            # s改成5
            # text = text.replace('s', '5')
            # 9改成g
            # text = text.replace('9', 'g')

            if not text:  # 过滤掉全部被删除的情况
                print(f"{filename} 识别结果过滤后为空")
                os.remove(temp_txt + ".txt")
                continue

            ext = os.path.splitext(filename)[1]
            new_name = f"{text}{ext}"
            new_path = os.path.join(folder_path, new_name)
            counter = 1
            while os.path.exists(new_path):
                new_name = f"{text}_{counter}{ext}"
                new_path = os.path.join(folder_path, new_name)
                counter += 1
            os.rename(file_path, new_path)
            print(f"{filename} -> {new_name}")
        else:
            print(f"{filename} 未识别到文字")

        # 删除临时文本文件
        os.remove(temp_txt + ".txt")

    except Exception as e:
        print(f"处理失败: {filename}, 错误: {e}")
