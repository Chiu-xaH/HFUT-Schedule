package com.hfut.schedule.network.api.repo

import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.network.api.model.response.json.library.BorrowedStatus
import com.hfut.schedule.network.api.model.response.json.library.LibraryBorrowRecord
import com.hfut.schedule.network.api.model.response.json.library.LibrarySearchRow
import com.hfut.schedule.network.api.model.response.json.library.LibraryStatusDto
import com.xah.common.logic.state.UiStateHolder

interface LibraryRepositoryInf {
    suspend fun checkLibraryNetwork(): Int
    suspend fun checkLibraryLogin(token : String,holder : UiStateHolder<Boolean>)
    suspend fun getStatus(token : String,holder : UiStateHolder<LibraryStatusDto>)
    suspend fun getBorrowed(token : String, page : Int, status: BorrowedStatus? = null, pageSize : Int = Constant.DEFAULT_PAGE_SIZE, holder : UiStateHolder<List<LibraryBorrowRecord>>)
    suspend fun search(
        page : Int,
        keyword : String,
        holder : UiStateHolder<List<LibrarySearchRow>>
    )
}