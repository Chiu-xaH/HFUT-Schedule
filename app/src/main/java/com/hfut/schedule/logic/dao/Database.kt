package com.hfut.schedule.logic.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.hfut.schedule.App.MyApplication

class Database (context: Context, name:String, version:Int,code : String) : SQLiteOpenHelper(context,name,null,version) {
    val Book = code
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(Book)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}
}