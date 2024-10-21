package com.hfut.schedule.logic.datamodel.zjgd

data class FeeResponse(val map : FeeMap)
data class FeeMap(val showData : Map<String,String>)
enum class FeeType {
    WEB,ELECTRIC,SHOWER
}
data class ShowerFeeResponse(val map : ShowerFeeMap)
data class ShowerFeeMap(val data : ShowerFee)
data class ShowerFee(val telPhone : String,
                     val identifier : String?,
                     val name : String?,
                     val accountMoney : Int,
                     val accountGivenMoney : Int
    )
