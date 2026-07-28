package com.hfut.schedule.network.api.repo

import com.hfut.schedule.network.api.model.response.json.community.CommunityApplyingFriendRecord
import com.hfut.schedule.network.api.model.response.json.community.CommunityBookPosition
import com.hfut.schedule.network.api.model.response.json.community.CommunityBorrowRecord
import com.hfut.schedule.network.api.model.response.json.community.CommunityBus
import com.hfut.schedule.network.api.model.response.json.community.CommunityDormitory
import com.hfut.schedule.network.api.model.response.json.community.CommunityDormitoryScore
import com.hfut.schedule.network.api.model.response.json.community.CommunityDormitoryUser
import com.hfut.schedule.network.api.model.response.json.community.CommunityFailRateRecord
import com.hfut.schedule.network.api.model.response.json.community.CommunityGrade
import com.hfut.schedule.network.api.model.response.json.community.CommunityGradeAll
import com.hfut.schedule.network.api.model.response.json.community.CommunityGradeAverage
import com.hfut.schedule.network.api.model.request.community.CommunityLibraryContent
import com.hfut.schedule.network.api.model.response.json.community.CommunityLibraryRecord
import com.hfut.schedule.network.api.model.response.json.community.CommunitySchoolMap
import com.hfut.schedule.network.api.model.response.json.community.CommunityStuAppDetail
import com.hfut.schedule.network.api.model.response.json.community.CommunityToday
import com.hfut.schedule.network.api.model.response.json.community.DormitoryWeeklyScoresDto
import com.xah.common.logic.state.UiStateHolder

interface CommunityRepositoryInf {
    suspend fun loginCommunity(ticket : String,holder : UiStateHolder<String>)
    suspend fun searchFailRate(token : String, name: String, page : Int,code : String?,holder : UiStateHolder<Pair<String?,List<CommunityFailRateRecord>>>)
    suspend fun checkCommunityLogin(token: String,holder : UiStateHolder<Boolean>)
    suspend fun getGrade(token: String, year : String, term : String,holder : UiStateHolder<CommunityGrade>)
    suspend fun getAllSemestersRankings(
        token: String,
        semesters: List<Int>,
        holder: UiStateHolder<Map<Int, CommunityGrade>>
    )
    suspend fun getAvgGrade(token: String,holder : UiStateHolder<CommunityGradeAverage>)
    suspend fun getAllAvgGrade(token: String,holder : UiStateHolder<List<CommunityGradeAll>>)
    suspend fun searchBooks(token: String, name: String, page: Int,holder : UiStateHolder<List<CommunityLibraryRecord>>)
    suspend fun getBookPosition(token: String,callNo: String,holder : UiStateHolder<List<CommunityBookPosition>>)
    fun getCoursesFromCommunity(token : String, studentId: String? = null)
    fun openFriend(token : String)
    suspend fun getDormitory(token : String,holder : UiStateHolder<CommunityDormitory>)
    suspend fun getDormitoryInfo(token : String, dormitoryFromCommunityResp : UiStateHolder<CommunityDormitory>, dormitoryInfoFromCommunityResp : UiStateHolder<List<CommunityDormitoryUser>>) : Unit?
    suspend fun addFriendApply(token : String, username : String,holder : UiStateHolder<String>)
    suspend fun getApplying(token : String,holder : UiStateHolder<List<CommunityApplyingFriendRecord?>>)
    suspend fun getMaps(token : String,holder : UiStateHolder<List<CommunitySchoolMap>>)
    suspend fun getStuApps(token : String,holder : UiStateHolder<List<CommunityStuAppDetail>>)
    suspend fun getBus(token : String,holder : UiStateHolder<List<CommunityBus>>)
    suspend fun communityBooks(token : String, type : CommunityLibraryContent, page : Int = 1, booksChipData : UiStateHolder<List<CommunityBorrowRecord>>)
    suspend fun getToday(token : String,holder : UiStateHolder<CommunityToday>)
    fun getFriends(token : String)
    fun checkApplying(token : String, id : String, isOk : Boolean)
    suspend fun getDormitoryScore(
        token : String,
        week : Int? = null,
        semester : String? = null,
        holder : UiStateHolder<List<CommunityDormitoryScore>>
    )
    suspend fun getAllDormitoryScores(
        token : String,
        semester : String,
        semesterInt : Int,
        holder : UiStateHolder<DormitoryWeeklyScoresDto>,
    )
}
