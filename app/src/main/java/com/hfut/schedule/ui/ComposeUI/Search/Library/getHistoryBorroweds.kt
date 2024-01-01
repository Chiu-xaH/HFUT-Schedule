package com.hfut.schedule.ui.ComposeUI.Search.Library

import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.datamodel.Community.BorrowRecords
import com.hfut.schedule.logic.datamodel.Community.BorrowResponse
import com.hfut.schedule.logic.utils.SharePrefs.prefs


val HISTORY = "History"
val BORROWED = "Borrowed"

fun getBorrow(PrefsJson : String) : MutableList<BorrowRecords> {
    val json = prefs.getString(PrefsJson,MyApplication.NullBorrow)
    val records = Gson().fromJson(json,BorrowResponse::class.java).result.records
    var Items = mutableListOf<BorrowRecords>()
    for (i in 0 until records.size) {
        val name = records[i].bookName
        val author = records[i].author
        val num = records[i].callNumber
        val ReturnTime = records[i].returnTime
        val OutTime = records[i].outTime
        Items.add(BorrowRecords(name,author,OutTime,ReturnTime,num))
    }
    return Items
}