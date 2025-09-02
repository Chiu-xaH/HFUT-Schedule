package com.hfut.schedule.logic.network.repo

import com.google.gson.Gson
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.application.MyApplication.Companion.ADMISSION_COOKIE_HEADER
import com.hfut.schedule.logic.enumeration.AdmissionType
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.model.AdmissionDetailBean
import com.hfut.schedule.logic.model.AdmissionDetailResponseHistory
import com.hfut.schedule.logic.model.AdmissionDetailResponsePlan
import com.hfut.schedule.logic.model.AdmissionListResponse
import com.hfut.schedule.logic.model.AdmissionMapBean
import com.hfut.schedule.logic.model.AdmissionTokenResponse
import com.hfut.schedule.logic.model.ForecastAllBean
import com.hfut.schedule.logic.model.ForecastResponse
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
import com.hfut.schedule.logic.model.toVercelForecastRequestBody
import com.hfut.schedule.logic.model.zjgd.BillBean
import com.hfut.schedule.logic.network.StatusCode
import com.hfut.schedule.logic.network.api.AdmissionService
import com.hfut.schedule.logic.network.api.DormitoryScore
import com.hfut.schedule.logic.network.api.HaiLeWashingService
import com.hfut.schedule.logic.network.api.OfficeHallService
import com.hfut.schedule.logic.network.api.TeachersService
import com.hfut.schedule.logic.network.api.VercelForecastService
import com.hfut.schedule.logic.network.api.WorkService
import com.hfut.schedule.logic.network.servicecreator.AdmissionServiceCreator
import com.hfut.schedule.logic.network.servicecreator.DormitoryScoreServiceCreator
import com.hfut.schedule.logic.network.servicecreator.HaiLeWashingServiceCreator
import com.hfut.schedule.logic.network.servicecreator.OfficeHallServiceCreator
import com.hfut.schedule.logic.network.servicecreator.TeacherServiceCreator
import com.hfut.schedule.logic.network.servicecreator.VercelForecastServiceCreator
import com.hfut.schedule.logic.network.servicecreator.WorkServiceCreator
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.component.network.onListenStateHolderForNetwork
import kotlinx.coroutines.flow.first
import retrofit2.awaitResponse

// Repo迁移计划
object Repository {
    private val teacher = TeacherServiceCreator.create(TeachersService::class.java)
    private val workSearch = WorkServiceCreator.create(WorkService::class.java)
    private val xuanChengDormitory = DormitoryScoreServiceCreator.create(DormitoryScore::class.java)
    private val forecast = VercelForecastServiceCreator.create(VercelForecastService::class.java)
    private val haiLe = HaiLeWashingServiceCreator.create(HaiLeWashingService::class.java)
    private val admission = AdmissionServiceCreator.create(AdmissionService::class.java)
    private val hall = OfficeHallServiceCreator.create(OfficeHallService::class.java)


    suspend fun officeHallSearch(
        text : String,
        page : Int,
        holder : StateHolder<List<OfficeHallSearchBean>>
    ) = launchRequestSimple(
        holder = holder,
        request = { hall.search(
            name = text,
            page = page,
            pageSize = prefs.getString("OfficeHallRequest",MyApplication.DEFAULT_PAGE_SIZE.toString())?.toInt() ?: MyApplication.DEFAULT_PAGE_SIZE,
        ).awaitResponse() },
        transformSuccess = { _,json -> parseOfficeHallSearch(json) }
    )
    @JvmStatic
    private fun parseOfficeHallSearch(json : String) : List<OfficeHallSearchBean> = try {
        Gson().fromJson(json, OfficeHallSearchResponse::class.java).data.records
    } catch (e : Exception) { throw e }

    suspend fun searchTeacher(name: String = "", direction: String = "",teacherSearchData : StateHolder<TeacherResponse>) = launchRequestSimple(
        holder = teacherSearchData,
        request = { teacher.searchTeacher(name=name, direction = direction, size = prefs.getString("TeacherSearchRequest",MyApplication.DEFAULT_PAGE_SIZE.toString()) ?: MyApplication.DEFAULT_PAGE_SIZE.toString() ).awaitResponse() },
        transformSuccess = { _,json -> parseTeacherSearch(json) }
    )

    @JvmStatic
    private fun parseTeacherSearch(json : String) : TeacherResponse = try {
        Gson().fromJson(json, TeacherResponse::class.java)
    } catch (e : Exception) { throw e }


    suspend fun getAdmissionList(type : AdmissionType,holder : StateHolder<Pair<AdmissionType,Map<String, List<AdmissionMapBean>>>>) = launchRequestSimple(
        holder = holder,
        request = {  admission.getList(type.type).awaitResponse() },
        transformSuccess = { _,json -> parseAdmissionList(type,json) }
    )

