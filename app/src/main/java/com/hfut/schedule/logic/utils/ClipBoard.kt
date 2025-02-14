package com.hfut.schedule.logic.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.ui.utils.components.MyToast

object ClipBoard {
    fun copy(str : String,tip : String = "已复制到剪切板") {
        try {
            val clipboard = MyApplication.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText(null, str)
            clipboard.setPrimaryClip(clipData)
            MyToast(tip)
        } catch (e:Exception) {
            MyToast("复制到剪切板失败")
        }
    }
}