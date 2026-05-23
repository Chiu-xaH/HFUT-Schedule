package com.hfut.schedule.ui.screen.fix.fix

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.dev.CrashHandler
import com.hfut.schedule.logic.util.sys.PermissionSet
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.Starter.emailMe
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.network.util.Constant
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.icon.LoadingIcon
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.nav.destination.VersionInfoDestination

import com.hfut.schedule.ui.screen.home.cube.sub.MyAPIItem
import com.hfut.schedule.ui.screen.home.cube.sub.VersionInfo
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.style.color.ShimmerAngle
import com.xah.common.ui.style.color.shimmerEffect
import com.xah.shared.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FixUI(
    innerPadding : PaddingValues,
    vm : NetWorkViewModel,
) {
    val context = LocalContext.current

    var showBottomSheet_version by remember { mutableStateOf(false) }
    if (showBottomSheet_version) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_version = false },
            showBottomSheet = showBottomSheet_version
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = androidx.compose.ui.graphics.Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar(VersionInfoDestination.title.asString())
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ) {
                    VersionInfo()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            vm.getMyApi()
        }
    }

    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(innerPadding)
    ) {
        Spacer(modifier = Modifier.height(CARD_NORMAL_DP*2))
        CustomCard(color = MaterialTheme.colorScheme.surface) {
            TransplantListItem(
                headlineContent = { Text(text = VersionInfoDestination.title.asString()) },
                supportingContent = { Text(text = stringResource(R.string.about_settings_version_info_description))},
                modifier = Modifier.clickable { showBottomSheet_version = true },
                leadingContent = { Icon(painter = painterResource(id = R.drawable.sdk), contentDescription = "")}
            )
            PaddingHorizontalDivider()
            BugShare()
            PaddingHorizontalDivider()
            TransplantListItem(
                headlineContent = { Text(text = "下载最新版本") },
                leadingContent = { Icon(painterResource(R.drawable.cloud_download), contentDescription = "Localized description",) },
                modifier = Modifier.clickable{ Starter.startWebUrlOuter(context,Constant.GITEE_UPDATE_URL + "releases/tag/Android") }
            )
            PaddingHorizontalDivider()
            TransplantListItem(
                headlineContent = { Text(text = "刷新登录状态") },
                leadingContent = { Icon(painterResource(R.drawable.rotate_right), contentDescription = "Localized description",) },
                modifier = Modifier.clickable {
                    refreshLogin(context)
                }
            )
            PaddingHorizontalDivider()
            TransplantListItem(
                headlineContent = { Text(text = "联系开发者") },
                leadingContent = { Icon(painterResource(R.drawable.mail), contentDescription = "Localized description",) },
                modifier = Modifier.clickable{ emailMe(context) }
            )
        }
        MyAPIItem(color = MaterialTheme.colorScheme.surface)
    }
}


@Composable
fun BugShare() {
    val activity = LocalActivity.current
    val scope = rememberCoroutineScope()

    TransplantListItem(
        headlineContent = { Text(text = "崩溃日志抓取") },
        leadingContent = {
            if(CrashHandler.isLoggingEnabled) {
                LoadingIcon()
            } else{
                Icon(
                    painter = painterResource(id = R.drawable.slow_motion_video), contentDescription = ""
                )
            }
        },
        modifier = Modifier.clickable {
            activity?.let { PermissionSet.checkAndRequestStoragePermission(it) }
            if(!CrashHandler.isLoggingEnabled) {
                CrashHandler.enableLogging()
            } else {
                CrashHandler.disableLogging()
                showToast("日志抓取已关闭")
            }
        },
        trailingContent = {
            Switch(checked = CrashHandler.isLoggingEnabled, onCheckedChange = {
                activity?.let { PermissionSet.checkAndRequestStoragePermission(it) }
                if(!CrashHandler.isLoggingEnabled) {
                    CrashHandler.enableLogging()
                } else {
                    CrashHandler.disableLogging()
                }
            })
        },
        supportingContent = {
            if(CrashHandler.isLoggingEnabled) {
                Text("正在记录日志,请复现崩溃闪退的操作,当崩溃后，在Download文件夹寻找崩溃日志")
            } else {
                Text("点击开始抓取，崩溃后，日志将保存于Download文件夹")
            }
        }
    )
    PaddingHorizontalDivider()
    var loading by remember { mutableStateOf(false) }
    var num by remember { mutableIntStateOf(0) }
    LaunchedEffect(loading) {
        num = LogUtil.getCachedLogsSize()
    }
    TransplantListItem(
        headlineContent = { Text(text = "导出错误日志 (${num}条)") },
        leadingContent = {
            if(loading) {
                LoadingIcon()
            } else {
                Icon(painterResource(R.drawable.save), contentDescription = "Localized description",)
            }
        },
        modifier = Modifier.clickable {
            scope.launch {
                activity?.let { PermissionSet.checkAndRequestStoragePermission(it) }
                loading = true
                CrashHandler.saveErrorLog()
                loading = false
            }
        },
        supportingContent = {
            Text("可导出非崩溃情况下的错误日志，导出后，在Download文件夹寻找错误日志")
        },
    )
}
