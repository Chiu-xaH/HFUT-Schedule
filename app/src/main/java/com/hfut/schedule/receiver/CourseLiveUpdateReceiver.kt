package com.hfut.schedule.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.AppNotificationManager
import com.hfut.schedule.logic.util.sys.CourseLiveUpdateScheduler
import com.xah.shared.LogUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CourseLiveUpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                when (intent.action) {
                    CourseLiveUpdateScheduler.ACTION_SHOW -> showCourseLiveUpdate(context, intent)
                    CourseLiveUpdateScheduler.ACTION_FINISH -> finishCourseLiveUpdate(intent)
                    Intent.ACTION_BOOT_COMPLETED,
                    Intent.ACTION_MY_PACKAGE_REPLACED,
                    CourseLiveUpdateScheduler.ACTION_RESCHEDULE -> {
                        if (DataStoreManager.enableLiveCourseReminder.first()) {
                            CourseLiveUpdateScheduler.scheduleAll(context)
                        }
                    }
                }
            } catch (e: Exception) {
                LogUtil.error(e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showCourseLiveUpdate(context: Context, intent: Intent) {
        val courseName = intent.getStringExtra(CourseLiveUpdateScheduler.EXTRA_COURSE_NAME) ?: return
        val startMillis = intent.getLongExtra(CourseLiveUpdateScheduler.EXTRA_START_MILLIS, -1L)
        val endMillis = intent.getLongExtra(CourseLiveUpdateScheduler.EXTRA_END_MILLIS, -1L)
        if (startMillis <= 0L || endMillis <= 0L) return
        if (System.currentTimeMillis() >= endMillis) {
            AppNotificationManager.cancelCourseLiveUpdate(courseName, startMillis)
            return
        }

        AppNotificationManager.showCourseLiveUpdate(
            courseName = courseName,
            place = intent.getStringExtra(CourseLiveUpdateScheduler.EXTRA_PLACE),
            teacher = intent.getStringExtra(CourseLiveUpdateScheduler.EXTRA_TEACHER),
            startMillis = startMillis,
            endMillis = endMillis,
            contentIntent = CourseLiveUpdateScheduler.buildOpenCourseIntent(
                context = context,
                courseName = courseName,
                place = intent.getStringExtra(CourseLiveUpdateScheduler.EXTRA_PLACE),
                startMillis = startMillis,
            ),
        )
    }

    private fun finishCourseLiveUpdate(intent: Intent) {
        val courseName = intent.getStringExtra(CourseLiveUpdateScheduler.EXTRA_COURSE_NAME) ?: return
        val startMillis = intent.getLongExtra(CourseLiveUpdateScheduler.EXTRA_START_MILLIS, -1L)
        if (startMillis <= 0L) return
        AppNotificationManager.cancelCourseLiveUpdate(courseName, startMillis)
    }
}
