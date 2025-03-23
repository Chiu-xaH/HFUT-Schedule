package com.hfut.schedule.logic.beans.zjgd


data class FeeResponse(val map : FeeMap)
data class FeeMap(val showData : Map<String,String>)
enum class FeeType(val code : Int,val payTypeId : Int) {
    WEB(code = 281, payTypeId = 1),
    ELECTRIC(code = 261, payTypeId = 101),
    SHOWER(code = 223, payTypeId = 101)
}
data class ShowerFeeResponse(val map : ShowerFeeMap)
data class ShowerFeeMap(val data : ShowerFee)
data class ShowerFee(val telPhone : String,
                     val identifier : String?,
                     val name : String?,
                     val accountMoney : Int,
                     val accountGivenMoney : Int
    )
