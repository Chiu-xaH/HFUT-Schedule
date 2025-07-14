package com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer

import com.hfut.schedule.logic.model.jxglstu.MyApplyModels
import com.hfut.schedule.logic.model.jxglstu.TransferData
import com.hfut.schedule.logic.model.jxglstu.courseType
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo

enum class Campus(val description : String) {
    HEFEI("合肥"),XUANCHENG("宣城")
}
enum class CampusDetail(val description : String) {
    TXL("屯溪路"),FCH("翡翠湖"),XC("宣城")
}

fun getCampus() : Campus {
    return if(getPersonInfo().school?.contains("宣城") == true) {
        Campus.XUANCHENG
    } else {
        Campus.HEFEI
    }
}
fun getEventCampus() : EventCampus = when(getCampus()) {
    Campus.HEFEI -> EventCampus.HEFEI
    Campus.XUANCHENG -> EventCampus.XUANCHENG
}


enum class EventCampus  {
    HEFEI,XUANCHENG,DEFAULT
}

fun getMyTransfer(list : List<MyApplyModels>?,index : Int) : TransferData {
    val n = TransferData(null,0,null, courseType(""), courseType(""),0,0)
    return try {
        list?.get(index)?.changeMajorSubmit ?: n
    } catch (e : Exception) {
        n
    }
}


fun getApplyStatus(list : List<MyApplyModels>?, index : Int) : Boolean? {
    return try {
       val data = list?.get(index)?.applyStatus ?: ""
        data == "ACCEPTED"
    } catch (_:Exception) {
        null
    }
}

data class MyApplyScore(val gpaValue : String,val gpaSort : String,val scoreValue : String,val scoreSort : String,val examValue : String,val examSort : String)

fun getMyApplyScore()  {
}

data class ChangeMajorInfo(val title: String, val batchId: String, val applicationDate: String, val admissionDate: String)
