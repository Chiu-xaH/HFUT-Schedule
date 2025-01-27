package com.hfut.schedule.ui.activity.home.search.functions.transferMajor

import com.google.gson.Gson
import com.hfut.schedule.logic.beans.jxglstu.MyApplyModels
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.beans.jxglstu.MyApplyResponse
import com.hfut.schedule.logic.beans.jxglstu.TransferData
import com.hfut.schedule.logic.beans.jxglstu.TransferResponse
import com.hfut.schedule.logic.beans.jxglstu.courseType
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.ui.activity.home.search.functions.person.getPersonInfo
import org.jsoup.Jsoup

enum class CampusId {
    HEFEI,XUANCHENG
}
fun getCampus() : CampusId {
    return if(getPersonInfo().school?.contains("宣城") == true) {
        CampusId.XUANCHENG
    } else {
        CampusId.HEFEI
    }
}

fun getTransfer(vm : NetWorkViewModel) : List<TransferData> {
    val list = mutableListOf<TransferData>()
    return try {
        val json = vm.transferData.value
        Gson().fromJson(json, TransferResponse::class.java).data
    } catch (e : Exception) {
        list
    }
}



fun getMyTransferPre(vm : NetWorkViewModel) : List<MyApplyModels>? {
    //  val list = mutableListOf<TransferData>()
    return try {
        val json = vm.myApplyData.value

        val data = Gson().fromJson(json, MyApplyResponse::class.java).models
        data.ifEmpty {
            emptyList()
        }
        // list
    } catch (e : Exception) {
        //list
        emptyList()
    }
}



fun getMyTransfer(vm : NetWorkViewModel,index : Int) : TransferData {
    //  val list = mutableListOf<TransferData>()
    return try {
        getMyTransferPre(vm)?.get(index)!!.changeMajorSubmit
        // list
    } catch (e : Exception) {
        //list
        TransferData(null,0, courseType(""), courseType(""),0,0)
    }
}


fun getApplyStatus(vm : NetWorkViewModel,index : Int) : Boolean? {
    return try {
       val data = getMyTransferPre(vm)?.get(index)!!.applyStatus
        data == "ACCEPTED"
    } catch (_:Exception) {
        null
    }
}

data class MyApplyScore(val gpaValue : String,val gpaSort : String,val scoreValue : String,val scoreSort : String,val examValue : String,val examSort : String)

fun getMyApplyScore()  {
}

data class ChangeMajorInfo(val title: String, val batchId: String, val applicationDate: String, val admissionDate: String)

fun getTransferList(vm: NetWorkViewModel): List<ChangeMajorInfo> {
    val html = vm.transferListData.value
    try {
        val document = Jsoup.parse(html!!)
        val result = mutableListOf<ChangeMajorInfo>()

        // 获取所有的 turn-panel 元素
        val turnPanels = document.select(".turn-panel")
        for (panel in turnPanels) {
            val title = panel.select(".turn-title span").text()
            val dataValue = panel.select(".change-major-enter").attr("data")
            val applicationDate = panel.select(".open-date .text-primary").text()
            val admissionDate = panel.select(".select-date .text-warning").text()

            if (title.isNotBlank() && dataValue.isNotBlank()) {
                result.add(
                    ChangeMajorInfo(
                        title = title,
                        batchId = dataValue,
                        applicationDate = applicationDate,
                        admissionDate = admissionDate
                    )
                )
            }
        }
        return result
    } catch (e: Exception) {
        return emptyList()
    }
}