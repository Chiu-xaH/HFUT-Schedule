package com.hfut.schedule.logic.utils

import android.content.Intent
import com.hfut.schedule.App.MyApplication

object StartUri {
    fun StartUri(Uri : String) {
        val it = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(Uri))
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        MyApplication.context.startActivity(it)
    }
}
