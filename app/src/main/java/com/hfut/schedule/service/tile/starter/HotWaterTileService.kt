package com.hfut.schedule.service.tile.starter

import android.service.quicksettings.TileService
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.network.api.model.Constant

class HotWaterTileService : TileService() {
    override fun onClick() {
        super.onClick()
        Starter.startAppUrl(this, Constant.ALIPAY_HOT_WATER_URL)
    }
}