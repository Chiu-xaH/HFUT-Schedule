package com.hfut.schedule.logic.util.sys

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri

object PermissionSet {

    private const val REQUEST_CODE_STORAGE = 1001
    private const val REQUEST_CODE_STORAGE_MANAGER = 1002
    private const val REQUEST_CODE_CALENDAR = 1003
    private const val REQUEST_CODE_CAMERA = 1004
    private const val REQUEST_CODE_NOTIFICATION = 1005

    @JvmStatic
    fun checkAndRequestStoragePermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = "package:${activity.packageName}".toUri()
                    activity.startActivityForResult(intent, 1)
                } catch (e: Exception) {
                    // 某些手机拉不出来 , 使用全局设置页面
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    activity.startActivityForResult(intent, 1)
                }
            }

        } else {
            // Android 10 及以下
            val needReq = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).any {
                ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
            }

            if (needReq) {
                ActivityCompat.requestPermissions(activity, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 1)
            }
        }
    }

    @JvmStatic
    fun checkAndRequestCalendarPermission(activity: Activity) {
        Handler(Looper.getMainLooper()).post {
            if(
                ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                return@post
            }
            if(ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_CALENDAR),1)
            if(ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_CALENDAR),1)
        }
    }
    @JvmStatic
    fun checkAndRequestCameraPermission(activity: Activity) {
        Handler(Looper.getMainLooper()).post {
            if(ContextCompat.checkSelfPermission(activity,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA),1)
        }
    }
    @JvmStatic
    fun checkAndRequestNotificationPermission(activity: Activity) {
        runOnMain(activity) {
            if (activity.isInvalid()) return@runOnMain

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    safeRequestPermissions(
                        activity = activity,
                        permissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        requestCode = REQUEST_CODE_NOTIFICATION,
                        fallback = {
                            openNotificationSettings(activity)
                        }
                    )
                }
            }
        }
    }

    private fun safeRequestPermissions(
        activity: Activity,
        permissions: Array<String>,
        requestCode: Int,
        fallback: (() -> Unit)? = null
    ) {
        if (activity.isInvalid()) return

        try {
            ActivityCompat.requestPermissions(activity, permissions, requestCode)
        } catch (_: Exception) {
            // 部分定制 ROM / 精简系统 / 权限管理组件异常时，
            // requestPermissions 底层启动系统权限组件可能失败。
            fallback?.invoke()
        }
    }

    private fun openManageAllFilesSettings(activity: Activity) {
        if (activity.isInvalid()) return

        try {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = "package:${activity.packageName}".toUri()
            }
            activity.startActivityForResult(intent, REQUEST_CODE_STORAGE_MANAGER)
        } catch (_: Exception) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                activity.startActivityForResult(intent, REQUEST_CODE_STORAGE_MANAGER)
            } catch (_: Exception) {
                openAppDetailsSettings(activity)
            }
        }
    }

    private fun openNotificationSettings(activity: Activity) {
        if (activity.isInvalid()) return

        try {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, activity.packageName)
            }
            activity.startActivity(intent)
        } catch (_: Exception) {
            openAppDetailsSettings(activity)
        }
    }

    private fun openAppDetailsSettings(activity: Activity) {
        if (activity.isInvalid()) return

        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = "package:${activity.packageName}".toUri()
            }
            activity.startActivity(intent)
        } catch (_: Exception) {
            // 最后兜底：不再继续处理，避免权限逻辑导致应用崩溃。
        }
    }

    private fun runOnMain(activity: Activity, block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (!activity.isInvalid()) block()
        } else {
            Handler(Looper.getMainLooper()).post {
                if (!activity.isInvalid()) block()
            }
        }
    }

    private fun Activity.isInvalid(): Boolean {
        return isFinishing || isDestroyed
    }
}