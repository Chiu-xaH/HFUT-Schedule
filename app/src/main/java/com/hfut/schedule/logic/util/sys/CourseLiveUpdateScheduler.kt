package com.hfut.schedule.logic.util.sys

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.hfut.schedule.activity.MainActivity
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.receiver.CourseLiveUpdateReceiver
import com.hfut.schedule.service.CourseLiveUpdateService
import com.hfut.schedule.ui.nav.destination.CourseLiveUpdateDetailDestination
import com.xah.shared.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.Calendar

object CourseLiveUpdateScheduler {
    const val ACTION_SHOW = "com.hfut.schedule.action.SHOW_COURSE_LIVE_UPDATE"
    const val ACTION_FINISH = "com.hfut.schedule.action.FINISH_COURSE_LIVE_UPDATE"
    const val ACTION_RESCHEDULE = "com.hfut.schedule.action.RESCHEDULE_COURSE_LIVE_UPDATE"

    const val EXTRA_COURSE_NAME = "course_name"
    const val EXTRA_PLACE = "place"
    const val EXTRA_TEACHER = "teacher"
    const val EXTRA_START_MILLIS = "start_millis"
    const val EXTRA_END_MILLIS = "end_millis"

    private const val DEFAULT_REMIND_BEFORE_MINUTES = 20
    private const val WINDOW_CHECK_INTERVAL_MILLIS = 10 * 60_000L
    private const val MAX_SCHEDULED_ALARMS = 420
    private const val RESCHEDULE_INTERVAL_MILLIS = 6 * 60 * 60_000L
    private const val RESCHEDULE_BEFORE_NEXT_MILLIS = 30 * 60_000L
    private const val RESCHEDULE_REQUEST_CODE = 0x436F7572

    suspend fun scheduleAll(context: Context = MyApplication.context): Int = withContext(Dispatchers.IO) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val now = System.currentTimeMillis()
        val remindBeforeMinutes = getRemindBeforeMinutes()
        val courses = getJxglstuCourseSchedule()
            .filter { it.time.end.toMillis() > now }
            .sortedBy { it.time.start.toMillis() }
        val alarmRequests = mutableListOf<CourseAlarmRequest>()
        val scheduledCourseKeys = mutableSetOf<String>()

        cancelScheduledAlarms(
            context = context,
            alarmManager = alarmManager,
            courses = courses,
            remindBeforeMinutes = remindBeforeMinutes,
            cancelNotifications = false,
        )

