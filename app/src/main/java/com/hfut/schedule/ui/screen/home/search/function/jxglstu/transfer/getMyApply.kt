package com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer

import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import org.jsoup.Jsoup


fun isSuccessTransfer() : Boolean {
    return getPersonInfo().status == "转专业"
}

data class MyApplyInfoBean(
    val meetSchedule : PlaceAndTime? ,//面试安排
    val examSchedule : PlaceAndTime?,//笔试安排
    val grade : ApplyGrade
)

data class PlaceAndTime(
    val place : String,
    val time : String
)
data class ApplyGrade (
    val gpa : GradeAndRank,
    val operateAvg : GradeAndRank,
    val weightAvg : GradeAndRank,
    val transferAvg : GradeAndRank
)
data class GradeAndRank(
    val score : Double,//分数
    val rank : Int?//名次
)

