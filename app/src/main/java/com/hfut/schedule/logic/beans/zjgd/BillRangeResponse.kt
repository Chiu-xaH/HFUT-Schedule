package com.hfut.schedule.logic.beans.zjgd

data class BillRangeResponse (val data : BillRangeDatas)

data class BillRangeDatas(val income : Float,
                          val expenses : Float)