#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'EOF'
统计 Git 提交中的新增/删除行数。

用法：
  git_line_stats.sh                    # 今天，本机 Git 用户
  git_line_stats.sh --today            # 今天
  git_line_stats.sh --date 2026-11-01  # 指定日期
  git_line_stats.sh --days 30           # 最近 30 天，按天统计
  git_line_stats.sh --all               # 全部历史，按天统计
  git_line_stats.sh --uncommitted       # 当前未提交改动

选项：
  --author VALUE       按作者筛选；默认使用 git config user.email
  --all-authors        不筛选作者
  --repo PATH          仓库路径；默认当前目录
  -h, --help           显示帮助

说明：
  改动量 = 新增 + 删除；净增 = 新增 - 删除。
  二进制文件不会计入行数。
EOF
}

mode="today"
days=30
date_value=""
repo="."
author=""
all_authors=false

while (($# > 0)); do
  case "$1" in
    --today)
      mode="today"
      shift
      ;;
    --date)
      mode="date"
      date_value="${2:?--date 需要 YYYY-MM-DD}"
      shift 2
      ;;
    --days)
      mode="days"
      days="${2:?--days 需要正整数}"
      shift 2
      ;;
    --all)
      mode="all"
      shift
      ;;
    --uncommitted)
      mode="uncommitted"
      shift
      ;;
    --author)
      author="${2:?--author 需要作者名称或邮箱}"
      shift 2
      ;;
    --all-authors)
      all_authors=true
      shift
      ;;
    --repo)
      repo="${2:?--repo 需要仓库路径}"
      shift 2
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "未知参数：$1" >&2
      usage >&2
      exit 2
      ;;
  esac
done

git -C "$repo" rev-parse --is-inside-work-tree >/dev/null 2>&1 || {
  echo "不是 Git 仓库：$repo" >&2
  exit 1
}

if [[ "$days" != *[!0-9]* ]] && ((days > 0)); then
  :
else
  echo "--days 必须是正整数" >&2
  exit 2
fi

if [[ "$mode" != "uncommitted" ]] && ! $all_authors && [[ -z "$author" ]]; then
  author="$(git -C "$repo" config user.email || true)"
  if [[ -z "$author" ]]; then
    author="$(git -C "$repo" config user.name || true)"
  fi
  if [[ -z "$author" ]]; then
    echo "未配置 Git 用户，请传 --author 或 --all-authors" >&2
    exit 1
  fi
fi

print_total() {
  awk '
    $1 ~ /^[0-9]+$/ && $2 ~ /^[0-9]+$/ {
      added += $1
      deleted += $2
    }
    END {
      printf "新增: %d 行\n删除: %d 行\n改动: %d 行\n净增: %+d 行\n",
        added, deleted, added + deleted, added - deleted
    }
  '
}

print_daily() {
  awk '
    /^@@@/ {
      day = substr($0, 4)
      seen[day] = 1
      next
    }
    $1 ~ /^[0-9]+$/ && $2 ~ /^[0-9]+$/ {
      added[day] += $1
      deleted[day] += $2
    }
    END {
      printf "%-12s %10s %10s %10s %10s\n", "日期", "新增", "删除", "改动", "净增"
      for (day in seen) {
        printf "%-12s %10d %10d %10d %+10d\n",
          day, added[day], deleted[day],
          added[day] + deleted[day], added[day] - deleted[day]
      }
    }
  ' | sort -r
}

author_args=()
if ! $all_authors; then
  author_args=(--author="$author")
fi

case "$mode" in
  uncommitted)
    echo "未提交改动："
    git -C "$repo" diff HEAD --numstat | print_total
    ;;

  today)
    echo "今天的已提交改动${author:+（$author）}："
    git -C "$repo" log \
      --since="midnight" \
      --no-merges \
      "${author_args[@]}" \
      --numstat --format=tformat: | print_total
    ;;

  date)
    if [[ ! "$date_value" =~ ^[0-9]{4}-[0-9]{2}-[0-9]{2}$ ]]; then
      echo "日期格式必须为 YYYY-MM-DD" >&2
      exit 2
    fi
    echo "$date_value 的已提交改动${author:+（$author）}："
    git -C "$repo" log \
      --since="$date_value 00:00:00" \
      --until="$date_value 23:59:59" \
      --no-merges \
      "${author_args[@]}" \
      --numstat --format=tformat: | print_total
    ;;

  days)
    echo "最近 $days 天的已提交改动${author:+（$author）}："
    git -C "$repo" log \
      --since="$days days ago" \
      --no-merges \
      "${author_args[@]}" \
      --date=short \
      --numstat --format='@@@%ad' | print_daily
    ;;

  all)
    echo "全部历史已提交改动${author:+（$author）}："
    git -C "$repo" log \
      --no-merges \
      "${author_args[@]}" \
      --date=short \
      --numstat --format='@@@%ad' | print_daily
    ;;
esac
