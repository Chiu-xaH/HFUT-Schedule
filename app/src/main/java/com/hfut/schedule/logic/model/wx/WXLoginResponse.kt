package com.hfut.schedule.logic.model.wx

data class WXLoginResponse(
    override val data: WXLoginBean,
    override val msg: String,
) : WXBaseResponse()

data class WXLoginBean(
    val TGT : String
)


