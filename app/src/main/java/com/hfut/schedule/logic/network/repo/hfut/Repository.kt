package com.hfut.schedule.logic.network.repo.hfut

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.enumeration.AdmissionType
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.model.AdmissionDetailBean
import com.hfut.schedule.logic.model.AdmissionDetailResponseHistory
import com.hfut.schedule.logic.model.AdmissionDetailResponsePlan
import com.hfut.schedule.logic.model.AdmissionListResponse
import com.hfut.schedule.logic.model.AdmissionMapBean
import com.hfut.schedule.logic.model.AdmissionTokenResponse
import com.hfut.schedule.logic.model.HaiLeDeviceDetailBean
import com.hfut.schedule.logic.model.HaiLeDeviceDetailRequestBody
import com.hfut.schedule.logic.model.HaiLeDeviceDetailResponse
import com.hfut.schedule.logic.model.HaiLeNearPositionBean
import com.hfut.schedule.logic.model.HaiLeNearPositionRequestDTO
import com.hfut.schedule.logic.model.HaiLeNearPositionResponse
import com.hfut.schedule.logic.model.OfficeHallSearchBean
import com.hfut.schedule.logic.model.OfficeHallSearchResponse
import com.hfut.schedule.logic.model.SearchEleResponse
import com.hfut.schedule.logic.model.TeacherResponse
import com.hfut.schedule.logic.model.WorkSearchResponse
import com.hfut.schedule.logic.model.XuanquResponse
import com.hfut.schedule.logic.model.zhijian.ZhiJianCourseItem
import com.hfut.schedule.logic.model.zhijian.ZhiJianCourseItemDto
import com.hfut.schedule.logic.model.zhijian.ZhiJianCoursesResponse
import com.hfut.schedule.logic.network.api.AdmissionService
import com.hfut.schedule.logic.network.api.DormitoryScore
import com.hfut.schedule.logic.network.api.HaiLeWashingService
import com.hfut.schedule.logic.network.api.OfficeHallService
import com.hfut.schedule.logic.network.api.PeService
import com.hfut.schedule.logic.network.api.StuService
import com.hfut.schedule.logic.network.api.TeachersService
import com.hfut.schedule.logic.network.api.WorkService
import com.hfut.schedule.logic.network.api.ZhiJianService
import com.hfut.schedule.logic.network.util.launchRequestState
import com.hfut.schedule.logic.network.servicecreator.AdmissionServiceCreator
import com.hfut.schedule.logic.network.servicecreator.DormitoryScoreServiceCreator
import com.hfut.schedule.logic.network.servicecreator.HaiLeWashingServiceCreator
import com.hfut.schedule.logic.network.servicecreator.OfficeHallServiceCreator
import com.hfut.schedule.logic.network.servicecreator.PeServiceCreator
import com.hfut.schedule.logic.network.servicecreator.StuServiceCreator
import com.hfut.schedule.logic.network.servicecreator.TeacherServiceCreator
import com.hfut.schedule.logic.network.servicecreator.WorkServiceCreator
import com.hfut.schedule.logic.network.servicecreator.ZhiJianServiceCreator
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.ui.component.network.onListenStateHolderForNetwork
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import kotlinx.coroutines.flow.first
import retrofit2.awaitResponse
import java.time.LocalDate

// Repo迁移计划
object Repository {
    private val teacher = TeacherServiceCreator.create(TeachersService::class.java)
    private val workSearch = WorkServiceCreator.create(WorkService::class.java)
    private val xuanChengDormitory = DormitoryScoreServiceCreator.create(DormitoryScore::class.java)
    private val haiLe = HaiLeWashingServiceCreator.create(HaiLeWashingService::class.java)
    private val admission = AdmissionServiceCreator.create(AdmissionService::class.java)
    private val hall = OfficeHallServiceCreator.create(OfficeHallService::class.java)
    private val stu = StuServiceCreator.create(StuService::class.java)
    private val zhiJian = ZhiJianServiceCreator.create(ZhiJianService::class.java)
    private val pe = PeServiceCreator.create(PeService::class.java)



    suspend fun checkPeLogin(cookie : String,holder : StateHolder<Boolean>) = launchRequestState(
        holder = holder,
        request = { pe.checkLogin(cookie) },
        transformSuccess = { _,json -> parseCheckPeLogin(json) }
    )
    @JvmStatic
    private fun parseCheckPeLogin(json: String) : Boolean = try {
        json.contains("成功")
    } catch (e : Exception) { throw e }


