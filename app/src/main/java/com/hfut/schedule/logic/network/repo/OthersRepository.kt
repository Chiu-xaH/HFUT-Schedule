package com.hfut.schedule.logic.network.repo


import com.google.gson.reflect.TypeToken
import com.hfut.schedule.logic.model.enumeration.AdmissionType
import com.xah.common.logic.model.CampusRegion
import com.hfut.schedule.network.api.model.response.json.admission.AdmissionDetailBean
import com.hfut.schedule.network.api.model.response.json.admission.AdmissionDetailHistoryResponse
import com.hfut.schedule.network.api.model.response.json.admission.AdmissionDetailPlanResponse
import com.hfut.schedule.network.api.model.response.json.admission.AdmissionListResponse
import com.hfut.schedule.network.api.model.response.json.admission.Admission
import com.hfut.schedule.network.api.model.response.json.admission.AdmissionTokenResponse
import com.hfut.schedule.network.api.model.response.html.Department
import com.hfut.schedule.network.api.model.response.json.haile.HaiLeDeviceDetailBean
import com.hfut.schedule.network.api.model.response.json.haile.HaiLeDeviceDetailResponse
import com.hfut.schedule.network.api.model.response.json.haile.HaiLeNearPositionBean
import com.hfut.schedule.logic.model.dto.HaiLeNearPositionRequestDto
import com.hfut.schedule.network.api.model.response.json.haile.HaiLeNearPositionResponse
import com.hfut.schedule.network.api.model.response.json.zhijian.ZhiJianMsgResponse
import com.hfut.schedule.network.api.model.response.json.hall.OfficeHallSearchRecord
import com.hfut.schedule.network.api.model.response.json.hall.OfficeHallSearchResponse
import com.hfut.schedule.network.api.model.response.json.huixin.OldElectricResponse
import com.hfut.schedule.network.api.model.response.json.second.SecondClassActivitiesResponse
import com.hfut.schedule.network.api.model.response.json.second.SecondClassActivity
import com.hfut.schedule.network.api.model.response.json.teacher.TeacherResponse
import com.hfut.schedule.network.api.model.response.json.work.WorkSearchResponse
import com.hfut.schedule.network.api.model.response.html.OldDormitoryXuanCheng
import com.hfut.schedule.network.api.model.response.json.zhijian.ZhiJianCourseTable
import com.hfut.schedule.logic.model.dto.ZhiJianCourseTableDto
import com.hfut.schedule.logic.model.dto.toDto
import com.hfut.schedule.network.api.model.response.json.zhijian.ZhiJianCourseTableResponse
import com.hfut.schedule.logic.util.network.launchRequestState
import com.xah.common.logic.state.UiStateHolder
import com.xah.common.logic.state.NetworkUiState

import com.hfut.schedule.logic.util.parse.roundOffString
import com.hfut.schedule.network.api.impl.AdmissionServiceCreator
import com.hfut.schedule.network.api.impl.DormitoryScoreServiceCreator
import com.hfut.schedule.network.api.impl.HaiLeWashingServiceCreator
import com.hfut.schedule.network.api.impl.HfutServiceCreator
import com.hfut.schedule.network.api.impl.OfficeHallServiceCreator
import com.hfut.schedule.network.api.impl.PeServiceCreator
import com.hfut.schedule.network.api.impl.SecondClassServiceCreator
import com.hfut.schedule.network.api.impl.StuServiceCreator
import com.hfut.schedule.network.api.impl.TeacherServiceCreator
import com.hfut.schedule.network.api.impl.WorkServiceCreator
import com.hfut.schedule.network.api.impl.ZhiJianServiceCreator
import com.hfut.schedule.network.api.inf.AdmissionService
import com.hfut.schedule.network.api.inf.DormitoryScore
import com.hfut.schedule.network.api.inf.HaiLeWashingService
import com.hfut.schedule.network.api.inf.HfutService
import com.hfut.schedule.network.api.inf.OfficeHallService
import com.hfut.schedule.network.api.inf.PeService
import com.hfut.schedule.network.api.inf.SecondClassService
import com.hfut.schedule.network.api.inf.StuService
import com.hfut.schedule.network.api.inf.TeachersService
import com.hfut.schedule.network.api.inf.WorkService
import com.hfut.schedule.network.api.inf.ZhiJianService
import com.hfut.schedule.network.api.model.request.haile.HaiLeDeviceDetailRequest
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.network.core.GsonInstance
import com.hfut.schedule.ui.component.network.onListenStateHolderForNetwork
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import kotlinx.coroutines.flow.first
import org.jsoup.Jsoup
import java.time.LocalDate

