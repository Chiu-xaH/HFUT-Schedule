package com.hfut.schedule.service.tile.base

import android.Manifest
import android.os.Handler
import android.os.Looper
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import androidx.annotation.RequiresPermission
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.network.repo.LoginSchoolNetRepository
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.logic.util.sys.AppNotificationManager
import com.hfut.schedule.ui.component.network.onListenStateHolder
import com.xah.shared.LogUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

open class LoginSchoolNetTileService(private val campus : CampusRegion) : TileService() {

    private val loginSchoolNetResponse = StateHolder<Boolean>()

    override fun onTileAdded() {
        super.onTileAdded()
        updateInactiveTile()
    }

    override fun onStartListening() {
        super.onStartListening()
        updateInactiveTile()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onClick() {
        super.onClick()
        val job = Job()
        CoroutineScope(job).launch {
            loginSchoolNetResponse.clear()
            LoginSchoolNetRepository.loginSchoolNet(campus = campus,loginSchoolNetResponse = loginSchoolNetResponse)
            onListenStateHolder(loginSchoolNetResponse, onError = { codeInt, e ->
                val code = codeInt?.toString() ?: ""
                val eMsg = e?.message
                val t = if (eMsg?.contains(
                        "Unable to resolve host",
                        ignoreCase = true
                    ) == true || eMsg?.contains(
                        "Failed to connect to",
                        ignoreCase = true
                    ) == true || eMsg?.contains("Connection reset", ignoreCase = true) == true
                ) {
                    "网络连接失败"
                } else if (eMsg?.contains("10000ms") == true) {
                    "网络连接超时"
                } else {
                    "错误 $code $e"
                }
                notice(t)
            }) { data ->
                if (data) {
                    updateInactiveTile()
                    notice("登陆成功")
                } else {
                    notice("登陆失败")
                }
                job.cancel()
            }
        }
    }

    private fun updateInactiveTile() {
        qsTile.apply {
            label = "校园网"
            state = Tile.STATE_INACTIVE
            updateTile()
        }
    }

    private fun toast(text : String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(this,text, Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun sendNotice(text : String) = try {
        AppNotificationManager.sendNotification(
            channel = AppNotificationManager.AppNotificationChannel.LOGIN_SCHOOL_NET,
            content = "登录校园网: $text",
            intent = null
        )
    } catch (e : Exception) {
        LogUtil.error(e)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun notice(text: String) {
        toast(text)
        sendNotice(text)
    }
}