package com.hfut.schedule.logic.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.ui.utils.components.MyToast

object ClipBoard {
    @JvmStatic
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
    @JvmStatic
    fun paste(): String {
        return try {
            val clipboard = MyApplication.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            if (clipboard.hasPrimaryClip() && clipboard.primaryClip!!.itemCount > 0) {
                clipboard.primaryClip!!.getItemAt(0).text?.toString() ?: ""
            } else {
                ""
            }
        } catch (e: Exception) {
            MyToast("获取剪切板内容失败")
            ""
        }
    }
}