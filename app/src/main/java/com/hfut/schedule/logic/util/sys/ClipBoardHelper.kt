package com.hfut.schedule.logic.util.sys

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.hfut.schedule.application.MyApplication
import com.xah.uicommon.util.LogUtil

object ClipBoardHelper {
    private const val DEFAULT_TIPS = "已复制到剪切板"
    fun copy(str : String, tips : String? = DEFAULT_TIPS) {
        try {
            val clipboard = MyApplication.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText(null, str)
            clipboard.setPrimaryClip(clipData)
            tips?.let { showToast(it) }
        } catch (e : Exception) {
            LogUtil.error(e)
            showToast("复制到剪切板失败")
        }
    }

    fun paste(): String? {
        return try {
            val clipboard = MyApplication.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            if (clipboard.hasPrimaryClip() && clipboard.primaryClip!!.itemCount > 0) {
                clipboard.primaryClip!!.getItemAt(0).text?.toString()
            } else {
                null
            }
        } catch (e: Exception) {
            LogUtil.error(e)
            showToast("获取剪切板内容失败")
            null
        }
    }
}