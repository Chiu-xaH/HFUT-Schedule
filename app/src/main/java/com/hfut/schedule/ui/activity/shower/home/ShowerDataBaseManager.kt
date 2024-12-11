package com.hfut.schedule.ui.activity.shower.home

import android.annotation.SuppressLint
import android.content.ContentValues
import com.hfut.schedule.logic.dao.dataBaseShower

object ShowerDataBaseManager {
    //添加项目
    val dbwritableDatabase =  dataBaseShower.writableDatabase
    fun addItems(name : String, macLocation : String) {
        dataBaseShower.writableDatabase
        val values1 = ContentValues().apply {
            put("mac",macLocation)
            put("name",name)
        }
        dbwritableDatabase.insert("Shower", null, values1)
    }
    //删除项目
    fun removeItems(id : Int) {
        dbwritableDatabase.delete("Shower","id = ?", arrayOf(id.toString()))
    }
    //编辑项目
    fun EditItems(id : Int,title : String, info : String, remark : String) {
        val values1 = ContentValues().apply {

        }
        dbwritableDatabase.update("Shower",values1,"id = ?", arrayOf(id.toString()))
    }

    @SuppressLint("Range")
    fun queryAll() : MutableList<ShowerDBItem> {
        val l = mutableListOf<ShowerDBItem>()
        val dbwritableDatabase =  dataBaseShower.writableDatabase
        val cursor = dbwritableDatabase.query("Shower",null,null,null,null,null,null)
        if(cursor.moveToFirst()){
            do{
                val mac = cursor.getString(cursor.getColumnIndex("mac"))
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val ids = cursor.getInt(cursor.getColumnIndex("id"))
                l.add(ShowerDBItem(ids,name,mac))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return l
    }
}

data class ShowerDBItem(val id : Int,val name : String,val mac : String)
