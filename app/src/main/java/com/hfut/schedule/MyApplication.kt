package com.hfut.schedule

import android.app.Application
import android.content.Context

class MyApplication : Application() {
    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        // 在onCreate方法中初始化Context对象
        context = applicationContext
    }
}