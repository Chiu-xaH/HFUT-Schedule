package com.hfut.schedule.logic.util.sys

import android.content.Context
import androidx.annotation.StringRes
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.xah.uicommon.util.ToastUtil

fun showDevelopingToast(context : Context = MyApplication.context) = showToast(R.string.toast_developing,context)

//fun showToast(text: UiText,context: Context = MyApplication.context) = ToastUtil.showToast(context,text.asString(context))

fun showToast(text: String,context: Context = MyApplication.context) = ToastUtil.showToast(context,text)

private fun showToast(@StringRes text: Int,context: Context = MyApplication.context) = ToastUtil.showToast(context,context.getString(text))

