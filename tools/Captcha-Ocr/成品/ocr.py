import os
import subprocess
import tempfile
import re

TESSDATA_DIR = os.path.dirname(os.path.abspath(__file__))
LANG_MODEL = "captcha"


def ocr(image_bytes: bytes) -> str:
    with tempfile.NamedTemporaryFile(suffix=".png", delete=False) as tmp_img:
        tmp_img.write(image_bytes)
        tmp_img_path = tmp_img.name

    tmp_txt_path = tmp_img_path + "_out"

    try:
        cmd = [
            "tesseract",
            tmp_img_path,
            tmp_txt_path,
            "-l", LANG_MODEL,
            "--psm", "8",
            "--tessdata-dir", TESSDATA_DIR
        ]
        subprocess.run(cmd, check=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)

        # 读取输出文本
        txt_file = tmp_txt_path + ".txt"
        if not os.path.exists(txt_file):
            raise FileNotFoundError("Tesseract 输出文件不存在")

        with open(txt_file, "r", encoding="utf-8") as f:
            text = f.read().strip()

        # 只保留英文和数字
        text = re.sub(r'[^a-zA-Z0-9]', '', text)
        return text
    finally:
        # 清理临时文件
        if os.path.exists(tmp_img_path):
            os.remove(tmp_img_path)
        if os.path.exists(tmp_txt_path + ".txt"):
            os.remove(tmp_txt_path + ".txt")
