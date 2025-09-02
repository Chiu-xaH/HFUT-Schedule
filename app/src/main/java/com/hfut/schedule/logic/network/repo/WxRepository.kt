package com.hfut.schedule.logic.network.repo

import androidx.core.net.toUri
import com.google.gson.Gson
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.model.wx.WXClassmatesBean
import com.hfut.schedule.logic.model.wx.WXClassmatesResponse
import com.hfut.schedule.logic.model.wx.WXLoginResponse
import com.hfut.schedule.logic.model.wx.WXPersonInfoBean
import com.hfut.schedule.logic.model.wx.WXPersonInfoResponse
import com.hfut.schedule.logic.model.wx.WXQrCodeLoginResponse
import com.hfut.schedule.logic.model.wx.WXQrCodeResponse
import com.hfut.schedule.logic.network.api.WXService
import com.hfut.schedule.logic.network.servicecreator.WXServiceCreator
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import retrofit2.awaitResponse

object WxRepository {
    private val wx = WXServiceCreator.create(WXService::class.java)
    suspend fun wxLogin(holder : StateHolder<String>) = launchRequestSimple(
        holder = holder,
        request = { wx.login().awaitResponse() },
        transformSuccess = { _,json -> parseWxLogin(json) }
    )
    @JvmStatic
    private suspend fun parseWxLogin(json : String) : String = try {
        val bean = Gson().fromJson(json, WXLoginResponse::class.java)
        val msg = bean.msg
        if(msg.contains("success")) {
            // 保存
            val auth = bean.data.TGT
            DataStoreManager.saveWxAuth(auth)
            auth
        } else {
            throw Exception(msg)
        }
    } catch (e : Exception) { throw e }


    suspend fun wxGetPersonInfo(auth : String,holder : StateHolder<WXPersonInfoBean>) = launchRequestSimple(
        holder = holder,
        request = { wx.getMyInfo(auth).awaitResponse() },
        transformSuccess = { _,json -> parseWxPersonInfo(json) }
    )
    @JvmStatic
    private fun parseWxPersonInfo(json : String) : WXPersonInfoBean = try {
        val bean = Gson().fromJson(json, WXPersonInfoResponse::class.java)
        val msg = bean.msg
        if(msg.contains("success")) {
            saveString("WX_PERSON_INFO", json)
            bean.data
        } else {
            throw Exception(msg)
        }
    } catch (e : Exception) { throw e }


    suspend fun wxGetClassmates(nodeId : String,auth : String,holder : StateHolder<WXClassmatesBean>) = launchRequestSimple(
        holder = holder,
        request = { wx.getClassmates(nodeId,auth).awaitResponse() },
        transformSuccess = { _,json -> parseWxClassmates(json) }
    )
    @JvmStatic
    private fun parseWxClassmates(json : String) : WXClassmatesBean = try {
        val bean = Gson().fromJson(json, WXClassmatesResponse::class.java)
        val msg = bean.msg
        if(msg.contains("success")) {
            bean.data
        } else {
            throw Exception(msg)
        }
    } catch (e : Exception) { throw e }

    suspend fun wxLoginCas(url : String,auth : String,holder : StateHolder<Pair<String, Boolean>>) = launchRequestSimple(
        holder = holder,
        request = {
            // 先解析原 URL
            val originalUri = url.toUri()
            // 用原路径和查询参数替换 host
            val newUrl = originalUri.buildUpon()
                .encodedAuthority(MyApplication.WX_URL.toUri().encodedAuthority)
                .scheme(MyApplication.WX_URL.toUri().scheme)
                .build()
                .toString()
            // 处理URL 将其HOST换成
            // 然后发送网络请求 GET 携带 @Header("Authorization") auth : String
            wx.loginCas(newUrl,auth).awaitResponse()
        },
        transformSuccess = { _,json -> parseWxLoginCas(json) }
    )

    @JvmStatic
    private fun parseWxLoginCas(json : String) : Pair<String, Boolean> = try {
        val bean = Gson().fromJson(json, WXQrCodeResponse::class.java)
        val msg = bean.msg
        if(msg.contains("success")) {
            Pair("扫码成功",true)
        } else {
            Pair(msg,false)
        }
    } catch (e : Exception) { throw e }


    suspend fun wxConfirmLogin(uuid : String,auth : String,holder : StateHolder<String>) = launchRequestSimple(
        holder = holder,
        request = { wx.confirmLogin(uuid,auth).awaitResponse() },
        transformSuccess = { _,json -> parseWxConfirmLogin(json) }
    )
    @JvmStatic
    private fun parseWxConfirmLogin(json : String) : String = try {
        val bean = Gson().fromJson(json, WXQrCodeLoginResponse::class.java)
        val msg = bean.msg
        if(msg.contains("success")) {
            bean.data
        } else {
            throw Exception(msg)
        }
    } catch (e : Exception) { throw e }

}