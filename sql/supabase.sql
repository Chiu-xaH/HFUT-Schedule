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
