host = "https://jwglapp.hfut.edu.cn/"

api = {
    "SEARCH_PROGRAM": "eams-micro-server/api/v1/plan/search/major",
    "PROGRAM": "eams-micro-server/api/v1/plan/search/major/plan-courses/{}"
}
# Authorization 抓包获取
auth = "eyJhbGciOiJSUzUxMiJ9.eyJBVFRSX3VzZXJObyI6IjE2ODk1MyIsInN1YiI6IjIwMjMyMTg1MjkiLCJpc3MiOiJqd2dsYXBwLmhmdXQuZWR1LmNuIiwiZGV2aWNlSWQiOiJERVZJQ0VfSUQiLCJBVFRSX2lkZW50aXR5VHlwZUlkIjoiIiwiQVRUUl9hY2NvdW50SWQiOiI0NDA2NjQiLCJBVFRSX3VzZXJJZCI6IjE2ODk1MyIsIkFUVFJfaWRlbnRpdHlUeXBlQ29kZSI6IlN0dWRlbnQiLCJBVFRSX2lkZW50aXR5VHlwZU5hbWUiOiIiLCJBVFRSX29yZ2FuaXphdGlvbk5hbWUiOiIiLCJBVFRSX3VzZXJOYW1lIjoi6LW15oCd5ra1IiwiZXhwIjoxNzM5ODUwNjA0LCJBVFRSX29yZ2FuaXphdGlvbklkIjoiIiwiaWF0IjoxNzM3MjU4NjA0LCJqdGkiOiJJZC1Ub2tlbi1OMlNqZHpYdmVGbEgyMFhiIiwicmVxIjoid3g1ZGI0OWM4ZGI5MGJjYzZmIiwiQVRUUl9vcmdhbml6YXRpb25Db2RlIjoiIn0.NEGljgy54cmrJzjFpvk0K6kmewL4IgcWAwi4CkRI09IPLZMLNTQLriebpLFBLRRpkCrOANlDIwmvwAjEzNbZ1TocHSFuWPSGeu3D3bq_bBb0nsgA8QhM4vvDpAXNbGQKn05bGZF4bmwkfUbUzu2P_YbQWL-ibM5syZrmYayCUvVDW6O4ZMEtNI6D_GKFblJa7_Xjq-iO6mttiWEQ92Jg6kFBfLqpDpBlSrGwi06VvblQ1xC59ViF716Kl260aFoVQW2Sbfm7PUayqSiiBTSlQpKANeT2c7MD7V2kYScHZ3fsa0VGUhuVINgMDqflesOPREcvbMniofZ_Mp1nYmFuPQ"

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
