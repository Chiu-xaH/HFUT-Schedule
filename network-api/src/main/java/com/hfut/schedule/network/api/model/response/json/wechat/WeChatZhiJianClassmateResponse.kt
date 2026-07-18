package com.hfut.schedule.network.api.model.response.json.wechat

data class WeChatZhiJianClassmateResponse(
    override val msg: String,
    override val data: WeChatZhiJianClassmates
) :  WeChatZhiJianBaseResponse()

data class WeChatZhiJianClassmates(
    val records : List<WeChatZhiJianClassmate>,
    val total : Int,
)

data class WeChatZhiJianClassmate(
    val id : String,
    val name : String,
    val photoUrl : String?,
    val phone : String?,
    val email : String?
)
