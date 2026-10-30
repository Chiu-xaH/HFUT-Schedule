package com.hfut.schedule.logic.util.sys

import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.hfut.schedule.application.MyApplication

object TemperatureUtil {

    fun getBatteryTemperature(): Float? {
        val intent = MyApplication.context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        ) ?: return null
        val raw = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Int.MIN_VALUE)
        return if (raw == Int.MIN_VALUE) {
            null
        } else {
            raw / 10f
        }
    }

    fun isOverheating(): Boolean? {
        val temperature = getBatteryTemperature() ?: return null
        return temperature > 40f
    }
}