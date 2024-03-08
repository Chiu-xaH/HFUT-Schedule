package com.hfut.schedule.widget

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import com.hfut.schedule.activity.CardActivity

class GlanceWidgetCard : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Column(
                modifier = GlanceModifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer).clickable( actionStartActivity<CardActivity>()),
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                GlanceCardUI()
            }
        }
    }
}