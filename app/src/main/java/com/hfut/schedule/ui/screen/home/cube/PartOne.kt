package com.hfut.schedule.ui.screen.home.cube

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.App.MyApplication.Companion.context
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.FixBarItems
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.logic.util.sys.Starter.startWebUrl
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.screen.home.cube.sub.MyAPIItem
import com.hfut.schedule.ui.screen.home.cube.sub.PersonPart
import com.hfut.schedule.ui.screen.home.cube.sub.update.PatchUpdateUI
import com.hfut.schedule.ui.screen.home.cube.sub.update.UpdateUI
import com.hfut.schedule.ui.screen.home.cube.sub.update.VersionInfo
import com.hfut.schedule.ui.screen.home.cube.sub.update.getPatchVersions
import com.hfut.schedule.ui.screen.home.cube.sub.update.getUpdates
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.bsdiffs.util.BsdiffUpdate
import dev.chrisbanes.haze.HazeState


fun apiCheck() : Boolean {
    return try {
        val classes = getPersonInfo().className
        return classes!!.contains("计算机") && classes.contains("23")
    } catch (e : Exception) {
        false
    }
}
@SuppressLint("SuspiciousIndentation")
@Composable
fun PartOne(navController: NavController) {
    CustomCard (
        color = MaterialTheme.colorScheme.surface
    ){
        TransplantListItem(
            headlineContent = { Text(text = "外观与效果") },
            supportingContent = { Text(text = "色彩 动效 动态模糊")},
            leadingContent = {
                Icon(painter = painterResource(id = R.drawable.format_paint), contentDescription ="" )
            },
            modifier = Modifier.clickable { navController.navigate(Screen.UIScreen.route)  }
        )
        PaddingHorizontalDivider()
        TransplantListItem(
            headlineContent = { Text(text = "应用及配置") },
            supportingContent = { Text(text = "交互 缓存清理 偏好配置")},
            leadingContent = {
                Icon(painter = painterResource(id = R.drawable.joystick), contentDescription ="",  )
            },
            modifier = Modifier.clickable { navController.navigate(Screen.APPScreen.route)  }
        )
        PaddingHorizontalDivider()
        TransplantListItem(
            headlineContent = { Text(text = "网络") },
            supportingContent = { Text(text = "预加载 一卡通密码")},
            leadingContent = {
                Icon(painter = painterResource(id = R.drawable.net), contentDescription ="" )
            },
            modifier = Modifier.clickable { navController.navigate(Screen.NetWorkScreen.route)  }
        )
        PaddingHorizontalDivider()
        TransplantListItem(
            headlineContent = { Text(text = "维护与关于") },
            supportingContent = { Text(text = "反馈 关于 疑难修复")},
            leadingContent = {
                Icon(painter = painterResource(id = R.drawable.partner_exchange), contentDescription ="",modifier = Modifier.size(22.dp).padding(start = 1.dp) )
            },
            modifier = Modifier.clickable { navController.navigate(Screen.FIxAboutScreen.route)  }
        )
    }

}

enum class DetailSettings {
    Home,UI,APP,NetWork,FIxAbout
}
sealed class Screen(val route: String) {
    data object HomeScreen : Screen(DetailSettings.Home.name)
    data object UIScreen : Screen(DetailSettings.UI.name)
    data object NetWorkScreen : Screen(DetailSettings.NetWork.name)
    data object APPScreen : Screen(DetailSettings.APP.name)
    data object FIxAboutScreen : Screen(DetailSettings.FIxAbout.name)
    data object FIxScreen : Screen(FixBarItems.Fix.name)
    data object DebugScreen : Screen("DEBUG")
    data object DeveloperScreen : Screen("Developer")
    data object GestureStudyScreen : Screen("GestureStudyScreen")

