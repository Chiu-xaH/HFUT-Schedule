package com.hfut.schedule.logic.network.repo.hfut

import com.google.gson.Gson
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.enumeration.LibraryItems
import com.hfut.schedule.logic.model.community.ApplyFriendResponse
import com.hfut.schedule.logic.model.community.ApplyingLists
import com.hfut.schedule.logic.model.community.ApplyingResponse
import com.hfut.schedule.logic.model.community.AvgResult
import com.hfut.schedule.logic.model.community.BookPositionBean
import com.hfut.schedule.logic.model.community.BookPositionResponse
import com.hfut.schedule.logic.model.community.BorrowRecords
import com.hfut.schedule.logic.model.community.BorrowResponse
import com.hfut.schedule.logic.model.community.BusBean
import com.hfut.schedule.logic.model.community.BusResponse
import com.hfut.schedule.logic.model.community.DormitoryBean
import com.hfut.schedule.logic.model.community.DormitoryInfoResponse
import com.hfut.schedule.logic.model.community.DormitoryResponse
import com.hfut.schedule.logic.model.community.DormitoryUser
import com.hfut.schedule.logic.model.community.FailRateRecord
import com.hfut.schedule.logic.model.community.FailRateResponse
import com.hfut.schedule.logic.model.community.GradeAllResponse
import com.hfut.schedule.logic.model.community.GradeAllResult
import com.hfut.schedule.logic.model.community.GradeAvgResponse
import com.hfut.schedule.logic.model.community.GradeResponse
import com.hfut.schedule.logic.model.community.GradeResult
import com.hfut.schedule.logic.model.community.LibRecord
import com.hfut.schedule.logic.model.community.LibraryResponse
import com.hfut.schedule.logic.model.community.LoginCommunityResponse
import com.hfut.schedule.logic.model.community.MapBean
import com.hfut.schedule.logic.model.community.MapResponse
import com.hfut.schedule.logic.model.community.StuAppBean
import com.hfut.schedule.logic.model.community.StuAppsResponse
import com.hfut.schedule.logic.model.community.TodayResponse
import com.hfut.schedule.logic.model.community.TodayResult
import com.hfut.schedule.logic.network.api.CommunityService
import com.hfut.schedule.logic.network.util.launchRequestState
import com.hfut.schedule.logic.network.servicecreator.CommunityServiceCreator
import com.hfut.schedule.logic.network.util.StatusCode
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.network.onListenStateHolderForNetwork
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse

object CommunityRepository {
    private val community = CommunityServiceCreator.create(CommunityService::class.java)

    suspend fun loginCommunity(ticket : String,holder : StateHolder<String>) = launchRequestState(
        holder = holder,
        request = { community.login(ticket) },
        transformSuccess = { _, json -> parseCommunity(json) }
    )
    @JvmStatic
    private fun parseCommunity(json : String) : String = try {
        if (json.contains(StatusCode.OK.code.toString())) {
            val token = Gson().fromJson(json, LoginCommunityResponse::class.java).result.token!!
            SharedPrefs.saveString("TOKEN", token)
            showToast("智慧社区登陆成功")
            token
        } else {
            showToast("智慧社区登陆失败")
            throw Exception(json)
        }
    } catch (e : Exception) {
        showToast("智慧社区登陆失败")
        throw e
    }

    suspend fun searchFailRate(token : String, name: String, page : Int,holder : StateHolder<List<FailRateRecord>>) =
        launchRequestState(
            holder = holder,
            request = {
                community.getFailRate(
                    token,
                    name,
                    page,
                )
            },
            transformSuccess = { _, json -> parseFailRate(json) }
        )
    @JvmStatic
    private fun parseFailRate(json : String) : List<FailRateRecord> = try {
        if(json.contains("操作成功")) {
            Gson().fromJson(json, FailRateResponse::class.java).result.records
        } else
            throw Exception(json)
    } catch (e : Exception) { throw e }

    suspend fun checkCommunityLogin(token: String,holder : StateHolder<Boolean>) =
        launchRequestState(
            holder = holder,
            request = { community.getExam(token) },
            transformSuccess = { _, _ -> true }
        )

