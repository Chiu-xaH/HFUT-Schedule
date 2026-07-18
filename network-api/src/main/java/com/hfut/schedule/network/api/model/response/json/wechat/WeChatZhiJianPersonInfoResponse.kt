package com.hfut.schedule.network.api.model.response.json.wechat

import com.google.gson.annotations.SerializedName

data class WeChatZhiJianPersonInfoResponse(
    override val msg: String,
    override val data: WeChatZhiJianPersonInfo
) : WeChatZhiJianBaseResponse()

data class WeChatZhiJianPersonInfo(
    val qq : String?,
    val mail : String?,
    val orgId : String,
    val officePhone : String?,
    val phone : String?,
    @SerializedName("avatorUrl")
    val imageUrl : String?,
)
