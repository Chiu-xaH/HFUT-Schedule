package com.hfut.schedule.logic.model.dto

import com.hfut.schedule.network.api.model.response.json.xiaowuxing.XiaoWuXingUserInfo

data class XiaoWuXingLoginInfoDto(
    val data : XiaoWuXingUserInfo,
    val token : String,
)