    suspend fun getGrade(token: String, year : String, term : String,holder : StateHolder<GradeResult>) =
        launchRequestState(
            holder = holder,
            request = { community.getGrade(token, year, term) },
            transformSuccess = { _, json -> parseGradeFromCommunity(json) }
        )
    @JvmStatic
    private fun parseGradeFromCommunity(json : String) : GradeResult = try {
        if(json.contains("success"))
            Gson().fromJson(json, GradeResponse::class.java).result
        else
            throw Exception(json)
    } catch (e : Exception) { throw e }

    suspend fun getAvgGrade(token: String,holder : StateHolder<AvgResult>) = launchRequestState(
        holder = holder,
        request = { community.getAvgGrade(token) },
        transformSuccess = { _, json -> parseAvgGradeFromCommunity(json) }
    )
    @JvmStatic
    private fun parseAvgGradeFromCommunity(result : String) : AvgResult = try {
        if(result.contains("success"))
            Gson().fromJson(result, GradeAvgResponse::class.java).result
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    suspend fun getAllAvgGrade(token: String,holder : StateHolder<List<GradeAllResult>>) =
        launchRequestState(
            holder = holder,
            request = { community.getAllAvgGrade(token) },
            transformSuccess = { _, json -> parseAllAvgGradeFromCommunity(json) }
        )
    @JvmStatic
    private fun parseAllAvgGradeFromCommunity(result : String) : List<GradeAllResult> = try {
        if(result.contains("success"))
            Gson().fromJson(result, GradeAllResponse::class.java).result
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    suspend fun searchBooks(token: String, name: String, page: Int,holder : StateHolder<List<LibRecord>>) =
        launchRequestState(
            holder = holder,
            request = {
                community.searchBooks(
                    token,
                    name,
                    page
                )
            },
            transformSuccess = { _, json -> parseSearchBooks(json) }
        )
    @JvmStatic
    private fun parseSearchBooks(json : String) : List<LibRecord> = try {
        if(json.contains("操作成功"))
            Gson().fromJson(json, LibraryResponse::class.java).result.records
        else
            throw Exception(json)
    } catch (e : Exception) { throw e }

    suspend fun getBookPosition(token: String,callNo: String,holder : StateHolder<List<BookPositionBean>>) =
        launchRequestState(
            holder = holder,
            request = { community.getBookPosition(token, callNo) },
            transformSuccess = { _, json -> parseBookPosition(json) }
        )
    @JvmStatic
    private fun parseBookPosition(json : String) : List<BookPositionBean> = try {
        if(json.contains("成功"))
            Gson().fromJson(json, BookPositionResponse::class.java).result
        else
            throw Exception(json)
    } catch (e : Exception) { throw e }

    fun getCoursesFromCommunity(token : String, studentId: String? = null) {
        val call = token.let { community.getCourse(it,studentId) }
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(studentId == null)
                    SharedPrefs.saveString("Course", response.body()?.string())
                else
                    SharedPrefs.saveString("Course${studentId}", response.body()?.string())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun openFriend(token : String) {
        val call = token.let { community.switchShare(it, CommunityService.RequestJson(1)) }
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    suspend fun getDormitory(token : String,holder : StateHolder<DormitoryBean>) =
        launchRequestState(
            holder = holder,
            request = { community.getDormitory(token) },
            transformSuccess = { _, json -> parseDormitory(json) }
        )
    @JvmStatic
    private fun parseDormitory(result : String) : DormitoryBean = try {
        if (result.contains("操作成功")) {
            val list = Gson().fromJson(result, DormitoryResponse::class.java).result
            if(list.isEmpty()) throw Exception("无住宿信息") else list[0]
        }
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    suspend fun getDormitoryInfo(token : String, dormitoryFromCommunityResp : StateHolder<DormitoryBean>, dormitoryInfoFromCommunityResp : StateHolder<List<DormitoryUser>>) =
        onListenStateHolderForNetwork(
            dormitoryFromCommunityResp,
            dormitoryInfoFromCommunityResp
        ) { d ->
            launchRequestState(
                holder = dormitoryInfoFromCommunityResp,
                request = {
                    community.getDormitoryInfo(token, d.campus, d.room, d.dormitory)
                },
                transformSuccess = { _, json -> parseDormitoryInfo(json) }
            )
        }
    @JvmStatic
    private fun parseDormitoryInfo(result : String) : List<DormitoryUser> = try {
        if (result.contains("操作成功")) {
            val list1 = Gson().fromJson(result, DormitoryInfoResponse::class.java).result.profileList
            list1.flatMap { it.userList }.distinct()
        }
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    suspend fun addFriendApply(token : String, username : String,holder : StateHolder<String>) =
        launchRequestState(
            holder = holder,
            request = {
                community.applyAdd(token, CommunityService.RequestJsonApply(username))
                    
            },
            transformSuccess = { _, json -> parseApplyFriend(json) }
        )
    @JvmStatic
    private fun parseApplyFriend(result : String) : String = try {
        if (result.contains("success"))
            Gson().fromJson(result, ApplyFriendResponse::class.java).message
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    suspend fun getApplying(token : String,holder : StateHolder<List<ApplyingLists?>>) =
        launchRequestState(
            holder = holder,
            request = {
                community.getApplyingList(
                    token,
                )
            },
            transformSuccess = { _, json -> parseApplyFriends(json) }
        )
    @JvmStatic
    private fun parseApplyFriends(result : String) : List<ApplyingLists?> = try {
        if(result.contains("success"))
            Gson().fromJson(result, ApplyingResponse::class.java).result.records
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    suspend fun getMaps(token : String,holder : StateHolder<List<MapBean>>) = launchRequestState(
        holder = holder,
        request = { community.getCampusMap(token) },
        transformSuccess = { _, json -> parseMaps(json) }
    )
    @JvmStatic
    private fun parseMaps(result : String) : List<MapBean> = try {
        if(result.contains("操作成功"))
            Gson().fromJson(result, MapResponse::class.java).result
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    suspend fun getStuApps(token : String,holder : StateHolder<List<StuAppBean>>) =
        launchRequestState(
            holder = holder,
            request = { community.getStuApps(token) },
            transformSuccess = { _, json -> parseStuApps(json) }
        )
    @JvmStatic
    private fun parseStuApps(result : String) : List<StuAppBean> = try {
        if(result.contains("操作成功")) {
            val list = Gson().fromJson(result, StuAppsResponse::class.java).result
            val totalList = list.flatMap { it.subList }
            totalList.filter { it.url?.startsWith(MyApplication.Companion.STU_URL) == true }
        }
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    suspend fun getBus(token : String,holder : StateHolder<List<BusBean>>) = launchRequestState(
        holder = holder,
        request = { community.getBus(token) },
        transformSuccess = { _, json -> parseBus(json) }
    )
    @JvmStatic
    private fun parseBus(result : String) : List<BusBean> = try {
        if(result.contains("操作成功")) {
            Gson().fromJson(result, BusResponse::class.java).result
        }
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    suspend fun communityBooks(token : String, type : LibraryItems, page : Int = 1, booksChipData : StateHolder<List<BorrowRecords>>) =
        launchRequestState(
            holder = booksChipData,
            request = {
                val size = 500
                when (type) {
                    LibraryItems.OVERDUE -> community.getOverDueBook(
                        token,
                        page.toString(),
                        size.toString()
                    )

                    LibraryItems.HISTORY -> community.getHistoryBook(
                        token,
                        page.toString(),
                        size.toString()
                    )

                    LibraryItems.BORROWED -> community.getBorrowedBook(
                        token,
                        page.toString(),
                        size.toString()
                    )
                }
            },
            transformSuccess = { _, json -> parseMyBookFromCommunity(json) }
        )
    @JvmStatic
    private fun parseMyBookFromCommunity(json : String) : List<BorrowRecords> = try {
        if(json.contains("success"))
            Gson().fromJson(json, BorrowResponse::class.java).result.records
        else
            throw Exception(json)
    } catch (e : Exception) { throw e }

    suspend fun getToday(token : String,holder : StateHolder<TodayResult>) = launchRequestState(
        holder = holder,
        request = { community.getToday(token) },
        transformSuccess = { _, json -> parseTodayFromCommunity(json) }
    )
    @JvmStatic
    private fun parseTodayFromCommunity(result : String) : TodayResult = try {
        Gson().fromJson(result, TodayResponse::class.java).result
    } catch (e : Exception) { throw e }

    fun getFriends(token : String) {
        val call = token.let { community.getFriends(it) }
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                SharedPrefs.saveString("feiends", response.body()?.string())
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun checkApplying(token : String, id : String, isOk : Boolean) {
        val call = token.let { community.checkApplying(it,
            CommunityService.RequestApplyingJson(id,if(isOk) 1 else 0)) }
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
}