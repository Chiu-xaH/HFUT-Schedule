package com.hfut.schedule.ui.screen.home.cube.sub.update

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
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
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.model.GiteeReleaseResponse
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.sys.AppDownloadManager.installApk
import com.hfut.schedule.logic.util.sys.AppDownloadManager.installPatchedApk
import com.hfut.schedule.logic.util.sys.AppDownloadManager.openDownload
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.button.BottomButton
import com.hfut.schedule.ui.component.button.LargeButton
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.bsdiffs.model.Patch
import com.xah.bsdiffs.util.BsdiffUpdate
import com.xah.bsdiffs.util.parsePatch
import com.xah.uicommon.component.status.LoadingUI
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.File

sealed class DownloadResult {
    data class Downloaded(val file : File) : DownloadResult()
    data class Progress(val downloadId: Long, val progress: Int) : DownloadResult()
    data class Success(val downloadId: Long, val file: File, val uri: Uri) : DownloadResult()
    data class Failed(val downloadId: Long, val reason: String?) : DownloadResult()
    object Prepare : DownloadResult()
}

fun downloadFile(
    context: Context,
    url: String,
    fileName: String,
    delayTimesLong: Long = 1000L,
    requestBuilder: (DownloadManager.Request) -> DownloadManager.Request = { it },
    customDownloadId: Long? = null
): Flow<DownloadResult> = flow {
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val destDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    if (!destDir.exists()) {
        destDir.mkdirs()
    }

    val destFile = File(destDir, fileName)

    if (destFile.exists()) {
        // 下载过
        emit(DownloadResult.Downloaded(destFile))
        return@flow
    }

    var request = DownloadManager.Request(url.toUri())
        .setTitle(fileName)
        .setDescription("下载 $fileName")
        .setDestinationUri(Uri.fromFile(destFile))
        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        .addRequestHeader("Range", "bytes=0-")

    request = requestBuilder(request)

    val downloadId = customDownloadId ?: downloadManager.enqueue(request)

    val query = DownloadManager.Query().setFilterById(downloadId)

    var downloading = true
    while (downloading) {
        val cursor = downloadManager.query(query)
        if (cursor != null && cursor.moveToFirst()) {
            val bytesDownloaded = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
            val bytesTotal = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

            if (bytesTotal > 0) {
                val progress = (bytesDownloaded * 100 / bytesTotal).toInt()
                emit(DownloadResult.Progress(downloadId, progress))
            }

            when (cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))) {
                DownloadManager.STATUS_SUCCESSFUL -> {
                    downloading = false
                    emit(
                        DownloadResult.Success(
                            downloadId = downloadId,
                            file = destFile,
                            uri = Uri.fromFile(destFile)
                        )
                    )
                }

                DownloadManager.STATUS_FAILED -> {
                    downloading = false
                    cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON)).let { reason ->
                        val msg = when (reason) {
                            DownloadManager.ERROR_CANNOT_RESUME -> "不能恢复下载（可能是服务器不支持断点续传）"
                            DownloadManager.ERROR_DEVICE_NOT_FOUND -> "设备存储不可用"
                            DownloadManager.ERROR_FILE_ERROR -> "文件系统错误"
                            DownloadManager.ERROR_HTTP_DATA_ERROR -> "HTTP 数据错误（网络或服务器断开）"
                            DownloadManager.ERROR_INSUFFICIENT_SPACE -> "存储空间不足"
                            DownloadManager.ERROR_TOO_MANY_REDIRECTS -> "重定向次数太多"
                            DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> "无法处理的 HTTP 响应码"
                            DownloadManager.ERROR_UNKNOWN -> "未知错误"
                            else -> "原因: $reason"
                        }
                        Log.e("Download", "下载失败: $msg")
                        emit(DownloadResult.Failed(downloadId, msg))
                    }
                }
            }
        }
        cursor?.close()
        delay(delayTimesLong)
    }
}.flowOn(Dispatchers.IO)

class UpdateViewModel() : ViewModel() {
    private val _downloadState = MutableStateFlow<DownloadResult>(
        DownloadResult.Prepare
    )
    val downloadState: StateFlow<DownloadResult> = _downloadState

    private var downloadJob: Job? = null

    fun startDownload(url : String,filename : String,context: Context) {
        // 避免重复下载
        if (downloadJob != null) return

        downloadJob = viewModelScope.launch {
            downloadFile(
                context = context,
                url = url,
                fileName = filename
            ).collect { result ->
                _downloadState.value = result
            }
        }
    }

    fun reset() {
        downloadJob?.cancel()
        downloadJob = null
        _downloadState.value = DownloadResult.Prepare
    }
}

