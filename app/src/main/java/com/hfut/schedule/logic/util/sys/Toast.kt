package com.hfut.schedule.logic.util.sys

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.hfut.schedule.App.MyApplication

fun showToast(text : String) = Handler(Looper.getMainLooper()).post{ Toast.makeText(MyApplication.context,text,Toast.LENGTH_SHORT).show() }