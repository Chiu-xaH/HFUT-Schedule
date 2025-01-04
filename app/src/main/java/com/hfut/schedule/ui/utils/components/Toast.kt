package com.hfut.schedule.ui.utils.components

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.hfut.schedule.App.MyApplication
var num = 1
var num2  = 1
fun MyToast(text : String) {
    Handler(Looper.getMainLooper()).post{ Toast.makeText(MyApplication.context,text,Toast.LENGTH_SHORT).show() }
}