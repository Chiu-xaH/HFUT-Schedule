package com.hfut.schedule.ui.screen.home.focus.funiction

import android.os.Handler
import android.os.Looper
import com.hfut.schedule.network.api.model.response.json.huixin.HuiXinCardResponse
import com.hfut.schedule.logic.model.dto.HuiXinCardInfoDto
import com.hfut.schedule.logic.util.parse.roundOffString
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.network.core.GsonInstance
import com.hfut.schedule.ui.util.state.GlobalUiStateHolder
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.logic.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


//使用指尖工大接口获取一卡通余额
suspend fun initCardNetwork(vm : NetWorkViewModel) = withContext(Dispatchers.IO) {
    val auth = prefs.getString("auth","")
    async { vm.getHuiXinCardInfo("bearer $auth") }.await()
    launch {
        Handler(Looper.getMainLooper()).post {
            vm.huiXinCardInfoResponse.observeForever { result ->
                if (result != null && result.contains("操作成功")) {
                    try {
                        val yuedata = GsonInstance.fromJson(result, HuiXinCardResponse::class.java).data.card[0]
                        val limite = transferNum(yuedata.autoTransLimit)
                        val amt = transferNum(yuedata.autoTransAmt)
                        val name = yuedata.name
                        val account = yuedata.account
                        var now = transferNum(yuedata.balance)
                        SharedPrefs.saveString("card_now", now.toString())
                        val settle = transferNum(yuedata.unsettleAmount)
                        SharedPrefs.saveString("card_settle", settle.toString())
                        now += settle
                        val str = now.roundOffString(2)
                        val balance = str
                        SharedPrefs.saveString("card", str)
                        SharedPrefs.saveString("card_account", account)
                        GlobalUiStateHolder.cardValue = HuiXinCardInfoDto(balance, settle.toString(), now.toString(),amt.toString(),limite.toString(),name)
                    } catch (e: Exception) {
                        LogUtil.error(e)
                    }
                }
            }
        }
    }
}

fun transferNum(num : Int) : Float {
    var num_float = num.toFloat()
    num_float /= 100
    val settle = num_float
    return settle
}
