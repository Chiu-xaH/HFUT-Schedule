package com.hfut.schedule.ui.Activity.success.search.Search.Transfer

import com.google.gson.Gson
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.datamodel.Jxglstu.MyApplyResponse
import com.hfut.schedule.logic.datamodel.Jxglstu.TransferData
import com.hfut.schedule.logic.datamodel.Jxglstu.TransferResponse
import com.hfut.schedule.logic.datamodel.Jxglstu.courseType
import com.hfut.schedule.logic.utils.SharePrefs
import org.jsoup.Jsoup

enum class CampusId {
    HEFEI,XUANCHENG
}
fun getCampus() : String? {
    val info = SharePrefs.prefs.getString("info","")


    val doc = info?.let { Jsoup.parse(it) }
    val elements = doc?.select("dl dt, dl dd")

    val infoMap = mutableMapOf<String, String>()
    if (elements != null) {
        for (i in 0 until elements.size step 2) {
            val key = elements[i].text()
            val value = elements[i+1].text()
            infoMap[key] = value
        }
    }

    return infoMap[elements?.get(18)?.text()]

}

fun getTransfer(vm : LoginSuccessViewModel) : MutableList<TransferData> {
    val list = mutableListOf<TransferData>()
    return try {
        val json = vm.transferData.value
        val data = Gson().fromJson(json, TransferResponse::class.java).data
        for (i in data.indices) {
            val planCount = data[i].preparedStdCount
            val count = data[i].applyStdCount
            val request = data[i].registrationConditions
            val department = data[i].department
            val major = data[i].major
            list.add(TransferData(request, department, major, planCount, count))
        }
        list
    } catch (e : Exception) {
        list
    }
}

fun getMyTransfer(vm : LoginSuccessViewModel) : TransferData {
    //  val list = mutableListOf<TransferData>()
    return try {
        val json = vm.myApplyData.value
        Gson().fromJson(json, MyApplyResponse::class.java).models[0].changeMajorSubmit

        // list
    } catch (e : Exception) {
        //list
        TransferData(null, courseType(""), courseType(""),0,0)
    }
}

fun getApplyStatus(vm : LoginSuccessViewModel) : Boolean? {
    return try {
        val json = vm.myApplyData.value
        val data = Gson().fromJson(json, MyApplyResponse::class.java).models[0].applyStatus
        data == "ACCEPTED"
    } catch (_:Exception) {
        null
    }
}