package com.hfut.schedule.logic.enumeration

import com.hfut.schedule.App.MyApplication

enum class LoginType(val service : String) {
    JXGLSTU(MyApplication.JXGLSTU_URL + "neusoft-sso/login"),
//    ONE,
    STU(MyApplication.STU_URL + "xsfw/sys/swmzhcptybbapp/*default/index.do"),
    COMMUNITY(MyApplication.COMMUNITY_URL),
//    ZJGD
}