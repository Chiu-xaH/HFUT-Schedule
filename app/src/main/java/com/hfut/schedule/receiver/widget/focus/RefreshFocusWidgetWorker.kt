package com.hfut.schedule.receiver.widget.focus

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class RefreshFocusWidgetWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // 调用你封装好的刷新函数
            refreshFocusWidget(applicationContext)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "refresh_glance_widget"

        fun getStatus(context : Context) {
            val workInfos = WorkManager.Companion.getInstance(context)
                .getWorkInfosForUniqueWork(WORK_NAME)
                .get()

            if (workInfos.isNullOrEmpty()) {
                // 没有注册
                Log.d("WorkManager：刷新小组件","任务状态: 未注册")
            } else {
                // 已经注册
                val state = workInfos[0].state
                Log.d("WorkManager：刷新小组件","任务状态: $state")
            }
        }

        fun startPeriodicWork(context: Context, intervalMinutes: Long = 30L) {
            val request = PeriodicWorkRequestBuilder<RefreshFocusWidgetWorker>(
                intervalMinutes, TimeUnit.MINUTES
            )
                .setInitialDelay(intervalMinutes, TimeUnit.MINUTES)
                .build()

            WorkManager.Companion.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }

        fun stopPeriodicWork(context: Context) {
            WorkManager.Companion.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}