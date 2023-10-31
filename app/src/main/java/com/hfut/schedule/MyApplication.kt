package com.hfut.schedule

import android.app.Application
import android.content.Context

class MyApplication : Application() {
    companion object {
        lateinit var context: Context
        const val LoginURL = "https://cas.hfut.edu.cn/"
        const val JxglstuURL = "http://jxglstu.hfut.edu.cn/eams5-student/"
        const val RedirectURL = "https://cas.hfut.edu.cn/cas/login?service=http%3A%2F%2Fjxglstu.hfut.edu.cn%2Feams5-student%2Fneusoft-sso%2Flogin&exception.message=A+problem+occurred+restoring+the+flow+execution+with+key+%27e1s1%27"
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}