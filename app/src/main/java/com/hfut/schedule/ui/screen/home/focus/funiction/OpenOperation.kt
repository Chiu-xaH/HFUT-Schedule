package com.hfut.schedule.ui.screen.home.focus.funiction

import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.showToast

fun openOperation(info : String) {
    if(info.contains("学习通")) Starter.startLaunchAPK("com.chaoxing.mobile","学习通")
    else if(info.contains("U校园")) Starter.startWebUrl("https://u.unipus.cn/")
    else if(info.contains("雨课堂"))  Starter.startLaunchAPK("com.xuetangx.ykt","雨课堂")
    else if(info.contains("MOOC"))  Starter.startLaunchAPK("com.netease.edu.ucmooc","中国大学MOOC")
    else if(info.contains("http")) Starter.startWebUrl(info)
    else showToast("此条未做点击适配")
}