package com.hfut.schedule.network.api.repo

import com.hfut.schedule.network.api.model.response.json.wechat.WeChatZhiJianClassmates
import com.hfut.schedule.network.api.model.response.json.wechat.WeChatZhiJianPersonInfo
import com.xah.common.logic.state.UiStateHolder

interface WeChatZhiJianRepositoryInf {
    suspend fun wxLogin(holder : UiStateHolder<String>)
    suspend fun wxGetPersonInfo(auth : String,holder : UiStateHolder<WeChatZhiJianPersonInfo>)
    suspend fun wxGetClassmates(nodeId : String,auth : String,holder : UiStateHolder<WeChatZhiJianClassmates>)
    suspend fun wxLoginCas(url : String,auth : String,holder : UiStateHolder<Pair<String, Boolean>>)
    suspend fun wxConfirmLogin(uuid : String,auth : String,holder : UiStateHolder<String>)
}