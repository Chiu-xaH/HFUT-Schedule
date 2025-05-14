package com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb

import com.google.gson.Gson
import com.hfut.schedule.logic.model.zjgd.FeeResponse
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.ui.screen.home.search.function.person.getPersonInfo
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import org.json.JSONObject

// 废弃的方法
fun getWebInfoOld(html : String) : WebInfo {

    try {
        //本段照搬前端
        val flow = html.substringAfter("flow").substringBefore(" ").substringAfter("'").toDouble()
        val fee = html.substringAfter("fee").substringBefore(" ").substringAfter("'").toDouble()
        var flow0 = flow % 1024
        val flow1 = flow - flow0
        flow0 *= 1000
        flow0 -= flow0 % 1024
        var fee1 = fee - fee % 100
        var flow3 = "."
        if (flow0 / 1024 < 10) flow3 = ".00"
        else { if (flow0 / 1024 < 100) flow3 = ".0"; }
        val resultFee = (fee1 / 10000).toString()
        val resultFlow : String = ((flow1 / 1024).toString() + flow3 + (flow0 / 1024)).substringBefore(".")

        return WebInfo(resultFee,resultFlow)
    } catch (e : Exception) {
        return WebInfo("未获取到数据","未获取到数据")
    }

}

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


// 新的方法
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