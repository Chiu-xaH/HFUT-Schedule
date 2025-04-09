package com.hfut.schedule.ui.activity.home.search.functions.library

import com.google.gson.Gson
import com.hfut.schedule.logic.beans.community.BookPositionBean
import com.hfut.schedule.logic.beans.community.BookPositionResponse
import com.hfut.schedule.logic.beans.community.BorrowRecords
import com.hfut.schedule.logic.beans.community.BorrowResponse
import com.hfut.schedule.logic.beans.community.LibRecord
import com.hfut.schedule.logic.beans.community.LibraryResponse
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.viewmodel.NetWorkViewModel

fun getBorrow(vm: NetWorkViewModel) : List<BorrowRecords> {
    return try {
        Gson().fromJson(vm.booksChipData.value,BorrowResponse::class.java).result.records
    } catch (_:Exception) {
        emptyList()
    }
}

fun getBooks(vm : NetWorkViewModel) : List<LibRecord> {
    val library = vm.libraryData.value
    try {
        if(library != null && library.contains("操作成功")){
            val data = Gson().fromJson(library, LibraryResponse::class.java)
            return data.result.records
        } else {
            return emptyList()
        }
    } catch (e:Exception) {
        return emptyList()
    }
}

fun getBookDetail(vm: NetWorkViewModel) : List<BookPositionBean> {
    val json = vm.bookPositionData.value
    return try {
        Gson().fromJson(json,BookPositionResponse::class.java).result
    } catch (e : Exception) {
        emptyList()
    }
}