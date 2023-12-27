package com.hfut.schedule.logic.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.hfut.schedule.MyApplication

class Database (context: Context, name:String, version:Int) : SQLiteOpenHelper(context,name,null,version) {
    val Book = "create table Book ("+
            " id integer primary key autoincrement," +
            "title text," +
            "info text," +
            "type integer," +
            "remark text )"
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(Book)
        Toast.makeText(MyApplication.context,"创建数据库成功", Toast.LENGTH_SHORT).show()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}
}