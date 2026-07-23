package com.hfut.schedule.network.api.repo

import com.hfut.schedule.network.api.model.response.json.guagua.GuaGuaBillResponse
import com.hfut.schedule.network.api.model.response.json.guagua.GuaGuaLoginResponse
import com.xah.common.logic.state.UiStateHolder

interface GuaGuaRepositoryInf {
    /*
    fun getGuaGuaUserInfo(guaGuaUserInfo : MutableLiveData<String?>)
     */
    suspend fun guaGuaLogin(phoneNumber : String, password : String,loginResult : UiStateHolder<GuaGuaLoginResponse>)
    suspend fun guaGuaStartShower(phoneNumber: String, macLocation : String, loginCode : String,startShowerResult : UiStateHolder<String>)
    suspend fun guaGuaGetBills(billsResult : UiStateHolder<GuaGuaBillResponse>)
    suspend fun guaGuaGetUseCode(useCodeResult : UiStateHolder<String>)
    suspend fun guaGuaReSetUseCode(newCode : String,reSetCodeResult : UiStateHolder<String>)
}