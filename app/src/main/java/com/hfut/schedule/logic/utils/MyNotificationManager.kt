package com.hfut.schedule.logic.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.VersionUtils.androidVersionToSDK

object MyNotificationManager {
    private val manager = MyApplication.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    enum class NotificationChannel(val title : String,val importance: Int) {
        DOWNLOAD_OK(title = "下载完成通知",NotificationManager.IMPORTANCE_DEFAULT)
    }

    // 创建通知通道（适用于 Android 8.0+）
    fun createNotificationChannel(channelId: String, channelName: String, importance: Int) {
        if(isChannelCreated(channelId)) {
            return
        }
        if (VersionUtils.sdkInt >= androidVersionToSDK("8")!!) {
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "通知通道"
            }
            manager.createNotificationChannel(channel)
        }
    }

    // 显示通知
    fun showNotification(
        channelId: String,
        notificationId: Int,
        title: String,
        content: String,
        // 点击操作
        intent: PendingIntent? = null,
        smallIcon: Int = R.drawable.notifications
    ) {
        val builder = NotificationCompat.Builder(MyApplication.context, channelId)
            .setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // 点击后自动取消
            .apply {
                intent?.let { setContentIntent(it) }
            }

        manager.notify(notificationId, builder.build())
    }

    // 取消某个通知
    fun cancelNotification(notificationId: Int) {
        manager.cancel(notificationId)
    }

    // 取消所有通知
    fun cancelAllNotifications() {
        manager.cancelAll()
    }

    private fun isChannelCreated(channelId: String): Boolean {
        return if (VersionUtils.sdkInt >= androidVersionToSDK("8")!!) {
            manager.getNotificationChannel(channelId) != null
        } else {
            true // 低于 Android 8.0（Oreo）不需要通道，默认返回 true
        }
    }


    fun initDownloadChannel() {
        createNotificationChannel(
            NotificationChannel.DOWNLOAD_OK.name,
            NotificationChannel.DOWNLOAD_OK.title,
            NotificationChannel.DOWNLOAD_OK.importance
        )
    }
}
