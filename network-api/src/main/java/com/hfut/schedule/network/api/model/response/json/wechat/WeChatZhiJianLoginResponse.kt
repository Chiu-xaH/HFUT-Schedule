package com.hfut.schedule.network.api.model.response.json.wechat

import com.google.gson.annotations.SerializedName

data class WeChatZhiJianLoginResponse(
    override val data: WeChatZhiJianLoginData,
    override val msg: String,
) : WeChatZhiJianBaseResponse()

data class WeChatZhiJianLoginData(
    @SerializedName("TGT")
    val ticket : String
)


