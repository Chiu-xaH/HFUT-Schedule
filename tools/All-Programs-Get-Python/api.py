host = "https://jwglapp.hfut.edu.cn/"

api = {
    "SEARCH_PROGRAM": "eams-micro-server/api/v1/plan/search/major",
    "PROGRAM": "eams-micro-server/api/v1/plan/search/major/plan-courses/{}"
}
# Authorization 抓包获取 JWT格式 只需配置这里即可
auth = ""

base_header = {
    "Authorization": auth
}

total_num = 1

post_search_json = {
    "nameZhLike": "",  # 检索中文，默认不填写获取全部
    "grades": [],  # 检索级，用逗号隔开 ，例如[2023,2024]，默认不填写获取全部
    # "departments": [],
    # "major": None,
    # "majorDirection": None,
    # "education": None,
    # "stdType": None,
    # "cultivateType": None,
    "pageSize": total_num,
    "currentPage": 1
}

def update_num():
    post_search_json["pageSize"] = total_num