    data object DownloadScreen : Screen("Download")
    data object CalendarScreen : Screen("Calendar")
    data object LockScreen : Screen("Lock")
    data object FocusCardScreen : Screen("FocusCard")
    data object RequestRangeScreen : Screen("RequestRange")
    data object PasswordScreen : Screen("Password")

}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun HomeSettingScreen(navController: NavController,
                      innerPaddings : PaddingValues,
                      hazeState: HazeState,
                      vm : NetWorkViewModel
) {
   //
    val currentVersion = AppVersion.getVersionName()
    var hasCleaned by remember { mutableStateOf(false) }
    val showUpdate = currentVersion != getUpdates().version
    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .fillMaxSize()
        .padding(innerPaddings)) {


        Spacer(modifier = Modifier.height(5.dp))

        DividerTextExpandedWith(text = "登录信息") {
            PersonPart()
        }


        MyAPIItem(color = MaterialTheme.colorScheme.surface)

        if (showUpdate) {
            DividerTextExpandedWith(text = "更新版本") {
                UpdateUI(vm)
                if(AppVersion.getSplitType() ==  AppVersion.SplitType.ARM64) {
                    val patchItem = getPatchVersions().find { item ->
                        currentVersion == item.oldVersion
                    }

                    if (patchItem != null ) {
                        Spacer(Modifier.height(CARD_NORMAL_DP))
                        PaddingHorizontalDivider(isDashed = true)
                        PatchUpdateUI(patchItem,vm)
                    } else {
                        // 清理
                        if(!hasCleaned) {
                            hasCleaned = BsdiffUpdate.deleteCache(context)
                        }
                    }
                }
                Spacer(Modifier.height(CARD_NORMAL_DP))
                PaddingHorizontalDivider(isDashed = true)
                Spacer(Modifier.height(CARD_NORMAL_DP))
                CustomCard(color = MaterialTheme.colorScheme.surface) {
                    GithubDownloadUI()
                }
            }
        }

        DividerTextExpandedWith(text = "常驻项目") {
            AlwaysItem(hazeState,showUpdate)
        }


        DividerTextExpandedWith(text = "应用设置") {
            PartOne(navController)
        }

        Spacer(modifier = Modifier.height(5.dp))
    }

}


@Composable
fun GithubDownloadUI() {
    TransplantListItem(
        headlineContent = {
            Text("备用更新通道")
        },
        supportingContent = {
            Text("通过Github发行版下载最新版本,不会限速;\n请注意选择后缀'arm64-v8a'(即普通Android移动设备)的APK下载")
        },
        leadingContent = { Icon(painterResource(R.drawable.github),null)},
        modifier = Modifier.clickable {
            Starter.startWebView("${MyApplication.GITHUB_URL}${MyApplication.GITHUB_DEVELOPER_NAME}/${MyApplication.GITHUB_REPO_NAME}/releases/latest")
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlwaysItem(hazeState: HazeState,showUpdate : Boolean) {
    val version by remember { mutableStateOf(getUpdates()) }
    val currentVersion by remember { mutableStateOf(AppVersion.getVersionName()) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val isPreview = remember { AppVersion.isPreview() }

    if (showBottomSheet) {
        HazeBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
//            sheetState = sheetState,
//            shape = bottomSheetRound(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("本版本新特性")
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
    CustomCard(
        color = MaterialTheme.colorScheme.surface
    ) {
        TransplantListItem(
            headlineContent = { Text(text = "刷新登录状态") },
            supportingContent = { Text(text = "如果一卡通或者考试成绩等无法查询,可能是登陆过期,需重新登录一次") },
            leadingContent = { Icon(painterResource(R.drawable.rotate_right), contentDescription = "Localized description",) },
            modifier = Modifier.clickable { refreshLogin() },
        )
        if (currentVersion == version.version || isPreview) {
            PaddingHorizontalDivider()
            TransplantListItem(
                headlineContent = { Text(text = "本版本新特性") },
                supportingContent = { Text(text = if(isPreview) "当前为内部测试版" else "当前已为最新版本 $currentVersion") },
                leadingContent = {
                    Icon(painterResource(R.drawable.info), contentDescription = "Localized description",)
                },
                modifier = Modifier.clickable{
                    showBottomSheet = true
                }
            )
        }
        if(!showUpdate) {
            PaddingHorizontalDivider()
            GithubDownloadUI()
        }
    }
}