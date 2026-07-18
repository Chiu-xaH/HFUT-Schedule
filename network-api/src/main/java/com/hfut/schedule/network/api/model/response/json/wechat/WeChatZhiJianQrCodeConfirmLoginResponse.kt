package com.hfut.schedule.network.api.model.response.json.wechat

data class WeChatZhiJianQrCodeConfirmLoginResponse(
    override val data: String,
    override val msg: String
) : WeChatZhiJianBaseResponse()