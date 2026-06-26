package com.hfut.schedule.service

import android.app.Service
import android.content.Context
import android.content.Intent
import com.hfut.schedule.logic.util.sys.AppNotificationManager
import com.hfut.schedule.logic.util.sys.CourseLiveUpdateScheduler
import com.xah.common.logic.util.LogUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext
import java.util.concurrent.ConcurrentHashMap

class CourseLiveUpdateService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val activeCourses: MutableSet<String> = ConcurrentHashMap.newKeySet()

    companion object {
        const val ACTION_STOP_SERVICE = "com.hfut.schedule.action.STOP_COURSE_LIVE_SERVICE"

        fun stopService(context: Context) {
            context.startService(
                Intent(context, CourseLiveUpdateService::class.java)
                    .setAction(ACTION_STOP_SERVICE)
            )
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_SERVICE) {
            stopSelf()
            return START_NOT_STICKY
        }

        val courseName = intent?.getStringExtra(CourseLiveUpdateScheduler.EXTRA_COURSE_NAME)
            ?: return START_NOT_STICKY
        val startMillis = intent.getLongExtra(CourseLiveUpdateScheduler.EXTRA_START_MILLIS, -1L)
        val endMillis = intent.getLongExtra(CourseLiveUpdateScheduler.EXTRA_END_MILLIS, -1L)
        if (startMillis <= 0L || endMillis <= 0L) return START_NOT_STICKY
        if (System.currentTimeMillis() >= endMillis) {
            AppNotificationManager.cancelCourseLiveUpdate(courseName, startMillis)
            return START_NOT_STICKY
        }

        val courseKey = "$courseName@$startMillis"
        val wasEmpty = activeCourses.isEmpty()
        activeCourses.add(courseKey)

        val place = intent.getStringExtra(CourseLiveUpdateScheduler.EXTRA_PLACE)
        val teacher = intent.getStringExtra(CourseLiveUpdateScheduler.EXTRA_TEACHER)

        // Immediately show + foreground with this course's notification.
        // Must be called within 5 seconds on Android 14+.
        showAndUpdateForeground(courseName, place, teacher, startMillis, endMillis)

        // Start the 60-second refresh loop for this course.
        scope.launch {
            refreshLoop(courseName, place, teacher, startMillis, endMillis)
        }

        return if (wasEmpty) START_STICKY else START_REDELIVER_INTENT
    }

    private fun showAndUpdateForeground(
        courseName: String,
        place: String?,
        teacher: String?,
        startMillis: Long,
        endMillis: Long,
    ) {
        val notification = AppNotificationManager.showCourseLiveUpdate(
            courseName = courseName,
            place = place,
            teacher = teacher,
            startMillis = startMillis,
            endMillis = endMillis,
            contentIntent = CourseLiveUpdateScheduler.buildOpenCourseIntent(
                this, courseName, place, startMillis
            ),
            asForeground = true,
        ) ?: return

        startForeground(
            AppNotificationManager.courseLiveNotificationId(courseName, startMillis),
            notification,
        )
    }

    private suspend fun refreshLoop(
        courseName: String,
        place: String?,
        teacher: String?,
        startMillis: Long,
        endMillis: Long,
    ) {
        try {
            while (coroutineContext.isActive && System.currentTimeMillis() < endMillis) {
                delay(60_000L)
                if (System.currentTimeMillis() >= endMillis) break
                AppNotificationManager.showCourseLiveUpdate(
                    courseName = courseName,
                    place = place,
                    teacher = teacher,
                    startMillis = startMillis,
                    endMillis = endMillis,
                    contentIntent = CourseLiveUpdateScheduler.buildOpenCourseIntent(
                        this, courseName, place, startMillis
                    ),
                )
            }
        } catch (e: Exception) {
            LogUtil.error(e)
        } finally {
            AppNotificationManager.cancelCourseLiveUpdate(courseName, startMillis)
            activeCourses.remove("$courseName@$startMillis")
            if (activeCourses.isEmpty()) {
                stopSelf()
            }
        }
    }

    override fun onDestroy() {
        scope.cancel()
        activeCourses.clear()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null
}
