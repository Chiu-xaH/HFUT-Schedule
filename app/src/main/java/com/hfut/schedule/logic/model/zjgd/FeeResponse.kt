package com.hfut.schedule.logic.model.zjgd


data class FeeResponse(val map : FeeMap)
data class FeeMap(val showData : Map<String,String>)
enum class FeeType(val code : Int,val payTypeId : Int) {
    NET_XUANCHENG(code = 281, payTypeId = 1),
    ELECTRIC_XUANCHENG(code = 261, payTypeId = 101),
    SHOWER_XUANCHENG(code = 223, payTypeId = 101),
    SHOWER_HEFEI(code = 222, payTypeId = -1) ,// PAYTYPEID待定
    WASHING_HEFEI(code = 26, payTypeId = -1), // PAYTYPEID待定
    ELECTRIC_HEFEI_UNDERGRADUATE(code = 1, payTypeId = -1) ,// PAYTYPEID待定
    ELECTRIC_HEFEI_GRADUATE(code = 2, payTypeId = -1) // PAYTYPEID待定
}

data class ShowerFeeResponse(val map : ShowerFeeMap)
data class ShowerFeeMap(val data : ShowerFee)
data class ShowerFee(val telPhone : String,
                     val identifier : String?,
                     val name : String?,
                     val accountMoney : Int,
                     val accountGivenMoney : Int
    )
