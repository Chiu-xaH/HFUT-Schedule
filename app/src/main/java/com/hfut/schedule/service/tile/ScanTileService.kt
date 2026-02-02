package com.hfut.schedule.service.tile

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.hfut.schedule.activity.MainActivity
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.screen.AppNavRoute
import com.xah.uicommon.util.LogUtil

class ScanTileService : TileService() {
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onClick() {
        super.onClick()

        unlockAndRun {
            try {
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("route", AppNavRoute.ScanQrCode.route)
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
                showToast("打开失败 " + e.message)
            }
        }
    }
}