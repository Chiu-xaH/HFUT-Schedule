package com.hfut.schedule.ui.screen.home.search.function.library

import com.google.gson.Gson
import com.hfut.schedule.logic.model.community.LibRecord
import com.hfut.schedule.logic.model.community.LibraryResponse
import com.hfut.schedule.viewmodel.network.NetWorkViewModel

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