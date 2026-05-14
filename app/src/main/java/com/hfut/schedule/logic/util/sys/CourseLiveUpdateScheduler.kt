package com.hfut.schedule.logic.util.sys

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.ContextCompat
import com.hfut.schedule.activity.MainActivity
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.receiver.CourseLiveUpdateReceiver
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.CourseDetailOrigin
import com.xah.uicommon.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.Calendar

object CourseLiveUpdateScheduler {
    const val ACTION_SHOW = "com.hfut.schedule.action.SHOW_COURSE_LIVE_UPDATE"
    const val ACTION_FINISH = "com.hfut.schedule.action.FINISH_COURSE_LIVE_UPDATE"
    const val ACTION_RESCHEDULE = "com.hfut.schedule.action.RESCHEDULE_COURSE_LIVE_UPDATE"
    const val ACTION_REFRESH = "com.hfut.schedule.action.REFRESH_COURSE_LIVE_UPDATE"

    const val EXTRA_COURSE_NAME = "course_name"
    const val EXTRA_PLACE = "place"
    const val EXTRA_TEACHER = "teacher"
    const val EXTRA_START_MILLIS = "start_millis"
    const val EXTRA_END_MILLIS = "end_millis"

    private const val DEFAULT_REMIND_BEFORE_MINUTES = 20
    private const val WINDOW_CHECK_INTERVAL_MILLIS = 10 * 60_000L

    suspend fun scheduleAll(context: Context = MyApplication.context): Int = withContext(Dispatchers.IO) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val now = System.currentTimeMillis()
        val remindBeforeMinutes = getRemindBeforeMinutes()
        var scheduledCount = 0

        getJxglstuCourseSchedule().forEach { course ->
            val startMillis = course.time.start.toMillis()
            val endMillis = course.time.end.toMillis()
            val triggerMillis = startMillis - remindBeforeMinutes * 60_000L
            if (endMillis <= now) return@forEach

            if (triggerMillis > now) {
                val showIntent = Intent(context, CourseLiveUpdateReceiver::class.java).apply {
                    action = ACTION_SHOW
                    putCourseExtras(course.courseName, course.place, course.teacher, startMillis, endMillis)
                }
                setCourseAlarm(
                    alarmManager = alarmManager,
                    triggerMillis = triggerMillis,
                    pendingIntent = PendingIntent.getBroadcast(
                        context,
                        requestCode(course.courseName, startMillis, ACTION_SHOW),
                        showIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    ),
                    alarmClockInfo = AlarmManager.AlarmClockInfo(
                        triggerMillis,
                        buildOpenCourseIntent(context, course.courseName, startMillis)
                    )
                )
            }

            scheduleWindowChecks(
                context = context,
                alarmManager = alarmManager,
                courseName = course.courseName,
                place = course.place,
                teacher = course.teacher,
                remindStartMillis = triggerMillis,
                startMillis = startMillis,
                endMillis = endMillis,
                now = now
            )

            val finishIntent = Intent(context, CourseLiveUpdateReceiver::class.java).apply {
                action = ACTION_FINISH
                putCourseExtras(course.courseName, course.place, course.teacher, startMillis, endMillis)
            }
            setCourseAlarm(
                alarmManager = alarmManager,
                triggerMillis = endMillis,
                pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode(course.courseName, startMillis, ACTION_FINISH),
                    finishIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            scheduledCount++
        }

        scheduledCount
    }

    suspend fun showCurrentWindowCourses(context: Context = MyApplication.context): Int = withContext(Dispatchers.IO) {
        if (!canPostNotification(context)) return@withContext 0

        val now = System.currentTimeMillis()
        val remindBeforeMinutes = getRemindBeforeMinutes()
        var shownCount = 0
        getJxglstuCourseSchedule().forEach { course ->
            val startMillis = course.time.start.toMillis()
            val endMillis = course.time.end.toMillis()
            val remindStartMillis = startMillis - remindBeforeMinutes * 60_000L
            if (now !in remindStartMillis until endMillis) return@forEach

            AppNotificationManager.showCourseLiveUpdate(
                courseName = course.courseName,
                place = course.place,
                teacher = course.teacher,
                startMillis = startMillis,
                endMillis = endMillis,
                contentIntent = buildOpenCourseIntent(context, course.courseName, startMillis)
            )
            shownCount++
        }
        shownCount
    }

    suspend fun cancelAll(context: Context = MyApplication.context) = withContext(Dispatchers.IO) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val remindBeforeMinutes = getRemindBeforeMinutes()
        getJxglstuCourseSchedule().forEach { course ->
            val startMillis = course.time.start.toMillis()
            val endMillis = course.time.end.toMillis()
            val remindStartMillis = startMillis - remindBeforeMinutes * 60_000L
            listOf(ACTION_SHOW, ACTION_FINISH, ACTION_REFRESH).forEach { action ->
                val intent = Intent(context, CourseLiveUpdateReceiver::class.java).apply {
                    this.action = action
                    putCourseExtras(course.courseName, course.place, course.teacher, startMillis, endMillis)
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode(course.courseName, startMillis, action),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.cancel(pendingIntent)
            }
            cancelWindowChecks(
                context = context,
                alarmManager = alarmManager,
                courseName = course.courseName,
                place = course.place,
                teacher = course.teacher,
                remindStartMillis = remindStartMillis,
                startMillis = startMillis,
                endMillis = endMillis
            )
            AppNotificationManager.cancelCourseLiveUpdate(course.courseName, startMillis)
        }
    }

