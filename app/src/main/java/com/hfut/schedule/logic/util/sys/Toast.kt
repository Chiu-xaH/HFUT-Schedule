package com.hfut.schedule.logic.util.sys

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.hfut.schedule.application.MyApplication
import com.xah.uicommon.util.ToastUtil

fun showToast(text: String,context: Context = MyApplication.context) = ToastUtil.showToast(context,text)