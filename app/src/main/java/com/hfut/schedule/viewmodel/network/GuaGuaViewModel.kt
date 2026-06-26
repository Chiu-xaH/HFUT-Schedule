package com.hfut.schedule.viewmodel.network

import androidx.lifecycle.ViewModel
import com.hfut.schedule.logic.model.guagua.GuaGuaLoginResponse
import com.hfut.schedule.logic.model.guagua.GuaguaBillsResponse
import com.hfut.schedule.logic.network.repo.GuaGuaRepository
import com.xah.common.logic.state.UiStateHolder

@Deprecated("为KMP适配计划的开始做铺垫，即将被合入至`NetworkViewModel`统一管理")
class GuaGuaViewModel : ViewModel() {
    var loginResult = UiStateHolder<GuaGuaLoginResponse>()
    suspend fun login(phoneNumber : String, password : String) = GuaGuaRepository.guaGuaLogin(phoneNumber,password,loginResult)

    val startShowerResult = UiStateHolder<String>()
    suspend fun startShower(phoneNumber: String, macLocation : String, loginCode : String) = GuaGuaRepository.guaGuaStartShower(phoneNumber,macLocation,loginCode,startShowerResult)

    var billsResult = UiStateHolder<GuaguaBillsResponse>()
    suspend fun getBills() = GuaGuaRepository.guaGuaGetBills(billsResult)

    var useCodeResult = UiStateHolder<String>()
    suspend fun getUseCode() = GuaGuaRepository.guaGuaGetUseCode(useCodeResult)

    var reSetCodeResult = UiStateHolder<String>()
    suspend fun reSetUseCode(newCode : String) = GuaGuaRepository.guaGuaReSetUseCode(newCode,reSetCodeResult)
}



