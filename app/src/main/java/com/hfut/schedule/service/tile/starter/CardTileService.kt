package com.hfut.schedule.service.tile.starter

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.service.quicksettings.TileService
import android.widget.Toast
import com.hfut.schedule.activity.screen.CardActivity
import com.hfut.schedule.logic.util.sys.Starter.startActivitySafely
import com.xah.common.logic.util.LogUtil

class CardTileService : TileService() {
    override fun onClick() {
        super.onClick()

        unlockAndRun {
            try {
                val intent = Intent(this, CardActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
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