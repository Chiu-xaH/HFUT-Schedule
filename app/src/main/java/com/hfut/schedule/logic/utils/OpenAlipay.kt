package com.hfut.schedule.logic.utils

import android.content.Intent
import android.net.Uri
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.ui.UIUtils.MyToast

object OpenAlipay {
    fun openAlipay(URL : String) {
        try {
            val intent = Intent(Intent.ACTION_DEFAULT, Uri.parse(URL))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            MyApplication.context.startActivity(intent)
        } catch (e : Exception) {
          MyToast("打开支付宝失败")
        }
    }
}


