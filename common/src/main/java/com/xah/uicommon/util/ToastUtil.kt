package com.xah.uicommon.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

object ToastUtil {
    private var toast : Toast? = null

    fun showToast(context : Context,msg : String) {
        Handler(Looper.getMainLooper()).post {
            toast?.cancel()
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT)
            toast?.show()
        }
    }
}