package com.hfut.manage.ui.utils

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.hfut.manage.MyApplication


fun MyToast(text : String) {
    Handler(Looper.getMainLooper()).post{ Toast.makeText(MyApplication.context,text,Toast.LENGTH_SHORT).show() }
}