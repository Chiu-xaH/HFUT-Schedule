package com.hfut.schedule.network.model.response.huixin

enum class HuiXinFeeType(
    val code : Int,
    val payTypeId : Int
) {
    NET_XUANCHENG(code = 281, payTypeId = 1),
    ELECTRIC_XUANCHENG(code = 261, payTypeId = 101),
    SHOWER_XUANCHENG(code = 223, payTypeId = 101),
    SHOWER_HEFEI(code = 222, payTypeId = -1) ,// PAYTYPEID待定
    WASHING_HEFEI(code = 26, payTypeId = -1), // PAYTYPEID待定
    ELECTRIC_HEFEI_UNDERGRADUATE(code = 1, payTypeId = -1) ,// PAYTYPEID待定
//    ELECTRIC_HEFEI_GRADUATE(code = 2, payTypeId = -1) // PAYTYPEID待定
}