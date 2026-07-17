package com.hfut.schedule.logic.network.repo


import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.enumeration.LibraryItems
import com.hfut.schedule.network.model.response.community.CommunityApplyFriendResponse
import com.hfut.schedule.network.model.response.community.CommunityApplyingFriendRecord
import com.hfut.schedule.network.model.response.community.CommunityApplyingFriendResponse
import com.hfut.schedule.network.model.response.community.CommunityGradeAverage
import com.hfut.schedule.network.model.response.community.CommunityBookPosition
import com.hfut.schedule.network.model.response.community.CommunityBookPositionResponse
import com.hfut.schedule.network.model.response.community.CommunityBorrowRecord
import com.hfut.schedule.network.model.response.community.CommunityBorrowResponse
import com.hfut.schedule.network.model.response.community.CommunityBus
import com.hfut.schedule.network.model.response.community.CommunityBusResponse
import com.hfut.schedule.network.model.response.community.CommunityDormitory
import com.hfut.schedule.network.model.response.community.CommunityDormitoryInfoResponse
import com.hfut.schedule.network.model.response.community.CommunityDormitoryResponse
import com.hfut.schedule.network.model.response.community.CommunityDormitoryScore
import com.hfut.schedule.network.model.response.community.CommunityDormitoryScoreResponse
import com.hfut.schedule.network.model.response.community.CommunityDormitoryUser
import com.hfut.schedule.logic.model.DormitoryWeeklyScores
import com.hfut.schedule.logic.model.WeekScore
import com.hfut.schedule.network.model.response.community.CommunityFailRateRecord
import com.hfut.schedule.network.model.response.community.CommunityFailRateResponse
import com.hfut.schedule.network.model.response.community.CommunityGradeAllResponse
import com.hfut.schedule.network.model.response.community.CommunityGradeAll
import com.hfut.schedule.network.model.response.community.CommunityGradeAverageResponse
import com.hfut.schedule.network.model.response.community.CommunityGradeResponse
import com.hfut.schedule.network.model.response.community.CommunityGrade
import com.hfut.schedule.network.model.response.community.CommunityLibraryRecord
import com.hfut.schedule.network.model.response.community.CommunityLibraryResponse
import com.hfut.schedule.network.model.response.community.CommunityLoginResponse
import com.hfut.schedule.network.model.response.community.CommunitySchoolMap
import com.hfut.schedule.network.model.response.community.CommunitySchoolMapResponse
import com.hfut.schedule.network.model.response.community.CommunityStuAppDetail
import com.hfut.schedule.network.model.response.community.CommunityStuAppResponse
import com.hfut.schedule.network.model.response.community.CommunityTodayResponse
import com.hfut.schedule.network.model.response.community.CommunityToday
import com.hfut.schedule.logic.util.network.launchRequestState
import com.xah.common.logic.state.UiStateHolder
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.sys.showToast
import com.xah.common.logic.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.hfut.schedule.network.api.CommunityService
import com.hfut.schedule.network.impl.CommunityServiceCreator
import com.hfut.schedule.network.helper.Constant
import com.hfut.schedule.network.helper.GsonInstance
import com.hfut.schedule.network.model.StatusCode
import com.hfut.schedule.ui.component.network.onListenStateHolderForNetwork
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

object CommunityRepository {
    private val community = CommunityServiceCreator.create(CommunityService::class.java)

