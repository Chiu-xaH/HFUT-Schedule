import sqlite3
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import matplotlib
import pytz
from datetime import datetime


COLUMNS = [
    "date_time",
    "user_name",
    "device_name",
    "app_version_name",
    "system_version",
    "student_id",
    "campus",
    "department",
    "app_version_code",
]


def parse_datetime_flexible(s):
    try:
        return datetime.strptime(s, "%Y-%m-%d %H:%M:%S.%f")
    except ValueError:
        return datetime.strptime(s, "%Y-%m-%d %H:%M:%S")


def merge_small_categories(series, threshold=0.01, show_subitems=True):
    total = series.sum()
    small_items = [idx for idx, val in series.items() if val / total < threshold]

    def group_func(x):
        return x if x not in small_items else "其他"

    grouped = series.groupby(group_func).sum()

    if "其他" in grouped.index and small_items:
        if show_subitems:
            other_items = [
                str(x)[:2] if "学院" in str(x) else str(x)
                for x in small_items
            ]
            other_label = f"其他({','.join(other_items)})" if other_items else "其他"
        else:
            other_label = "其他"

        grouped = grouped.rename(index={"其他": other_label})

    return grouped


def load_user_app_usage(file_path):
    rows = []

    with open(file_path, "r", encoding="utf-8") as file:
        for line in file:
            line = line.rstrip("\r\n")

            if not line.strip():
                continue

            # 如果文件中混入 COPY / \.，跳过
            if line.strip().upper().startswith("COPY "):
                continue

            if line.strip() == r"\.":
                continue

            # 处理 PostgreSQL NULL
            parts = line.split("\t")

            # 兼容你当前文件末尾可能存在的 "\"
            if len(parts) > 9 and parts[-1].strip() == "\\":
                parts = parts[:-1]

            if len(parts) != 9:
                print(f"警告：跳过异常数据，字段数={len(parts)}：{line}")
                continue

            parts = [None if x == r"\N" else x for x in parts]

            rows.append(parts)

    if not rows:
        raise ValueError("没有读取到任何 user_app_usage 数据")

    df = pd.DataFrame(rows, columns=COLUMNS)

    # 保持原 SQLite 表的类型语义
    df["system_version"] = pd.to_numeric(
        df["system_version"],
        errors="coerce",
    ).astype("Int64")

    df["app_version_code"] = pd.to_numeric(
        df["app_version_code"],
        errors="coerce",
    ).astype("Int64")

    # student_id 必须保持字符串，避免前导 0 被转换
    df["student_id"] = df["student_id"].astype("string")

    return df


def get_last_time():
    query = "SELECT * FROM user_app_usage"
    df = pd.read_sql_query(query, conn)

    # 数据预处理：提取日期
    df["date_time"] = df["date_time"].apply(parse_datetime_flexible)
    df["date"] = df["date_time"].dt.date

    # 计算数据库中最新的时间并转为东八区
    utc_latest = df["date_time"].max().replace(tzinfo=pytz.utc)
    cst_latest = utc_latest.astimezone(
        pytz.timezone("Asia/Shanghai")
    )

    return f"截止：{cst_latest.strftime('%Y-%m-%d %H:%M:%S')}"


def build_user_visit_chart():
    # 从数据库中读取数据
    query = "SELECT * FROM user_app_usage"
    df = pd.read_sql_query(query, conn)

    # 数据预处理：提取日期
    df["date_time"] = df["date_time"].apply(parse_datetime_flexible)
    df["date"] = df["date_time"].dt.date

    # 按日期统计每天的总访问量
    daily_visits = (
        df.groupby("date")
        .size()
        .reset_index(name="total_visits")
    )

    # 绘制折线图（美化）
    plt.figure(figsize=(12, 6))

    sns.lineplot(
        data=daily_visits,
        x="date",
        y="total_visits",
        linewidth=2.0,
        color="steelblue",
    )

    plt.title(
        "Visitors",
        fontsize=14,
        fontweight="bold",
    )

    plt.xlabel("")
    plt.ylabel("访问量", fontsize=12)

    # 美化坐标轴
    plt.xticks(
        rotation=30,
        fontsize=10,
    )

    plt.yticks(fontsize=10)

    plt.grid(
        alpha=0.3,
        linestyle="--",
    )

    plt.tight_layout()
    plt.savefig(
        "visits.png",
        dpi=300,
    )

    plt.close()


