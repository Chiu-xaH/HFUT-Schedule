package com.hfut.schedule.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.utils.StartApp

class ShortCutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StartApp.openAlipay(MyApplication.AlipayHotWaterURL)
        this.finish()
    }
}

