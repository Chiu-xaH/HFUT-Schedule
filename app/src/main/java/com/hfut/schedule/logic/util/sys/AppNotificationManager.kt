package com.hfut.schedule.logic.util.sys

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.getPassedMinutesInRange
import java.time.Duration
import kotlin.math.ceil
import kotlin.math.roundToInt

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
    private const val COURSE_LIVE_ID_BASE = 18000
    private const val EXTRA_REQUEST_PROMOTED_ONGOING = "android.requestPromotedOngoing"
    private val manager = MyApplication.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // name作为其channelId,title为标题,importance重要性
    enum class AppNotificationChannel(val title : String, val importance: Int) {
        DOWNLOAD_OK(title = "下载完成通知",NotificationManager.IMPORTANCE_DEFAULT),
        COURSE_PROGRESS(title = "上课进度",NotificationManager.IMPORTANCE_HIGH),
        COURSE_LIVE_UPDATE(title = "上课提醒", NotificationManager.IMPORTANCE_HIGH),
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

    private fun canPostNotification(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    MyApplication.context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

    @RequiresApi(36)
    private var progressStyle = if(AppVersion.sdkInt >= 36) {
        NotificationCompat.ProgressStyle()
            .setStyledByProgress(false)
            .setProgressTrackerIcon(IconCompat.createWithResource(MyApplication.context, R.drawable.expand_circle_right))
            .setProgressSegments(
                listOf(
                    NotificationCompat.ProgressStyle.Segment(500).setColor(Color.YELLOW),
                    NotificationCompat.ProgressStyle.Segment(100).setColor(Color.GREEN),
                    NotificationCompat.ProgressStyle.Segment(500).setColor(Color.YELLOW),
                )
            )
    } else null


    @RequiresApi(36)
    fun updateCourseProgress(courseName : String,startTime : String,endTime : String) = if(AppVersion.sdkInt >= 36) {
        createNotificationChannel(AppNotificationChannel.COURSE_PROGRESS)
        // 设置进度
        getPassedMinutesInRange(startTime,endTime)?.let { progressStyle!!.progress = it*10 } ?: manager.cancel(COURSE_PROGRESS_ID)

        val builder = NotificationCompat.Builder(MyApplication.context,AppNotificationChannel.COURSE_PROGRESS.name)
            .setSmallIcon(R.drawable.notifications)
            .setContentTitle(AppNotificationChannel.COURSE_PROGRESS.title)
            .setContentText(courseName)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(progressStyle)
            .setShortCriticalText("ss")
            .setWhen(System.currentTimeMillis().plus(11 * 60 * 1000 /* 10 min */))
            .setUsesChronometer(true)
            .setChronometerCountDown(true)
        // 更新
        manager.cancel(COURSE_PROGRESS_ID)
        manager.notify(COURSE_PROGRESS_ID, builder.build())
    } else Unit

    fun showCourseLiveUpdate(
        courseName: String,
        place: String?,
        teacher: String?,
        startMillis: Long,
        endMillis: Long,
        contentIntent: PendingIntent,
        asForeground: Boolean = false,
    ): Notification? {
        if (!canPostNotification()) return null
        createNotificationChannel(AppNotificationChannel.COURSE_LIVE_UPDATE)

        val context = MyApplication.context
        val notificationId = courseLiveNotificationId(courseName, startMillis)
        val placeText = place?.takeIf { it.isNotBlank() } ?: "教室待确认"
        val teacherText = teacher?.takeIf { it.isNotBlank() } ?: "待确认"
        val contentText = "老师:$teacherText | 地点:$placeText"
        val subText = buildCourseLiveSubText(startMillis, endMillis)
        val shortPlaceText = buildShortPlaceText(placeText)

        val notification = if (AppVersion.sdkInt >= 36) {
            buildAndroid16CourseLiveNotification(
                courseName = courseName,
                contentText = contentText,
                subText = subText,
                shortText = shortPlaceText,
                startMillis = startMillis,
                endMillis = endMillis,
                contentIntent = contentIntent
            )
        } else {
            NotificationCompat.Builder(context, AppNotificationChannel.COURSE_LIVE_UPDATE.name)
                .setSmallIcon(R.drawable.hfut_badge)
                .setContentTitle("上课提醒：$courseName")
                .setContentText(contentText)
                .setStyle(NotificationCompat.BigTextStyle().bigText("$contentText\n点击查看课程详细信息"))
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .build()
        }

        if (!asForeground) {
            manager.notify(notificationId, notification)
        }
        return notification
    }

    @RequiresApi(36)
    private fun buildAndroid16CourseLiveNotification(
        courseName: String,
        contentText: String,
        subText: String,
        shortText: String,
        startMillis: Long,
        endMillis: Long,
        contentIntent: PendingIntent,
    ): Notification {
        val now = System.currentTimeMillis()
        val totalMinutes = Duration.ofMillis((endMillis - startMillis).coerceAtLeast(1)).toMinutes().toInt().coerceAtLeast(1)
        val passedMinutes = Duration.ofMillis((now - startMillis).coerceAtLeast(0)).toMinutes().toInt()
        val progress = ((passedMinutes.toFloat() / totalMinutes) * 1000).roundToInt().coerceIn(0, 1000)
        val passedSegment = progress.coerceIn(1, 999)

        val style = Notification.ProgressStyle()
            .setStyledByProgress(false)
            .setProgress(progress)
            .setProgressSegments(
                listOf(
                    Notification.ProgressStyle.Segment(passedSegment).setColor(Color.GREEN),
                    Notification.ProgressStyle.Segment(1000 - passedSegment).setColor(Color.LTGRAY),
                )
            )

        return Notification.Builder(MyApplication.context, AppNotificationChannel.COURSE_LIVE_UPDATE.name)
            .setSmallIcon(R.drawable.hfut_badge)
            .setLargeIcon(Icon.createWithResource(MyApplication.context, R.drawable.hfut_badge))
            .setContentTitle("上课提醒：$courseName")
            .setContentText(contentText)
            .setSubText(subText)
            .setShortCriticalText(shortText)
            .setContentIntent(contentIntent)
            .setShowWhen(false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(Notification.PRIORITY_HIGH)
            .setCategory(Notification.CATEGORY_EVENT)
            .addExtras(Bundle().apply {
                putBoolean(EXTRA_REQUEST_PROMOTED_ONGOING, true)
            })
            .setStyle(style)
            .build()
    }

    private fun buildCourseLiveSubText(startMillis: Long, endMillis: Long): String {
        val now = System.currentTimeMillis()
        return if (now < startMillis) {
            val minutes = ceil((startMillis - now) / 60_000.0).toInt().coerceAtLeast(1)
            "${minutes}分钟后上课"
        } else {
            val minutes = ceil((endMillis - now) / 60_000.0).toInt().coerceAtLeast(1)
            "${minutes}分钟后下课"
        }
    }

    private fun buildShortPlaceText(placeText: String): String {
        val compactText = placeText
            .replace(Regex("[(（].*?[)）]"), "")
            .replace(Regex("\\s+"), "")
            .substringBefore("、")
            .substringBefore(",")
            .substringBefore("，")
            .substringBefore(";")
            .substringBefore("；")
            .replace("翡翠湖校区", "")
            .replace("屯溪路校区", "")
            .replace("宣城校区", "")
            .replace("敬亭学堂", "敬亭")
            .replace("新安学堂", "新安")
            .replace("学堂", "")
            .replace("教学楼", "教")
            .replace("综合楼", "综")
            .replace("科教楼", "科教")
            .replace("号楼", "楼")
            .ifBlank { "教室待定" }

        if (compactText.length <= 6) return compactText

        Regex("[\\u4e00-\\u9fa5A-Za-z]{0,3}\\d{2,4}[A-Za-z]?")
            .findAll(compactText)
            .lastOrNull()
            ?.value
            ?.takeIf { it.isNotBlank() }
            ?.let { return it.take(6) }

        return compactText.take(6)
    }

    fun cancelCourseLiveUpdate(courseName: String, startMillis: Long) {
        manager.cancel(courseLiveNotificationId(courseName, startMillis))
    }

    internal fun courseLiveNotificationId(courseName: String, startMillis: Long): Int =
        COURSE_LIVE_ID_BASE + "$courseName@$startMillis".hashCode().let { if (it == Int.MIN_VALUE) 0 else kotlin.math.abs(it % 8000) }
}
