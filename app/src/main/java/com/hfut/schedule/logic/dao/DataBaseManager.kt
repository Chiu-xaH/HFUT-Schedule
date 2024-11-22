package com.hfut.schedule.logic.dao

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.hfut.schedule.App.MyApplication

open class DataBaseManager(private val dbName: String) {

    private var createTableQuery =
        "create table $dbName ("+
                " id integer primary key autoincrement)"

    val db = Database(MyApplication.context,"${dbName}.db",1, createTableQuery)

    val dbwritableDatabase: SQLiteDatabase =  db.writableDatabase

    fun setCreateQueryTable(query : String) {
        createTableQuery = query
    }
    open fun add(value : ContentValues) {
        db.writableDatabase
        dbwritableDatabase.insert(dbName, null,  value)
    }
    //删除项目
    open fun remove(id: Int) {
        dbwritableDatabase.delete(dbName,"id = ?", arrayOf(id.toString()))
    }

    @SuppressLint("Range")
    open fun query(): Cursor? {
        return dbwritableDatabase.query(dbName, null, null, null, null, null, null)
    }
}
