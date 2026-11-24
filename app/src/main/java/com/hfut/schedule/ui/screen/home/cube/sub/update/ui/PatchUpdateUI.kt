package com.hfut.schedule.ui.screen.home.cube.sub.update.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.parse.roundOffString
import com.hfut.schedule.logic.util.sys.AppDownloadManager
import com.hfut.schedule.logic.util.sys.AppDownloadManager.installPatchedApk
import com.hfut.schedule.logic.util.sys.AppDownloadManager.openDownload
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.network.api.model.response.json.gitee.GiteeReleaseResponse
import com.hfut.schedule.ui.component.button.BUTTON_PADDING
import com.hfut.schedule.ui.component.button.BottomButton
import com.hfut.schedule.ui.component.button.LargeButton
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.icon.CircleProgressIcon
import com.hfut.schedule.ui.screen.home.cube.sub.update.logic.DownloadResult
import com.hfut.schedule.ui.screen.home.cube.sub.update.logic.UpdateViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.bsdiffs.model.Patch
import com.xah.bsdiffs.util.BsdiffUpdate
import com.xah.bsdiffs.util.parsePatch
import com.xah.common.logic.state.NetworkUiState
import com.xah.common.logic.util.EMPTY_STRING
import com.xah.common.ui.component.status.LoadingUI
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/* 本kt文件已完成多语言文案适配 */

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
        if(uiStatePatch is NetworkUiState.Success)
            return@LaunchedEffect
        vm.giteePatchSizeResp.clear()
        vm.getGiteePatchSize(patch)
    }

    val context = LocalContext.current

    if(loadingPatch) {
        LoadingUI(stringResource(R.string.settings_update_patch_loading))
    } else {
        Spacer(Modifier.height(CARD_NORMAL_DP))
        CustomCard( color = MaterialTheme.colorScheme.surface) {
            TransplantListItem(
                headlineContent = { Text(text = stringResource(
                    R.string.settings_update_merge_patch_title,
                    patch.newVersion.let { if (it == update?.name) stringResource(R.string.settings_update_patch_title_latest) else (EMPTY_STRING + it) })
                ) },
                supportingContent = {
                    Text(text = stringResource(R.string.settings_update_patch_description))
                },
                leadingContent = { Icon(painterResource(R.drawable.package_2), contentDescription = "Localized description",) },
            )

            when (downloadState) {
                is DownloadResult.Prepare -> {
                    // 准备阶段
                    BottomButton(
                        onClick = {
                            update?.name?.let {
                                viewModel.startDownload(
                                    "${Constant.GITEE_UPDATE_URL}releases/download/Android/$patchFileName",
                                    patchFileName,
                                    context
                                )
                            }
                        },
                        text =
                            "${
                                patch.newVersion.let {
                                    if(it != update?.name)
                                        stringResource(
                                            R.string.settings_update_patch_button_download_to_version,
                                            it
                                        )
                                    else
                                        stringResource(R.string.settings_update_patch_button__download_to_latest)
                                }
                            }${
                                if(uiStatePatch is NetworkUiState.Success) {
                                    stringResource(
                                        R.string.settings_update_button_download_file_size,
                                        (uiStatePatch as NetworkUiState.Success).data.roundOffString(2)
                                    )
                                } else EMPTY_STRING
                            }",
                    )
                }
                else -> {}
            }
        }

        when (downloadState) {
            is DownloadResult.Downloaded -> {
                // 检查是否有下载好的文件 有就显示
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = APP_HORIZONTAL_DP, vertical = BUTTON_PADDING-CARD_NORMAL_DP), horizontalArrangement = Arrangement.Center)  {
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1/3f),
                        text = stringResource(R.string.settings_update_button_install),
                        icon = R.drawable.apk_document,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(BUTTON_PADDING))
                    LargeButton(
                        onClick = {
                            openDownload()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1/3f),
                        text = stringResource(R.string.settings_update_button_download_manage),
                        icon = R.drawable.folder_managed,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(Modifier.width(BUTTON_PADDING))
                    LargeButton(
                        onClick = {
                            (downloadState as DownloadResult.Downloaded).file.delete()
                            viewModel.reset()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1/3f),
                        text = stringResource(R.string.settings_update_button_delete),
                        icon = R.drawable.delete,
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                }
            }
            is DownloadResult.Prepare -> {}
            is DownloadResult.Progress -> {
                val progress = (downloadState as DownloadResult.Progress).progress
                val downloadId = (downloadState as DownloadResult.Progress).downloadId
                // 更新进度
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = APP_HORIZONTAL_DP, vertical = BUTTON_PADDING-CARD_NORMAL_DP), horizontalArrangement = Arrangement.Center)  {
                    LargeButton(
                        onClick = {
                            openDownload()
                        },
                        enabled = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1/3f),
                        text = "${progress}%",
                        icon = {
                            CircleProgressIcon(progress/100f)
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(BUTTON_PADDING))
                    LargeButton(
                        onClick = {
                            openDownload()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1/3f),
                        text = stringResource(R.string.settings_update_button_download_manage),
                        icon = R.drawable.folder_managed,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(Modifier.width(BUTTON_PADDING))
                    LargeButton(
                        onClick = {
                            AppDownloadManager.dlManager.remove(downloadId)
                            viewModel.reset()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1/3f),
                        text = stringResource(R.string.settings_update_button_delete),
                        icon = R.drawable.delete,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            is DownloadResult.Success -> {
                val downloadId = (downloadState as DownloadResult.Success).downloadId
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = APP_HORIZONTAL_DP, vertical = BUTTON_PADDING-CARD_NORMAL_DP), horizontalArrangement = Arrangement.Center)  {
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1/3f),
                        text = stringResource(R.string.settings_update_button_install),
                        icon = R.drawable.apk_document,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(BUTTON_PADDING))
                    LargeButton(
                        onClick = {
                            openDownload()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1/3f),
                        text = stringResource(R.string.settings_update_button_download_manage),
                        icon = R.drawable.folder_managed,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(Modifier.width(BUTTON_PADDING))
                    LargeButton(
                        onClick = {
                            AppDownloadManager.dlManager.remove(downloadId)
                            viewModel.reset()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1/3f),
                        text = stringResource(R.string.settings_update_button_delete),
                        icon = R.drawable.delete,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            is DownloadResult.Failed -> {
                val downloadId = (downloadState as DownloadResult.Failed).downloadId
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = APP_HORIZONTAL_DP, vertical = BUTTON_PADDING-CARD_NORMAL_DP), horizontalArrangement = Arrangement.Center)  {
                    LargeButton(
                        onClick = {
                            viewModel.reset()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1/3f),
                        text =  stringResource(R.string.settings_update_button_failed),
                        icon = R.drawable.refresh,
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                    Spacer(Modifier.width(BUTTON_PADDING))
                    LargeButton(
                        onClick = {
                            openDownload()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1/3f),
                        text = stringResource(R.string.settings_update_button_download_manage),
                        icon = R.drawable.folder_managed,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(Modifier.width(BUTTON_PADDING))
                    LargeButton(
                        onClick = {
                            AppDownloadManager.dlManager.remove(downloadId)
                            viewModel.reset()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1/3f),
                        text = stringResource(R.string.settings_update_button_delete),
                        icon = R.drawable.delete,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Spacer(Modifier.height(CARD_NORMAL_DP))

                CardListItem(
                    headlineContent = {
                        Text( stringResource(R.string.settings_update_failed_title))
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