package com.hfut.schedule.activity.funiction

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.utils.Starter

class ShortCutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Starter.startAppUrl(MyApplication.AlipayHotWaterURL)
        this.finish()
    }
}

