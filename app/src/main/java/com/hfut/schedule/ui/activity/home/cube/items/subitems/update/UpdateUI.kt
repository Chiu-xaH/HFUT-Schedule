package com.hfut.schedule.ui.activity.home.cube.items.subitems.update

import android.app.DownloadManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.APPVersion
import com.hfut.schedule.logic.utils.MyDownloadManager
import com.hfut.schedule.logic.utils.MyDownloadManager.downloadManage
import com.hfut.schedule.logic.utils.MyDownloadManager.getDownloadProgress
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.ui.utils.NavigateAndAnimationManager
import com.hfut.schedule.ui.utils.components.BottomButton
import com.hfut.schedule.ui.utils.components.MyCustomCard
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.components.TransplantListItem

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun UpdateUI() {
    val handler = Handler(Looper.getMainLooper())
    var pro by remember { mutableStateOf(0f) }
    var able by remember { mutableStateOf(true) }
    val runnable = object : Runnable {
        override fun run() {
            val id = MyDownloadManager.getDownloadId(MyDownloadManager.DownloadIds.UPDATE)
            val progress = getDownloadProgress(id)
            // 更新 UI，例如进度条
            pro = progress / 100f
            if (progress < 100) {
                handler.postDelayed(this, 1000) // 每秒查询一次
            }
        }
    }
    handler.post(runnable)

    val version by remember { mutableStateOf(getUpdates()) }

    var expandItems by remember { mutableStateOf(false) }

    MyCustomCard(hasElevation = false, containerColor = MaterialTheme.colorScheme.errorContainer) {
        TransplantListItem(
            headlineContent = { Text(text = "发现新版本") },
            supportingContent = { Text(text = "${APPVersion.getVersionName()} → ${version.version}") },
            leadingContent = { Icon(painterResource(R.drawable.arrow_upward), contentDescription = "Localized description",) },
            trailingContent = {
                FilledTonalIconButton(
                    onClick = { expandItems = !expandItems },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                ) { Icon(painterResource(id = if(!expandItems) R.drawable.expand_content else R.drawable.collapse_content), contentDescription = "")
                }
            },
            modifier = Modifier.clickable{ Starter.startWebUrl(MyApplication.UpdateURL+ "/releases/tag/Android") },
        )

        AnimatedVisibility(
            visible = expandItems,
            enter = slideInVertically(
                initialOffsetY = { -40 }
            ) + expandVertically(
                expandFrom = Alignment.Top
            ) + scaleIn(
                transformOrigin = TransformOrigin(0.5f, 0f)
            ) + fadeIn(initialAlpha = 0.3f),
            exit = slideOutVertically() + shrinkVertically() + fadeOut() + scaleOut(targetScale = 1.2f)) {
            TransplantListItem(
                headlineContent = { Text(text ="更新日志") },
                supportingContent = {
                    getUpdates().text?.let { Text(text = " $it") }
                },
                leadingContent = { Icon(painter = painterResource(id = R.drawable.hotel_class), contentDescription = "") },
//                colors = MaterialTheme.colorScheme.errorContainer
            )
        }
        AnimatedVisibility(
            visible = able,
            exit = NavigateAndAnimationManager.upDownAnimation.exit,
            enter = NavigateAndAnimationManager.upDownAnimation.enter
        ) {
            BottomButton(
                onClick = {
                    getUpdates().version?.let { MyDownloadManager.update(it) }
                    able = false
//                    expandItems = true
                },
                text = "下载更新",
                color = MaterialTheme.colorScheme.error.copy(.07f)
            )
        }
    }
    AnimatedVisibility(
        visible = !able || pro > 0,
        exit = NavigateAndAnimationManager.upDownAnimation.exit,
        enter = NavigateAndAnimationManager.upDownAnimation.enter
    ) {
        Column(modifier = Modifier.padding(horizontal = 7.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center)  {
                Button(
                    onClick = {
                        getUpdates().version?.let { MyDownloadManager.update(it) }
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
                            val id = MyDownloadManager.getDownloadId(MyDownloadManager.DownloadIds.UPDATE)
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
}

