package com.hfut.schedule.logic.datamodel.LePaoYun

data class LePaoYunRecordResponse(val data : RecordDatas)

data class RecordDatas(val rank : List<rank>)

data class rank(val rank : rankinfos)

data class rankinfos(val year : String,
                     val month : String,
                     val monthKm : String,
                     val rankList : List<items>)

data class items(val recordEndTime : String,
                 val recordMileage : Double,
                 val isQualified : String)