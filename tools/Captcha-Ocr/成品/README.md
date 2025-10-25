# 构建 Docker 镜像
```bash
docker build -t captcha-ocr .
```
# 运行 Docker 容器
```bash
docker run -p 5000:5000 captcha-ocr
```
# 导出 Docker 镜像
```bash
docker save -o captcha-ocr.tar captcha-ocr
```