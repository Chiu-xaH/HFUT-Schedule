package com.hfut.schedule.logic.model.xiaowuxing

import com.hfut.schedule.network.model.response.xiaowuxing.XiaoWuXingUserInfo

data class XiaoWuXingLoginInfo(
    val data : XiaoWuXingUserInfo,
    val token : String,
)