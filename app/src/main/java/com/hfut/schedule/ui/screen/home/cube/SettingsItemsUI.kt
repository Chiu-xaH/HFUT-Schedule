package com.hfut.schedule.ui.screen.home.cube

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication.Companion.context
import com.hfut.schedule.logic.model.GiteeReleaseResponse
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.sys.PermissionSet
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.network.util.Constant
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.SmallCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.destination.SettingsAboutDestination
import com.hfut.schedule.ui.destination.SettingsAppearanceDestination
import com.hfut.schedule.ui.destination.SettingsConfigurationDestination
import com.hfut.schedule.ui.destination.SettingsNetworkDestination
import com.hfut.schedule.ui.destination.VersionInfoDestination
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.cube.sub.MyAPIItem
import com.hfut.schedule.ui.screen.home.cube.sub.PersonPart
import com.hfut.schedule.ui.screen.home.cube.sub.update.PatchUpdateUI
import com.hfut.schedule.ui.screen.home.cube.sub.update.UpdateUI
import com.hfut.schedule.ui.screen.home.cube.sub.update.getPatchVersions
import com.hfut.schedule.ui.screen.home.cube.sub.update.getUpdates
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.bsdiffs.model.Patch
import com.xah.bsdiffs.util.BsdiffUpdate
import com.xah.common.ui.component.text.BottomTip
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.container.container.SharedContainer
import com.xah.navigation.util.LocalNavController
import kotlinx.coroutines.launch

