package com.hfut.schedule.logic.util.sys

import android.Manifest
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.annotation.RequiresPermission
import com.hfut.schedule.application.MyApplication
import com.sharednav.common.util.LogUtil

/**
 * 需存储权限
 */
@RequiresPermission(anyOf = ["android.permission.READ_WALLPAPER_INTERNAL", Manifest.permission.MANAGE_EXTERNAL_STORAGE])
fun getWallpaper(context: Context = MyApplication.context): Bitmap? {
    try {
        val manager = WallpaperManager.getInstance(context)
        return (manager.drawable as? BitmapDrawable)?.bitmap
    } catch (e : Exception) {
        LogUtil.error(e)
        return null
    }
}