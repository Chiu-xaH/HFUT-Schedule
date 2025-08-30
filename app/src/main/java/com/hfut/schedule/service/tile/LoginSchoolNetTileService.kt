package com.hfut.schedule.service.tile

import android.Manifest
import android.os.Handler
import android.os.Looper
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import androidx.annotation.RequiresPermission
import com.hfut.schedule.logic.network.repo.Repository
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.logic.util.sys.AppNotificationManager
import com.hfut.schedule.ui.component.network.onListenStateHolder
import com.hfut.schedule.logic.enumeration.CampusRegion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

open class LoginSchoolNetTileService(private val campus : CampusRegion) : TileService() {

    private val loginSchoolNetResponse = StateHolder<Boolean>()

    override fun onTileAdded() {
        super.onTileAdded()
        qsTile.label = "校园网"
        qsTile.updateTile()
    }

    fun toast(text : String) {
        Handler(Looper.getMainLooper()).post{
            Toast.makeText(this,text,Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun sendNotice(text : String) = try {
        AppNotificationManager.sendNotification(
            channel = AppNotificationManager.AppNotificationChannel.LOGIN_SCHOOL_NET,
            content = "登录校园网: $text",
            intent = null
        )
    } catch (e : Exception) {

    }
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun notice(text: String) {
        toast(text)
        sendNotice(text)
    }
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onClick() {
        super.onClick()
        val job = Job()
        CoroutineScope(job).launch {
            loginSchoolNetResponse.clear()
            Repository.loginSchoolNet(campus = campus,loginSchoolNetResponse = loginSchoolNetResponse)
            onListenStateHolder(loginSchoolNetResponse, onError = { codeInt,e ->
                val code = codeInt?.toString() ?: ""
                val eMsg = e?.message
                val t = if(eMsg?.contains("Unable to resolve host",ignoreCase = true) == true || eMsg?.contains("Failed to connect to",ignoreCase = true) == true ||  eMsg?.contains("Connection reset",ignoreCase = true) == true) {
                    "网络连接失败"
                } else if(eMsg?.contains("10000ms") == true) {
                    "网络连接超时"
                } else {
                    "错误 $code $e"
                }
                notice(t)
            }) { data ->
                if(data) {
                    qsTile.state = Tile.STATE_ACTIVE
                    qsTile.updateTile()
                    notice("登陆成功")
                } else {
                    notice("登陆失败")
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

