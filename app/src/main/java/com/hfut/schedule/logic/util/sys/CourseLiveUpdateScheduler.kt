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
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.receiver.CourseLiveUpdateReceiver
import com.hfut.schedule.service.CourseLiveUpdateService
import com.hfut.schedule.ui.nav.destination.CourseLiveUpdateDetailDestination
import com.hfut.schedule.ui.screen.home.calendar.common.examToCalendar
import com.xah.shared.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.Calendar

object CourseLiveUpdateScheduler {
    val ACTION_SHOW = "${AppVersion.appPackageName}.action.SHOW_COURSE_LIVE_UPDATE"
    val ACTION_FINISH = "${AppVersion.appPackageName}.action.FINISH_COURSE_LIVE_UPDATE"
    val ACTION_RESCHEDULE = "${AppVersion.appPackageName}.action.RESCHEDULE_COURSE_LIVE_UPDATE"

    const val EXTRA_COURSE_NAME = "course_name"
    const val EXTRA_PLACE = "place"
    const val EXTRA_TEACHER = "teacher"
    const val EXTRA_START_MILLIS = "start_millis"
    const val EXTRA_END_MILLIS = "end_millis"
    const val EXTRA_EVENT_TYPE = "event_type"

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
        val reminderItems = getLiveReminderItems()
            .filter { it.endMillis > now }
            .sortedBy { it.startMillis }
        val alarmRequests = mutableListOf<CourseAlarmRequest>()
        val scheduledCourseKeys = mutableSetOf<String>()

        cancelScheduledAlarms(
            context = context,
            alarmManager = alarmManager,
            reminderItems = reminderItems,
            remindBeforeMinutes = remindBeforeMinutes,
            cancelNotifications = false,
        )

