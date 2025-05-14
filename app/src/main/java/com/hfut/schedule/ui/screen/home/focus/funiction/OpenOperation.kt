package com.hfut.schedule.ui.screen.home.focus.funiction

import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.showToast
// U校园 U校园AI版 微信 超星学习通 长江雨课堂 中国大学MOOC
fun openOperation(info : String) {
    if(info.contains("学习通")) Starter.startLaunchAPK("com.chaoxing.mobile","学习通")
    else if(info.contains("U校园")) Starter.startWebUrl(if(!info.contains("AI",ignoreCase = true))"https://u.unipus.cn/" else "https://ucloud.unipus.cn/")
    else if(info.contains("雨课堂"))  Starter.startLaunchAPK("com.xuetangx.ykt","雨课堂")
    else if(info.contains("MOOC"))  Starter.startLaunchAPK("com.netease.edu.ucmooc","中国大学MOOC")
    else if(isWebUrl(info)) Starter.startWebUrl(info)
    else showToast("此条未做点击适配")
}
fun isWebUrl(text: String): Boolean {
    // 正则匹配常见的网址格式，例如：www.xxx.com、xxx.com、http(s)://xxx.com 等
    val urlRegex = Regex(
        pattern = """^(https?://)?([a-zA-Z0-9\-]+\.)+[a-zA-Z]{2,}(/\S*)?$""",
        option = RegexOption.IGNORE_CASE
    )
    return urlRegex.matches(text.trim())
}
