package com.hfut.schedule.network.api.model.response.json.wechat

abstract class WeChatZhiJianBaseResponse {
    abstract val msg : String
    abstract val data : Any?
}