        reminderItems.forEach { item ->
            val startMillis = item.startMillis
            val endMillis = item.endMillis
            val triggerMillis = startMillis - remindBeforeMinutes * 60_000L
            val courseKey = "${item.title}@$startMillis"

            if (triggerMillis > now) {
                val showIntent = Intent(context, CourseLiveUpdateReceiver::class.java).apply {
                    action = ACTION_SHOW
                    putCourseExtras(item.title, item.place, item.subtitle, startMillis, endMillis, item.eventType)
                }
                alarmRequests += CourseAlarmRequest(
                    triggerMillis = triggerMillis,
                    pendingIntent = PendingIntent.getBroadcast(
                        context,
                        requestCode(item.title, startMillis, ACTION_SHOW),
                        showIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    ),
                    alarmClockInfo = AlarmManager.AlarmClockInfo(
                        triggerMillis,
                        buildOpenIntent(context, item)
                    )
                )
            }

            val finishIntent = Intent(context, CourseLiveUpdateReceiver::class.java).apply {
                action = ACTION_FINISH
                putCourseExtras(item.title, item.place, item.subtitle, startMillis, endMillis, item.eventType)
            }
            alarmRequests += CourseAlarmRequest(
                triggerMillis = endMillis,
                pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode(item.title, startMillis, ACTION_FINISH),
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
        getLiveReminderItems().forEach { item ->
            val startMillis = item.startMillis
            val endMillis = item.endMillis
            val remindStartMillis = startMillis - remindBeforeMinutes * 60_000L
            if (now !in remindStartMillis until endMillis) return@forEach

            AppNotificationManager.showCourseLiveUpdate(
                courseName = item.title,
                place = item.place,
                teacher = item.subtitle,
                startMillis = startMillis,
                endMillis = endMillis,
                contentIntent = buildOpenIntent(context, item),
                eventType = item.eventType,
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
            reminderItems = getLiveReminderItems(),
            remindBeforeMinutes = remindBeforeMinutes,
            cancelNotifications = true,
        )
        CourseLiveUpdateService.stopService(context)
    }

    private fun cancelScheduledAlarms(
        context: Context,
        alarmManager: AlarmManager,
        reminderItems: List<LiveReminderItem>,
        remindBeforeMinutes: Int,
        cancelNotifications: Boolean,
    ) {
        cancelRescheduleAlarm(context, alarmManager)
        reminderItems.forEach { item ->
            val startMillis = item.startMillis
            val endMillis = item.endMillis
            val remindStartMillis = startMillis - remindBeforeMinutes * 60_000L
            listOf(ACTION_SHOW, ACTION_FINISH).forEach { action ->
                val intent = Intent(context, CourseLiveUpdateReceiver::class.java).apply {
                    this.action = action
                    putCourseExtras(item.title, item.place, item.subtitle, startMillis, endMillis, item.eventType)
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode(item.title, startMillis, action),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.cancel(pendingIntent)
            }
            cancelWindowChecks(
                context = context,
                alarmManager = alarmManager,
                courseName = item.title,
                place = item.place,
                teacher = item.subtitle,
                remindStartMillis = remindStartMillis,
                startMillis = startMillis,
                endMillis = endMillis
            )
            if (cancelNotifications) {
                AppNotificationManager.cancelCourseLiveUpdate(item.title, startMillis)
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

    private suspend fun getLiveReminderItems(): List<LiveReminderItem> =
        getCourseReminderItems() + getExamReminderItems()

    private suspend fun getCourseReminderItems(): List<LiveReminderItem> =
        getJxglstuCourseSchedule().map { course ->
            LiveReminderItem(
                title = course.courseName,
                place = course.place,
                subtitle = course.teacher,
                startMillis = course.time.start.toMillis(),
                endMillis = course.time.end.toMillis(),
                eventType = "上课",
                destination = LiveReminderDestination.COURSE,
            )
        }

    private suspend fun getExamReminderItems(): List<LiveReminderItem> =
        examToCalendar().mapNotNull { exam ->
            val day = exam.day ?: return@mapNotNull null
            val startTime = exam.startTime ?: return@mapNotNull null
            val endTime = exam.endTime ?: return@mapNotNull null
            val title = exam.course ?: return@mapNotNull null
            val startMillis = parseReminderMillis(day, startTime) ?: return@mapNotNull null
            val endMillis = parseReminderMillis(day, endTime) ?: return@mapNotNull null
            LiveReminderItem(
                title = title,
                place = exam.place,
                subtitle = exam.type,
                startMillis = startMillis,
                endMillis = endMillis,
                eventType = "考试",
                destination = LiveReminderDestination.EXAM,
            )
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

    fun buildOpenLiveReminderIntent(
        context: Context,
        eventType: String,
        title: String,
        place: String?,
        startMillis: Long,
    ): PendingIntent =
        if (eventType == "考试") {
            buildOpenExamIntent(context, title, place, startMillis)
        } else {
            buildOpenCourseIntent(context, title, place, startMillis)
        }

    private fun buildOpenIntent(context: Context, item: LiveReminderItem): PendingIntent =
        when (item.destination) {
            LiveReminderDestination.COURSE -> buildOpenCourseIntent(context, item.title, item.place, item.startMillis)
            LiveReminderDestination.EXAM -> buildOpenExamIntent(context, item.title, item.place, item.startMillis)
        }

    private fun buildOpenExamIntent(
        context: Context,
        examName: String,
        place: String?,
        startMillis: Long,
    ): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_COURSE_NAME, examName)
            putExtra(EXTRA_PLACE, place)
            putExtra(EXTRA_START_MILLIS, startMillis)
        }
        return PendingIntent.getActivity(
            context,
            requestCode(examName, startMillis, "open_exam"),
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
                alarmManager.setAlarmClock(
                    alarmClockInfo,
                    pendingIntent
                )
            } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            }
            return true
        }
        // 暂时禁用，要不然日志太多了
//        catch (e: SecurityException) {
//            LogUtil.error(e)
//            return try {
//                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
//                true
//            } catch (fallbackException: RuntimeException) {
//                LogUtil.error(fallbackException)
//                false
//            }
//        } catch (e: IllegalStateException) {
//            LogUtil.error(e)
//            return false
//        }
        catch (e: Exception) {
            LogUtil.error(e)
            return false
        }
    }

    private data class CourseAlarmRequest(
        val triggerMillis: Long,
        val pendingIntent: PendingIntent,
        val alarmClockInfo: AlarmManager.AlarmClockInfo? = null,
    )

    private data class LiveReminderItem(
        val title: String,
        val place: String?,
        val subtitle: String?,
        val startMillis: Long,
        val endMillis: Long,
        val eventType: String,
        val destination: LiveReminderDestination,
    )

    private enum class LiveReminderDestination {
        COURSE,
        EXAM,
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
        eventType: String = "上课",
    ) {
        putExtra(EXTRA_COURSE_NAME, courseName)
        putExtra(EXTRA_PLACE, place)
        putExtra(EXTRA_TEACHER, teacher)
        putExtra(EXTRA_START_MILLIS, startMillis)
        putExtra(EXTRA_END_MILLIS, endMillis)
        putExtra(EXTRA_EVENT_TYPE, eventType)
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

    private fun parseReminderMillis(date: String, time: String): Long? {
        val dateParts = date.split("-").mapNotNull { it.toIntOrNull() }
        val timeParts = time.split(":").mapNotNull { it.toIntOrNull() }
        if (dateParts.size != 3 || timeParts.size != 2) return null
        return try {
            DateTimeBean(
                year = dateParts[0],
                month = dateParts[1],
                day = dateParts[2],
                hour = timeParts[0],
                minute = timeParts[1],
            ).toMillis()
        } catch (e: Exception) {
            LogUtil.error(e)
            null
        }
    }

    private fun requestCode(courseName: String, startMillis: Long, action: String): Int {
        val hash = "$action@$courseName@$startMillis".hashCode()
        return if (hash == Int.MIN_VALUE) 0 else kotlin.math.abs(hash)
    }
}
