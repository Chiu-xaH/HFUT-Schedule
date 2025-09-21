package com.hfut.schedule.logic.enumeration

import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.network.encodeUrl

// 使用CAS统一认证登陆 还有一种OAuth2登录，慧新易校用
enum class CasLoginType(val service : String?, val description: String) {
    JXGLSTU(MyApplication.JXGLSTU_URL + "neusoft-sso/login","教务系统"),
    ONE(null,"信息门户"),
    STU(MyApplication.STU_URL + "xsfw/sys/xgutilapp/userinfo/getConfigUserInfo.do","学工系统"),
    COMMUNITY(MyApplication.COMMUNITY_URL,"智慧社区"),
    LIBRARY(MyApplication.NEW_LIBRARY_URL + "svc/sso/login/callback/portal/hfut","图书馆"),
    // 办事大厅

    // 智慧后勤
    REPAIR_XC(MyApplication.REPAIR_XC_URL + "index.html","宣城校区智慧后勤"),
    REPAIR(MyApplication.REPAIR_URL + "index.html","合肥校区智慧后勤"),
    // 体测平台
    PE(MyApplication.PE_URL + "bdlp_h5_fitness_test/public/index.php/index/login/hfutLogin","体测平台"),
    // 指间工大
    ZHI_JIAN(MyApplication.ZHI_JIAN_URL  + "wui/cas-entrance.jsp?path=${encodeUrl(MyApplication.ZHI_JIAN_URL + "wui/index.html#/main")}&ssoType=CAS","指间工大")
    // ...
}

// 使用WEBVPN登录需要同理
