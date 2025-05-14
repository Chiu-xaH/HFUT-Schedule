package com.hfut.schedule.service.tile

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.getCampus

class LoginWebTileService : TileService() {
    override fun onTileAdded() {
        super.onTileAdded()
        if(getCampus() != Campus.XUANCHENG) {
            qsTile.state = Tile.STATE_UNAVAILABLE
        } else {
            qsTile.state = Tile.STATE_INACTIVE
        }
        qsTile.updateTile()
    }
    override fun onClick() {
        super.onClick()
        Toast.makeText(this@LoginWebTileService, "已发送登录请求", Toast.LENGTH_SHORT).show()
    }
}

