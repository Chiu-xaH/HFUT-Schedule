package com.hfut.schedule.ui.screen.home.focus.funiction

import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import com.hfut.schedule.logic.model.zjgd.BalanceResponse
import com.hfut.schedule.logic.model.zjgd.ReturnCard
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.UIViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode


//使用指尖工大接口获取一卡通余额
suspend fun initCardNetwork(vm : NetWorkViewModel, vmUI : UIViewModel) = withContext(Dispatchers.IO) {
    val auth = prefs.getString("auth","")
    async { vm.getHuiXinCardInfo("bearer $auth") }.await()
    launch {
        Handler(Looper.getMainLooper()).post {
            vm.huiXinCardInfoResponse.observeForever { result ->
                if (result != null && result.contains("操作成功")) {
                    try {
                        val yuedata = Gson().fromJson(result, BalanceResponse::class.java).data.card[0]
                        val limite = transferNum(yuedata.autotrans_limite)
                        val amt = transferNum(yuedata.autotrans_amt)
                        val name = yuedata.name
                        val account = yuedata.account
                        var now = transferNum(yuedata.db_balance)
                        SharedPrefs.saveString("card_now", now.toString())
                        val settle = transferNum(yuedata.unsettle_amount)
                        SharedPrefs.saveString("card_settle", settle.toString())
                        now += settle
                        val str = formatDecimal(now.toDouble(),2)
                        val balance = str
                        SharedPrefs.saveString("card", str)
                        SharedPrefs.saveString("card_account", account)
                        vmUI.cardValue = ReturnCard(balance, settle.toString(), now.toString(),amt.toString(),limite.toString(),name)
                    } catch (_: Exception) { }
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


//@SuppressLint("Range")
//fun getCustomItems() : List<AddFocus> {
//    val list = mutableListOf<AddFocus>()
//    try {
//        val dbwritableDatabase =  dataBase.writableDatabase
//        val cursor = dbwritableDatabase.query("Book",null,null,null,null,null,null)
//        if(cursor.moveToFirst()){
//            do{
//                val titles = cursor.getString(cursor.getColumnIndex("title"))
//                val infos = cursor.getString(cursor.getColumnIndex("info"))
//                val remarks = cursor.getString(cursor.getColumnIndex("remark"))
//                val ids = cursor.getString(cursor.getColumnIndex("id")).toInt()
//                val AddFocu = AddFocus(titles,infos,remarks,ids)
//                list.add(AddFocu)
//            } while (cursor.moveToNext())
//        }
//        cursor.close()
//        return list
//    } catch (e : Exception) {
//        return emptyList()
//    }
//}

