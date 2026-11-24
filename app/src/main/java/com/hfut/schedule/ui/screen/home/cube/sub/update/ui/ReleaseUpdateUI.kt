package com.hfut.schedule.ui.screen.home.cube.sub.update.ui

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.parse.roundOffString
import com.hfut.schedule.logic.util.sys.AppDownloadManager
import com.hfut.schedule.logic.util.sys.AppDownloadManager.installApk
import com.hfut.schedule.logic.util.sys.AppDownloadManager.openDownload
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.network.api.model.response.json.gitee.GiteeReleaseResponse
import com.hfut.schedule.ui.component.button.AnimatedExpandIconButton
import com.hfut.schedule.ui.component.button.BUTTON_PADDING
import com.hfut.schedule.ui.component.button.BottomButton
import com.hfut.schedule.ui.component.button.LargeButton
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.icon.CircleProgressIcon
import com.hfut.schedule.ui.screen.home.cube.sub.update.logic.DownloadResult
import com.hfut.schedule.ui.screen.home.cube.sub.update.logic.UpdateViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.logic.state.NetworkUiState
import com.xah.common.logic.util.EMPTY_STRING
import com.xah.common.ui.style.APP_HORIZONTAL_DP

/* 本kt文件已完成多语言文案适配 */

// 全量更新UI
@Composable
fun ReleaseUpdateUI(
    vm : NetWorkViewModel,
    update : GiteeReleaseResponse?,
    viewModel: UpdateViewModel = viewModel<UpdateViewModel>(key = "update")
) {
    val updateState = vm.giteeUpdatesResp.state.collectAsState()
    val canDownload = updateState.value is NetworkUiState.Success
    val context = LocalContext.current
    val downloadState by viewModel.downloadState.collectAsState()
    var expandItems by remember { mutableStateOf(false) }
    val uiState by vm.giteeApkSizeResp.state.collectAsState()

    LaunchedEffect(update) {
        if(uiState is NetworkUiState.Success)
            return@LaunchedEffect
        update?.name?.let {
            vm.giteeApkSizeResp.clear()
            vm.getGiteeApkSize(it)
        }
    }

    CustomCard(color = MaterialTheme.colorScheme.surface) {
        TransplantListItem(
            headlineContent = { Text(text = if(canDownload) stringResource(R.string.settings_update_title_find_new_version) else stringResource(
                R.string.settings_update_title_failed
            )) },
            supportingContent = { Text(text = if(canDownload) "${AppVersion.getVersionName()} → ${update?.name}" else stringResource(
                R.string.settings_update_description, update?.name ?: EMPTY_STRING
            )) },
            leadingContent = {
                BadgedBox(
                    badge = {
                        if(update?.assets?.isNotEmpty() ?: false) {
                            Badge()
                        }
                    }
                )  {
                    Icon(painterResource(R.drawable.arrow_upward), contentDescription = "Localized description",)
                }
            },
            trailingContent = {
                if(canDownload) {
                    AnimatedExpandIconButton(
                        onClick = { expandItems = !expandItems },
                        valueState = expandItems,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                }
            },
            modifier = Modifier.clickable{ Starter.startWebUrlOuter(context,Constant.GITEE_UPDATE_URL+ "/releases/tag/Android") },
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
                headlineContent = { Text(text = stringResource(R.string.settings_update_detail_title)) },
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
                                    "${Constant.GITEE_UPDATE_URL}releases/download/Android/${version}.apk",
                                    "${MyApplication.APP_NAME}_${version}.apk",
                                    context
                                )
                            }
                        },
                        text =
                            stringResource(
                                R.string.settings_update_button_download,
                                if (uiState is NetworkUiState.Success) {
                                    stringResource(
                                        R.string.settings_update_button_download_file_size,
                                        (uiState as NetworkUiState.Success).data.roundOffString(2)
                                    )
                                } else EMPTY_STRING
                            ),
                    )
                }
            }
            else -> {}
        }
    }

    when (downloadState) {
        is DownloadResult.Downloaded -> {
            // 检查是否有下载好的文件 有就显示
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = APP_HORIZONTAL_DP, vertical = BUTTON_PADDING-CARD_NORMAL_DP),
                horizontalArrangement = Arrangement.Center
            )  {
                LargeButton(
                    onClick = {
                        installApk((downloadState as DownloadResult.Downloaded).file,context)
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
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        is DownloadResult.Prepare -> {}
        is DownloadResult.Progress -> {
            val downloadId = (downloadState as DownloadResult.Progress).downloadId
            val progress = (downloadState as DownloadResult.Progress).progress
            // 更新进度
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = APP_HORIZONTAL_DP, vertical = BUTTON_PADDING-CARD_NORMAL_DP),
                horizontalArrangement = Arrangement.Center
            )  {
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
                .padding(horizontal = APP_HORIZONTAL_DP, vertical = BUTTON_PADDING-CARD_NORMAL_DP),
                horizontalArrangement = Arrangement.Center
            )  {
                LargeButton(
                    onClick = {
                        installApk((downloadState as DownloadResult.Success).file,context)
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
                .padding(horizontal = APP_HORIZONTAL_DP, vertical = BUTTON_PADDING-CARD_NORMAL_DP),
                horizontalArrangement = Arrangement.Center
            )  {
                LargeButton(
                    onClick = {
                        viewModel.reset()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1/3f),
                    text = stringResource(R.string.settings_update_button_failed),
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
                    Text(stringResource(R.string.settings_update_failed_title))
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