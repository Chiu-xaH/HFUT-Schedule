import os
from PIL import Image

def preprocess_folder(folder_path):
    for filename in os.listdir(folder_path):
        file_path = os.path.join(folder_path, filename)
        if not os.path.isfile(file_path):
            continue
        try:
            # 打开图片并转换为灰度
            img = Image.open(file_path).convert('L')
            # 直接覆盖原文件
            img.save(file_path)
            print(f"处理完成: {filename}")
        except Exception as e:
            print(f"处理失败: {filename}, 错误: {e}")

# 使用示例
preprocess_folder("captchas")
