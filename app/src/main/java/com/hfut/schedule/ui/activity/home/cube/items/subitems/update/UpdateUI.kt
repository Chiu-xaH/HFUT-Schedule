package com.hfut.schedule.ui.activity.home.cube.items.subitems.update

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.LocalActivity
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.VersionUtils
import com.hfut.schedule.logic.utils.MyDownloadManager
import com.hfut.schedule.logic.utils.MyDownloadManager.downloadManage
import com.hfut.schedule.logic.utils.MyDownloadManager.getDownloadProgress
import com.hfut.schedule.logic.utils.MyDownloadManager.getDownloadStatus
import com.hfut.schedule.logic.utils.MyDownloadManager.installApk
import com.hfut.schedule.logic.utils.MyDownloadManager.noticeInstall
import com.hfut.schedule.logic.utils.MyDownloadManager.openDownload
import com.hfut.schedule.logic.utils.MyDownloadManager.refused
import com.hfut.schedule.logic.utils.PermissionManager
import com.hfut.schedule.logic.utils.data.SharePrefs
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.ui.utils.NavigateAnimationManager
import com.hfut.schedule.ui.utils.components.BottomButton
import com.hfut.schedule.ui.utils.components.LoadingUI
import com.hfut.schedule.ui.utils.components.MyCustomCard
import com.hfut.schedule.ui.utils.components.showToast
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.components.appHorizontalDp
import com.xah.bsdiffs.BsdiffUpdate
import com.xah.bsdiffs.model.Patch
import com.xah.bsdiffs.parsePatch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun UpdateUI() {
    val handler = Handler(Looper.getMainLooper())
    var pro by remember { mutableFloatStateOf(0f) }
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
    // 下载完成后触发
    LaunchedEffect(pro) {
        if(pro == 1f && !refused) {
            noticeInstall()
            refused = true
            installApk()
        }
    }

    val activity = LocalActivity.current


    val version by remember { mutableStateOf(getUpdates()) }

    var expandItems by remember { mutableStateOf(false) }

    MyCustomCard(hasElevation = false, containerColor = MaterialTheme.colorScheme.errorContainer) {
        TransplantListItem(
            headlineContent = { Text(text = "最新版本") },
            supportingContent = { Text(text = "${VersionUtils.getVersionName()} → ${version.version}") },
            leadingContent = { Icon(painterResource(R.drawable.arrow_upward), contentDescription = "Localized description",) },
            trailingContent = {
                FilledTonalIconButton(
                    onClick = { expandItems = !expandItems },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                ) { Icon(painterResource(id = if(!expandItems) R.drawable.expand_content else R.drawable.collapse_content), contentDescription = "")
                }
            },
            modifier = Modifier.clickable{ Starter.startWebUrl(MyApplication.GITEE_UPDATE_URL+ "/releases/tag/Android") },
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
            exit = NavigateAnimationManager.upDownAnimation.exit,
            enter = NavigateAnimationManager.upDownAnimation.enter
        ) {
            BottomButton(
                onClick = {
                    activity?.let { ac ->
                        getUpdates().version?.let { MyDownloadManager.update(it,ac) }
                        able = false
                    }
                },
                text = "下载并安装",
                color = MaterialTheme.colorScheme.error.copy(.07f)
            )
        }
    }
    AnimatedVisibility(
        visible = !able || pro > 0,
        exit = NavigateAnimationManager.upDownAnimation.exit,
        enter = NavigateAnimationManager.upDownAnimation.enter
    ) {
        Column(modifier = Modifier.padding(horizontal = 7.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center)  {
                Button(
                    onClick = {
                        activity?.let { ac ->
                            getUpdates().version?.let { MyDownloadManager.update(it,ac) }
                        }
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
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(20.dp))
                        }
                        Text(text = if(able && pro == 0f)"下载更新" else "${(pro*100).toInt()} %")

                    }

                }
                if(pro > 0)
                    FilledTonalButton(onClick = {
                        if (pro == 1f) {
                            installApk()
                        } else {
                            openDownload()
                        }
                    }, modifier = Modifier
                        .weight(.5f)
                        .padding(horizontal = 8.dp)) {
                        Text(text = if(pro == 1f) "安装" else "管理下载任务")
                    }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun PatchUpdateUI(patch: Patch) {
    var loadingPatch by remember { mutableStateOf(false) }

    val context = LocalActivity.current
    val handler = Handler(Looper.getMainLooper())
    var pro by remember { mutableFloatStateOf(0f) }
    var able by remember { mutableStateOf(true) }
    val runnable = object : Runnable {
        override fun run() {
            val id = MyDownloadManager.getDownloadId(MyDownloadManager.DownloadIds.PATCH)
            val progress = getDownloadProgress(id)
            // 更新 UI，例如进度条
            pro = progress / 100f
            if (progress < 100) {
                handler.postDelayed(this, 1000) // 每秒查询一次
            }
        }
    }
    val patchFileName = parsePatch(patch)
    handler.post(runnable)
    // 下载完成后触发
    LaunchedEffect(pro) {
        if(pro == 1f && !refused) {
            showToast("补丁包下载完成，请点击安装按钮")
            refused = true
        }
    }



    if(loadingPatch) {
        LoadingUI()
    } else {
        MyCustomCard(hasElevation = false, containerColor = MaterialTheme.colorScheme.surfaceContainer) {
            TransplantListItem(
                headlineContent = { Text(text = "增量更新") },
                supportingContent = {
                    Text(text = "开发者为若干最近版本提供补丁包，使用户以更少的下载流量实现版本更新")
                },
                leadingContent = { Icon(painterResource(R.drawable.package_2), contentDescription = "Localized description",) },
            )


            AnimatedVisibility(
                visible = able,
                exit = NavigateAnimationManager.upDownAnimation.exit,
                enter = NavigateAnimationManager.upDownAnimation.enter
            ) {
                BottomButton(
                    onClick = {
                        context?.let {
                            MyDownloadManager.downloadPatch(patchFileName, it)
                            able = false
                        }
                    },
                    text = patch.newVersion.let { if(it != getUpdates().version) "更新至 $it" else "下载增量包" },
                    color = MaterialTheme.colorScheme.primary.copy(.07f)
                )
            }
        }
        AnimatedVisibility(
            visible = !able || pro > 0,
            exit = NavigateAnimationManager.upDownAnimation.exit,
            enter = NavigateAnimationManager.upDownAnimation.enter
        ) {

            Column(modifier = Modifier.padding(horizontal = 7.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center)  {
                    Button(
                        onClick = {
                            context?.let { MyDownloadManager.downloadPatch(patchFileName, it) }
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
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(20.dp))
                            }
                            Text(text = if(able && pro == 0f)"下载补丁包" else "${(pro*100).toInt()} %")

                        }
                    }
                    if(pro > 0)
                        FilledTonalButton(onClick = {
                            if (pro == 1f) {
                                BsdiffUpdate.mergePatchApk(MyApplication.context,patch,onLoad = { load -> loadingPatch = load })
                            } else {
                                openDownload()
                            }
                        }, modifier = Modifier
                            .weight(.5f)
                            .padding(horizontal = 8.dp)) {
                            Text(text = if(pro == 1f) "安装补丁包" else "管理下载任务")
                        }
                }
            }
        }
    }
}