    suspend fun getZhiJianCourses(studentId : String, mondayDate : String, token : String,holder : StateHolder<List<ZhiJianCourseItemDto>>) =
        launchRequestState(
            holder = holder,
            request = {
                zhiJian.getCourses(token, buildZhiJianJson(mondayDate, studentId))
            },
            transformSuccess = { _, json -> parseZhiJianCourses(json, mondayDate) }
        )
    @JvmStatic
    private fun buildZhiJianJson(date: String, idNumber: String): String {
        val map = mapOf(
            "date" to date,
            "id_number" to idNumber
        )
        return Gson().toJson(map)
    }
    @JvmStatic
    private fun parseZhiJianCourses(json : String,mondayDate : String) : List<ZhiJianCourseItemDto> = try {
        val gson = Gson()
        val root = gson.fromJson(json, ZhiJianCoursesResponse::class.java)
        val data = root.data

        // 提取 kbdata 字符串
        val rawStr = data.rawJsonString
        // 再把这个字符串解析成二维数组
        val listType = object : TypeToken<List<ZhiJianCourseItem>>() {}.type
        val rawData: List<ZhiJianCourseItem> = gson.fromJson(rawStr, listType)
        val monday = LocalDate.parse(mondayDate)
        val sunday = monday.plusDays(6)

        rawData.filter { item ->
            // mondayDate始终传周一YYYY-MM-DD
            // 过滤掉it.date大于周日的项目，即只允许mondayDate(周一)~本周日
            val d = LocalDate.parse(item.date)
            !d.isBefore(monday) && !d.isAfter(sunday)
        }.mapNotNull {
            it.toDto()
        }
    } catch (e : Exception) { throw e }

    suspend fun zhiJianCheckLogin(token : String,holder : StateHolder<Boolean>) =
        launchRequestState(
            holder = holder,
            request = { zhiJian.checkLogin(token) },
            transformSuccess = { _, json -> parseZhiJianCheckLogin(json) }
        )

    @JvmStatic
    private fun parseZhiJianCheckLogin(json : String) : Boolean = try {
        json.contains(getPersonInfo().studentId!!) || json.contains(getPersonInfo().name!!)
    } catch (e : Exception) { throw e }

    suspend fun checkStuLogin(cookie : String,checkStuLoginResp : StateHolder<Boolean>) =
        launchRequestState(
            request = { stu.checkLogin(cookie) },
            holder = checkStuLoginResp,
            transformSuccess = { _, json -> parseCheckStuLogin(json) }
        )

    @JvmStatic
    private fun parseCheckStuLogin(json : String) = try {
        val sId = getPersonInfo().studentId ?: throw Exception("无学号")
        json.contains(sId)
    } catch (e : Exception) { throw e }




    suspend fun officeHallSearch(
        text : String,
        page : Int,
        holder : StateHolder<List<OfficeHallSearchBean>>
    ) = launchRequestState(
        holder = holder,
        request = {
            hall.search(
                name = text,
                page = page
            )
        },
        transformSuccess = { _, json -> parseOfficeHallSearch(json) }
    )
    @JvmStatic
    private fun parseOfficeHallSearch(json : String) : List<OfficeHallSearchBean> = try {
        Gson().fromJson(json, OfficeHallSearchResponse::class.java).data.records
    } catch (e : Exception) { throw e }

    suspend fun searchTeacher(name: String = "", direction: String = "",teacherSearchData : StateHolder<TeacherResponse>) =
        launchRequestState(
            holder = teacherSearchData,
            request = {
                teacher.searchTeacher(
                    name = name,
                    direction = direction,
                )
            },
            transformSuccess = { _, json -> parseTeacherSearch(json) }
        )

    @JvmStatic
    private fun parseTeacherSearch(json : String) : TeacherResponse = try {
        Gson().fromJson(json, TeacherResponse::class.java)
    } catch (e : Exception) { throw e }


    suspend fun getAdmissionList(type : AdmissionType, holder : StateHolder<Pair<AdmissionType, Map<String, List<AdmissionMapBean>>>>) =
        launchRequestState(
            holder = holder,
            request = { admission.getList(type.type) },
            transformSuccess = { _, json -> parseAdmissionList(type, json) }
        )

    @JvmStatic
    private fun parseAdmissionList(type: AdmissionType, json : String) : Pair<AdmissionType, Map<String, List<AdmissionMapBean>>> = try {
        Pair(type, Gson().fromJson(json, AdmissionListResponse::class.java).data.list)
    } catch (e : Exception) { throw e }

    suspend fun getAdmissionDetail(type : AdmissionType, bean : AdmissionMapBean, region: String, holder : StateHolder<AdmissionDetailBean>, tokenHolder : StateHolder<AdmissionTokenResponse>) =
        onListenStateHolderForNetwork(tokenHolder, holder) { token ->
            launchRequestState(
                holder = holder,
                request = {
                    admission.getDetail(
                        type.type,
                        region,
                        bean.year,
                        bean.subject,
                        bean.campus,
                        bean.type,
                        MyApplication.Companion.ADMISSION_COOKIE_HEADER + token.cookie,
                        token.data
                    )
                },
                transformSuccess = { _, json -> parseAdmissionDetail(type, json) }
            )
        }