// Repo迁移计划
object OthersRepository {
    private val teacher = TeacherServiceCreator.create(TeachersService::class.java)
    private val workSearch = WorkServiceCreator.create(WorkService::class.java)
    private val xuanChengDormitory = DormitoryScoreServiceCreator.create(DormitoryScore::class.java)
    private val haiLe = HaiLeWashingServiceCreator.create(HaiLeWashingService::class.java)
    private val admission = AdmissionServiceCreator.create(AdmissionService::class.java)
    private val hall = OfficeHallServiceCreator.create(OfficeHallService::class.java)
    private val stu = StuServiceCreator.create(StuService::class.java)
    private val zhiJian = ZhiJianServiceCreator.create(ZhiJianService::class.java)
    private val pe = PeServiceCreator.create(PeService::class.java)
    private val secondClass = SecondClassServiceCreator.create(SecondClassService::class.java)
    private val hfut = HfutServiceCreator.create(HfutService::class.java)

    suspend fun checkPeLogin(cookie : String,holder : UiStateHolder<Boolean>) = launchRequestState(
        holder = holder,
        request = { pe.checkLogin(cookie) },
        transformSuccess = { _, json -> parseCheckPeLogin(json) }
    )
    @JvmStatic
    private fun parseCheckPeLogin(json: String) : Boolean = try {
        json.contains("成功")
    } catch (e : Exception) { throw e }


    suspend fun checkSecondClassLogin(cookie : String,holder : UiStateHolder<Boolean>) = launchRequestState(
        holder = holder,
        request = { secondClass.checkLogin(cookie) },
        transformSuccess = { _, json -> parseCheckSecondClassLogin(json) }
    )
    @JvmStatic
    private fun parseCheckSecondClassLogin(json: String) : Boolean = try {
        val name = getPersonInfo().name ?: return false
        json.contains(name)
    } catch (e : Exception) { throw e }


