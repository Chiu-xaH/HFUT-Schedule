import time
import requests
import os

student_id = 123456
session_id = "7e5ad041-59a1-46bd-85e7-120efxaee5cc"
# 会暂时封IP,无论是时间多长，服了
delay_seconds = 10
start = 1
end = 200
# 保存目录（可选）
save_dir = "D:\\results"

# 请求头
headers = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36 Edg/129.0.0.0",
    "Cookie" : f"SESSION={session_id}"
}

os.makedirs(save_dir, exist_ok=True)
# 扫描 batchId 从 start 到 end
for batch_id in range(start,end):
    url = f"http://jxglstu.hfut.edu.cn/eams5-student/for-std/change-major-apply/get-applies?auto=true&batchId={batch_id}&studentId={student_id}"
    try:
        response = requests.get(url, headers=headers)
        print(f"[batchId={batch_id}] 状态码: {response.status_code}")

        if response.status_code == 200:
            json_text = response.text.strip()
            if json_text:
                # 保存
                filename = os.path.join(save_dir, f"{batch_id}.json")
                with open(filename, "w", encoding="utf-8") as f:
                    f.write(json_text)
                print(f"已保存: {filename}")
            else:
                print("返回内容为空，未保存。")
    except Exception as e:
        print(f"[batchId={batch_id}] 请求出错: {e}")

    # 延迟
    time.sleep(delay_seconds)
