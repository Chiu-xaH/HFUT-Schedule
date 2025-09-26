package com.hfut.schedule.logic.network.repo.hfut

import com.google.gson.Gson
import com.hfut.schedule.logic.model.library.BorrowedStatus
import com.hfut.schedule.logic.model.library.LibraryBorrowedBean
import com.hfut.schedule.logic.model.library.LibraryBorrowedResponse
import com.hfut.schedule.logic.model.library.LibraryStatus
import com.hfut.schedule.logic.model.library.LibraryStatusResponse
import com.hfut.schedule.logic.network.api.LibraryService
import com.hfut.schedule.logic.network.servicecreator.LibraryServiceCreator
import com.hfut.schedule.logic.network.util.launchRequestNone
import com.hfut.schedule.logic.network.util.launchRequestSimple
import com.hfut.schedule.logic.util.getPageSize
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import retrofit2.awaitResponse

object LibraryRepository {
    private val library = LibraryServiceCreator.create(LibraryService::class.java)

    suspend fun checkLibraryNetwork() = launchRequestNone {
        library.check().awaitResponse()
    }

    suspend fun checkLibraryLogin(token : String,holder : StateHolder<Boolean>) =
        launchRequestSimple(
            holder = holder,
            request = { library.checkLogin(token).awaitResponse() },
            transformSuccess = { _, json -> parseCheckLibraryLogin(json) }
        )
    @JvmStatic
    private fun parseCheckLibraryLogin(json : String) : Boolean {
        try {
            val sId = getPersonInfo().studentId ?: return false
            val name = getPersonInfo().name ?: return false
            return json.contains(sId) || json.contains(name)
        } catch (e : Exception) { throw e }
    }

    suspend fun getStatus(token : String,holder : StateHolder<LibraryStatus>) = launchRequestSimple(
        holder = holder,
        request = { library.getStatus(auth = token).awaitResponse() },
        transformSuccess = { _,json -> parseLibraryStatus(json) }
    )
    @JvmStatic
    private fun parseLibraryStatus(json: String) : LibraryStatus = try {
        if(json.contains("成功")) {
            var bookShelfCount = 0
            var borrowCount = 0
            var reserveCount = 0
            var entrustCount = 0
            var followCount = 0
            var collectCount = 0
            var downloadCount  = 0
            var sharingCount = 0
            var recommendCount = 0

            val data = Gson().fromJson(json, LibraryStatusResponse::class.java).data
            for(item in data) {
                when(item.code) {
                    "mybookshelf" -> bookShelfCount = item.count
                    "myborrow" -> borrowCount = item.count
                    "myreserve" -> reserveCount = item.count
                    "myentrust" -> entrustCount = item.count
                    "myfollow" -> followCount = item.count
                    "mycollect" -> collectCount = item.count
                    "mydownload" -> downloadCount = item.count
                    "mysharing" -> sharingCount = item.count
                    "myrecommend" -> recommendCount = item.count
                }
            }
            LibraryStatus(bookShelfCount,borrowCount,reserveCount,entrustCount,followCount,collectCount,downloadCount,sharingCount,recommendCount)
        } else {
            throw Exception("登陆状态失效 $json")
        }
    } catch (e : Exception) { throw e }

    suspend fun getBorrowed(token : String, page : Int, status: BorrowedStatus? = null, pageSize : Int = getPageSize(), holder : StateHolder<List<LibraryBorrowedBean>>) = launchRequestSimple(
        holder = holder,
        request = { library.getBorrowed(token,page,status?.status, pageSize = pageSize ).awaitResponse() },
        transformSuccess = { _,json -> parseBorrowed(json) }
    )
    @JvmStatic
    private fun parseBorrowed(json : String) : List<LibraryBorrowedBean> = try {
        Gson().fromJson(json, LibraryBorrowedResponse::class.java).data.list
    } catch (e : Exception) { throw e }
}