    suspend fun loginCommunity(ticket : String,holder : UiStateHolder<String>) = launchRequestState(
        holder = holder,
        request = { community.login(ticket) },
        transformSuccess = { _, json -> parseCommunity(json) }
    )
    @JvmStatic
    private fun parseCommunity(json : String) : String = try {
        if (json.contains(StatusCode.OK.code.toString())) {
            val token = GsonInstance.fromJson(json, CommunityLoginResponse::class.java).result.token!!
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

    suspend fun searchFailRate(token : String, name: String, page : Int,code : String?,holder : UiStateHolder<Pair<String?,List<CommunityFailRateRecord>>>) =
        launchRequestState(
            holder = holder,
            request = {
                community.getFailRate(
                    token,
                    name,
                    page,
                )
            },
            transformSuccess = { _, json -> Pair(code,parseFailRate(json)) }
        )
    @JvmStatic
    private fun parseFailRate(json : String) : List<CommunityFailRateRecord> = try {
        if(json.contains("操作成功")) {
            GsonInstance.fromJson(json, CommunityFailRateResponse::class.java).result.records
        } else
            throw Exception(json)
    } catch (e : Exception) { throw e }

    suspend fun checkCommunityLogin(token: String,holder : UiStateHolder<Boolean>) =
        launchRequestState(
            holder = holder,
            request = { community.getExam(token) },
            transformSuccess = { _, _ -> true }
        )

    suspend fun getGrade(token: String, year : String, term : String,holder : UiStateHolder<CommunityGrade>) =
        launchRequestState(
            holder = holder,
            request = { community.getGrade(token, year, term) },
            transformSuccess = { _, json -> parseGradeFromCommunity(json) }
        )
    @JvmStatic
    private fun parseGradeFromCommunity(json : String) : CommunityGrade = try {
        if(json.contains("success"))
            GsonInstance.fromJson(json, CommunityGradeResponse::class.java).result
        else
            throw Exception(json)
    } catch (e : Exception) { throw e }

    suspend fun getAvgGrade(token: String,holder : UiStateHolder<CommunityGradeAverage>) = launchRequestState(
        holder = holder,
        request = { community.getAvgGrade(token) },
        transformSuccess = { _, json -> parseAvgGradeFromCommunity(json) }
    )
    @JvmStatic
    private fun parseAvgGradeFromCommunity(result : String) : CommunityGradeAverage = try {
        if(result.contains("success"))
            GsonInstance.fromJson(result, CommunityGradeAverageResponse::class.java).result
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    suspend fun getAllAvgGrade(token: String,holder : UiStateHolder<List<CommunityGradeAll>>) =
        launchRequestState(
            holder = holder,
            request = { community.getAllAvgGrade(token) },
            transformSuccess = { _, json -> parseAllAvgGradeFromCommunity(json) }
        )
    @JvmStatic
    private fun parseAllAvgGradeFromCommunity(result : String) : List<CommunityGradeAll> = try {
        if(result.contains("success"))
            GsonInstance.fromJson(result, CommunityGradeAllResponse::class.java).result
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    suspend fun searchBooks(token: String, name: String, page: Int,holder : UiStateHolder<List<CommunityLibraryRecord>>) =
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
    private fun parseSearchBooks(json : String) : List<CommunityLibraryRecord> = try {
        if(json.contains("操作成功"))
            GsonInstance.fromJson(json, CommunityLibraryResponse::class.java).result.records
        else
            throw Exception(json)
    } catch (e : Exception) { throw e }

    suspend fun getBookPosition(token: String,callNo: String,holder : UiStateHolder<List<CommunityBookPosition>>) =
        launchRequestState(
            holder = holder,
            request = { community.getBookPosition(token, callNo) },
            transformSuccess = { _, json -> parseBookPosition(json) }
        )
    @JvmStatic
    private fun parseBookPosition(json : String) : List<CommunityBookPosition> = try {
        if(json.contains("成功"))
            GsonInstance.fromJson(json, CommunityBookPositionResponse::class.java).result
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

    suspend fun getDormitory(token : String,holder : UiStateHolder<CommunityDormitory>) =
        launchRequestState(
            holder = holder,
            request = { community.getDormitory(token) },
            transformSuccess = { _, json -> parseDormitory(json) }
        )
    @JvmStatic
    private fun parseDormitory(result : String) : CommunityDormitory = try {
        if (result.contains("操作成功")) {
            GsonInstance.fromJson(result, CommunityDormitoryResponse::class.java).result ?: throw Exception("无住宿信息")
        }
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    suspend fun getDormitoryInfo(token : String, dormitoryFromCommunityResp : UiStateHolder<CommunityDormitory>, dormitoryInfoFromCommunityResp : UiStateHolder<List<CommunityDormitoryUser>>) =
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
    private fun parseDormitoryInfo(result : String) : List<CommunityDormitoryUser> = try {
        if (result.contains("操作成功")) {
            val list1 = GsonInstance.fromJson(result, CommunityDormitoryInfoResponse::class.java).result?.profileList ?: throw Exception("未查询到宿舍")
            list1.flatMap { it.userList }.distinct()
        }
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    suspend fun addFriendApply(token : String, username : String,holder : UiStateHolder<String>) =
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
            GsonInstance.fromJson(result, CommunityApplyFriendResponse::class.java).message
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    suspend fun getApplying(token : String,holder : UiStateHolder<List<CommunityApplyingFriendRecord?>>) =
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
    private fun parseApplyFriends(result : String) : List<CommunityApplyingFriendRecord?> = try {
        if(result.contains("success"))
            GsonInstance.fromJson(result, CommunityApplyingFriendResponse::class.java).result.records
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    suspend fun getMaps(token : String,holder : UiStateHolder<List<CommunitySchoolMap>>) = launchRequestState(
        holder = holder,
        request = { community.getCampusMap(token) },
        transformSuccess = { _, json -> parseMaps(json) }
    )
    @JvmStatic
    private fun parseMaps(result : String) : List<CommunitySchoolMap> = try {
        if(result.contains("操作成功"))
            GsonInstance.fromJson(result, CommunitySchoolMapResponse::class.java).result
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    suspend fun getStuApps(token : String,holder : UiStateHolder<List<CommunityStuAppDetail>>) =
        launchRequestState(
            holder = holder,
            request = { community.getStuApps(token) },
            transformSuccess = { _, json -> parseStuApps(json) }
        )
    @JvmStatic
    private fun parseStuApps(result : String) : List<CommunityStuAppDetail> = try {
        if(result.contains("操作成功")) {
            val list = GsonInstance.fromJson(result, CommunityStuAppResponse::class.java).result
            val totalList = list.flatMap { it.subList }
            totalList.filter { it.url?.startsWith(Constant.STU_URL) == true }
        }
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    suspend fun getBus(token : String,holder : UiStateHolder<List<CommunityBus>>) = launchRequestState(
        holder = holder,
        request = { community.getBus(token) },
        transformSuccess = { _, json -> parseBus(json) }
    )
    @JvmStatic
    private fun parseBus(result : String) : List<CommunityBus> = try {
        if(result.contains("操作成功")) {
            GsonInstance.fromJson(result, CommunityBusResponse::class.java).result
        }
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    suspend fun communityBooks(token : String, type : LibraryItems, page : Int = 1, booksChipData : UiStateHolder<List<CommunityBorrowRecord>>) =
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
    private fun parseMyBookFromCommunity(json : String) : List<CommunityBorrowRecord> = try {
        if(json.contains("success"))
            GsonInstance.fromJson(json, CommunityBorrowResponse::class.java).result.records
        else
            throw Exception(json)
    } catch (e : Exception) { throw e }

    suspend fun getToday(token : String,holder : UiStateHolder<CommunityToday>) = launchRequestState(
        holder = holder,
        request = { community.getToday(token) },
        transformSuccess = { _, json -> parseTodayFromCommunity(json) }
    )
    @JvmStatic
    private fun parseTodayFromCommunity(result : String) : CommunityToday = try {
        GsonInstance.fromJson(result, CommunityTodayResponse::class.java).result
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

    suspend fun getDormitoryScore(
        token : String,
        week : Int? = null,
        semester : String? = null,
        holder : UiStateHolder<List<CommunityDormitoryScore>>
    ) = launchRequestState(
        holder = holder,
        request = { community.getDormitoryScoreDetail(token, week, semester) },
        transformSuccess = { _, json -> parseDormitoryScore(json) }
    )

    suspend fun getAllDormitoryScores(
        token : String,
        semester : String,
        semesterInt : Int,
        holder : UiStateHolder<DormitoryWeeklyScores>,
    ) {
        val cacheKey = LargeStringDataManager.getDormitoryScoreKey(semesterInt)
        val cached = LargeStringDataManager.read(cacheKey)

        if (cached != null) {
            LogUtil.debug("DormitoryScore: cache hit, key=$cacheKey")
            val result = GsonInstance.fromJson(cached, DormitoryWeeklyScores::class.java)
            if (result != null) {
                launchRequestState(
                    holder = holder,
                    request = { community.getDormitoryScoreDetail(token, 0, semester) },
                    transformSuccess = { _, _ -> result }
                )
            } else {
                launchRequestState(
                    holder = holder,
                    request = { community.getDormitoryScoreDetail(token, 0, semester) },
                    transformSuccess = { _, _ -> throw Exception("卫生评分缓存解析失败") }
                )
            }
        } else {
            LogUtil.debug("DormitoryScore: fetching all weeks, semester=$semester")

            launchRequestState(
                holder = holder,
                request = { community.getDormitoryScoreDetail(token, 1, semester) },
                transformSuccess = { _, _ ->
                    val weekScores = fetchAllWeekScores(token, semester)

                    LogUtil.debug("DormitoryScore: total weeks with data=${weekScores.size}")

                    if (weekScores.isNotEmpty()) {
                        val result = DormitoryWeeklyScores(semester, weekScores)
                        LargeStringDataManager.save(cacheKey, GsonInstance.toJson(result))
                        result
                    } else {
                        throw Exception("无卫生评分数据")
                    }
                }
            )
        }
    }

    private suspend fun fetchAllWeekScores(
        token : String,
        semester : String,
    ) : List<WeekScore> = withContext(Dispatchers.IO) {
        val scores = mutableListOf<WeekScore>()

        for (week in 1..MyApplication.MAX_WEEK) {
            currentCoroutineContext().ensureActive()

            try {
                val call = community.getDormitoryScoreDetail(token, week, semester)
                val response = call.execute()

                if (response.isSuccessful) {
                    val json = response.body()?.string() ?: continue

                    if (json.contains("操作成功")) {
                        val allScores = parseDormitoryScore(json)
                        val totalItem = allScores.find { it.title == "评分" }
                        val totalValue = totalItem?.value?.toDoubleOrNull()

                        if (totalValue != null) {
                            scores.add(WeekScore(week, allScores, totalValue))
                            LogUtil.debug("DormitoryScore: week=$week, 评分=$totalValue")
                        }
                    }
                }
            } catch (e: Exception) {
                LogUtil.error(e)
            }
        }

        scores
    }
 
    @JvmStatic
    private fun parseDormitoryScore(result : String) : List<CommunityDormitoryScore> = try {
        if (result.contains("操作成功")) {
            GsonInstance.fromJson(result, CommunityDormitoryScoreResponse::class.java)?.result ?: emptyList()
        } else {
            throw Exception(result)
        }
    } catch (e : Exception) { throw e }
}