    @JvmStatic
    private fun parseAdmissionDetail(type : AdmissionType, json : String) : AdmissionDetailBean = try {
        when(type) {
            AdmissionType.HISTORY -> {
                val parsed = Gson().fromJson(json, AdmissionDetailResponseHistory::class.java)
                AdmissionDetailBean.History(parsed.data)
            }
            AdmissionType.PLAN -> {
                val parsed = Gson().fromJson(json, AdmissionDetailResponsePlan::class.java)
                AdmissionDetailBean.Plan(parsed.data)
            }
        }
    } catch (e : Exception) { throw e }



    suspend fun getAdmissionToken(holder : StateHolder<AdmissionTokenResponse>) =
        launchRequestState(
            holder = holder,
            request = {
                val state = holder.state.first()
                val cookie = if (state !is UiState.Success) {
                    ""
                } else {
                    MyApplication.Companion.ADMISSION_COOKIE_HEADER + state.data.cookie
                }
                admission.getToken(cookie = cookie)
            },
            transformSuccess = { _, json -> parseAdmissionToken(json) }
        )

    @JvmStatic
    private fun parseAdmissionToken(json : String) : AdmissionTokenResponse = try {
        Gson().fromJson(json, AdmissionTokenResponse::class.java)
    } catch (e : Exception) { throw e }



    suspend fun searchWorks(keyword: String?, page: Int = 1, type: Int, campus: CampusRegion, workSearchResult : StateHolder<WorkSearchResponse>) =
        launchRequestState(
            holder = workSearchResult,
            request = {
                workSearch.search(
                    keyword = keyword,
                    page = page,
                    type = type.let { if (it == 0) null else it },
                    token = "yxqqnn1700000" + if (campus == CampusRegion.XUANCHENG) "119" else "002"
                )
            },
            transformSuccess = { _, json -> parseWorkResponse(json) },
        )

    @JvmStatic
    private fun parseWorkResponse(resp : String): WorkSearchResponse = try {
        // 去掉前缀，提取 JSON 部分
        val jsonStr = resp.removePrefix("var __result = ").removeSuffix(";").trim()
        Gson().fromJson(jsonStr, WorkSearchResponse::class.java)
    } catch (e : Exception) { throw e }





    @JvmStatic
    private fun parseElectric(result : String) : String = try {
        if (result.contains("query_elec_roominfo")) {
            var msg = Gson().fromJson(result, SearchEleResponse::class.java).query_elec_roominfo.errmsg

            if(msg.contains("剩余金额"))
                formatDecimal(msg.substringAfter("剩余金额").substringAfter(":").toDouble(), 2)
            else
                throw Exception(msg)
        }
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    suspend fun searchDormitoryXuanCheng(code : String,dormitoryResult : StateHolder<List<XuanquResponse>>) =
        launchRequestState(
            holder = dormitoryResult,
            request = { xuanChengDormitory.search(code) },
            transformSuccess = { _, html -> parseDormitoryXuanCheng(html) }
        )

    @JvmStatic
    private fun parseDormitoryXuanCheng(html : String) : List<XuanquResponse> = try {
        // 定义一个正则表达式来匹配HTML标签
        val regex = """<td rowspan="(\d+)">(\d+)</td>\s*<td>(\d+)</td>\s*<td>(\d+)</td>\s*<td rowspan="\d+">(\d{4}-\d{2}-\d{2})</td>""".toRegex()

        val data = html.let {
            regex.findAll(it).map {
                XuanquResponse(score = it.groupValues[2].toInt(), date = it.groupValues[5])
            }.toList()
        }
        data
    }  catch (e : Exception) { throw e }


    suspend fun getHaiLeNear(bean : HaiLeNearPositionRequestDTO, holder : StateHolder<List<HaiLeNearPositionBean>>) =
        launchRequestState(
            holder = holder,
            request = { haiLe.getNearPlaces(bean.toRequestBody()) },
            transformSuccess = { _, json -> parseHaiLeNear(json) }
        )

    @JvmStatic
    private fun parseHaiLeNear(result: String): List<HaiLeNearPositionBean> = try {
        if(result.contains("success")) {
            Gson().fromJson(result, HaiLeNearPositionResponse::class.java).data.items
        } else {
            throw Exception(result)
        }
    } catch (e: Exception) { throw e }


    suspend fun getHaiLDeviceDetail(bean : HaiLeDeviceDetailRequestBody, holder : StateHolder<List<HaiLeDeviceDetailBean>>) =
        launchRequestState(
            holder = holder,
            request = { haiLe.getDeviceDetail(bean) },
            transformSuccess = { _, json -> parseHaiLeDeviceDetail(json) }
        )

    @JvmStatic
    private fun parseHaiLeDeviceDetail(result: String): List<HaiLeDeviceDetailBean> = try {
        if(result.contains("success")) {
            Gson().fromJson(result, HaiLeDeviceDetailResponse::class.java).data.items
        } else {
            throw Exception(result)
        }
    } catch (e: Exception) { throw e }
}