// 全量更新UI
@Composable
fun UpdateUI(
    vm : NetWorkViewModel,
    update : GiteeReleaseResponse?,
    viewModel: UpdateViewModel = viewModel<UpdateViewModel>(key = "update")
) {
    val updateState = vm.giteeUpdatesResp.state.collectAsState()
    val canDownload = updateState.value is UiState.Success
    val context = LocalContext.current
    val downloadState by viewModel.downloadState.collectAsState()
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

    CustomCard(color = MaterialTheme.colorScheme.surface) {
        TransplantListItem(
            headlineContent = { Text(text = if(canDownload)"发现新版本" else "检查更新失败") },
            supportingContent = { Text(text = if(canDownload) "${AppVersion.getVersionName()} → ${update?.name}" else "${update?.name} 点击手动下载") },
            leadingContent = {
                BadgedBox(
                    badge = {
                        Badge()
                    }
                )  {
                    Icon(painterResource(R.drawable.arrow_upward), contentDescription = "Localized description",)
                }
            },
            trailingContent = {
                if(canDownload) {
                    FilledTonalIconButton(
                        onClick = { expandItems = !expandItems },
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


        when (downloadState) {
            is DownloadResult.Prepare -> {
                if(canDownload) {
                    // 准备阶段
                    BottomButton(
                        onClick = {
                            update?.name?.let { version ->
                                viewModel.startDownload(
                                    "${MyApplication.GITEE_UPDATE_URL}releases/download/Android/${version}.apk",
                                    "聚在工大_${version}.apk",
                                    context
                                )
                            }
                        },
                        text =
                            "下载" + if(uiState is UiState.Success) {
                                " ("+formatDecimal((uiState as UiState.Success).data,2) + "MB)"
                            } else "",
                    )
                }
            }
            else -> {}
        }
    }

    when (downloadState) {
        is DownloadResult.Downloaded -> {
            Spacer(Modifier.height(CARD_NORMAL_DP))
            // 检查是否有下载好的文件 有就显示
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = APP_HORIZONTAL_DP),
                horizontalArrangement = Arrangement.Center
            )  {
                LargeButton(
                    onClick = {
                        installApk((downloadState as DownloadResult.Downloaded).file,context)
                    },
                    modifier = Modifier.fillMaxWidth().weight(1/3f),
                    text = "安装",
                    icon = R.drawable.apk_document,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.width(APP_HORIZONTAL_DP/3))
                LargeButton(
                    onClick = {
                        (downloadState as DownloadResult.Downloaded).file.delete()
                        viewModel.reset()
                    },
                    modifier = Modifier.fillMaxWidth().weight(1/3f),
                    text = "删除",
                    icon = R.drawable.delete,
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
                Spacer(Modifier.width(APP_HORIZONTAL_DP/3))
                LargeButton(
                    onClick = {
                        openDownload()
                    },
                    modifier = Modifier.fillMaxWidth().weight(1/3f),
                    text = "下载管理",
                    icon = R.drawable.folder_managed,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
        is DownloadResult.Prepare -> {}
        is DownloadResult.Progress -> {
            Spacer(Modifier.height(CARD_NORMAL_DP))
            // 更新进度
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = APP_HORIZONTAL_DP),
                horizontalArrangement = Arrangement.Center
            )  {
                LargeButton(
                    onClick = {
                        openDownload()
                    },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().weight(.5f),
                    text = "${(downloadState as DownloadResult.Progress).progress}%",
                    icon = R.drawable.apk_install,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.width(APP_HORIZONTAL_DP/3))
                LargeButton(
                    onClick = {
                        openDownload()
                    },
                    modifier = Modifier.fillMaxWidth().weight(.5f),
                    text = "下载管理",
                    icon = R.drawable.folder_managed,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
        is DownloadResult.Success -> {
            Spacer(Modifier.height(CARD_NORMAL_DP))
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = APP_HORIZONTAL_DP),
                horizontalArrangement = Arrangement.Center
            )  {
                LargeButton(
                    onClick = {
                        installApk((downloadState as DownloadResult.Success).file,context)
                    },
                    modifier = Modifier.fillMaxWidth().weight(.5f),
                    text = "安装",
                    icon = R.drawable.apk_document,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.width(APP_HORIZONTAL_DP/3))
                LargeButton(
                    onClick = {
                        openDownload()
                    },
                    modifier = Modifier.fillMaxWidth().weight(.5f),
                    text = "下载管理",
                    icon = R.drawable.folder_managed,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
        is DownloadResult.Failed -> {
            Spacer(Modifier.height(CARD_NORMAL_DP))
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = APP_HORIZONTAL_DP),
                horizontalArrangement = Arrangement.Center
            )  {
                LargeButton(
                    onClick = {
                        viewModel.reset()
                    },
                    modifier = Modifier.fillMaxWidth().weight(.5f),
                    text = "重试",
                    icon = R.drawable.refresh,
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
                Spacer(Modifier.width(APP_HORIZONTAL_DP/3))
                LargeButton(
                    onClick = {
                        openDownload()
                    },
                    modifier = Modifier.fillMaxWidth().weight(.5f),
                    text = "下载管理",
                    icon = R.drawable.folder_managed,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Spacer(Modifier.height(CARD_NORMAL_DP))
            CardListItem(
                headlineContent = {
                    Text("下载失败")
                },
                color = MaterialTheme.colorScheme.surface,
                supportingContent = {
                    (downloadState as DownloadResult.Failed).reason?.let { Text(it ) }
                },
                leadingContent = {
                    Icon(painterResource(R.drawable.warning),null)
                },
            )
        }
    }
}

// 增量更新UI
@Composable
fun PatchUpdateUI(
    patch: Patch,
    vm: NetWorkViewModel,
    update : GiteeReleaseResponse?,
    viewModel: UpdateViewModel = viewModel<UpdateViewModel>(key = "patch")
) {
    val coroutineScope = rememberCoroutineScope()
    var loadingPatch by remember { mutableStateOf(false) }
    val downloadState by viewModel.downloadState.collectAsState()
    val patchFileName = parsePatch(patch)
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

            when (downloadState) {
                is DownloadResult.Prepare -> {
                    // 准备阶段
                    BottomButton(
                        onClick = {
                            update?.name?.let { version ->
                                viewModel.startDownload(
                                    "${MyApplication.GITEE_UPDATE_URL}releases/download/Android/$patchFileName",
                                    patchFileName,
                                    context
                                )
                            }
                        },
                        text =
                            patch.newVersion.let { if(it != update?.name) "更新至 $it" else "下载" } +
                                    if(uiStatePatch is UiState.Success) {
                                        " ("+ formatDecimal((uiStatePatch as UiState.Success).data,2) + "MB)"
                                    } else ""
                        ,
                    )
                }
                else -> {}
            }
        }

        when (downloadState) {
            is DownloadResult.Downloaded -> {
                Spacer(Modifier.height(CARD_NORMAL_DP))
                // 检查是否有下载好的文件 有就显示
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP), horizontalArrangement = Arrangement.Center)  {
                    LargeButton(
                        onClick = {
                            coroutineScope.launch {
                                async { loadingPatch = true }.await()
                                async {
                                    BsdiffUpdate.mergePatchApk(context,patch){
                                        installPatchedApk(context)
                                    }
                                }.await()
                                launch { loadingPatch = false }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().weight(1/3f),
                        text = "安装",
                        icon = R.drawable.apk_document,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(APP_HORIZONTAL_DP/3))
                    LargeButton(
                        onClick = {
                            (downloadState as DownloadResult.Downloaded).file.delete()
                            viewModel.reset()
                        },
                        modifier = Modifier.fillMaxWidth().weight(1/3f),
                        text = "删除",
                        icon = R.drawable.delete,
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                    Spacer(Modifier.width(APP_HORIZONTAL_DP/3))
                    LargeButton(
                        onClick = {
                            openDownload()
                        },
                        modifier = Modifier.fillMaxWidth().weight(1/3f),
                        text = "下载管理",
                        icon = R.drawable.folder_managed,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            is DownloadResult.Prepare -> {}
            is DownloadResult.Progress -> {
                Spacer(Modifier.height(CARD_NORMAL_DP))
                // 更新进度
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP), horizontalArrangement = Arrangement.Center)  {
                    LargeButton(
                        onClick = {
                            openDownload()
                        },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth().weight(.5f),
                        text = "${(downloadState as DownloadResult.Progress).progress}%",
                        icon = R.drawable.apk_install,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(APP_HORIZONTAL_DP/3))
                    LargeButton(
                        onClick = {
                            openDownload()
                        },
                        modifier = Modifier.fillMaxWidth().weight(.5f),
                        text = "下载管理",
                        icon = R.drawable.folder_managed,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            is DownloadResult.Success -> {
                Spacer(Modifier.height(CARD_NORMAL_DP))
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP), horizontalArrangement = Arrangement.Center)  {
                    LargeButton(
                        onClick = {
                            coroutineScope.launch {
                                async { loadingPatch = true }.await()
                                async {
                                    BsdiffUpdate.mergePatchApk(context,patch){
                                        installPatchedApk(context)
                                    }
                                }.await()
                                launch { loadingPatch = false }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().weight(.5f),
                        text = "安装",
                        icon = R.drawable.apk_document,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(APP_HORIZONTAL_DP/3))
                    LargeButton(
                        onClick = {
                            openDownload()
                        },
                        modifier = Modifier.fillMaxWidth().weight(.5f),
                        text = "下载管理",
                        icon = R.drawable.folder_managed,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            is DownloadResult.Failed -> {
                Spacer(Modifier.height(CARD_NORMAL_DP))
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP), horizontalArrangement = Arrangement.Center)  {
                    LargeButton(
                        onClick = {
                            viewModel.reset()
                        },
                        modifier = Modifier.fillMaxWidth().weight(.5f),
                        text = "重试",
                        icon = R.drawable.refresh,
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                    Spacer(Modifier.width(APP_HORIZONTAL_DP/3))
                    LargeButton(
                        onClick = {
                            openDownload()
                        },
                        modifier = Modifier.fillMaxWidth().weight(.5f),
                        text = "下载管理",
                        icon = R.drawable.folder_managed,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                Spacer(Modifier.height(CARD_NORMAL_DP))

                CardListItem(
                    headlineContent = {
                        Text("下载失败")
                    },
                    color = MaterialTheme.colorScheme.surface,
                    supportingContent = {
                        (downloadState as DownloadResult.Failed).reason?.let { Text(it ) }
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.warning),null)
                    },
                )
            }
        }
    }
}
