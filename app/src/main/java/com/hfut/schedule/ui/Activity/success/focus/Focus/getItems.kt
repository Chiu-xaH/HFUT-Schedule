package com.hfut.schedule.ui.Activity.success.focus.Focus

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.ViewModel.UIViewModel
import com.hfut.schedule.logic.dao.dataBase
import com.hfut.schedule.logic.datamodel.Focus.AddFocus
import com.hfut.schedule.logic.datamodel.MyAPIResponse
import com.hfut.schedule.logic.datamodel.MyList
import com.hfut.schedule.logic.datamodel.Schedule
import com.hfut.schedule.logic.datamodel.zjgd.BalanceResponse
import com.hfut.schedule.logic.datamodel.zjgd.ReturnCard
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.UIUtils.MyToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception
import java.math.BigDecimal
import java.math.RoundingMode


//使用指尖工大接口获取一卡通余额
fun GetZjgdCard(vm : LoginSuccessViewModel,vmUI : UIViewModel) {
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
                                val yuedata = Gson().fromJson(result, BalanceResponse::class.java)
                                var num = yuedata.data.card[0].db_balance.toString()
                                //待圈存
                                var num_settle = yuedata.data.card[0].unsettle_amount.toString()
                                var num_float = num.toFloat()
                                var num_settle_float = num_settle.toFloat()
                                num_float /= 100
                                val now = num_float.toString()
                                SharePrefs.Save("card_now", num_float.toString())
                                num_settle_float /= 100
                                val settle = num_settle_float.toString()
                                SharePrefs.Save("card_settle", num_settle_float.toString())
                                num_float += num_settle_float
                                val bd = BigDecimal(num_float.toString())
                                val str = bd.setScale(2, RoundingMode.HALF_UP).toString()
                                val balance = str
                                SharePrefs.Save("card", str)
                                vmUI.CardValue.value = ReturnCard(balance, settle, now)
                                //return ReturnCard(balance, settle, now)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getTodayNet(vm : LoginSuccessViewModel, vmUI : UIViewModel) {

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
    var Schedule = mutableListOf<Schedule>()
    return try {
        val my = prefs.getString("my", MyApplication.NullMy)
        val data = Gson().fromJson(my, MyAPIResponse::class.java).Lessons
        val list = data.Schedule
        list.forEach { Schedule.add(it) }
        Schedule
    } catch (e : Exception) {
        e.printStackTrace()
        MyToast("解析出错,请联系开发者纠正")
        Schedule
    }
}

fun MyWangKe() : MutableList<MyList> {
    var Wabgke = mutableListOf<MyList>()
    return try {
        val my = prefs.getString("my", MyApplication.NullMy)
        val data = Gson().fromJson(my, MyAPIResponse::class.java).Lessons
        val list = data.MyList
        list.forEach {  Wabgke.add(it) }
        Wabgke
    } catch (e : Exception) {
        e.printStackTrace()
        MyToast("解析出错,请联系开发者纠正")
        Wabgke
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