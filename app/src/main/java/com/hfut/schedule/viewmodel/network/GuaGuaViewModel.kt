package com.hfut.schedule.viewmodel.network

import androidx.lifecycle.ViewModel
import com.hfut.schedule.logic.model.guagua.GuaGuaLoginResponse
import com.hfut.schedule.logic.model.guagua.GuaguaBillsResponse
import com.hfut.schedule.logic.network.repo.Repository
import com.hfut.schedule.logic.util.network.StateHolder

// 5个函数
class GuaGuaViewModel : ViewModel() {
    var loginResult = StateHolder<GuaGuaLoginResponse>()
    suspend fun login(phoneNumber : String, password : String) = Repository.guaGuaLogin(phoneNumber,password,loginResult)

    val startShowerResult = StateHolder<String>()
    suspend fun startShower(phoneNumber: String, macLocation : String, loginCode : String) = Repository.guaGuaStartShower(phoneNumber,macLocation,loginCode,startShowerResult)

    var billsResult = StateHolder<GuaguaBillsResponse>()
    suspend fun getBills() = Repository.guaGuaGetBills(billsResult)

    var useCodeResult = StateHolder<String>()
    suspend fun getUseCode() = Repository.guaGuaGetUseCode(useCodeResult)

    var reSetCodeResult = StateHolder<String>()
    suspend fun reSetUseCode(newCode : String) = Repository.guaGuaReSetUseCode(newCode,reSetCodeResult)
}



