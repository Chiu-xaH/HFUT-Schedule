package com.hfut.schedule.ui.Activity.success.search.Search.FailRate

import com.google.gson.Gson
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.datamodel.Community.FailRateRecord
import com.hfut.schedule.logic.datamodel.Community.FailRateResponse
import com.hfut.schedule.logic.datamodel.Community.courseFailRateDTOList
import com.hfut.schedule.logic.utils.SharePrefs

fun getFailRate(vm : LoginSuccessViewModel)  :  MutableList<FailRateRecord>{
    val json = vm.FailRateData.value
    var Addlists = mutableListOf<FailRateRecord>()
    if (json != null) {
        if(json.contains("操作成功")) {
            val result = Gson().fromJson(json, FailRateResponse::class.java)
            val record = result.result.records
            for(i in 0 until record.size) {
                val courseName = record[i].courseName
                val list = record[i].courseFailRateDTOList
                Addlists.add(FailRateRecord(courseName,list))
            }
        }
    }
    return Addlists
}

fun getLists(item : Int,vm : LoginSuccessViewModel) :  MutableList<courseFailRateDTOList> {
    val json =  vm.FailRateData.value
    var Addlists = mutableListOf<courseFailRateDTOList>()
    if (json != null) {
        if(json.contains("操作成功")) {
            val result = Gson().fromJson(json, FailRateResponse::class.java)
            val record = result.result.records
            val page = result.result.current
            val totalPages = result.result.pages
            //for(i in 0 until record.size) {
            val list = record[item].courseFailRateDTOList
            for(j in 0 until list.size) {
                val terms = "${list[j].xn}年 第${list[j].xq}学期"
                val avarange = list[j].avgScore
                val totalNum = list[j].totalCount
                val failNum = list[j].failCount
                val failRate = list[j].failRate
                Addlists.add(courseFailRateDTOList(list[j].xn,list[j].xq,avarange,totalNum,failNum,failRate))
            }
        }
    }
    return Addlists
}
