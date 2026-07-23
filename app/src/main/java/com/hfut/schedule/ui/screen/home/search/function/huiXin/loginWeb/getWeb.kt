package com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb


import com.hfut.schedule.network.api.model.response.json.huixin.HuiXinFeeResponse
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.saveString
import com.hfut.schedule.network.api.model.response.dto.SchoolNetInfo
import com.hfut.schedule.network.core.GsonInstance
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.logic.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONObject

suspend fun getXwxPsk() : String? = withContext(Dispatchers.IO) {
    return@withContext try {
        val psk = DataStoreManager.xwxPassword.first()
        if(psk.isEmpty() || psk.isBlank()) {
            val defaultPsk = getPersonInfo().chineseID?.takeLast(6)
            defaultPsk?.let { DataStoreManager.saveXwxPassword(it) }
            defaultPsk
        } else {
            psk
        }
    } catch (e : Exception) {
        LogUtil.error(e)
        null
    }
}

suspend fun getCardPsk() : String? = withContext(Dispatchers.IO) {
    return@withContext try {
        val isDefault = DataStoreManager.enableUseDefaultCardPassword.first()
        if(isDefault) {
            val seven = getPersonInfo().chineseID?.takeLast(7)
            if (seven == null) return@withContext null
            // 处理X结尾
            if(seven.last() == 'X') seven.take(6) else seven.takeLast(6)
        } else {
            val pwd = DataStoreManager.customCardPassword.first()
            if (pwd.isEmpty() || pwd.length != 6) null else pwd
        }
    } catch (e : Exception) {
        LogUtil.error(e)
        null
    }
}


fun getWebInfo(vm: NetWorkViewModel) : SchoolNetInfo? {
    val json = vm.infoValue.value
    try {
        if(json != null && json.contains("success")&& !json.contains("账号不存在")) {
            val jsons = GsonInstance.fromJson(json, HuiXinFeeResponse::class.java).map
            val data = jsons.showData

            val jsonObject = JSONObject(json)
            val dataObject = jsonObject.getJSONObject("map").getJSONObject("data")
            dataObject.put("myCustomInfo", "undefined：undefined")
            val postJson = dataObject.toString()

            val webInfo = data["本期已使用流量"]?.let {
                data["储值余额"]?.let { it1 ->
                    SchoolNetInfo(
                        it1.substringBefore("（"),
                        it.substringBefore("（"),
                        postJson
                    )
                }
            }
            saveString("memoryWeb",webInfo?.flow)
            return webInfo
        } else {
            return null
        }
    } catch (e:Exception) {
        LogUtil.error(e)
        return null
    }
}