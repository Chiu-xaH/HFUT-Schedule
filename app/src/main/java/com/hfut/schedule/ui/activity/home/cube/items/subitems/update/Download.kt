package com.hfut.schedule.ui.activity.home.cube.items.subitems.update

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import com.hfut.schedule.ui.utils.components.LoadingUI
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.ui.utils.components.MyToast
import java.io.File

fun installApk(uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    intent.setDataAndType(uri, "application/vnd.android.package-archive")
    MyApplication.context.startActivity(intent)
}

fun downloadManage(version: String) {
    val request = DownloadManager.Request(Uri.parse(MyApplication.UpdateURL + "releases/download/Android/${version}.apk"))
    request.setTitle("下载 聚在工大 ${version} 中")
    request.setDescription("请等待")
    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "app.apk")

    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE or DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)


    request.setMimeType("application/vnd.android.package-archive")

    val fileUri = Uri.fromFile(
        File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            System.currentTimeMillis().toString() + "-" + version
        )
    )
    request.setDestinationUri(fileUri)
    val downloadManager = MyApplication.context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager


    val downloadId = downloadManager.enqueue(request)

    SharePrefs.saveLong("download",downloadId)


    //注册下载任务完成的监听
    MyApplication.context.registerReceiver(object : BroadcastReceiver() {

        @SuppressLint("UnspecifiedRegisterReceiverFlag")
        override fun onReceive(context: Context, intent: Intent) {

            //已经完成
            if (intent.action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {

                //获取下载ID
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                val uri = downloadManager.getUriForDownloadedFile(id)

                installApk(uri)

            } else if (intent.action.equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {

                //如果还未完成下载，跳转到下载中心
                val viewDownloadIntent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)
                viewDownloadIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(viewDownloadIntent)

            }

        }
    }, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
}

@SuppressLint("Range")
fun getDownloadProgress(downloadId: Long): Int {
    val downloadManager = MyApplication.context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val query = DownloadManager.Query().setFilterById(downloadId)
    val cursor = downloadManager.query(query)

    if (cursor != null && cursor.moveToFirst()) {
        val totalSize = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
        val downloadedSize = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
        cursor.close()
        if (totalSize > 0) {
            return (downloadedSize * 100L / totalSize).toInt()
        }
    }
    return 0
}


@SuppressLint("SuspiciousIndentation")
@Composable
fun downloadUI() {
    val handler = Handler(Looper.getMainLooper())
    var pro by remember { mutableStateOf(0f) }
    var able by remember { mutableStateOf(true) }
    val runnable = object : Runnable {
        override fun run() {
            val id = SharePrefs.prefs.getLong("download",-1)
            val progress = getDownloadProgress(id)
            // 更新 UI，例如进度条
            pro = progress / 100f
            if (progress < 100) {
                handler.postDelayed(this, 1000) // 每秒查询一次
            }
        }
    }
    handler.post(runnable)

    Column(modifier = Modifier.padding(horizontal = 7.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            //    CircularProgressIndicator(
            //      progress = pro,
            // )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center)  {
            Button(
                onClick = {
                    getUpdates().version?.let { downloadManage(it) }
                    able = false
                },
                modifier = Modifier
                    .weight(.5f)
                    .padding(horizontal = 8.dp),
                enabled = able
            ) {
                Row {
                    if(pro != 0f && pro != 1f) {
                        CircularProgressIndicator(
                            progress = { pro },
                            modifier = Modifier.size(20.dp).height(20.dp),
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                    }
                    Text(text = if(able)"下载更新" else "准备下载")

                }

            }
            if(pro > 0)
                FilledTonalButton(onClick = {
                    //获取下载ID
                    val downloadManager = MyApplication.context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    if (pro == 1f) {
                        val id = SharePrefs.prefs.getLong("download",-1)
                        val uri = downloadManager.getUriForDownloadedFile(id)
                        installApk(uri)
                    } else MyToast("正在下载")
                }, modifier = Modifier
                    .weight(.5f)
                    .padding(horizontal = 8.dp)) {
                    Text(text =  "${(pro*100).toInt()} %" + if(pro == 1f) " 安装" else "")
                }
        }
    }
}
