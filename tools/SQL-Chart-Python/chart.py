import sqlite3
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import matplotlib
import pytz
from datetime import datetime, timedelta

def parse_datetime_flexible(s):
    try:
        return datetime.strptime(s, "%Y-%m-%d %H:%M:%S.%f")
    except ValueError:
        return datetime.strptime(s, "%Y-%m-%d %H:%M:%S")

def merge_small_categories(series, threshold=0.005):
    total = series.sum()
    small_items = [idx for idx, val in series.items() if val / total < threshold]

    def group_func(x):
        return x if x not in small_items else '其他'

    grouped = series.groupby(group_func).sum()

    if '其他' in grouped.index and small_items:
        # 省略学院
        other_items = [
            str(x)[:2] if "学院" in str(x) else str(x)
            for x in small_items
            ]
        other_label = f"其他({','.join(other_items)})" if other_items else "其他"

        grouped = grouped.rename(index={'其他': other_label})

    return grouped

def build_usage_query(start: str = None, end: str = None) -> str:
    base_query = "SELECT DISTINCT * FROM user_app_usage"
    conditions = []

    tz_cst = pytz.timezone("Asia/Shanghai")

    if start:
        # 转为东八区时间，再转 UTC 时间
        dt_start = tz_cst.localize(datetime.strptime(start, "%Y-%m-%d"))
        utc_start = dt_start.astimezone(pytz.utc)
        conditions.append(f"date_time >= '{utc_start.strftime('%Y-%m-%d %H:%M:%S')}'")

    if end:
        # 当天23:59:59 CST，转为 UTC
        dt_end = tz_cst.localize(datetime.strptime(end, "%Y-%m-%d") + timedelta(hours=23, minutes=59, seconds=59))
        utc_end = dt_end.astimezone(pytz.utc)
        conditions.append(f"date_time <= '{utc_end.strftime('%Y-%m-%d %H:%M:%S')}'")

    if conditions:
        base_query += " WHERE " + " AND ".join(conditions)

    return base_query


if(__name__ == "__main__"):
    # 设置全局字体为支持中文的字体（根据系统设置不同）
    matplotlib.rcParams['font.sans-serif'] = ['SimHei']  # 黑体，适用于 Windows
    matplotlib.rcParams['axes.unicode_minus'] = False    # 负号正常显示
    c_sql_file = "create.sql"
    with open(c_sql_file, 'r', encoding='utf-8') as file:
        create_sql_script = file.read()
    # 读取 .sql 文件内容
    sql_file = "user_app_usage_rows.sql"
    with open(sql_file, 'r', encoding='utf-8') as file:
        sql_script = file.read()

    # 创建内存数据库并执行 SQL 脚本
    conn = sqlite3.connect(":memory:")
    cursor = conn.cursor()
    cursor.executescript(create_sql_script)
    cursor.executescript(sql_script)


    # 从数据库中读取数据
    query = "SELECT * FROM user_app_usage"  # 替换为你的表名
    df = pd.read_sql_query(query, conn)

    # 数据预处理：提取日期
    df['date_time'] = df['date_time'].apply(parse_datetime_flexible)
    # df['date_time'] = pd.to_datetime(df['date_time'])
    df['date'] = df['date_time'].dt.date

    # 计算数据库中最新的时间并转为东八区
    utc_latest = df['date_time'].max().replace(tzinfo=pytz.utc)
    cst_latest = utc_latest.astimezone(pytz.timezone('Asia/Shanghai'))
    latest_time_str = f"截止：{cst_latest.strftime('%Y-%m-%d %H:%M:%S')}"


    # 按日期统计每天的总访问量
    daily_visits = df.groupby('date').size().reset_index(name='total_visits')

    # 绘制图表
    plt.figure(figsize=(12, 6))
    
    sns.lineplot(data=daily_visits, x='date', y='total_visits', marker='o')
    plt.title("Visitors")
    plt.xlabel("")
    plt.ylabel("")
    # plt.xticks(rotation=0)
    plt.grid()
    plt.tight_layout()

    # 保存或展示图表
    plt.savefig("visits.png",dpi=300)
    # plt.show()
    # 从数据库中读取数据
    query = build_usage_query()  # 饼图数据源
    df = pd.read_sql_query(query, conn)
    # 饼图
    # 确保 student_id 是字符串
    df['student_id'] = df['student_id'].astype(str)

    # 提取前两位作为类别
    df['student_prefix'] = df['student_id'].str[:4]

    # 2*2布局
    fig, axes = plt.subplots(2, 2, figsize=(18, 6))

    # campus 饼图
    df['campus'].value_counts().plot.pie(
        ax=axes[0][0],
        autopct='%1.1f%%',
        startangle=140,
        title='校区'
    )

    # department 饼图
    merge_small_categories(df['department'].value_counts()).plot.pie(
        ax=axes[0][1],
        autopct='%1.1f%%',
        startangle=140,
        title='学院'
    )

    # student_id 前缀 饼图
    df['student_prefix'].value_counts().plot.pie(
        ax=axes[1][0],
        autopct='%1.1f%%',
        startangle=140,
        title='学号年份'
    )

    # student_id 前缀 饼图
    merge_small_categories(df['system_version'].value_counts()).plot.pie(
        ax=axes[1][1],
        autopct='%1.1f%%',
        startangle=140,
        title='Android API 版本'
    )
    

    for row in axes:
        for ax in row:
            ax.set_ylabel("")

    plt.tight_layout()
    fig.text(0.5, -0.05, latest_time_str, ha='center', fontsize=10)
    plt.savefig("pie_charts.png", bbox_inches="tight",dpi=300)


    # 从数据库中获取最新版本数据
    query = """
WITH usage_with_max_date AS (
    SELECT
        student_id,
        user_name,
        app_version_code,
        app_version_name,
        MAX(date_time) AS max_date_time_utc
    FROM user_app_usage
    WHERE app_version_name IS NOT NULL
    GROUP BY student_id, app_version_code
),
latest_version_per_user AS (
    SELECT u1.*
    FROM usage_with_max_date u1
    WHERE NOT EXISTS (
        SELECT 1
        FROM usage_with_max_date u2
        WHERE u2.student_id = u1.student_id
        AND u2.app_version_code > u1.app_version_code
    )
)
SELECT *
FROM latest_version_per_user
ORDER BY app_version_code DESC, student_id;
"""


    df_latest = pd.read_sql_query(query, conn)

    # 按版本名称统计
    version_counts = df_latest['app_version_name'].value_counts()

    # 合并小类（可选）
    version_counts = merge_small_categories(version_counts, threshold=0.005)

    # 放大图像
    plt.figure(figsize=(14, 14))

    version_counts.plot.pie(
        labels=version_counts.index,  # 显示版本名称
        autopct='%1.1f%%',
        startangle=140
    )

    plt.ylabel("")
    plt.title('应用版本分布', fontsize=20)  # 标题大一点
    plt.tight_layout()
    plt.savefig("app_version.png", bbox_inches="tight", dpi=300)  # 提高分辨率)
    # 关闭数据库连接
    conn.close()
