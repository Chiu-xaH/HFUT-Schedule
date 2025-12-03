# 爬取全校专业培养方案
数据来源：合工大教务 https://jwglapp.hfut.edu.cn/

注意：合肥校区学生只能抓到合肥校区专业，宣城校区学生只能抓到宣城校区专业

## 培养方案接入聚在工大

1.Fork并Clone [Chiu-xaH.github.io](https://github.com/Chiu-xaH/Chiu-xaH.github.io)

2.(按使用方法)使用脚本下载后，其会生成文件夹programs，将里面的文件放置到 Chiu-xaH.github.io 仓库的/program/hefei中

3.向我PR，合并请求后APP(4.13.4+)即可查询到培养方案

## 脚本使用方法
【下载】 [跳转到目录](/tools/All-Programs-Get-Python)

【依赖安装】 pip install requests

【必须配置】 填写api.py中的auth变量，从【合工大教务】公众号-移动教务，或微信打开 https://jwglapp.hfut.edu.cn/ 登录，抓包请求头Authorization

【运行】 python main.py
