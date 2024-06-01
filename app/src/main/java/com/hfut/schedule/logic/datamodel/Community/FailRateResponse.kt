package com.hfut.schedule.logic.datamodel.Community

data class FailRateResponse(val result : FailRate)

data class FailRate(val records : List<FailRateRecord>,
                    val current : Int,
                    val pages  : Int)

data class FailRateRecord(val courseName : String,val courseFailRateDTOList : List<courseFailRateDTOList>)

data class courseFailRateDTOList(val xn : String,//学期
                                 val xq : String,//第几学期
                                 val avgScore : Double,//平均分
                                 val totalCount : Int,//总人数
                                 val failCount : Int,//挂科人数
                                 val successRate : Float//挂科率
)
