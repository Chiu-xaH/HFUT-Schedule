package com.hfut.schedule.ui.activity.home.search.functions.library

import com.google.gson.Gson
import com.hfut.schedule.logic.beans.community.BorrowRecords
import com.hfut.schedule.logic.beans.community.BorrowResponse
import com.hfut.schedule.logic.utils.SharePrefs.prefs

fun getBorrow(PrefsJson : String) : List<BorrowRecords> {
    try {
        val json = prefs.getString(PrefsJson,null)
        return Gson().fromJson(json,BorrowResponse::class.java).result.records
    } catch (_:Exception) {
        return emptyList()
    }
}