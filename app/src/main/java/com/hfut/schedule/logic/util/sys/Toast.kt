package com.hfut.schedule.logic.util.sys

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.hfut.schedule.application.MyApplication

fun showToast(text: String,context: Context = MyApplication.context) = Handler(Looper.getMainLooper()).post {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}