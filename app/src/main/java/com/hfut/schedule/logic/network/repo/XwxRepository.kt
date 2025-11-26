package com.hfut.schedule.logic.network.repo

import com.google.gson.Gson
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.model.xwx.XwxDocPreviewRequestBody
import com.hfut.schedule.logic.model.xwx.XwxDocPreviewResponseBody
import com.hfut.schedule.logic.model.xwx.XwxFunction
import com.hfut.schedule.logic.model.xwx.XwxFunctionsRequestBody
import com.hfut.schedule.logic.model.xwx.XwxFunctionsResponseBody
import com.hfut.schedule.logic.model.xwx.XwxLoginBean
import com.hfut.schedule.logic.model.xwx.XwxLoginInfo
import com.hfut.schedule.logic.model.xwx.XwxLoginRequestBody
import com.hfut.schedule.logic.model.xwx.XwxLoginResponseBody
import com.hfut.schedule.logic.model.xwx.XwxSchoolBean
import com.hfut.schedule.logic.model.xwx.XwxSchoolListResponseBody
import com.hfut.schedule.logic.model.xwx.XwxUserInfo
import com.hfut.schedule.logic.model.xwx.isXwxRequestSuccessful
import com.hfut.schedule.logic.network.api.XwxService
import com.hfut.schedule.logic.network.servicecreator.XwxServiceCreator
import com.hfut.schedule.logic.network.util.launchRequestState
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object XwxRepository {
    private val xwx = XwxServiceCreator.create(XwxService::class.java)

    suspend fun getSchoolList(
        holder: StateHolder<List<XwxSchoolBean>>
    ) = launchRequestState(
        holder = holder,
        request = { xwx.getSchoolList() },
        transformSuccess = { _,json -> parseSchoolList(json) }
    )
    @JvmStatic
    private fun parseSchoolList(json : String) : List<XwxSchoolBean> = try {
        val result = Gson().fromJson(json, XwxSchoolListResponseBody::class.java)
        if(isXwxRequestSuccessful(result.code) == false) {
            throw Exception("登录状态失效")
        }
        result.result.data.flatMap { it.list }
    } catch (e : Exception) { throw e }

    suspend fun login(
        schoolCode : Long,
        username : String,
        password : String,
        holder : StateHolder<Boolean>
    ) = launchRequestState(
        holder = holder,
        request = { xwx.login(XwxLoginRequestBody(schoolCode = schoolCode, userId = username,password = password)) },
        transformSuccess = { _,json -> parseLogin(json) }
    )
    @JvmStatic
    private suspend fun parseLogin(json : String)  = withContext(Dispatchers.IO) {
        try {
            val result = withContext(Dispatchers.Default) {
                Gson().fromJson(json, XwxLoginResponseBody::class.java)
            }
            if(!isXwxRequestSuccessful(result.code)) {
                throw Exception("登录状态失效")
            }
            launch {
                LargeStringDataManager.save(MyApplication.context, LargeStringDataManager.XWX_USER_INFO,json)
            }
            showToast("登陆成功")
            true
        } catch (e : Exception) { throw e }
    }

    suspend fun getFunctions(
        schoolCode : Long,
        username : String,
        token : String,
        holder : StateHolder<List<XwxFunction>>
    ) = launchRequestState(
        holder = holder,
        request = { xwx.getFunctions(token,XwxFunctionsRequestBody(schoolCode = schoolCode,userId = username),) },
        transformSuccess = { _,json -> parseFunctions(json) }
    )
    @JvmStatic
    private fun parseFunctions(json : String) : List<XwxFunction> = try {
        val result = Gson().fromJson(json, XwxFunctionsResponseBody::class.java)
        if(isXwxRequestSuccessful(result.code) == false) {
            throw Exception("登录状态失效")
        }
        result.result.data
    } catch (e: Exception) { throw e }

    suspend fun getDocPreview(
        schoolCode : Long,
        username : String,
        filePropertyType : Int,
        fileProperty : String,
        token : String,
        holder : StateHolder<String>
    ) = launchRequestState(
        holder = holder,
        request = { xwx.getDocPreview(token,XwxDocPreviewRequestBody(schoolCode = schoolCode, userId = username, fileProperty = fileProperty, filePropertyType = filePropertyType)) },
        transformSuccess = { _,json -> parseDocPreview(json) }
    )
    @JvmStatic
    private fun parseDocPreview(json : String) : String = try {
        val result = Gson().fromJson(json, XwxDocPreviewResponseBody::class.java)
        if(isXwxRequestSuccessful(result.code) == false) {
            throw Exception("登录状态失效")
        }
        result.result.imageBase64String
        // 转为图片
    } catch (e: Exception) { throw e }
}