    suspend fun getZhiJianCourses(studentId : String, mondayDate : String, token : String,holder : UiStateHolder<List<ZhiJianCourseTableDto>>) =
        launchRequestState(
            holder = holder,
            request = {
                zhiJian.getCourses(token, buildZhiJianJson(mondayDate, studentId))
            },
            transformSuccess = m@{ _, json ->
                if (json.contains("false")) {
                    val root = try {
                        GsonInstance.fromJson(json, ZhiJianMsgResponse::class.java).msg ?: json
                    } catch (e: Exception) {
                        throw e
                    }
                    val e = Exception(root)
                    throw Exception(e)
                }
                parseZhiJianCourses(json, mondayDate)
            }
        )
    @JvmStatic
    private fun buildZhiJianJson(date: String, idNumber: String): String {
        val map = mapOf(
            "date" to date,
            "id_number" to idNumber
        )
        return GsonInstance.toJson(map)
    }
    @JvmStatic
    private fun parseZhiJianCourses(json : String,mondayDate : String) : List<ZhiJianCourseTableDto> = try {
        val gson = GsonInstance
        val root = gson.fromJson(json, ZhiJianCourseTableResponse::class.java)
        val data = root.data

        // 提取 kbdata 字符串
        val rawStr = data.rawJsonString
        // 再把这个字符串解析成二维数组
        val listType = object : TypeToken<List<ZhiJianCourseTable>>() {}.type
        val rawData: List<ZhiJianCourseTable> = gson.fromJson(rawStr, listType)
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

    suspend fun zhiJianCheckLogin(token : String,holder : UiStateHolder<Boolean>) =
        launchRequestState(
            holder = holder,
            request = { zhiJian.checkLogin(token) },
            transformSuccess = { _, json -> parseZhiJianCheckLogin(json) }
        )

    @JvmStatic
    private fun parseZhiJianCheckLogin(json : String) : Boolean = try {
        json.contains(getPersonInfo().getStudentIdFinally()!!) || json.contains(getPersonInfo().name!!)
    } catch (e : Exception) { throw e }

    suspend fun checkStuLogin(cookie : String,checkStuLoginResp : UiStateHolder<Boolean>) =
        launchRequestState(
            request = { stu.checkLogin(cookie) },
            holder = checkStuLoginResp,
            transformSuccess = { _, json -> parseCheckStuLogin(json) }
        )

    @JvmStatic
    private fun parseCheckStuLogin(json : String) = try {
        val sId = getPersonInfo().getStudentIdFinally() ?: throw Exception("无学号")
        json.contains(sId)
    } catch (e : Exception) { throw e }


    suspend fun officeHallSearch(
        text : String,
        page : Int,
        holder : UiStateHolder<List<OfficeHallSearchRecord>>
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
    private fun parseOfficeHallSearch(json : String) : List<OfficeHallSearchRecord> = try {
        GsonInstance.fromJson(json, OfficeHallSearchResponse::class.java).data.records
    } catch (e : Exception) { throw e }

    suspend fun searchTeacher(name: String = "", direction: String = "",teacherSearchData : UiStateHolder<TeacherResponse>) =
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
        GsonInstance.fromJson(json, TeacherResponse::class.java)
    } catch (e : Exception) { throw e }


    suspend fun getAdmissionList(type : AdmissionType, holder : UiStateHolder<Pair<AdmissionType, Map<String, List<Admission>>>>) =
        launchRequestState(
            holder = holder,
            request = { admission.getList(type.type) },
            transformSuccess = { _, json -> parseAdmissionList(type, json) }
        )

    @JvmStatic
    private fun parseAdmissionList(type: AdmissionType, json : String) : Pair<AdmissionType, Map<String, List<Admission>>> = try {
        Pair(type, GsonInstance.fromJson(json, AdmissionListResponse::class.java).data.map)
    } catch (e : Exception) { throw e }

    suspend fun getAdmissionDetail(type : AdmissionType, bean : Admission, region: String, holder : UiStateHolder<AdmissionDetailBean>, tokenHolder : UiStateHolder<AdmissionTokenResponse>) =
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
                        Constant.ADMISSION_COOKIE_HEADER + token.cookie,
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
                val parsed = GsonInstance.fromJson(json, AdmissionDetailHistoryResponse::class.java)
                AdmissionDetailBean.History(parsed.data)
            }
            AdmissionType.PLAN -> {
                val parsed = GsonInstance.fromJson(json, AdmissionDetailPlanResponse::class.java)
                AdmissionDetailBean.Plan(parsed.data)
            }
        }
    } catch (e : Exception) { throw e }



    suspend fun getAdmissionToken(holder : UiStateHolder<AdmissionTokenResponse>) =
        launchRequestState(
            holder = holder,
            request = {
                val state = holder.state.first()
                val cookie = if (state !is NetworkUiState.Success) {
                    ""
                } else {
                    Constant.ADMISSION_COOKIE_HEADER + state.data.cookie
                }
                admission.getToken(cookie = cookie)
            },
            transformSuccess = { _, json -> parseAdmissionToken(json) }
        )

    @JvmStatic
    private fun parseAdmissionToken(json : String) : AdmissionTokenResponse = try {
        GsonInstance.fromJson(json, AdmissionTokenResponse::class.java)
    } catch (e : Exception) { throw e }



    suspend fun searchWorks(keyword: String?, page: Int = 1, type: Int, campus: CampusRegion, workSearchResult : UiStateHolder<WorkSearchResponse>) =
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
        GsonInstance.fromJson(jsonStr, WorkSearchResponse::class.java)
    } catch (e : Exception) { throw e }





    @JvmStatic
    private fun parseElectric(result : String) : String = try {
        if (result.contains("query_elec_roominfo")) {
            val msg = GsonInstance.fromJson(result, OldElectricResponse::class.java).roomInfo.msg

            if(msg.contains("剩余金额"))
                msg.substringAfter("剩余金额").substringAfter(":").toDouble().roundOffString(2)
            else
                throw Exception(msg)
        }
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    suspend fun searchDormitoryXuanCheng(code : String,dormitoryResult : UiStateHolder<List<OldDormitoryXuanCheng>>) =
        launchRequestState(
            holder = dormitoryResult,
            request = { xuanChengDormitory.search(code) },
            transformSuccess = { _, html -> parseDormitoryXuanCheng(html) }
        )

    @JvmStatic
    private fun parseDormitoryXuanCheng(html : String) : List<OldDormitoryXuanCheng> = try {
        // 定义一个正则表达式来匹配HTML标签
        val regex = """<td rowspan="(\d+)">(\d+)</td>\s*<td>(\d+)</td>\s*<td>(\d+)</td>\s*<td rowspan="\d+">(\d{4}-\d{2}-\d{2})</td>""".toRegex()

        val data = html.let {
            regex.findAll(it).map {
                OldDormitoryXuanCheng(score = it.groupValues[2].toInt(), date = it.groupValues[5])
            }.toList()
        }
        data
    }  catch (e : Exception) { throw e }


    suspend fun getHaiLeNear(bean : HaiLeNearPositionRequestDto, holder : UiStateHolder<List<HaiLeNearPositionBean>>) =
        launchRequestState(
            holder = holder,
            request = { haiLe.getNearPlaces(bean.toRequestBody()) },
            transformSuccess = { _, json -> parseHaiLeNear(json) }
        )

    @JvmStatic
    private fun parseHaiLeNear(result: String): List<HaiLeNearPositionBean> = try {
        if(result.contains("success")) {
            GsonInstance.fromJson(result, HaiLeNearPositionResponse::class.java).data.items
        } else {
            throw Exception(result)
        }
    } catch (e: Exception) { throw e }


    suspend fun getHaiLDeviceDetail(bean : HaiLeDeviceDetailRequest, holder : UiStateHolder<List<HaiLeDeviceDetailBean>>) =
        launchRequestState(
            holder = holder,
            request = { haiLe.getDeviceDetail(bean) },
            transformSuccess = { _, json -> parseHaiLeDeviceDetail(json) }
        )

    @JvmStatic
    private fun parseHaiLeDeviceDetail(result: String): List<HaiLeDeviceDetailBean> = try {
        if(result.contains("success")) {
            GsonInstance.fromJson(result, HaiLeDeviceDetailResponse::class.java).data.items
        } else {
            throw Exception(result)
        }
    } catch (e: Exception) { throw e }

    suspend fun getSecondClassActivities(
        cookie: String,
        page: Int = 1,
        holder : UiStateHolder<List<SecondClassActivity>>
    ) = launchRequestState(
            holder = holder,
            request = { secondClass.getActivities(cookie,page) },
            transformRedirect = { throw Exception("登陆状态失效") },
            transformSuccess = { _, json -> parseSecondClassActivities(json) }
        )

    @JvmStatic
    private fun parseSecondClassActivities(result: String): List<SecondClassActivity> = try {
        if(result.startsWith("<!DOCTYPE html>")) {
            throw Exception("登录状态失效")
        }
        val data = GsonInstance.fromJson(result, SecondClassActivitiesResponse::class.java)
        data.list
    } catch (e: Exception) { throw e }


    suspend fun getDepartments(holder : UiStateHolder<List<Department>>) =
        launchRequestState(
            holder = holder,
            request = { hfut.getDepartments() },
            transformSuccess = { _, html -> parseDepartments(html) }
        )

    @JvmStatic
    private fun parseDepartments(html : String) : List<Department> = try {
        val document = Jsoup.parse(html)

        document
            .select("ul.sz-list > li")
            .mapNotNull { li ->
                val a = li.selectFirst("a") ?: return@mapNotNull null

                val name = li
                    .selectFirst(".sz-list-tt")
                    ?.text()
                    ?.trim()
                    .orEmpty()

                val website = a.attr("href").trim()

                val rawIcon = li
                    .selectFirst("font img")
                    ?.attr("src")
                    ?.trim()
                    .orEmpty()

                val iconUrl = rawIcon.removePrefix("../")

                Department(
                    name = name,
                    url = website,
                    iconUrl = Constant.HFUT_URL + iconUrl
                )
            }

    } catch (e : Exception) { throw e }
}