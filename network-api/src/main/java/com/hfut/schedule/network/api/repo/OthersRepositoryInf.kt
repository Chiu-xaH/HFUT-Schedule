package com.hfut.schedule.network.api.repo

import com.hfut.schedule.network.api.model.request.haile.HaiLeDeviceDetailRequest
import com.hfut.schedule.network.api.model.request.haile.HaiLeNearPositionRequestDto
import com.hfut.schedule.network.api.model.response.html.Department
import com.hfut.schedule.network.api.model.response.html.OldDormitoryXuanCheng
import com.hfut.schedule.network.api.model.response.json.haile.HaiLeDeviceDetailBean
import com.hfut.schedule.network.api.model.response.json.haile.HaiLeNearPositionBean
import com.hfut.schedule.network.api.model.response.json.hall.OfficeHallSearchRecord
import com.hfut.schedule.network.api.model.response.json.second.SecondClassActivity
import com.hfut.schedule.network.api.model.response.json.teacher.TeacherResponse
import com.hfut.schedule.network.api.model.response.json.work.WorkSearchResponse
import com.hfut.schedule.network.api.model.response.json.zhijian.ZhiJianCourseTableDto
import com.xah.common.logic.model.CampusRegion
import com.xah.common.logic.state.UiStateHolder

interface OthersRepositoryInf {
    suspend fun checkPeLogin(cookie : String,holder : UiStateHolder<Boolean>)
    suspend fun checkSecondClassLogin(cookie : String,holder : UiStateHolder<Boolean>)
    suspend fun getZhiJianCourses(studentId : String, mondayDate : String, token : String,holder : UiStateHolder<List<ZhiJianCourseTableDto>>)
    suspend fun zhiJianCheckLogin(token : String,holder : UiStateHolder<Boolean>)
    suspend fun checkStuLogin(cookie : String,checkStuLoginResp : UiStateHolder<Boolean>)
    suspend fun officeHallSearch(
        text : String,
        page : Int,
        holder : UiStateHolder<List<OfficeHallSearchRecord>>
    )
    suspend fun searchTeacher(name: String = "", direction: String = "",teacherSearchData : UiStateHolder<TeacherResponse>)
    suspend fun searchWorks(keyword: String?, page: Int = 1, type: Int, campus: CampusRegion, workSearchResult : UiStateHolder<WorkSearchResponse>)
    suspend fun searchDormitoryXuanCheng(code : String,dormitoryResult : UiStateHolder<List<OldDormitoryXuanCheng>>)
    suspend fun getHaiLeNear(bean : HaiLeNearPositionRequestDto, holder : UiStateHolder<List<HaiLeNearPositionBean>>)
    suspend fun getHaiLDeviceDetail(bean : HaiLeDeviceDetailRequest, holder : UiStateHolder<List<HaiLeDeviceDetailBean>>)
    suspend fun getSecondClassActivities(
        cookie: String,
        page: Int = 1,
        holder : UiStateHolder<List<SecondClassActivity>>
    )
    suspend fun getDepartments(holder : UiStateHolder<List<Department>>)
}