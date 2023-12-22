package com.hfut.schedule.logic.datamodel.zjgd

data class BillRangeResponse (val data : BillRangeDatas)

data class BillRangeDatas(val income : Float,
                          val expenses : Float)