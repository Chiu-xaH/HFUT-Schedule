package com.hfut.schedule.logic.model.huixin

data class BillRangeResponse (val data : BillRangeDatas)

data class BillRangeDatas(val income : Float,
                          val expenses : Float)