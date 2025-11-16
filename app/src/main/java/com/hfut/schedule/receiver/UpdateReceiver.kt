package com.hfut.schedule.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hfut.schedule.logic.enumeration.BroadcastAction
import com.hfut.schedule.logic.util.sys.AppDownloadManager.installApk

class UpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // 下载更新完成后触发
        if(intent?.action == BroadcastAction.INSTALL_APK.name) {
//            installApk()
        }
    }
}