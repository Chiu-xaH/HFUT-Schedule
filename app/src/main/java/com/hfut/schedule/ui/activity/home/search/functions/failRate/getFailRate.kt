package com.hfut.schedule.ui.activity.home.search.functions.failRate

import com.google.gson.Gson
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.beans.community.FailRateRecord
import com.hfut.schedule.logic.beans.community.FailRateResponse
import com.hfut.schedule.logic.beans.community.courseFailRateDTOList

fun getFailRate(vm : NetWorkViewModel)  :  List<FailRateRecord>{
    val json = vm.FailRateData.value
    return try {
        if(json != null && json.contains("操作成功")) {
            Gson().fromJson(json, FailRateResponse::class.java).result.records
        } else {
            emptyList()
        }
    } catch (e:Exception) {
        emptyList()
    }
}

fun getLists(item : Int,vm : NetWorkViewModel) :  List<courseFailRateDTOList> {
    val json =  vm.FailRateData.value
    try {
        if(json != null && json.contains("操作成功")) {
            val result = Gson().fromJson(json, FailRateResponse::class.java)
            val record = result.result.records
            val page = result.result.current
            val totalPages = result.result.pages

            return record[item].courseFailRateDTOList
//            for(j in 0 until list.size) {
//                val terms = "${list[j].xn}年 第${list[j].xq}学期"
//                val avarange = list[j].avgScore
//                val totalNum = list[j].totalCount
//                val failNum = list[j].failCount
//                val successRate = list[j].successRate
//                Addlists.add(courseFailRateDTOList(list[j].xn,list[j].xq,avarange,totalNum,failNum,successRate))
//            }
        } else {
            return emptyList()
        }
    } catch (e:Exception ){
        return emptyList()
    }
}