/* 本kt文件已完成多语言文案适配 */
@SuppressLint("SuspiciousIndentation")
@Composable
fun SettingsItemsUI() {
    val navTopController = LocalNavController.current
    CustomCard (
        color = MaterialTheme.colorScheme.surface
    ){
        SharedContainer(
            key = SettingsAppearanceDestination.key,
            shape = MaterialTheme.shapes.medium.copy(
                bottomStart = RoundedCornerShape(0.dp).bottomStart,
                bottomEnd = RoundedCornerShape(0.dp).bottomEnd
            ),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            TransplantListItem(
                colors = MaterialTheme.colorScheme.surface,
                headlineContent = { Text(text = stringResource(R.string.appearance_settings_title)) },
                supportingContent = { Text(text = stringResource(R.string.appearance_settings_description))},
                leadingContent = {
                    Icon(painter = painterResource(id = R.drawable.format_paint), contentDescription ="" )
                },
                modifier = Modifier.clickable { navTopController.push(SettingsAppearanceDestination)  }
            )
        }
        PaddingHorizontalDivider()
        SharedContainer(
            key = SettingsConfigurationDestination.key,
            shape = RoundedCornerShape(0.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            TransplantListItem(
                colors = MaterialTheme.colorScheme.surface,
                headlineContent = { Text(text = stringResource(R.string.app_settings_title)) },
                supportingContent = { Text(text = stringResource(R.string.app_settings_description))},
                leadingContent = {
                    Icon(painter = painterResource(id = R.drawable.joystick), contentDescription ="",  )
                },
                modifier = Modifier.clickable {
                    navTopController.push(SettingsConfigurationDestination)
                }
            )
        }
        PaddingHorizontalDivider()
        SharedContainer(
            key = SettingsNetworkDestination.key,
            shape = RoundedCornerShape(0.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            TransplantListItem(
                colors = MaterialTheme.colorScheme.surface,
                headlineContent = { Text(text = stringResource(R.string.network_settings_title)) },
                supportingContent = { Text(text = stringResource(R.string.network_settings_description))},
                leadingContent = {
                    Icon(painter = painterResource(id = R.drawable.net), contentDescription ="" )
                },
                modifier = Modifier.clickable {
                    navTopController.push(SettingsNetworkDestination)
                }
            )
        }
        PaddingHorizontalDivider()
        SharedContainer(
            key = SettingsAboutDestination.key,
            shape = MaterialTheme.shapes.medium.copy(
                topStart = RoundedCornerShape(0.dp).topStart,
                topEnd = RoundedCornerShape(0.dp).topEnd
            ),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            TransplantListItem(
                colors = MaterialTheme.colorScheme.surface,
                headlineContent = { Text(text = stringResource(R.string.about_settings_title)) },
                supportingContent = { Text(text = stringResource(R.string.about_settings_description))},
                leadingContent = {
                    Icon(painter = painterResource(id = R.drawable.partner_exchange), contentDescription ="",modifier = Modifier
                        .size(22.dp)
                        .padding(start = 1.dp) )
                },
                modifier = Modifier.clickable {
                    navTopController.push(SettingsAboutDestination)
                }
            )
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun HomeSettingScreen(innerPaddings : PaddingValues, vm : NetWorkViewModel, ) {
    val currentVersion = remember { AppVersion.getVersionName() }

    var hasCleaned by remember { mutableStateOf(false) }

    val refreshNetwork = suspend {
        vm.giteeUpdatesResp.clear()
        vm.getUpdate()
    }

    val update by produceState<GiteeReleaseResponse?>(initialValue = null) {
        value = getUpdates(vm)
    }

    val uiState by vm.giteeUpdatesResp.state.collectAsState()

    LaunchedEffect(Unit) {
        if(uiState is UiState.Success) {
            return@LaunchedEffect
        }
        refreshNetwork()
    }

    val showUpdate = update != null

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceContainer)) {
        LazyColumn {
            item { InnerPaddingHeight(innerPaddings,true) }
            item {
                DividerTextExpandedWith(text = stringResource(R.string.settings_person_info_half_title)) {
                    PersonPart()
                }
            }
            item {
                MyAPIItem(color = MaterialTheme.colorScheme.surface)
            }
            item {
                if (showUpdate) {
                    val canUseUpdate = AppVersion.getSplitType() ==  AppVersion.SplitType.ARM64
                    DividerTextExpandedWith(text = stringResource(R.string.settings_update_half_title)) {
                        UpdateUI(vm,update)
                        if(canUseUpdate) {
                            val activity = LocalActivity.current
                            LaunchedEffect(activity) {
                                activity?.let { PermissionSet.checkAndRequestStoragePermission(it) }
                            }
                            val patchItem by produceState<Patch?>(initialValue = null) {
                                value = getPatchVersions(vm).find { item ->
                                    currentVersion == item.oldVersion
                                }
                            }

                            if (patchItem != null) {
                                Spacer(Modifier.height(CARD_NORMAL_DP))
                                PaddingHorizontalDivider(isDashed = true)
                                PatchUpdateUI(patchItem!!,vm,update)
                            } else if(!AppVersion.isDev) {
                                BottomTip(stringResource(R.string.settings_update_tips_none_patch))
                                // 清理
                                if(!hasCleaned) {
                                    hasCleaned = BsdiffUpdate.deleteCache(context)
                                }
                            }
                        }
                        if(!canUseUpdate) {
                            BottomTip(stringResource(R.string.settings_update_tips_different_architectures))
                        }
                    }
                }
            }
            item {
                DividerTextExpandedWith(text = stringResource(R.string.settings_always_display_half_title)) {
                    AlwaysItem(update)
                }
            }
            item {
                DividerTextExpandedWith(text = stringResource(R.string.settings_half_title)) {
                    SettingsItemsUI()
                }
            }
            item { InnerPaddingHeight(innerPaddings,false) }
        }
    }
}


@Composable
fun GithubDownloadUI() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    TransplantListItem(
        headlineContent = {
            Text(stringResource(R.string.settings_about_update_by_github_title))
        },
        supportingContent = {
            Text(stringResource(R.string.settings_about_update_by_github_description))
        },
        leadingContent = { Icon(painterResource(R.drawable.cloud_download),null)},
        modifier = Modifier.clickable {
            scope.launch {
                Starter.startWebView(context ,"${Constant.GITHUB_URL}${Constant.GITHUB_DEVELOPER_NAME}/${Constant.GITHUB_REPO_NAME}/releases/latest")
            }
        }
    )
}

@Composable
fun UpdateContents(vm : NetWorkViewModel) {
    val context = LocalContext.current
    val uiState by vm.githubFolderResp.state.collectAsState()
    val refreshNetwork = suspend {
        vm.githubFolderResp.clear()
        vm.getUpdateContents()
    }
    LaunchedEffect(Unit) {
        if(uiState is UiState.Success) {
            return@LaunchedEffect
        }
        refreshNetwork()
    }
    val scope = rememberCoroutineScope()
    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        val list = (uiState as UiState.Success).data.sortedBy {
            val tinyList = it.name.split(".")
            val v1 = tinyList[0].toInt()
            val v2 = tinyList[1].toInt()
            v1 * 1000 + v2
        }.reversed()
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP-CARD_NORMAL_DP*2),
        ) {
            items(list.size, key = { it }) { index ->
                val item = list[index]
                with(item) {
                    val versionName = name.replace(".md","")
                    SmallCard (
//                        color = cardNormalColor(),
                        modifier = Modifier.padding(CARD_NORMAL_DP)
                    ) {
                        TransplantListItem(
                            headlineContent = { Text("v$versionName") },
                            modifier = Modifier.clickable {
                                scope.launch {
                                    Starter.startWebView(context,"${Constant.GITHUB_REPO_URL}/blob/main/docs/update/${name}",versionName,null,R.drawable.github)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun AlwaysItem(update : GiteeReleaseResponse?) {
    val navHostTopController = LocalNavController.current
    val showBadge = update != null && update.assets.isNotEmpty()
    val currentVersion by remember { mutableStateOf(AppVersion.getVersionName()) }
    val isPreview = AppVersion.isDev
    val show = !showBadge || isPreview
    CustomCard(
        color = MaterialTheme.colorScheme.surface,
    ) {
        TransplantListItem(
            headlineContent = { Text(text = stringResource(R.string.network_settings_refresh_login_title)) },
            supportingContent = { Text(text = stringResource(R.string.network_settings_refresh_login_description)) },
            leadingContent = { Icon(painterResource(R.drawable.rotate_right), contentDescription = "Localized description",) },
            modifier = Modifier.clickable { refreshLogin(context) },
        )
        if (show) {
            PaddingHorizontalDivider()
            SharedContainer(
                key = VersionInfoDestination.key,
                shape = MaterialTheme.shapes.medium.copy(
                    topStart = RoundedCornerShape(0.dp).topStart,
                    topEnd = RoundedCornerShape(0.dp).topEnd
                ),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                TransplantListItem(
                    colors = MaterialTheme.colorScheme.surface,
                    headlineContent = { Text(text = stringResource(AppNavRoute.VersionInfo.label)) },
                    supportingContent = { Text(text = if(isPreview) stringResource(R.string.settings_version_info_description_preview) else stringResource(
                        R.string.settings_version_info_description, currentVersion
                    )) },
                    leadingContent = {
                        Icon(
                            painterResource(AppNavRoute.VersionInfo.icon),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {
                        navHostTopController.push(VersionInfoDestination)
                    }
                )
            }
        }
    }
}