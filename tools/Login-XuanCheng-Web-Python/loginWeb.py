import requests

url_1 = "http://172.18.2.2/" # 图书馆及邻近的敬亭学堂部分教室
url_2 = "http://172.18.3.3/" # 新安学堂部分教室、宿舍

api = {
    "LOGIN_1" : "0.htm", # 两个随便选一个接口登陆就行，或者不加都行
    "LOGIN_2" : "a30.htm",
    "LOGOUT" : "F.htm", # 注销
    "QUERY" : "a31.htm" # 获取流量、余额
}

post_data = {
    "DDDDD": "2023218529", # 学号
    "upass": "010371", # 密码：身份证后六位 如末尾X则选择X前面的六位
    "0MKKey": "宣州Login"
}

def login() : 
    # @POST JSON
    response_1 = requests.post(url_1 + api["LOGIN_1"], data=post_data)
    response_2 = requests.post(url_2 + api["LOGIN_1"], data=post_data)

    if "登录成功" in response_1.text:
        print("登录成功")
    else:
        print("登录失败")

    if "登录成功" in response_2.text:
        print("登录成功")
    else:
        print("登录失败")

def logout() :
    # GET
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
  
def query() :
    # 略
    return None

# 执行区
login()