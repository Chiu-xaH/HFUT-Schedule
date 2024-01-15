package com.hfut.schedule.ui.ComposeUI.Focus

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.dao.dataBase
import com.hfut.schedule.logic.datamodel.Focus.AddFocus
import com.hfut.schedule.logic.datamodel.MyAPIResponse
import com.hfut.schedule.logic.datamodel.MyList
import com.hfut.schedule.logic.datamodel.Schedule
import com.hfut.schedule.logic.datamodel.zjgd.BalanceResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jsoup.Jsoup


//使用指尖工大接口获取一卡通余额
fun zjgdcard(vm : LoginSuccessViewModel) {
    CoroutineScope(Job()).apply {
        launch {
            async {
                val auth = SharePrefs.prefs.getString("auth","")
                vm.getyue("bearer " + auth)
            }.await()
            async {
                delay(500)
                val yue = SharePrefs.prefs.getString("cardyue", MyApplication.NullCardblance)
                val yuedata = Gson().fromJson(yue, BalanceResponse::class.java)
                var num = yuedata.data.card[0].db_balance.toString()
                //待圈存
                var num_settle = yuedata.data.card[0].unsettle_amount.toString()


                var num_float = num.toFloat()
                var num_settle_float = num_settle.toFloat()
                num_float = num_float / 100
                SharePrefs.Save("card_now", num_float.toString())
                num_settle_float = num_settle_float / 100
                SharePrefs.Save("card_settle", num_settle_float.toString())
                num_float = num_settle_float + num_float

                SharePrefs.Save("card", num_float.toString())
            }
        }
    }
}

fun MySchedule() : MutableList<Schedule> {

    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
    val my = prefs.getString("my", MyApplication.NullMy)
    val data = Gson().fromJson(my, MyAPIResponse::class.java).Lessons
    val list = data.Schedule
    var Schedule = mutableListOf<Schedule>()
    list.forEach { Schedule.add(it) }
    return Schedule
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

fun MyWangKe() : MutableList<MyList> {

    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
    val my = prefs.getString("my", MyApplication.NullMy)
    val data = Gson().fromJson(my, MyAPIResponse::class.java).Lessons
    val list = data.MyList
    var Wabgke = mutableListOf<MyList>()
    list.forEach {  Wabgke.add(it) }
    return Wabgke
}