        courses.forEach { course ->
            val startMillis = course.time.start.toMillis()
            val endMillis = course.time.end.toMillis()
            val triggerMillis = startMillis - remindBeforeMinutes * 60_000L
            val courseKey = "${course.courseName}@$startMillis"

            if (triggerMillis > now) {
                val showIntent = Intent(context, CourseLiveUpdateReceiver::class.java).apply {
                    action = ACTION_SHOW
                    putCourseExtras(course.courseName, course.place, course.teacher, startMillis, endMillis)
                }
                alarmRequests += CourseAlarmRequest(
                    triggerMillis = triggerMillis,
                    pendingIntent = PendingIntent.getBroadcast(
                        context,
                        requestCode(course.courseName, startMillis, ACTION_SHOW),
                        showIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    ),
                    alarmClockInfo = AlarmManager.AlarmClockInfo(
                        triggerMillis,
                        buildOpenCourseIntent(context, course.courseName, course.place, startMillis)
                    )
                )
            }

            val finishIntent = Intent(context, CourseLiveUpdateReceiver::class.java).apply {
                action = ACTION_FINISH
                putCourseExtras(course.courseName, course.place, course.teacher, startMillis, endMillis)
            }
            alarmRequests += CourseAlarmRequest(
                triggerMillis = endMillis,
                pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode(course.courseName, startMillis, ACTION_FINISH),
                    finishIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            scheduledCourseKeys += courseKey
        }

        val sortedRequests = alarmRequests
            .filter { it.triggerMillis > now }
            .sortedBy { it.triggerMillis }
        val requestsToSchedule = sortedRequests.take(MAX_SCHEDULED_ALARMS)
        var firstUnscheduledMillis = sortedRequests.getOrNull(requestsToSchedule.size)?.triggerMillis
        for (request in requestsToSchedule) {
            if (!setCourseAlarm(
                    alarmManager = alarmManager,
                    triggerMillis = request.triggerMillis,
                    pendingIntent = request.pendingIntent,
                    alarmClockInfo = request.alarmClockInfo,
                )
            ) {
                firstUnscheduledMillis = request.triggerMillis
                break
            }
        }

        if (firstUnscheduledMillis != null) {
            val fallbackMillis = now + RESCHEDULE_INTERVAL_MILLIS
            val triggerMillis = maxOf(now + WINDOW_CHECK_INTERVAL_MILLIS, firstUnscheduledMillis - RESCHEDULE_BEFORE_NEXT_MILLIS)
                .let { minOf(it, fallbackMillis) }
            scheduleRescheduleAlarm(context, alarmManager, triggerMillis)
        }

        scheduledCourseKeys.size
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
                contentIntent = buildOpenCourseIntent(context, course.courseName, course.place, startMillis),
            )
            shownCount++
        }
        shownCount
    }

    suspend fun cancelAll(context: Context = MyApplication.context) = withContext(Dispatchers.IO) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val remindBeforeMinutes = getRemindBeforeMinutes()
        cancelScheduledAlarms(
            context = context,
            alarmManager = alarmManager,
            courses = getJxglstuCourseSchedule(),
            remindBeforeMinutes = remindBeforeMinutes,
            cancelNotifications = true,
        )
        CourseLiveUpdateService.stopService(context)
    }

    private fun cancelScheduledAlarms(
        context: Context,
        alarmManager: AlarmManager,
        courses: List<JxglstuCourseSchedule>,
        remindBeforeMinutes: Int,
        cancelNotifications: Boolean,
    ) {
        cancelRescheduleAlarm(context, alarmManager)
        courses.forEach { course ->
            val startMillis = course.time.start.toMillis()
            val endMillis = course.time.end.toMillis()
            val remindStartMillis = startMillis - remindBeforeMinutes * 60_000L
            listOf(ACTION_SHOW, ACTION_FINISH).forEach { action ->
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
            if (cancelNotifications) {
                AppNotificationManager.cancelCourseLiveUpdate(course.courseName, startMillis)
            }
        }
    }

    private fun scheduleRescheduleAlarm(
        context: Context,
        alarmManager: AlarmManager,
        triggerMillis: Long,
    ) {
        setCourseAlarm(
            alarmManager = alarmManager,
            triggerMillis = triggerMillis,
            pendingIntent = reschedulePendingIntent(context),
        )
    }

    private fun cancelRescheduleAlarm(context: Context, alarmManager: AlarmManager) {
        alarmManager.cancel(reschedulePendingIntent(context))
    }

    private fun reschedulePendingIntent(context: Context): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            RESCHEDULE_REQUEST_CODE,
            Intent(context, CourseLiveUpdateReceiver::class.java).apply {
                action = ACTION_RESCHEDULE
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

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

    fun buildOpenCourseIntent(
        context: Context,
        courseName: String,
        place: String?,
        startMillis: Long,
    ): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("route", CourseLiveUpdateDetailDestination::class.java.name)
            putExtra(EXTRA_COURSE_NAME, courseName)
            putExtra(EXTRA_PLACE, place)
            putExtra(EXTRA_START_MILLIS, startMillis)
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
    ): Boolean {
        try {
            if (alarmClockInfo != null) {
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
                return true
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
            return try {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
                true
            } catch (fallbackException: RuntimeException) {
                LogUtil.error(fallbackException)
                false
            }
        } catch (e: IllegalStateException) {
            LogUtil.error(e)
            return false
        }
        return true
    }

    private data class CourseAlarmRequest(
        val triggerMillis: Long,
        val pendingIntent: PendingIntent,
        val alarmClockInfo: AlarmManager.AlarmClockInfo? = null,
    )

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
