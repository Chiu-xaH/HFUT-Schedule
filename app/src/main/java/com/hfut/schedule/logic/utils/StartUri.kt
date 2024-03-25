package com.hfut.schedule.logic.utils

import android.content.Intent
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.ui.UIUtils.MyToast

object StartUri {
    fun StartUri(Uri : String) {
        try {
            val it = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(Uri))
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            MyApplication.context.startActivity(it)
        } catch (e : Exception) {
            MyToast("启动浏览器失败")
        }
    }
}
