import flask
from flask import request, jsonify

from ocr import ocr
from result import ResultEntity, StatusCode

app = flask.Flask(__name__)
app.config['MAX_CONTENT_LENGTH'] = 1 * 1024 * 1024  # 1MB上限


# 接收图片 返回识别结果(字符串)
@app.route('/ocr', methods=['POST','GET'])
def api_month_forecast():
    try:
        if 'file' not in request.files:
            return jsonify(ResultEntity.fail(StatusCode.BAD_REQUEST, "缺少文件参数"))

        file = request.files['file']
        image_bytes = file.read()

        # 调用 OCR
        text = ocr(image_bytes)

        return jsonify(ResultEntity.success(data=text))
    except Exception as e:
        return jsonify(ResultEntity.fail(StatusCode.BAD_REQUEST, f"失败: {str(e)}"))


if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5000)
