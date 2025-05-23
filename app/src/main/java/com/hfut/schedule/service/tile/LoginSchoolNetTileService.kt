package com.hfut.schedule.service.tile

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.hfut.schedule.logic.network.api.LoginWebsService
import com.hfut.schedule.logic.network.servicecreator.Login.LoginWeb2ServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Login.LoginWebServiceCreator
import com.hfut.schedule.logic.util.network.NetWork.launchRequestSimple
import com.hfut.schedule.logic.util.network.SimpleStateHolder
import com.hfut.schedule.ui.component.onListenStateHolder
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.getCardPsk
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus.HEFEI
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus.XUANCHENG
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.getCampus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

class LoginSchoolNetTileService : TileService() {
    fun canUse() {
        if(getCampus() != XUANCHENG) {
            qsTile.state = Tile.STATE_UNAVAILABLE
        } else {
            qsTile.state = Tile.STATE_INACTIVE
        }
        qsTile.updateTile()
    }


    val loginSchoolNetResponse = SimpleStateHolder<Boolean>()
    private val loginWeb = LoginWebServiceCreator.create(LoginWebsService::class.java)
    private val loginWeb2 = LoginWeb2ServiceCreator.create(LoginWebsService::class.java)
    suspend fun loginSchoolNet(campus: Campus = getCampus()) = withContext(Dispatchers.IO) {
        getPersonInfo().username?.let { uid -> getCardPsk()?.let { pwd ->
            when(campus) {
                HEFEI -> {
                    showToast("暂未支持")
                    return@withContext
                }
                XUANCHENG -> {
                    val location = "宣州Login"
                    launch {
                        launchRequestSimple(
                            holder = loginSchoolNetResponse,
                            request = { loginWeb.loginWeb(uid, pwd,location).awaitResponse() },
                            transformSuccess = { _,body -> parseLoginSchoolNet(body) }
                        )
                    }
                    launch {
                        launchRequestSimple(
                            holder = loginSchoolNetResponse,
                            request = { loginWeb2.loginWeb(uid, pwd,location).awaitResponse() },
                            transformSuccess = { _,body -> parseLoginSchoolNet(body) }
                        )
                    }
                }
            }
        } }
    }
    private fun parseLoginSchoolNet(result : String) : Boolean = try {
        if(result.contains("登录成功") && !result.contains("已使用")) {
            true
        } else if(result.contains("已使用")) {
            false
        } else {
            throw Exception(result)
        }
    } catch (e : Exception) { throw e }

    override fun onTileAdded() {
        super.onTileAdded()
        canUse()
    }

    override fun onClick() {
        super.onClick()
        val job = Job()
        CoroutineScope(job).launch {
            loginSchoolNetResponse.clear()
            loginSchoolNet()
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
        canUse()
    }

    override fun onTileRemoved() {
        super.onTileRemoved()
    }
}

