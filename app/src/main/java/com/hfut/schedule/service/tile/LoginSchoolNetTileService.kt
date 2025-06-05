package com.hfut.schedule.service.tile

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.hfut.schedule.logic.network.repo.Repository
import com.hfut.schedule.logic.util.network.StateHolder
import com.hfut.schedule.ui.component.onListenStateHolder
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus.XUANCHENG
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.getCampus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

open class LoginSchoolNetTileService(private val campus : Campus) : TileService() {

    val loginSchoolNetResponse = StateHolder<Boolean>()

    override fun onTileAdded() {
        super.onTileAdded()
        qsTile.label = "登录校园网"
        qsTile.updateTile()
    }

    override fun onClick() {
        super.onClick()
        val job = Job()
        CoroutineScope(job).launch {
            loginSchoolNetResponse.clear()
            Repository.loginSchoolNet(campus = campus,loginSchoolNetResponse = loginSchoolNetResponse)
            onListenStateHolder(loginSchoolNetResponse, onError = null) { data ->
                if(data) {
                    qsTile.state = Tile.STATE_ACTIVE
                    qsTile.updateTile()
                    showToast("登陆成功")
                } else {
                    showToast("登陆失败")
                }
                job.cancel()
            }
        }
    }

    override fun onStartListening() {
        super.onStartListening()
    }

    override fun onStopListening() {
        super.onStopListening()
    }

    override fun onTileRemoved() {
        super.onTileRemoved()
    }
}

