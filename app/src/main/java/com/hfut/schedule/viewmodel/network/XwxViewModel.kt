package com.hfut.schedule.viewmodel.network

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.hfut.schedule.logic.model.xwx.XwxFunction
import com.hfut.schedule.logic.model.xwx.XwxSchoolBean
import com.hfut.schedule.logic.network.repo.XwxRepository
import com.xah.common.logic.state.UiStateHolder

@Deprecated("为KMP适配计划的开始做铺垫，即将被合入至`NetworkViewModel`统一管理")
class XwxViewModel : ViewModel() {
    val schoolListResp = UiStateHolder<List<XwxSchoolBean>>()
    suspend fun getSchoolList() = XwxRepository.getSchoolList(schoolListResp)

    val loginResp = UiStateHolder<Boolean>()
    suspend fun login(
        schoolCode : Long,
        username : String,
        password : String,
    ) = XwxRepository.login(schoolCode,username,password,loginResp)

    val functionsResp = UiStateHolder<List<XwxFunction>>()
    suspend fun getFunctions(
        schoolCode : Long,
        username : String,
        token : String,
    ) = XwxRepository.getFunctions(schoolCode,username,token,functionsResp)

    val docPreviewResp = UiStateHolder<Bitmap>()
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