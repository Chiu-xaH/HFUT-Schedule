package com.hfut.schedule.service.tile.base

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.service.quicksettings.TileService
import android.widget.Toast
import com.hfut.schedule.activity.MainActivity
import com.hfut.schedule.logic.util.sys.Starter.startActivitySafely
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.logic.util.LogUtil

open class BaseDestinationTileService(
    private val destination: NavDestination
) : TileService() {
    override fun onClick() {
        super.onClick()

        unlockAndRun {
            try {
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("route", destination::class.java.name)
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

