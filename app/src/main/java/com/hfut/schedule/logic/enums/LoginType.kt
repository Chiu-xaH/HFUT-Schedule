package com.hfut.schedule.logic.enums

import com.hfut.schedule.App.MyApplication

enum class LoginType(val service : String) {
    JXGLSTU(MyApplication.JxglstuURL + "neusoft-sso/login"),
//    ONE,
    STU(MyApplication.StuURL + "xsfw/sys/swmzhcptybbapp/*default/index.do"),
    COMMUNITY(MyApplication.CommunityURL),
//    ZJGD
}