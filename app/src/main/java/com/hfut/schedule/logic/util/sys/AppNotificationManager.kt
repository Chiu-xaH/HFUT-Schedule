package com.hfut.schedule.logic.util.sys

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Icon
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.getPassedMinutesInRange

object AppNotificationManager {

    // 发送通知使用方法 调用sendNotification方法，传入AppNotificationChannel即可，AppNotificationChannel需要自己添加
    /*
    例如：AppNotificationManager.sendNotification(
            channel = AppNotificationManager.AppNotificationChannel.LOGIN_SCHOOL_NET,
            content = "登录校园网: $text",
            intent = null
        )
     */
    // 发送通知自动递增
    private var currentNotificationId = 1
    // 进度通知 新建进度通知则递增，更新进度通知沿用原有id
    private const val COURSE_PROGRESS_ID = 999
    private val manager = MyApplication.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // name作为其channelId,title为标题,importance重要性
    enum class AppNotificationChannel(val title : String, val importance: Int) {
        DOWNLOAD_OK(title = "下载完成通知",NotificationManager.IMPORTANCE_DEFAULT),
        COURSE_PROGRESS(title = "上课进度",NotificationManager.IMPORTANCE_DEFAULT),
        LOGIN_SCHOOL_NET(title = "磁贴通知",NotificationManager.IMPORTANCE_HIGH)
    }

    // 创建通知通道
    private fun createNotificationChannel(appNotificationChannel : AppNotificationChannel) = with(appNotificationChannel) {
        if(isChannelCreated(name)) {
            return
        }
        val channel = NotificationChannel(name, title, importance)
        manager.createNotificationChannel(channel)
    }

    // 检查通道是否创建->发送通知->返回id
    fun sendNotification(
        channel : AppNotificationChannel,
        content: String,
        // 点击操作
        intent: PendingIntent? = null,
        smallIcon: Int = R.drawable.notifications
    ) : Int {
        // 检查通道是否创建
        createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(MyApplication.context, channel.name)
            .setSmallIcon(smallIcon)
            .setContentTitle(channel.title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // 点击后自动取消
            .apply {
                intent?.let { setContentIntent(it) }
            }

        manager.notify(currentNotificationId, builder.build())

        return currentNotificationId++
    }

    // 取消某个通知
    fun cancelNotification(notificationId: Int) = manager.cancel(notificationId)

    // 取消所有通知
    fun cancelAllNotifications() {
        manager.cancelAll()
        currentNotificationId = 1
    }

    // APP最低版本就是8，不需要多余的判断
    private fun isChannelCreated(channelId: String): Boolean = manager.getNotificationChannel(channelId) != null

    @RequiresApi(36)
    private var progressStyle = if(AppVersion.sdkInt >= 36) {
        Notification.ProgressStyle()
            .setStyledByProgress(false)
            .setProgressTrackerIcon(Icon.createWithResource(MyApplication.context, R.drawable.expand_circle_right))
            .setProgressSegments(
                listOf(
                    Notification.ProgressStyle.Segment(500).setColor(Color.YELLOW),
                    Notification.ProgressStyle.Segment(100).setColor(Color.GREEN),
                    Notification.ProgressStyle.Segment(500).setColor(Color.YELLOW),
                )
            )
    } else null


    @RequiresApi(36)
    fun updateCourseProgress(courseName : String,startTime : String,endTime : String) = if(AppVersion.sdkInt >= 36) {
        createNotificationChannel(AppNotificationChannel.COURSE_PROGRESS)
        // 设置进度
        getPassedMinutesInRange(startTime,endTime)?.let { progressStyle!!.progress = it*10 } ?: manager.cancel(COURSE_PROGRESS_ID)

        val builder = Notification.Builder(MyApplication.context,AppNotificationChannel.COURSE_PROGRESS.name)
            .setSmallIcon(R.drawable.notifications)
            .setContentTitle(AppNotificationChannel.COURSE_PROGRESS.title)
            .setContentText(courseName)
            .setPriority(Notification.PRIORITY_DEFAULT)
            .setStyle(progressStyle)
        // 更新
        manager.cancel(COURSE_PROGRESS_ID)
        manager.notify(COURSE_PROGRESS_ID, builder.build())
    } else null
}
