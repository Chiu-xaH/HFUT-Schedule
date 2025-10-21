package com.hfut.schedule.ui.screen.home.cube.sub.update

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.GiteeReleaseResponse
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.sys.AppDownloadManager
import com.hfut.schedule.logic.util.sys.AppDownloadManager.getDownloadProgress
import com.hfut.schedule.logic.util.sys.AppDownloadManager.installApk
import com.hfut.schedule.logic.util.sys.AppDownloadManager.noticeInstall
import com.hfut.schedule.logic.util.sys.AppDownloadManager.openDownload
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.BottomButton
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.xah.uicommon.component.status.LoadingUI
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.bsdiffs.model.Patch
import com.xah.bsdiffs.util.BsdiffUpdate
import com.xah.bsdiffs.util.parsePatch
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// 全量更新UI
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun UpdateUI(vm : NetWorkViewModel,update : GiteeReleaseResponse?) {
    val updateState = vm.giteeUpdatesResp.state.collectAsState()
    val canDownload = updateState.value is UiState.Success
    val handler = Handler(Looper.getMainLooper())
    var pro by remember { mutableFloatStateOf(0f) }
    var able by remember { mutableStateOf(true) }
    val runnable = object : Runnable {
        override fun run() {
            val id = AppDownloadManager.getDownloadId(AppDownloadManager.DownloadIds.UPDATE)
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
        if(pro == 1f) {
            showToast("安装包已存在，可点击安装")
            noticeInstall()
        }
    }

    val activity = LocalActivity.current


    val context = LocalContext.current
    var expandItems by remember { mutableStateOf(false) }
    val uiState by vm.giteeApkSizeResp.state.collectAsState()

    LaunchedEffect(update) {
        if(uiState is UiState.Success)
            return@LaunchedEffect
        update?.name?.let {
            vm.giteeApkSizeResp.clear()
            vm.getGiteeApkSize(it)
        }
    }
    val scope = rememberCoroutineScope()

    CustomCard(color = if(canDownload) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface) {
        TransplantListItem(
            headlineContent = { Text(text = if(canDownload)"最新版本" else "检查更新失败") },
            supportingContent = { Text(text = if(canDownload) "${AppVersion.getVersionName()} → ${update?.name}" else "${update?.name} 点击手动下载") },
            leadingContent = { Icon(painterResource(R.drawable.arrow_upward), contentDescription = "Localized description",) },
            trailingContent = {
                if(canDownload) {
                    FilledTonalIconButton(
                        onClick = { expandItems = !expandItems },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                    ) { Icon(painterResource(id = if(!expandItems) R.drawable.expand_content else R.drawable.collapse_content), contentDescription = "")
                    }
                }
            },
            modifier = Modifier.clickable{ Starter.startWebUrl(context,MyApplication.GITEE_UPDATE_URL+ "/releases/tag/Android") },
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
            PaddingHorizontalDivider()
            TransplantListItem(
                headlineContent = { Text(text ="更新日志") },
                supportingContent = {
                    update?.body?.let { Text(text = it) }
                },
            )
        }
        AnimatedVisibility(
            visible = able && canDownload,
            exit = AppAnimationManager.upDownAnimation.exit,
            enter = AppAnimationManager.upDownAnimation.enter
        ) {
            BottomButton(
                onClick = {
                    activity?.let { ac ->
                        update?.name?.let { AppDownloadManager.update(it,ac) }
                        able = false
                    }
                },
                text = "下载并安装" + if(uiState is UiState.Success) {
                    " ("+formatDecimal((uiState as UiState.Success).data,2) + "MB)"
                } else "",
                textColor = Color. Unspecified,
                containerColor = MaterialTheme.colorScheme.error.copy(.07f)
            )
        }
    }
    AnimatedVisibility(
        visible = (!able || pro > 0) && canDownload,
        exit = AppAnimationManager.upDownAnimation.exit,
        enter = AppAnimationManager.upDownAnimation.enter
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP), horizontalArrangement = Arrangement.Center)  {
            // 下载完成 但是未安装
            if(able && pro == 1f) {
                FilledTonalButton(
                    onClick = {
                        openDownload()
                    },
                    modifier = Modifier.fillMaxWidth().weight(.5f),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Text(text = "下载管理" )
                }
            } else {
                Button(
                    onClick = {
                        activity?.let { ac ->
                            update?.name?.let { AppDownloadManager.update(it,ac) }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().weight(.5f),
                    shape = MaterialTheme.shapes.medium,
                    enabled = able
                ) {
                    Text(text =
                        if(able && pro == 0f) "下载"
                        else "${(pro*100).toInt()} %"
                    )
                }
            }
            if(pro > 0) {
                Spacer(Modifier.width(APP_HORIZONTAL_DP/3))
                FilledTonalButton(
                    onClick = {
                        if (pro == 1f) {
                            installApk()
                        } else {
                            openDownload()
                        }
                    },
                    shape = MaterialTheme.shapes.medium,
                    colors =if(pro != 1f)  ButtonDefaults. filledTonalButtonColors() else ButtonDefaults. buttonColors(),
                    modifier = Modifier.fillMaxWidth().weight(.5f)
                ) {
                    Text(text = if(pro == 1f) "安装" else "下载管理")
                }
            }
        }
    }
}

// 增量更新UI
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun PatchUpdateUI(patch: Patch,vm: NetWorkViewModel,update : GiteeReleaseResponse?) {
    val coroutineScope = rememberCoroutineScope()
    var loadingPatch by remember { mutableStateOf(false) }

    val activity = LocalActivity.current
    val handler = Handler(Looper.getMainLooper())
    var pro by remember { mutableFloatStateOf(0f) }
    var able by remember { mutableStateOf(true) }
    val runnable = object : Runnable {
        override fun run() {
            val id = AppDownloadManager.getDownloadId(AppDownloadManager.DownloadIds.PATCH)
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
        if(pro == 1f ) {
            showToast("补丁包下载完成，可点击安装按钮")
        }
    }

    val uiStatePatch by vm.giteePatchSizeResp.state.collectAsState()

    LaunchedEffect(Unit) {
        if(uiStatePatch is UiState.Success)
            return@LaunchedEffect
        vm.giteePatchSizeResp.clear()
        vm.getGiteePatchSize(patch)
    }

    val context = LocalContext.current

    if(loadingPatch) {
        LoadingUI("正在校验与合并")
    } else {
        Spacer(Modifier.height(CARD_NORMAL_DP))
        CustomCard( color = MaterialTheme.colorScheme.surface) {
            TransplantListItem(
                headlineContent = { Text(text = "增量更新至" +
                        patch.newVersion.let{ if (it == update?.name) "最新版本" else (""+ it) }
                ) },
                supportingContent = {
                    Text(text = "开发者为一个月(动态调整)内的过去版本的ARM64分包提供增量包，可节省至少40%的下载实现版本更新")
                },
                leadingContent = { Icon(painterResource(R.drawable.package_2), contentDescription = "Localized description",) },
            )


            AnimatedVisibility(
                visible = able,
                exit = AppAnimationManager.upDownAnimation.exit,
                enter = AppAnimationManager.upDownAnimation.enter
            ) {
                BottomButton(
                    onClick = {
                        activity?.let {
                            AppDownloadManager.downloadPatch(patchFileName, it)
                            able = false
                        }
                    },
                    text =
                        patch.newVersion.let { if(it != update?.name) "更新至 $it" else "下载增量包" } +
                                if(uiStatePatch is UiState.Success) {
                                   " ("+ formatDecimal((uiStatePatch as UiState.Success).data,2) + "MB)"
                                } else ""
                    ,
                    textColor = Color. Unspecified,
                    containerColor = MaterialTheme.colorScheme.outlineVariant.copy(.5f)
                )
            }
        }
        AnimatedVisibility(
            visible = !able || pro > 0,
            exit = AppAnimationManager.upDownAnimation.exit,
            enter = AppAnimationManager.upDownAnimation.enter
        ) {

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP), horizontalArrangement = Arrangement.Center)  {

                if(able && pro == 1f) {
                    FilledTonalButton(
                        onClick = {
                            openDownload()
                        },
                        modifier = Modifier.fillMaxWidth().weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Text(text = "下载管理" )
                    }
                } else {
                    Button(
                        onClick = {
                            activity?.let { AppDownloadManager.downloadPatch(patchFileName, it) }
                        },
                        modifier = Modifier.fillMaxWidth().weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                        enabled = able
                    ) {
                        Text(text =
                            if(able && pro == 0f) "下载"
                            else "${(pro*100).toInt()} %"
                        )
                    }
                }
                if(pro > 0) {
                    Spacer(Modifier.width(APP_HORIZONTAL_DP/3))
                    FilledTonalButton(
                        onClick = {
                            if (pro == 1f) {
                                coroutineScope.launch {
                                    async { loadingPatch = true }.await()
                                    async { BsdiffUpdate.mergePatchApk(context,patch) }.await()
                                    launch { loadingPatch = false }
                                }
                            } else {
                                openDownload()
                            }
                        },
                        shape = MaterialTheme.shapes.medium,
                        colors =if(pro != 1f)  ButtonDefaults. filledTonalButtonColors() else ButtonDefaults. buttonColors(),
                        modifier = Modifier.fillMaxWidth().weight(.5f)
                    ) {
                        Text(text = if(pro == 1f) "安装" else "管理下载任务")
                    }
                }
            }
        }
    }
}
