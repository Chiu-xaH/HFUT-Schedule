package com.hfut.schedule.logic.enumeration

import com.hfut.schedule.application.MyApplication
import okhttp3.HttpUrl
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// 使用CAS统一认证登陆 还有一种OAuth2登录，慧新易校用
enum class LoginType(val service : String?,val description: String) {
    JXGLSTU(MyApplication.JXGLSTU_URL + "neusoft-sso/login","教务系统"),
    ONE(null,"信息门户"),
    STU(MyApplication.STU_URL + "xsfw/sys/xgutilapp/userinfo/getConfigUserInfo.do","学工系统"),
    COMMUNITY(MyApplication.COMMUNITY_URL,"智慧社区"),
    HUI_XIN(MyApplication.HUI_XIN_URL + "/plat","慧新易校"),
    LIBRARY(MyApplication.NEW_LIBRARY_URL + "svc/sso/login/callback/portal/hfut","图书馆"),
    // 二课
    // 校友
    // 办事大厅
    // 体测平台
    // 指间工大
    ZHI_JIAN(MyApplication.ZHI_JIAN_URL  + "wui/cas-entrance.jsp?path=${encodeUrl(MyApplication.ZHI_JIAN_URL + "wui/index.html#/main")}&ssoType=CAS","指间工大")
    // ...
}

// 使用WEBVPN登录需要同理

fun encodeUrl(url: String): String {
    return URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
}

fun decodeUrl(url: String): String {
    return URLDecoder.decode(url, StandardCharsets.UTF_8.toString())
}