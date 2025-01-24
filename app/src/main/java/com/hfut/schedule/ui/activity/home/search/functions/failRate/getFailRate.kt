package com.hfut.schedule.ui.activity.home.search.functions.failRate

import com.google.gson.Gson
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.beans.community.FailRateRecord
import com.hfut.schedule.logic.beans.community.FailRateResponse
import com.hfut.schedule.logic.beans.community.courseFailRateDTOList

fun getFailRate(vm : NetWorkViewModel)  :  MutableList<FailRateRecord>{
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

fun getLists(item : Int,vm : NetWorkViewModel) :  MutableList<courseFailRateDTOList> {
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
                val successRate = list[j].successRate
                Addlists.add(courseFailRateDTOList(list[j].xn,list[j].xq,avarange,totalNum,failNum,successRate))
            }
        }
    }
    return Addlists
}
