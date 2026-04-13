package com.hfut.schedule.service.tile.starter

import android.service.quicksettings.TileService
import com.hfut.schedule.logic.util.sys.Starter

class ExpressTaoBaoTileService : TileService() {
    override fun onClick() {
        super.onClick()
        Starter.startTaoBaoExpress(this)
    }
}