    private fun scheduleWindowChecks(
        context: Context,
        alarmManager: AlarmManager,
        courseName: String,
        place: String?,
        teacher: String?,
        remindStartMillis: Long,
        startMillis: Long,
        endMillis: Long,
        now: Long,
    ) {
        var checkMillis = maxOf(startMillis, remindStartMillis + WINDOW_CHECK_INTERVAL_MILLIS)
        while (checkMillis < endMillis) {
            if (checkMillis > now) {
                val intent = Intent(context, CourseLiveUpdateReceiver::class.java).apply {
                    action = ACTION_SHOW
                    putCourseExtras(courseName, place, teacher, startMillis, endMillis)
                }
                setCourseAlarm(
                    alarmManager = alarmManager,
                    triggerMillis = checkMillis,
                    pendingIntent = PendingIntent.getBroadcast(
                        context,
                        requestCode(courseName, startMillis, "$ACTION_SHOW@$checkMillis"),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }
            checkMillis += WINDOW_CHECK_INTERVAL_MILLIS
        }
    }

    private fun cancelWindowChecks(
        context: Context,
        alarmManager: AlarmManager,
        courseName: String,
        place: String?,
        teacher: String?,
        remindStartMillis: Long,
        startMillis: Long,
        endMillis: Long,
    ) {
        var checkMillis = maxOf(startMillis, remindStartMillis + WINDOW_CHECK_INTERVAL_MILLIS)
        while (checkMillis < endMillis) {
            val intent = Intent(context, CourseLiveUpdateReceiver::class.java).apply {
                action = ACTION_SHOW
                putCourseExtras(courseName, place, teacher, startMillis, endMillis)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode(courseName, startMillis, "$ACTION_SHOW@$checkMillis"),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
            checkMillis += WINDOW_CHECK_INTERVAL_MILLIS
        }
    }

    fun scheduleNextRefresh(
        context: Context,
        courseName: String,
        place: String?,
        teacher: String?,
        startMillis: Long,
        endMillis: Long,
    ) {
        val now = System.currentTimeMillis()
        val nextRefreshMillis = now + 60_000L
        if (nextRefreshMillis >= endMillis) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, CourseLiveUpdateReceiver::class.java).apply {
            action = ACTION_REFRESH
            putCourseExtras(courseName, place, teacher, startMillis, endMillis)
        }
        setCourseAlarm(
            alarmManager = alarmManager,
            triggerMillis = nextRefreshMillis,
            pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode(courseName, startMillis, ACTION_REFRESH),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    fun buildOpenCourseIntent(
        context: Context,
        courseName: String,
        startMillis: Long,
    ): PendingIntent {
        val route = AppNavRoute.CourseDetail.withArgs(
            Uri.encode(courseName),
            Uri.encode("${CourseDetailOrigin.CALENDAR_JXGLSTU.t}@$startMillis")
        )
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("route", route)
        }
        return PendingIntent.getActivity(
            context,
            requestCode(courseName, startMillis, "open"),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun setCourseAlarm(
        alarmManager: AlarmManager,
        triggerMillis: Long,
        pendingIntent: PendingIntent,
        alarmClockInfo: AlarmManager.AlarmClockInfo? = null,
    ) {
        try {
            if (alarmClockInfo != null) {
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
                return
            }

            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms() -> {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
                }
                else -> {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
                }
            }
        } catch (e: SecurityException) {
            LogUtil.error(e)
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
        }
    }

    fun canPostNotification(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    private suspend fun getRemindBeforeMinutes(): Int =
        DataStoreManager.liveCourseReminderMinutes.first()
            .takeIf { it > 0 }
            ?: DEFAULT_REMIND_BEFORE_MINUTES

    private fun Intent.putCourseExtras(
        courseName: String,
        place: String?,
        teacher: String?,
        startMillis: Long,
        endMillis: Long,
    ) {
        putExtra(EXTRA_COURSE_NAME, courseName)
        putExtra(EXTRA_PLACE, place)
        putExtra(EXTRA_TEACHER, teacher)
        putExtra(EXTRA_START_MILLIS, startMillis)
        putExtra(EXTRA_END_MILLIS, endMillis)
    }

    private fun DateTimeBean.toMillis(): Long =
        Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    private fun requestCode(courseName: String, startMillis: Long, action: String): Int {
        val hash = "$action@$courseName@$startMillis".hashCode()
        return if (hash == Int.MIN_VALUE) 0 else kotlin.math.abs(hash)
    }
}
