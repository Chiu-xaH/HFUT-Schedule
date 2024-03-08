package com.hfut.schedule.widget

import androidx.compose.runtime.Composable
import androidx.glance.Button
import androidx.glance.action.actionStartActivity
import com.hfut.schedule.activity.CardActivity
import com.hfut.schedule.logic.utils.SharePrefs

@Composable
fun GlanceCardUI() {
    val now = SharePrefs.prefs.getString("card_now","00")
    Button(text = "余额 $now", onClick = { actionStartActivity<CardActivity>() })
}
