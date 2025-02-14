package com.hfut.schedule.ui.activity.home.focus.funictions

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.viewmodel.UIViewModel
import com.hfut.schedule.logic.dao.dataBase
import com.hfut.schedule.logic.beans.focus.AddFocus
import com.hfut.schedule.logic.beans.MyAPIResponse
import com.hfut.schedule.logic.beans.Schedule
import com.hfut.schedule.logic.beans.zjgd.BalanceResponse
import com.hfut.schedule.logic.beans.zjgd.ReturnCard
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.utils.components.MyToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode


//使用指尖工大接口获取一卡通余额
fun GetZjgdCard(vm : NetWorkViewModel, vmUI : UIViewModel) {
    CoroutineScope(Job()).apply {
        launch {
            async {
                val auth = prefs.getString("auth","")
                vm.getyue("bearer $auth")
            }.await()
            async {
                Handler(Looper.getMainLooper()).post {
                    vm.CardData.observeForever { result ->
                        if (result != null) {
                            if(result.contains("操作成功")) {
                                val yuedata = Gson().fromJson(result, BalanceResponse::class.java).data.card[0]
                                val limite = transferNum(yuedata.autotrans_limite)
                                val amt = transferNum(yuedata.autotrans_amt)
                                val name = yuedata.name
                                val account = yuedata.account
                              //  var num = yuedata.db_balance.toString()
                                //待圈存
                              //  var num_settle = yuedata.unsettle_amount.toString()
                              //  var num_float = num.toFloat()
                            //    var num_settle_float = num_settle.toFloat()
                              //  num_float /= 100
                              //  val now = num_float.toString()
                                var now = transferNum(yuedata.db_balance)
                                SharePrefs.saveString("card_now", now.toString())
                            //    num_settle_float /= 100
                              //  val settle = num_settle_float.toString()
                                var settle = transferNum(yuedata.unsettle_amount)
                                SharePrefs.saveString("card_settle", settle.toString())
                                now += settle
                                val bd = BigDecimal(now.toString())
                                val str = bd.setScale(2, RoundingMode.HALF_UP).toString()
                                val balance = str
                                SharePrefs.saveString("card", str)
                                SharePrefs.saveString("card_account", account)
                                SharePrefs.saveString("card_limit", limite.toString())
                                SharePrefs.saveString("card_amt", amt.toString())
                                vmUI.CardValue.value = ReturnCard(balance, settle.toString(), now.toString(),amt.toString(),limite.toString(),name)
                                //return ReturnCard(balance, settle, now)
                            }
                        }
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

fun getTodayNet(vm : NetWorkViewModel) {

        val CommuityTOKEN = prefs.getString("TOKEN","")
        CommuityTOKEN?.let { vm.getToday(it) }
}
@SuppressLint("Range")
fun AddedItems() : MutableList<AddFocus> {
    var AddFocus = mutableListOf<AddFocus>()
    val dbwritableDatabase =  dataBase.writableDatabase
    val cursor = dbwritableDatabase.query("Book",null,null,null,null,null,null)
    if(cursor.moveToFirst()){
        do{
            val titles = cursor.getString(cursor.getColumnIndex("title"))
            val infos = cursor.getString(cursor.getColumnIndex("info"))
            val remarks = cursor.getString(cursor.getColumnIndex("remark"))
            val ids = cursor.getString(cursor.getColumnIndex("id")).toInt()
            val AddFocu = AddFocus(titles,infos,remarks,ids)
            AddFocus.add(AddFocu)
        } while (cursor.moveToNext())
    }
    cursor.close()
    return AddFocus
}

fun MySchedule() : MutableList<Schedule> {
    return try {
        val my = prefs.getString("my", MyApplication.NullMy)
        val data = Gson().fromJson(my, MyAPIResponse::class.java).Lessons
        data.Schedule.toMutableList()
    } catch (e : Exception) {
        e.printStackTrace()
        MyToast("解析出错,请联系开发者纠正")
        mutableListOf()
    }
}

fun MyWangKe() : MutableList<Schedule> {
    return try {
        val my = prefs.getString("my", MyApplication.NullMy)
        val data = Gson().fromJson(my, MyAPIResponse::class.java).Lessons
        data.MyList.toMutableList()
    } catch (e : Exception) {
        e.printStackTrace()
        MyToast("解析出错,请联系开发者纠正")
        mutableListOf()
    }
}

fun getTimeStamp() : String? {
    val my = prefs.getString("my", MyApplication.NullMy)
    return try {
        if (my != null) {
            if(my.contains("更新")) {
                val data = Gson().fromJson(my, MyAPIResponse::class.java).TimeStamp
                data
            } else "未获取到"
        } else "未获取到"
    } catch (e : Exception) {
        null
    }
}