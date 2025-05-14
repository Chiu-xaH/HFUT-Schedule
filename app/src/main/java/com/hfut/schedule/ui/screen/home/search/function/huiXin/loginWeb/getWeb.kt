package com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb

import com.google.gson.Gson
import com.hfut.schedule.logic.model.zjgd.FeeResponse
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.ui.screen.home.search.function.person.getPersonInfo
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import org.json.JSONObject

data class WebInfo(val fee : String, val flow : String,val postJson : String = "")

fun getIdentifyID() : String? {
    return try {
        val seven = getPersonInfo().chineseID?.takeLast(7)
        var id = ""
        if (seven != null) {
            id = if(seven.last() == 'X') seven.take(6)
            else seven.takeLast(6)
        }
        id
    } catch (e : Exception) {
        null
    }
}


fun getWebInfo(vm: NetWorkViewModel) : WebInfo? {
    val json = vm.infoValue.value
    try {
        if(json != null && json.contains("success")&& !json.contains("账号不存在")) {
            val jsons = Gson().fromJson(json, FeeResponse::class.java).map
            val data = jsons.showData

            val jsonObject = JSONObject(json)
            val dataObject = jsonObject.getJSONObject("map").getJSONObject("data")
            dataObject.put("myCustomInfo", "undefined：undefined")
            val postJson = dataObject.toString()

            val webInfo = data["本期已使用流量"]?.let {
                data["储值余额"]?.let { it1 ->
                    WebInfo(
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
        return null
    }
}