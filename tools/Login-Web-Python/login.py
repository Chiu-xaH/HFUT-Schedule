import requests

url_1 = "http://172.18.2.2/" # 图书馆及邻近的敬亭学堂部分教室
url_2 = "http://172.18.3.3/" # 新安学堂部分教室、宿舍
url_3 = "http://172.16.200.11/" # 合肥

api = {
    "LOGIN_1" : "0.htm", # 三个随便选一个接口登陆就行，或者不加都行
    "LOGIN_2" : "a30.htm",
    "LOGIN_3" : "",
    "LOGOUT" : "F.htm", # 注销
    "QUERY" : "a31.htm" # 获取流量、余额
}

post_data = {
    "DDDDD": "", # 学号
    "upass": "", # 密码：身份证后六位 如末尾X则选择X前面的六位
    "0MKKey": "宣州Login" # 合肥为 "123"
}

def login() :
    response_1 = requests.post(url_1 + api["LOGIN_1"], data=post_data)
    response_2 = requests.post(url_2 + api["LOGIN_1"], data=post_data)

    if "成功" in response_1.text and "已使用" not in response_1.text:
        print("登录成功")
    elif "已使用" in response_1.text:
        print("登录失败")
    else:
        print(response_1.text)

    if "成功" in response_2.text and "已使用" not in response_2.text:
        print("登录成功")
    elif "已使用" in response_2.text:
        print("登录失败")
    else:
        print(response_2.text)

def logout() :
    response_1 = requests.get(url_1 + api["LOGOUT"])
    response_2 = requests.get(url_1 + api["LOGOUT"])

    if "注销成功" in response_1.text:
        print("注销成功")
    else:
        print("注销失败")

    if "注销成功" in response_2.text:
        print("注销成功")
    else:
        print("注销失败")

# 执行区
login()