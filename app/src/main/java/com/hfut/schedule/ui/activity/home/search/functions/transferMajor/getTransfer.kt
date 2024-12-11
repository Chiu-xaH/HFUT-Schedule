package com.hfut.schedule.ui.activity.home.search.functions.transferMajor

import com.google.gson.Gson
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.beans.Jxglstu.MyApplyResponse
import com.hfut.schedule.logic.beans.Jxglstu.TransferData
import com.hfut.schedule.logic.beans.Jxglstu.TransferResponse
import com.hfut.schedule.logic.beans.Jxglstu.courseType
import com.hfut.schedule.logic.utils.SharePrefs
import org.jsoup.Jsoup

enum class CampusId {
    HEFEI,XUANCHENG
}
fun getCampus() : String? {
    try {
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
    } catch (_:Exception) {
        return null
    }
}

fun getTransfer(vm : NetWorkViewModel) : MutableList<TransferData> {
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

fun getMyTransfer(vm : NetWorkViewModel) : TransferData {
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

fun getApplyStatus(vm : NetWorkViewModel) : Boolean? {
    return try {
        val json = vm.myApplyData.value
        val data = Gson().fromJson(json, MyApplyResponse::class.java).models[0].applyStatus
        data == "ACCEPTED"
    } catch (_:Exception) {
        null
    }
}

data class MyApplyScore(val gpaValue : String,val gpaSort : String,val scoreValue : String,val scoreSort : String,val examValue : String,val examSort : String)

fun getMyApplyScore()  {
}