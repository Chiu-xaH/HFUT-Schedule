package com.hfut.schedule.ui.screen.home.focus.funiction

import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.isValidWebUrl

// U校园 U校园AI版 微信 超星学习通 长江雨课堂 中国大学MOOC
fun openOperation(info : String) {
    if(info.contains("学习通")) Starter.startAppLaunch(Starter.AppPackages.CHAO_XING)
    else if(info.contains("U校园")) Starter.startWebUrl(if(!info.contains("AI",ignoreCase = true)) MyApplication.UNIPUS_URL else MyApplication.UNIPUS_AI_URL )
    else if(info.contains("雨课堂"))  Starter.startAppLaunch(Starter.AppPackages.RAIN_CLASSROOM)
    else if(info.contains("MOOC"))  Starter.startAppLaunch(Starter.AppPackages.MOOC)
    else if(isValidWebUrl(info)) Starter.startWebUrl(info)
    else showToast("此条未做点击适配")
}
