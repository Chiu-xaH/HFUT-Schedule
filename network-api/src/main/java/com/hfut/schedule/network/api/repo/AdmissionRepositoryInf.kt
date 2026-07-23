package com.hfut.schedule.network.api.repo

import com.hfut.schedule.network.api.model.request.admission.AdmissionType
import com.hfut.schedule.network.api.model.response.json.admission.Admission
import com.hfut.schedule.network.api.model.response.json.admission.AdmissionDetailBean
import com.hfut.schedule.network.api.model.response.json.admission.AdmissionTokenResponse
import com.xah.common.logic.state.UiStateHolder

interface AdmissionRepositoryInf {
    suspend fun getAdmissionList(type : AdmissionType, holder : UiStateHolder<Pair<AdmissionType, Map<String, List<Admission>>>>)
    suspend fun getAdmissionDetail(type : AdmissionType, bean : Admission, region: String, holder : UiStateHolder<AdmissionDetailBean>, tokenHolder : UiStateHolder<AdmissionTokenResponse>) : Unit?
    suspend fun getAdmissionToken(holder : UiStateHolder<AdmissionTokenResponse>)
}