package com.hfut.schedule.ui.Activity.success.focus.Focus

import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.utils.StartApp
import com.hfut.schedule.ui.UIUtils.MyToast

fun openOperation(info : String) {
    if(info.contains("学习通")) StartApp.startApp("com.chaoxing.mobile",".activity.SplashActivity")
    else if(info.contains("U校园")) StartApp.StartUri("https://u.unipus.cn/")
    else if(info.contains("雨课堂"))  StartApp.startApp("com.xuetangx.ykt",".MainActivity")
    else if(info.contains("教务")) StartApp.StartUri(MyApplication.JxglstuURL)
    else MyToast("此条未做点击适配")
}