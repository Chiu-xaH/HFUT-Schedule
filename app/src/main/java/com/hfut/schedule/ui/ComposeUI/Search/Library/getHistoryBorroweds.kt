package com.hfut.schedule.ui.ComposeUI.Search.Library

import com.google.gson.Gson
import com.hfut.schedule.logic.datamodel.Community.BorrowHistoryRecords
import com.hfut.schedule.logic.datamodel.Community.BorrowHistoryResponse
import com.hfut.schedule.logic.utils.SharePrefs.prefs

fun getHistory() : MutableList<BorrowHistoryRecords> {
    val json = prefs.getString("History","")
    val records = Gson().fromJson(json,BorrowHistoryResponse::class.java).result.records
    var Items = mutableListOf<BorrowHistoryRecords>()
    for (i in 0 until records.size) {
        val name = records[i].bookName
        val author = records[i].author
        val num = records[i].callNumber
        val ReturnTime = records[i].returnTime
        val OutTime = records[i].outTime
        Items.add(BorrowHistoryRecords(name,author,OutTime,ReturnTime,num))
    }
    return Items
}

fun getBorrowed() : MutableList<BorrowHistoryRecords> {

    var Items = mutableListOf<BorrowHistoryRecords>()

    return Items
}