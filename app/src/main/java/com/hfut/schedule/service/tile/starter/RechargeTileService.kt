package com.hfut.schedule.service.tile.starter

import android.service.quicksettings.TileService
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.network.util.Constant

class RechargeTileService : TileService() {
    override fun onClick() {
        super.onClick()
        Starter.startAppUrl(this, Constant.ALIPAY_CARD_URL)
    }
}