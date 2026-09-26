package com.hfut.schedule.service.tile.base

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.service.quicksettings.TileService
import android.widget.Toast
import com.hfut.schedule.R
import com.hfut.schedule.activity.util.WebViewActivity
import com.hfut.schedule.logic.util.sys.Starter.startActivitySafely
import com.hfut.schedule.ui.util.getPureUrl
import com.xah.common.logic.util.LogUtil

open class BaseWebViewTileService(
    private val url : String,
    private val title : String = getPureUrl(url),
    private val icon : Int = R.drawable.net,
    private val cookies : String? = null,
) : TileService() {
    override fun onClick() {
        super.onClick()
        unlockAndRun {
            try {
                val intent = Intent(this, WebViewActivity::class.java).apply {
                    putExtra("url",url)
                    putExtra("title",title)
                    putExtra("icon",icon)
                    putExtra("cookies",cookies)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivitySafely(intent)
            } catch (e: Exception) {
                LogUtil.error(e)
                toast("打开失败 " + e.message)
            }
        }
    }

    private fun toast(text : String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(this,text, Toast.LENGTH_SHORT).show()
        }
    }
}