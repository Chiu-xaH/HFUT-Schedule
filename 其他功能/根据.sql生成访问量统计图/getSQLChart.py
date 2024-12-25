import sqlite3
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

# 读取 .sql 文件内容
sql_file = "UserData.sql"
with open(sql_file, 'r', encoding='utf-8') as file:
    sql_script = file.read()

# 创建内存数据库并执行 SQL 脚本
conn = sqlite3.connect(":memory:")
cursor = conn.cursor()
cursor.executescript(sql_script)

# 从数据库中读取数据
query = "SELECT * FROM UserData"  # 替换为你的表名
df = pd.read_sql_query(query, conn)

# 数据预处理：提取日期
df['dateTime'] = pd.to_datetime(df['dateTime'])
df['date'] = df['dateTime'].dt.date

# 按日期统计每天的总访问量
daily_visits = df.groupby('date').size().reset_index(name='total_visits')

# 绘制图表
plt.figure(figsize=(12, 6))
sns.lineplot(data=daily_visits, x='date', y='total_visits', marker='o')
plt.title("Visitors (Server During 3-Month)")
plt.xlabel("")
plt.ylabel("")
# plt.xticks(rotation=0)
plt.grid()
plt.tight_layout()

# 保存或展示图表
plt.savefig("visitsChart.png")
# plt.show()

# 关闭数据库连接
conn.close()
