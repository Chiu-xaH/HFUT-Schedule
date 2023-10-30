package com.hfut.schedule

import android.app.Application
import android.content.Context

class MyApplication : Application() {
    companion object {
        lateinit var context: Context
        const val LoginURL = "https://cas.hfut.edu.cn/"
        const val JxglstuURL = "http://jxglstu.hfut.edu.cn/eams5-student/"
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}