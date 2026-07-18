package com.hfut.schedule.logic.network.repo

import androidx.core.net.toUri

import com.hfut.schedule.network.api.model.response.json.wechat.WeChatZhiJianClassmates
import com.hfut.schedule.network.api.model.response.json.wechat.WeChatZhiJianClassmateResponse
import com.hfut.schedule.network.api.model.response.json.wechat.WeChatZhiJianLoginResponse
import com.hfut.schedule.network.api.model.response.json.wechat.WeChatZhiJianPersonInfo
import com.hfut.schedule.network.api.model.response.json.wechat.WeChatZhiJianPersonInfoResponse
import com.hfut.schedule.network.api.model.response.json.wechat.WeChatZhiJianQrCodeConfirmLoginResponse
import com.hfut.schedule.network.api.model.response.json.wechat.WeChatZhiJianQrCodeLoginResponse
import com.hfut.schedule.logic.util.network.launchRequestState
import com.xah.common.logic.state.UiStateHolder
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.network.api.impl.WxServiceCreator
import com.hfut.schedule.network.api.inf.WxService
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.network.core.GsonInstance

object WxRepository {
    private val wx = WxServiceCreator.create(WxService::class.java)
    suspend fun wxLogin(holder : UiStateHolder<String>) = launchRequestState(
        holder = holder,
        request = {
            wx.login(
                username = SharedPrefs.prefs.getString("Username", "") ?: "",
                password = SharedPrefs.prefs.getString("Password", "") ?: ""
            )
        },
        transformSuccess = { _, json -> parseWxLogin(json) }
    )
    @JvmStatic
    private suspend fun parseWxLogin(json : String) : String = try {
        val bean = GsonInstance.fromJson(json, WeChatZhiJianLoginResponse::class.java)
        val msg = bean.msg
        if(msg.contains("success")) {
            // 保存
            val auth = bean.data.ticket
            DataStoreManager.saveWxAuth(auth)
            auth
        } else {
            throw Exception(msg)
        }
    } catch (e : Exception) { throw e }


    suspend fun wxGetPersonInfo(auth : String,holder : UiStateHolder<WeChatZhiJianPersonInfo>) =
        launchRequestState(
            holder = holder,
            request = { wx.getMyInfo(auth) },
            transformSuccess = { _, json -> parseWxPersonInfo(json) }
        )
    @JvmStatic
    private fun parseWxPersonInfo(json : String) : WeChatZhiJianPersonInfo = try {
        val bean = GsonInstance.fromJson(json, WeChatZhiJianPersonInfoResponse::class.java)
        val msg = bean.msg
        if(msg.contains("success")) {
            SharedPrefs.saveString("WX_PERSON_INFO", json)
            bean.data
        } else {
            throw Exception(msg)
        }
    } catch (e : Exception) { throw e }


    suspend fun wxGetClassmates(nodeId : String,auth : String,holder : UiStateHolder<WeChatZhiJianClassmates>) =
        launchRequestState(
            holder = holder,
            request = { wx.getClassmates(nodeId, auth) },
            transformSuccess = { _, json -> parseWxClassmates(json) }
        )
    @JvmStatic
    private fun parseWxClassmates(json : String) : WeChatZhiJianClassmates = try {
        val bean = GsonInstance.fromJson(json, WeChatZhiJianClassmateResponse::class.java)
        val msg = bean.msg
        if(msg.contains("success")) {
            bean.data
        } else {
            throw Exception(msg)
        }
    } catch (e : Exception) { throw e }

    suspend fun wxLoginCas(url : String,auth : String,holder : UiStateHolder<Pair<String, Boolean>>) =
        launchRequestState(
            holder = holder,
            request = {
                // 先解析原 URL
                val originalUri = url.toUri()
                // 用原路径和查询参数替换 host
                val newUrl = originalUri.buildUpon()
                    .encodedAuthority(Constant.WX_URL.toUri().encodedAuthority)
                    .scheme(Constant.WX_URL.toUri().scheme)
                    .build()
                    .toString()
                // 处理URL 将其HOST换成
                // 然后发送网络请求 GET 携带 @Header("Authorization") auth : String
                wx.loginCas(newUrl, auth)
            },
            transformSuccess = { _, json -> parseWxLoginCas(json) }
        )

    @JvmStatic
    private fun parseWxLoginCas(json : String) : Pair<String, Boolean> = try {
        val bean = GsonInstance.fromJson(json, WeChatZhiJianQrCodeLoginResponse::class.java)
        val msg = bean.msg
        if(msg.contains("success")) {
            Pair("扫码成功",true)
        } else {
            Pair(msg,false)
        }
    } catch (e : Exception) { throw e }


    suspend fun wxConfirmLogin(uuid : String,auth : String,holder : UiStateHolder<String>) =
        launchRequestState(
            holder = holder,
            request = { wx.confirmLogin(uuid, auth) },
            transformSuccess = { _, json -> parseWxConfirmLogin(json) }
        )
    @JvmStatic
    private fun parseWxConfirmLogin(json : String) : String = try {
        val bean = GsonInstance.fromJson(json, WeChatZhiJianQrCodeConfirmLoginResponse::class.java)
        val msg = bean.msg
        if(msg.contains("success")) {
            bean.data
        } else {
            throw Exception(msg)
        }
    } catch (e : Exception) { throw e }

}