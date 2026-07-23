package com.hfut.schedule.logic.network.repo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64

import com.hfut.schedule.network.api.model.request.xiaowuxing.XiaoWuXingDocPreviewRequest
import com.hfut.schedule.network.api.model.response.json.xiaowuxing.XiaoWuXingDocPreviewResponse
import com.hfut.schedule.network.api.model.response.json.xiaowuxing.XiaoWuXingFunction
import com.hfut.schedule.network.api.model.request.xiaowuxing.XiaoWuXingFunctionsRequest
import com.hfut.schedule.network.api.model.response.json.xiaowuxing.XiaoWuXingFunctionResponse
import com.hfut.schedule.network.api.model.request.xiaowuxing.XiaoWuXingLoginRequest
import com.hfut.schedule.network.api.model.response.json.xiaowuxing.XiaoWuXingLoginResponse
import com.hfut.schedule.network.api.model.response.json.xiaowuxing.XiaoWuXingSchool
import com.hfut.schedule.network.api.model.response.json.xiaowuxing.XiaoWuXingSchoolListResponse
import com.hfut.schedule.network.api.inf.XiaoWuXingService
import com.hfut.schedule.logic.util.network.launchRequestState
import com.xah.common.logic.state.UiStateHolder
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.network.api.impl.XiaoWuXingServiceCreator
import com.hfut.schedule.network.api.repo.XiaoWuXingRepositoryInf
import com.hfut.schedule.network.core.GsonInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object XiaoWuXingRepository : XiaoWuXingRepositoryInf {
    private val xwx = XiaoWuXingServiceCreator.create(XiaoWuXingService::class.java)

    private fun isXwxRequestSuccessful(code: String) : Boolean = code == "0"

    override suspend fun getSchoolList(
        holder: UiStateHolder<List<XiaoWuXingSchool>>
    ) = launchRequestState(
        holder = holder,
        request = { xwx.getSchoolList() },
        transformSuccess = { _,json -> parseSchoolList(json) }
    )
    @JvmStatic
    private fun parseSchoolList(json : String) : List<XiaoWuXingSchool> = try {
        val result = GsonInstance.fromJson(json, XiaoWuXingSchoolListResponse::class.java)
        if(isXwxRequestSuccessful(result.code) == false) {
            throw Exception("登录状态失效")
        }
        result.result.data.flatMap { it.list }
    } catch (e : Exception) { throw e }

    override suspend fun login(
        schoolCode : Long,
        username : String,
        password : String,
        holder : UiStateHolder<Boolean>
    ) = launchRequestState(
        holder = holder,
        request = { xwx.login(XiaoWuXingLoginRequest(schoolCode = schoolCode, userId = username,password = password)) },
        transformSuccess = { _,json -> parseLogin(json) }
    )
    @JvmStatic
    private suspend fun parseLogin(json : String)  = withContext(Dispatchers.IO) {
        try {
            val result = withContext(Dispatchers.Default) {
                GsonInstance.fromJson(json, XiaoWuXingLoginResponse::class.java)
            }
            if(!isXwxRequestSuccessful(result.code)) {
                throw Exception("登录状态失效")
            }
            launch {
                LargeStringDataManager.save(LargeStringDataManager.XWX_USER_INFO,json)
            }
            showToast("登陆成功")
            true
        } catch (e : Exception) { throw e }
    }

    override suspend fun getFunctions(
        schoolCode : Long,
        username : String,
        token : String,
        holder : UiStateHolder<List<XiaoWuXingFunction>>
    ) = launchRequestState(
        holder = holder,
        request = { xwx.getFunctions(token,XiaoWuXingFunctionsRequest(schoolCode = schoolCode,userId = username),) },
        transformSuccess = { _,json -> parseFunctions(json) }
    )
    @JvmStatic
    private fun parseFunctions(json : String) : List<XiaoWuXingFunction> = try {
        val result = GsonInstance.fromJson(json, XiaoWuXingFunctionResponse::class.java)
        if(!isXwxRequestSuccessful(result.code)) {
            throw Exception("登录状态失效")
        }
        result.result.data
    } catch (e: Exception) { throw e }

    override suspend fun getDocPreview(
        schoolCode : Long,
        username : String,
        filePropertyType : Int,
        fileProperty : String,
        token : String,
        holder : UiStateHolder<Bitmap>
    ) = launchRequestState(
        holder = holder,
        request = { xwx.getDocPreview(token,XiaoWuXingDocPreviewRequest(schoolCode = schoolCode, userId = username, fileProperty = fileProperty, filePropertyType = filePropertyType)) },
        transformSuccess = { _,json -> parseDocPreview(json) }
    )
    @JvmStatic
    private fun parseDocPreview(json : String) : Bitmap = try {
        val result = GsonInstance.fromJson(json, XiaoWuXingDocPreviewResponse::class.java)
        if(!isXwxRequestSuccessful(result.code)) {
            throw Exception("登录状态失效")
        }
        val decodedByteArray = Base64.decode(result.result.imageBase64String.substringAfter("base64,").trimIndent(),Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.size)
        // 转为图片
    } catch (e: Exception) { throw e }
}