    @JvmStatic
    private fun parseAdmissionList(type: AdmissionType,json : String) : Pair<AdmissionType, Map<String, List<AdmissionMapBean>>> = try {
        Pair(type,Gson().fromJson(json, AdmissionListResponse::class.java).data.list)
    } catch (e : Exception) { throw e }

    suspend fun getAdmissionDetail(type : AdmissionType,bean : AdmissionMapBean,region: String,holder : StateHolder<AdmissionDetailBean>,tokenHolder : StateHolder<AdmissionTokenResponse>) =
        onListenStateHolderForNetwork(tokenHolder,holder) { token ->
            launchRequestSimple(
                holder = holder,
                request = {  admission.getDetail(type.type,region,bean.year,bean.subject,bean.campus,bean.type,ADMISSION_COOKIE_HEADER + token.cookie,token.data).awaitResponse() },
                transformSuccess = { _,json -> parseAdmissionDetail(type,json) }
            )
        }

    @JvmStatic
    private fun parseAdmissionDetail(type : AdmissionType,json : String) : AdmissionDetailBean = try {
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



    suspend fun getAdmissionToken(holder : StateHolder<AdmissionTokenResponse>) = launchRequestSimple(
        holder = holder,
        request = {
            val state = holder.state.first()
            val cookie = if(state !is UiState.Success) {
                ""
            } else {
                ADMISSION_COOKIE_HEADER + state.data.cookie
            }
            admission.getToken(cookie = cookie).awaitResponse()
        },
        transformSuccess = { _,json -> parseAdmissionToken(json) }
    )

    @JvmStatic
    private fun parseAdmissionToken(json : String) : AdmissionTokenResponse = try {
        Gson().fromJson(json, AdmissionTokenResponse::class.java)
    } catch (e : Exception) { throw e }



    suspend fun searchWorks(keyword: String?, page: Int = 1, type: Int, campus: CampusRegion, workSearchResult : StateHolder<WorkSearchResponse>) = launchRequestSimple(
        holder = workSearchResult,
        request = {
            workSearch.search(
                keyword = keyword,
                page = page,
                pageSize = prefs.getString("WorkSearchRequest",MyApplication.DEFAULT_PAGE_SIZE.toString())?.toIntOrNull() ?: MyApplication.DEFAULT_PAGE_SIZE,
                type = type.let { if(it == 0) null else it },
                token = "yxqqnn1700000" + if(campus == CampusRegion.XUANCHENG) "119" else "002"
            ).awaitResponse() },
        transformSuccess = { _, json -> parseWorkResponse(json) },
    )

    @JvmStatic
    private fun parseWorkResponse(resp : String): WorkSearchResponse = try {
        // 去掉前缀，提取 JSON 部分
        val jsonStr = resp.removePrefix("var __result = ").removeSuffix(";").trim()
        Gson().fromJson(jsonStr,WorkSearchResponse::class.java)
    } catch (e : Exception) { throw e }





    @JvmStatic
    private fun parseElectric(result : String) : String = try {
        if (result.contains("query_elec_roominfo")) {
            var msg = Gson().fromJson(result, SearchEleResponse::class.java).query_elec_roominfo.errmsg

            if(msg.contains("剩余金额"))
                formatDecimal(msg.substringAfter("剩余金额").substringAfter(":").toDouble(),2)
            else
                throw Exception(msg)
        }
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    suspend fun searchDormitoryXuanCheng(code : String,dormitoryResult : StateHolder<List<XuanquResponse>>) = launchRequestSimple(
        holder = dormitoryResult,
        request = { xuanChengDormitory.search(code).awaitResponse() },
        transformSuccess = { _,html -> parseDormitoryXuanCheng(html) }
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


    suspend fun getHaiLeNear(bean : HaiLeNearPositionRequestDTO,holder : StateHolder<List<HaiLeNearPositionBean>>) = launchRequestSimple(
        holder = holder,
        request = { haiLe.getNearPlaces(bean.toRequestBody()).awaitResponse() },
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


    suspend fun getHaiLDeviceDetail(bean : HaiLeDeviceDetailRequestBody,holder : StateHolder<List<HaiLeDeviceDetailBean>>) = launchRequestSimple(
        holder = holder,
        request = { haiLe.getDeviceDetail(bean).awaitResponse() },
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

    suspend fun getCardPredicted(bean : BillBean,cardPredictedData : StateHolder<ForecastAllBean>) = launchRequestSimple(
        holder = cardPredictedData,
        request = { forecast.getData(toVercelForecastRequestBody(bean)).awaitResponse() },
        transformSuccess = { _,json -> parseCardPredicted(json) }
    )

    @JvmStatic
    private fun parseCardPredicted(json : String) : ForecastAllBean = try {
        val data = Gson().fromJson(json,ForecastResponse::class.java)
        if(data.state == StatusCode.OK.code) {
            data.data
        } else {
            throw Exception(data.msg ?: "错误")
        }
    } catch (e : Exception) { throw e }
}