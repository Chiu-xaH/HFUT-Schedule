package com.hfut.schedule.logic.util.sys

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionManager {
    @JvmStatic
    fun checkAndRequestStoragePermission(activity: Activity) {
        Handler(Looper.getMainLooper()).post {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.parse("package:" + activity.packageName)
                    activity.startActivityForResult(intent, 1)
                }
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
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
        Handler(Looper.getMainLooper()).post {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 (API 33) 需要通知权限
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
                }
            }
        }
    }
}