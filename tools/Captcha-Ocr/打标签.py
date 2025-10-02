import os
from PIL import Image

# 图片目录
image_dir = "第二轮标注"
os.makedirs(image_dir, exist_ok=True)

for filename in os.listdir(image_dir):
    file_path = os.path.join(image_dir, filename)
    
    # 只处理图片文件
    if not (filename.lower().endswith((".png", ".jpg", ".jpeg", ".bmp", ".tiff"))):
        continue

    # 去掉扩展名
    name = os.path.splitext(filename)[0]
    
    # 打开图片
    img = Image.open(file_path)
    
    # 保存为 PNG
    png_path = os.path.join(image_dir, name + ".png")
    img.save(png_path, "PNG")
    
    # 删除原文件（如果原文件不是 PNG）
    if file_path != png_path:
        os.remove(file_path)
    
    # 保存标签文件
    txt_path = os.path.join(image_dir, name + ".gt.txt")
    with open(txt_path, "w", encoding="utf-8") as f:
        f.write(name)  # 标签就是文件名
    
    print(f"图片转换: {filename} -> {name}.png，标签文件: {name}.gt.txt")
