package com.hfut.schedule.ui.ComposeUI

import android.app.AlertDialog
import com.hfut.schedule.MyApplication
import com.hfut.schedule.activity.LoginActivity
import java.text.SimpleDateFormat
import java.util.Date

fun checkDate(startDate: String, endDate: String) {
    val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
    if (currentDate < startDate || currentDate > endDate) {
        val builder = AlertDialog.Builder(MyApplication.context)
        builder.setTitle("提示")
            .setMessage("请保证日期在2023-2024第一学期内，否则应用已过期，请更新")
            .setPositiveButton("获取更新") { dialog, which ->
                //跳转至浏览器打开URL
                LoginActivity().finish() }
            .setCancelable(false)
        builder.show()
    }
}