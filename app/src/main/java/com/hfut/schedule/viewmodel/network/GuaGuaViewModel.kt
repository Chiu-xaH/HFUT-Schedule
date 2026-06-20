package com.hfut.schedule.viewmodel.network

import androidx.lifecycle.ViewModel
import com.hfut.schedule.logic.model.guagua.GuaGuaLoginResponse
import com.hfut.schedule.logic.model.guagua.GuaguaBillsResponse
import com.hfut.schedule.logic.network.repo.GuaGuaRepository
import com.hfut.schedule.logic.network.repo.OthersRepository
import com.hfut.schedule.logic.util.network.state.StateHolder

@Deprecated("为KMP适配计划的开始做铺垫，即将被合入至`NetworkViewModel`统一管理")
class GuaGuaViewModel : ViewModel() {
    var loginResult = StateHolder<GuaGuaLoginResponse>()
    suspend fun login(phoneNumber : String, password : String) = GuaGuaRepository.guaGuaLogin(phoneNumber,password,loginResult)

    val startShowerResult = StateHolder<String>()
    suspend fun startShower(phoneNumber: String, macLocation : String, loginCode : String) = GuaGuaRepository.guaGuaStartShower(phoneNumber,macLocation,loginCode,startShowerResult)

    var billsResult = StateHolder<GuaguaBillsResponse>()
    suspend fun getBills() = GuaGuaRepository.guaGuaGetBills(billsResult)

    var useCodeResult = StateHolder<String>()
    suspend fun getUseCode() = GuaGuaRepository.guaGuaGetUseCode(useCodeResult)

    var reSetCodeResult = StateHolder<String>()
    suspend fun reSetUseCode(newCode : String) = GuaGuaRepository.guaGuaReSetUseCode(newCode,reSetCodeResult)
}



