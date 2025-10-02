# save_captchas_threadpool.py
import requests
from concurrent.futures import ThreadPoolExecutor, as_completed
import time, os, random
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry

save_dir = "captchas"
os.makedirs(save_dir, exist_ok=True)

url_tpl = "https://cas.hfut.edu.cn/cas/vercode?time={}"
COUNT = 1000
CONCURRENCY = 20  # 并发数：调小/调大自行试
TIMEOUT = 5

# 设置 session，启用连接池和重试策略
def make_session():
    s = requests.Session()
    s.headers.update({
        "User-Agent": "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36"
    })
    retry = Retry(total=3, backoff_factor=0.5, status_forcelist=(429,500,502,503,504))
    s.mount("https://", HTTPAdapter(max_retries=retry, pool_maxsize=100))
    return s

session = make_session()

def fetch_and_save(idx):
    try:
        t = int(time.time() * 1000) + random.randint(0, 999)  # 加点随机项避免完全重复
        full = url_tpl.format(t)
        # stream=True 可在写入大文件时分块，但验证码通常小，不必须
        r = session.get(full, timeout=TIMEOUT)
        if r.status_code == 200:
            path = os.path.join(save_dir, f"{idx}.jpg")
            with open(path, "wb") as f:
                f.write(r.content)
            return (idx, "ok")
        else:
            return (idx, f"status:{r.status_code}")
    except Exception as e:
        return (idx, f"err:{e}")

def main():
    start = time.perf_counter()
    with ThreadPoolExecutor(max_workers=CONCURRENCY) as ex:
        futures = [ex.submit(fetch_and_save, i) for i in range(COUNT)]
        for fut in as_completed(futures):
            idx, res = fut.result()
            print(f"{idx} -> {res}")
            # 可选：在某些情况下添加极短的随机延迟
            # time.sleep(random.uniform(0, 0.02))
    print("done, elapsed:", time.perf_counter() - start)

if __name__ == "__main__":
    main()
