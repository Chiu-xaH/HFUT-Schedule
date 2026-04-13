package com.hfut.schedule.service.tile.starter

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.service.quicksettings.TileService
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.hfut.schedule.activity.screen.CardActivity
import com.xah.shared.LogUtil

class CardTileService() : TileService() {
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onClick() {
        super.onClick()

        unlockAndRun {
            try {
                val intent = Intent(this, CardActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }

                // 创建 PendingIntent
                val pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                // 用新版 API
                startActivityAndCollapse(pendingIntent)

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