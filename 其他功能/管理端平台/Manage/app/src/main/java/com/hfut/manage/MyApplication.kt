package com.hfut.manage

import android.app.Application
import android.content.Context
import androidx.compose.ui.unit.dp

class MyApplication : Application() {
    companion object {
        lateinit var context: Context
        val Blur = 20.dp
        val URL = "http://47.113.187.217:890/"
        val GithubURL = "https://chiu-xah.github.io/"
    }
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}