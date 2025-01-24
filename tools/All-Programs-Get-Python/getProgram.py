import json
import os

import requests

import api


# 获取总数量
def get_num():
    url = api.host + api.api["SEARCH_PROGRAM"]
    post_json = api.post_search_json
    response = requests.post(url=url, json=post_json, headers=api.base_header)
    if response.status_code == 200:
        json = response.json()
        data = json["data"]
        api.total_num = data["_page_"]["totalRows"]


# 保存培养方案列表
def save_program_list():
    url = api.host + api.api["SEARCH_PROGRAM"]
    api.update_num()
    post_json = api.post_search_json
    response = requests.post(url=url, json=post_json, headers=api.base_header)
    if response.status_code == 200:
        jsons = response.json()
        data = jsons["data"]
        list_data = data["data"]
        new_dict = [
            {
                "id": item["id"],
                "grade": item["grade"],
                "name": item["nameZh"],
                "department": item["department"]["nameZh"],
                "education": item["education"]["nameZh"],
                "major": item["major"]["nameZh"],
            }
            for item in list_data
        ]
        with open("list.json", "w", encoding="utf-8") as f:
            json.dump(new_dict, f, ensure_ascii=False, indent=4)
        print("数据已保存到 list.json")
    else:
        print(f"请求失败，状态码：{response.status_code}，错误信息：{response.text}")


def read_lists():
    try:
        # 打开并读取 JSON 文件
        with open("list.json", "r", encoding="utf-8") as f:
            program_data = json.load(f)
        return program_data  # 返回读取到的数据
    except FileNotFoundError:
        print("错误：文件 list.json 不存在！")
        return None
    except json.JSONDecodeError:
        print("错误：文件 list.json 格式错误！")
        return None


def download_programs():
    lists = read_lists()
    if lists is not None:
        for item in lists:
            # 遍历，id填充进去
            program_id = item["id"]
            url = api.host + api.api["PROGRAM"].format(program_id)
            response = requests.get(url=url, headers=api.base_header)
            if response.status_code == 200:
                # 响应为JSON，保存，文件名以lists的grade + department + major + education + 培养方案.json 命名
                program_details = response.json()

                # 构造文件名
                grade = item.get("grade", "未知年级")
                department = item["department"]
                major = item["major"]
                education = item["education"]
                filename = f"{program_id}.json"

                # 创建保存目录（可选）
                os.makedirs("programs", exist_ok=True)
                filepath = os.path.join("programs", filename)

                # 保存文件
                with open(filepath, "w", encoding="utf-8") as f:
                    json.dump(program_details, f, ensure_ascii=False, indent=4)
                print(f"已保存：{filepath}")
            else:
                print(f"请求失败：ID={program_id}, 状态码={response.status_code}, 错误信息={response.text}")
    else:
        print("未能加载列表数据，无法下载培养方案。")
