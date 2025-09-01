package com.hfut.schedule.logic.enumeration

import com.hfut.schedule.App.MyApplication

// 使用CAS统一认证登陆
enum class LoginType(val service : String?,val description: String) {
    JXGLSTU(MyApplication.JXGLSTU_URL + "neusoft-sso/login","教务系统"),
    ONE(null,"信息门户"),
    STU(MyApplication.STU_URL + "xsfw/sys/swmzhcptybbapp/*default/index.do","学工系统"),
    COMMUNITY(MyApplication.COMMUNITY_URL,"智慧社区"),
    HUI_XIN(MyApplication.HUI_XIN_URL,"慧新易校"),
    LIBRARY(MyApplication.NEW_LIBRARY_URL,"图书馆"),
    // 二课
    // 校友
    // 办事大厅
    // 体测平台
    // ...
}

// 使用WEBVPN登录需要同理