package com.hfut.schedule.viewmodel.network

import androidx.lifecycle.ViewModel
import com.hfut.schedule.logic.model.xwx.XwxFunction
import com.hfut.schedule.logic.model.xwx.XwxSchoolBean
import com.hfut.schedule.logic.network.repo.XwxRepository
import com.hfut.schedule.logic.util.network.state.StateHolder

class XwxViewModel : ViewModel() {
    val schoolListResp = StateHolder<List<XwxSchoolBean>>()
    suspend fun getSchoolList() = XwxRepository.getSchoolList(schoolListResp)

    val loginResp = StateHolder<Boolean>()
    suspend fun login(
        schoolCode : Long,
        username : String,
        password : String,
    ) = XwxRepository.login(schoolCode,username,password,loginResp)

    val functionsResp = StateHolder<List<XwxFunction>>()
    suspend fun getFunctions(
        schoolCode : Long,
        username : String,
        token : String,
    ) = XwxRepository.getFunctions(schoolCode,username,token,functionsResp)

    val docPreviewResp = StateHolder<String>()
    suspend fun getDocPreview(
        schoolCode : Long,
        username : String,
        filePropertyType : Int,
        fileProperty : String,
        token : String,
    ) = XwxRepository.getDocPreview(
        schoolCode,
        username,
        filePropertyType,
        fileProperty,
        token,
        docPreviewResp
    )
}