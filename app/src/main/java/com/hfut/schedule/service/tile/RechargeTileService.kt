package com.hfut.schedule.service.tile

import android.content.Intent
import android.service.quicksettings.TileService
import android.util.Log
import androidx.core.net.toUri
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.sys.showToast
import com.xah.uicommon.util.LogUtil

class RechargeTileService : TileService() {
    override fun onClick() {
        super.onClick()
        try {
            val intent = Intent(Intent.ACTION_DEFAULT, MyApplication.ALIPAY_CARD_URL.toUri())
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (e : Exception) {
            LogUtil.error(e)
            showToast("打开支付宝失败 " + e.message)
        }
    }
}