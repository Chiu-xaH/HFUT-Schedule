package com.hfut.schedule.logic.dao

import com.hfut.schedule.App.MyApplication

val  WANGKE = 1
val  SCHEDULE = 2
val  COURSE = 3
val  TEST = 4
val createBook =
        "create table Book ("+
        " id integer primary key autoincrement," +
        "title text," +
        "info text," +
        "type integer," +
        "remark text )"
val createSchedule =
    "create table Schedule ("+
            " id integer primary key autoincrement," +
            "title text)"
val dataBase = Database(MyApplication.context,"Focus.db",1, createBook)
val dataBaseSchedule = Database(MyApplication.context,"Schedule.db",1, createSchedule)