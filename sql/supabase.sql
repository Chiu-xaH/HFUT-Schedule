CREATE DATABASE hfut_schedule
  ENCODING 'UTF8'
  LC_COLLATE='en_US.UTF-8'
  LC_CTYPE='en_US.UTF-8'
  TEMPLATE=template0;


-- \c hfut_schedule

CREATE TABLE event (
    id SERIAL PRIMARY KEY,                                 -- 主键，自增
    name VARCHAR(100) NOT NULL,                            -- 事件名称
    description TEXT DEFAULT NULL,                         -- 事件备注
    time_description VARCHAR(150),                         -- 时间描述，如“第3周 星期二 第4节”
    start_time TIMESTAMP,                                  -- 事件起始时间
    end_time TIMESTAMP,                                    -- 事件结束时间
    type VARCHAR(50) NOT NULL,                             --类型
    contributor_email VARCHAR(27),                            -- 贡献者
    campus VARCHAR(20) DEFAULT 'DEFAULT',
    created_time TIMESTAMP DEFAULT NOW(),
    contributor_class VARCHAR(30),                         -- 贡献者班级
    applicable_classes TEXT,                               -- 适用班级（英文逗号分隔字符串）
    url VARCHAR(500) DEFAULT NULL                          -- 可空 URL
);

CREATE TABLE web (
    id SERIAL PRIMARY KEY,                                 -- 主键，自增
    name VARCHAR(100) NOT NULL,                            -- 名称
    contributor_email VARCHAR(27),                            -- 贡献者
    created_time TIMESTAMP DEFAULT NOW(),
    url VARCHAR(500) NOT NULL                          -- URL
);

CREATE TABLE web_fork (
    id SERIAL PRIMARY KEY,                          -- 主键，自增
    web_id INT NOT NULL,                          -- 被点赞的事件ID
    user_email VARCHAR(27) NOT NULL,                     -- 点赞的用户（邮箱或学号）
    created_time TIMESTAMP DEFAULT NOW(),           -- 点赞时间

    CONSTRAINT fk_web FOREIGN KEY (web_id) REFERENCES web(id) ON DELETE CASCADE,
    CONSTRAINT unique_web UNIQUE (web_id, user_email)  -- 每个用户只能对同一个事件点赞一次
);


CREATE TABLE event_fork (
    id SERIAL PRIMARY KEY,                          -- 主键，自增
    event_id INT NOT NULL,                          -- 被点赞的事件ID
    user_email VARCHAR(27) NOT NULL,                     -- 点赞的用户（邮箱或学号）
    created_time TIMESTAMP DEFAULT NOW(),           -- 点赞时间

    CONSTRAINT fk_event FOREIGN KEY (event_id) REFERENCES event(id) ON DELETE CASCADE,
    CONSTRAINT unique_like UNIQUE (event_id, user_email)  -- 每个用户只能对同一个事件点赞一次
);

create or replace function get_event_fork_count(target_event_id INT)
returns int
language sql
as $$
  select count(*) from event_fork where event_id = target_event_id;
$$;


create or replace function get_event_count(
    class_text text,
    campus_text text,
    end_time_param timestamp
)
returns integer
language sql
as $$
    select count(*) from event
    where (
        applicable_classes = ''
        or applicable_classes ilike '%' || class_text || '%'
    )
    and (
        campus = 'DEFAULT'
        or campus = campus_text
    )
    and end_time >= end_time_param
$$;


create or replace function get_latest_event_end_time(
    class_text text,
    campus_text text
)
returns timestamp as $$
begin
    return (
        select max(created_time)
        from event
        where (
            applicable_classes = ''
            or applicable_classes ilike '%' || class_text || '%'
        )
        and (
            campus = 'DEFAULT'
            or campus = campus_text
        )
    );
end;
$$ language plpgsql;


drop function if exists get_user_count;

create function get_user_count()
returns bigint
security definer --
as $$
  select count(distinct (user_name, student_id)) from public.user_app_usage;
$$ language sql stable;


drop function if exists get_today_visit_count;

CREATE FUNCTION get_today_visit_count()
RETURNS bigint
SECURITY DEFINER
AS $$
 SELECT COUNT(*)
FROM user_app_usage
WHERE (date_time AT TIME ZONE 'UTC' AT TIME ZONE 'Asia/Shanghai') >= date_trunc('day', CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Shanghai')
  AND (date_time AT TIME ZONE 'UTC' AT TIME ZONE 'Asia/Shanghai') <  date_trunc('day', CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Shanghai') + INTERVAL '1 day';

$$ LANGUAGE sql STABLE;


CREATE TABLE "user_app_usage" (
  "date_time" TIMESTAMP NOT NULL DEFAULT NOW(),
  "user_name" VARCHAR(20) NOT NULL,
  "device_name" VARCHAR(100) NOT NULL,
  "app_version_name" VARCHAR(10) NOT NULL,
  "system_version" INTEGER NOT NULL,
  "student_id" VARCHAR(15),
  "campus" varchar(15),
  "department" varchar(50),
  "app_version_code" INTEGER NOT NULL
);



