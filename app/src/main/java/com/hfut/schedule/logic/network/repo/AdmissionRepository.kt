package com.hfut.schedule.logic.network.repo

import com.hfut.schedule.network.api.model.request.admission.AdmissionType
import com.hfut.schedule.logic.util.network.launchRequestState
import com.hfut.schedule.network.api.impl.AdmissionServiceCreator
import com.hfut.schedule.network.api.inf.AdmissionService
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.network.api.model.response.json.admission.Admission
import com.hfut.schedule.network.api.model.response.json.admission.AdmissionDetailBean
import com.hfut.schedule.network.api.model.response.json.admission.AdmissionDetailHistoryResponse
import com.hfut.schedule.network.api.model.response.json.admission.AdmissionDetailPlanResponse
import com.hfut.schedule.network.api.model.response.json.admission.AdmissionListResponse
import com.hfut.schedule.network.api.model.response.json.admission.AdmissionTokenResponse
import com.hfut.schedule.network.api.repo.AdmissionRepositoryInf
import com.hfut.schedule.network.core.GsonInstance
import com.hfut.schedule.ui.component.network.onListenStateHolderForNetwork
import com.xah.common.logic.state.NetworkUiState
import com.xah.common.logic.state.UiStateHolder
import kotlinx.coroutines.flow.first

object AdmissionRepository : AdmissionRepositoryInf {
    private val admission = AdmissionServiceCreator.create(AdmissionService::class.java)

    override suspend fun getAdmissionList(type : AdmissionType, holder : UiStateHolder<Pair<AdmissionType, Map<String, List<Admission>>>>) =
        launchRequestState(
            holder = holder,
            request = { admission.getList(type.type) },
            transformSuccess = { _, json -> parseAdmissionList(type, json) }
        )

    @JvmStatic
    private fun parseAdmissionList(type: AdmissionType, json : String) : Pair<AdmissionType, Map<String, List<Admission>>> = try {
        Pair(type, GsonInstance.fromJson(json, AdmissionListResponse::class.java).data.map)
    } catch (e : Exception) { throw e }

    override suspend fun getAdmissionDetail(type : AdmissionType, bean : Admission, region: String, holder : UiStateHolder<AdmissionDetailBean>, tokenHolder : UiStateHolder<AdmissionTokenResponse>) =
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



    override suspend fun getAdmissionToken(holder : UiStateHolder<AdmissionTokenResponse>) =
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

}