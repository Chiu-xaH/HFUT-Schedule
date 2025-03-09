package com.hfut.schedule.ui.activity.home.focus.funictions

import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.ui.utils.components.MyToast

fun openOperation(info : String) {
    if(info.contains("学习通")) Starter.startLaunchAPK("com.chaoxing.mobile","学习通")
    else if(info.contains("U校园")) Starter.startWebUrl("https://u.unipus.cn/")
    else if(info.contains("雨课堂"))  Starter.startLaunchAPK("com.xuetangx.ykt","雨课堂")
    else if(info.contains("教务")) Starter.startWebUrl(MyApplication.JXGLSTU_URL)
    else MyToast("此条未做点击适配")
}