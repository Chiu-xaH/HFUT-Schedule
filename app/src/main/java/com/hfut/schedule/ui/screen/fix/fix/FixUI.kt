package com.hfut.schedule.ui.screen.fix.fix

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.network.util.MyApiParse.getTimeStamp
import com.hfut.schedule.logic.util.development.CrashHandler
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.Starter.emailMe
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.icon.LoadingIcon
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.screen.home.cube.apiCheck
import com.hfut.schedule.ui.screen.home.cube.sub.MyAPIItem
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.util.TransitionBackHandler
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FixUI(innerPadding : PaddingValues, vm : NetWorkViewModel,navController: NavHostController) {
    val context = LocalContext.current
    val switch_api = prefs.getBoolean("SWITCHMYAPI", apiCheck())
    var showapi by remember { mutableStateOf(switch_api) }
    SharedPrefs.saveBoolean("SWITCHMYAPI", false, showapi)
    val scope = rememberCoroutineScope()

    val enablePredictive by DataStoreManager.enablePredictive.collectAsState(initial = AppVersion.CAN_PREDICTIVE)
    var scale by remember { mutableFloatStateOf(1f) }
    TransitionBackHandler(navController,enablePredictive) {
        scale = it
    }
    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            vm.getMyApi()
        }
    }
    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(innerPadding).scale(scale)) {
        Spacer(modifier = Modifier.height(5.dp))
        DividerTextExpandedWith("操作") {
            CustomCard(color = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { Text(text = "下载最新版本") },
                    leadingContent = { Icon(painterResource(R.drawable.cloud_download), contentDescription = "Localized description",) },
                    modifier = Modifier.clickable{ Starter.startWebUrl(context,MyApplication.GITEE_UPDATE_URL + "releases/tag/Android") }
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
                BugShare()
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "联系与反馈") },
                    leadingContent = { Icon(painterResource(R.drawable.mail), contentDescription = "Localized description",) },
                    modifier = Modifier.clickable{ emailMe(context) }
                )
            }
        }
        MyAPIItem(color = MaterialTheme.colorScheme.surface)
    }
}


@Composable
fun BugShare() {
    val activity = LocalActivity.current
    TransplantListItem(
        headlineContent = { Text(text = "崩溃日志抓取") },
        leadingContent = { Icon(painterResource(R.drawable.monitor_heart), contentDescription = "Localized description",) },
        modifier = Modifier.clickable {
            activity?.let { Starter.openDownloadFolder(it) }
        },
        supportingContent = {
            if(CrashHandler.isLoggingEnabled) {
                Text("日志抓取已开启,请复现崩溃闪退的操作,当崩溃后，去Download文件夹寻找崩溃日志")
            } else {
                Text("点击右侧开始抓取，崩溃后，日志将保存于Download文件夹")
            }
        },
        trailingContent = {
            Row{
                FilledTonalIconButton(onClick = {
                    if(!CrashHandler.isLoggingEnabled) {
                        CrashHandler.enableLogging()
                    } else {
                        CrashHandler.disableLogging()
                        showToast("日志抓取已关闭")
                    }
                }) {
                    if(CrashHandler.isLoggingEnabled) {
                        LoadingIcon()
                    } else{
                        Icon(painter = painterResource(id =
                            R.drawable.slow_motion_video
                        ), contentDescription = "")
                    }
                }
            }
        }
    )
}