def build_user_pie_chart():
    query = """
SELECT *
FROM (
    SELECT u.*,
           ROW_NUMBER() OVER (
               PARTITION BY user_name, student_id
           ) AS rn
    FROM user_app_usage u
) t
WHERE rn = 1;
"""

    df = pd.read_sql_query(
        query,
        conn,
    )

    # 饼图
    # 确保 student_id 是字符串
    df["student_id"] = df["student_id"].astype(str)

    # 提取前四位
    df["student_prefix"] = df["student_id"].str[:4]

    # 2*2布局
    fig, axes = plt.subplots(
        2,
        2,
        figsize=(18, 12),
    )

    # campus 饼图
    df["campus"].value_counts().plot.pie(
        ax=axes[0][0],
        autopct="%1.1f%%",
        startangle=140,
        title="校区",
    )

    # department 饼图
    merge_small_categories(
        df["department"].value_counts(),
        0.005,
    ).plot.pie(
        ax=axes[0][1],
        autopct="%1.1f%%",
        startangle=140,
        title="学院",
    )

    # student_id 前缀饼图
    merge_small_categories(
        df["student_prefix"].value_counts(),
        0.01,
    ).plot.pie(
        ax=axes[1][0],
        autopct="%1.1f%%",
        startangle=140,
        title="学号年份",
    )

    # system_version 饼图
    merge_small_categories(
        df["system_version"].value_counts(),
        0.015,
    ).plot.pie(
        ax=axes[1][1],
        autopct="%1.1f%%",
        startangle=140,
        title="Android API 版本",
    )

    for row in axes:
        for ax in row:
            ax.set_ylabel("")

    plt.tight_layout()

    fig.text(
        0.5,
        -0.05,
        get_last_time(),
        ha="center",
        fontsize=10,
    )

    plt.savefig(
        "pie_charts.png",
        bbox_inches="tight",
        dpi=300,
    )

    plt.close()


def build_app_version_chart():
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

    df_latest = pd.read_sql_query(
        query,
        conn,
    )

    # 按版本名称统计
    version_counts = df_latest["app_version_name"].value_counts()

    # 合并小类
    version_counts = merge_small_categories(
        version_counts,
        threshold=0.0025,
        show_subitems=False,
    )

    # 放大图像
    plt.figure(figsize=(14, 14))

    version_counts.plot.pie(
        labels=version_counts.index,
        autopct="%1.1f%%",
        startangle=140,
    )

    plt.ylabel("")

    # 设置标题
    plt.title(
        "应用版本分布",
        fontsize=20,
        y=1.05,
    )

    # 在标题下方放置时间信息
    plt.figtext(
        0.5,
        0.02,
        get_last_time(),
        ha="center",
        fontsize=10,
    )

    plt.tight_layout()

    plt.savefig(
        "app_version.png",
        bbox_inches="tight",
        dpi=300,
    )

    plt.close()


if __name__ == "__main__":
    # 设置全局字体为支持中文的字体
    matplotlib.rcParams["font.sans-serif"] = ["SimHei"]
    matplotlib.rcParams["axes.unicode_minus"] = False

    # Supabase 导出的数据文件
    sql_file = "user_app_usage_data.sql"

    # 读取 Supabase 数据
    df = load_user_app_usage(sql_file)

    print(f"读取到数据：{len(df)} 条")
    print(f"最早时间：{df['date_time'].min()}")
    print(f"最新时间：{df['date_time'].max()}")

    # 创建内存 SQLite
    conn = sqlite3.connect(":memory:")

    # 写入 SQLite
    df.to_sql(
        "user_app_usage",
        conn,
        index=False,
        if_exists="replace",
    )

    # 后面的图表逻辑全部保持原来的实现
    build_user_visit_chart()
    build_user_pie_chart()
    build_app_version_chart()

    # 关闭数据库
    conn.close()

    print("图表生成完成")