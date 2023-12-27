package com.hfut.schedule.logic.utils

import android.content.Intent
import android.net.Uri
import com.hfut.schedule.App.MyApplication

object OpenAlipay {
    fun openAlipay(URL : String) {
            val intent = Intent(Intent.ACTION_DEFAULT, Uri.parse(URL))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            MyApplication.context.startActivity(intent)
    }
}


