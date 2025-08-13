package com.hfut.schedule.logic.model.wx

data class WXQrCodeLoginResponse(
    override val data: String,
    override val msg: String
) : WXBaseResponse()


data class WXQrCodeResponse(
    val msg: String
)

