package com.hfut.schedule.logic.util.shortcut

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import com.hfut.schedule.R
import com.hfut.schedule.activity.MainActivity
import com.hfut.schedule.ui.screen.AppNavRoute

object AppShortcutManager {
    fun createScanShortcut(context: Context) {
        val shortcutManager = context.getSystemService(ShortcutManager::class.java)

        // 构造启动 MainActivity 的 Intent，带参数
        val intent = Intent(context, MainActivity::class.java).apply {
//            Intent.setAction = Intent.ACTION_VIEW
            action = Intent.ACTION_VIEW
            putExtra("route", AppNavRoute.ScanQrCode.route)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        // 构造 ShortcutInfo
        val shortcut = ShortcutInfo.Builder(context, "scan_shortcut")
            .setShortLabel("CAS扫码")
            .setLongLabel("CAS扫码")
            .setIcon(Icon.createWithResource(context, R.drawable.qr_code_scanner_shortcut))
            .setIntent(intent)  // 带参数
            .build()

        // 添加动态快捷方式
        shortcutManager?.dynamicShortcuts = listOf(shortcut)
    }
}