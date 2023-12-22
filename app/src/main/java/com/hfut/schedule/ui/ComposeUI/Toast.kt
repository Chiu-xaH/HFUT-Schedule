package com.hfut.schedule.ui.ComposeUI

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.hfut.schedule.MyApplication

fun MyToast(text : String) {
    Handler(Looper.getMainLooper()).post{ Toast.makeText(MyApplication.context,text,Toast.LENGTH_SHORT).show() }
}