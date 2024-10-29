package com.hfut.schedule.ui.Activity.success.cube.Settings.Update

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.APPVersion
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.UIUtils.DividerText
import com.hfut.schedule.ui.UIUtils.MyToast
import com.hfut.schedule.ui.UIUtils.ScrollText
import java.io.File


@SuppressLint("SuspiciousIndentation")
@Composable
fun VersionInfoCard() {
    if(APPVersion.getVersionName().contains("Preview")) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 15.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp),
            shape = MaterialTheme.shapes.medium,
        ) { ListItem(headlineContent = { Text(text = APPVersion.getVersionName(), fontSize = 28.sp) }) }
    } else
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 15.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp),
            shape = MaterialTheme.shapes.medium,
        ) {
            ListItem(headlineContent = { Text(text = "聚在工大 " + APPVersion.getVersionName(), fontSize = 28.sp) })
            Row {
                ListItem(
                    overlineContent = { Text(text = "2024-10-29") },
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.sdk), contentDescription = "") },
                    headlineContent = { ScrollText(text = "第${APPVersion.getVersionCode()}次更新") },
                    modifier = Modifier.weight(.5f)
                )
                ListItem(
                    headlineContent = { ScrollText(text = "Chiu-xaH") },
                    overlineContent = { Text(text = "构建") },
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.support), contentDescription = "") },
                    modifier = Modifier.weight(.5f).clickable {
                        MyToast("想成为下个版本的构建者吗?跟随 设置-维护关于-开源主页")
                    }
                )
            }
        }
}


@SuppressLint("SuspiciousIndentation")
@Composable
fun VersionInfo() {
    VersionInfoCard()
    DividerText(text = "新特性")
    UpdateItems("新增 呱呱物联的集成","现在用户可以直接用此APP扫码使用新洗浴设备、查看消费账单", UpdateType.ADD)
}

enum class UpdateType {
    //新增
    ADD,
    //下线
    DEGREE,
    //优化
    OPTIMIZE,
    //修复
    FIX,
    //重构
    RENEW,
    //其他
    OTHER
}
@Composable
fun UpdateItems(title : String,info : String?,type : UpdateType) {
    ListItem(
        headlineContent = { Text(text = title) },
        supportingContent = { info?.let { Text(text = it) } },
        leadingContent = { Icon(painter = painterResource(id = updateTypeIcons(type)), contentDescription = "") }
    )
}

fun updateTypeIcons(type : UpdateType) : Int {
    return when(type) {
        UpdateType.ADD -> R.drawable.add_circle
        UpdateType.DEGREE -> R.drawable.do_not_disturb_on
        UpdateType.OPTIMIZE -> R.drawable.tune
        UpdateType.FIX -> R.drawable.build
        UpdateType.RENEW -> R.drawable.alt_route
        UpdateType.OTHER -> R.drawable.stacks
    }
}

