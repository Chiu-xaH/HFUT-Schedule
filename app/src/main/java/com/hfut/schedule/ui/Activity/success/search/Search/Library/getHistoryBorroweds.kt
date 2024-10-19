package com.hfut.schedule.ui.Activity.success.search.Search.Library

import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.datamodel.Community.BorrowRecords
import com.hfut.schedule.logic.datamodel.Community.BorrowResponse
import com.hfut.schedule.logic.utils.SharePrefs.prefs

fun getBorrow(PrefsJson : String) : MutableList<BorrowRecords> {
    var items = mutableListOf<BorrowRecords>()
    try {
        val json = prefs.getString(PrefsJson,null)
        val records = Gson().fromJson(json,BorrowResponse::class.java).result.records

        for (i in 0 until records.size) {
            val name = records[i].bookName
            val author = records[i].author
            val num = records[i].callNumber
            val ReturnTime = records[i].returnTime
            val OutTime = records[i].outTime
            items.add(BorrowRecords(name,author,OutTime,ReturnTime,num))
        }
        return items
    } catch (_:Exception) {
        return items
    }

}