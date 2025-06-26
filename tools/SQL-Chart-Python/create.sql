CREATE TABLE "user_app_usage" (
  "date_time" TIMESTAMP NOT NULL DEFAULT (datetime('now')),
  "user_name" VARCHAR(20) NOT NULL,
  "device_name" VARCHAR(100) NOT NULL,
  "app_version_name" VARCHAR(10) NOT NULL,
  "system_version" INTEGER NOT NULL,
  "student_id" VARCHAR(15),
  "campus" varchar(15),
  "department" varchar(50),
  "app_version_code" INTEGER 
);