package com.hfut.schedule.logic

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.hfut.schedule.MyApplication

object OpenAlipay {
    fun openAlipay() {
            val intent = Intent(Intent.ACTION_DEFAULT, Uri.parse(MyApplication.AlipayURL))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            MyApplication.context.startActivity(intent)
    }
}


