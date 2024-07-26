package com.hfut.schedule.ui.Activity.success.focus.Focus

import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.utils.StartApp
import com.hfut.schedule.ui.UIUtils.MyToast

fun openOperation(info : String) {
    if(info.contains("学习通")) StartApp.startLaunchAPK("com.chaoxing.mobile","学习通")
    else if(info.contains("U校园")) StartApp.StartUri("https://u.unipus.cn/")
    else if(info.contains("雨课堂"))  StartApp.startLaunchAPK("com.xuetangx.ykt","雨课堂")
    else if(info.contains("教务")) StartApp.StartUri(MyApplication.JxglstuURL)
    else MyToast("此条未做点击适配")
}