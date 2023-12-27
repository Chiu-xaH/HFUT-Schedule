package com.hfut.schedule.ui.ComposeUI.Focus

import android.content.ContentValues
import com.hfut.schedule.logic.dao.SCHEDULE
import com.hfut.schedule.logic.dao.dataBase
//添加项目
fun AddItems(title : String, info : String, remark : String) {
    val dbwritableDatabase =  dataBase.writableDatabase
    dataBase.writableDatabase
    val values1 = ContentValues().apply {
        put("title", title)
        put("info", info)
        put("remark",remark)
        put("type", SCHEDULE)
    }
    dbwritableDatabase.insert("Book", null, values1)
}

//删除项目
fun RemoveItems(id : Int) {
    val dbwritableDatabase =  dataBase.writableDatabase
    dbwritableDatabase.delete("Book","id = ?", arrayOf(id.toString()))
}

//编辑项目
fun EditItems(id : Int,title : String, info : String, remark : String) {
    val dbwritableDatabase =  dataBase.writableDatabase
    val values1 = ContentValues().apply {
        put("title", title)
        put("info", info)
        put("remark",remark)
        put("type", SCHEDULE)
    }
    dbwritableDatabase.update("Book",values1,"id = ?", arrayOf(id.toString()))
}

