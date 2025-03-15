package com.hfut.schedule.ui.activity.home.cube.items.main

import android.annotation.SuppressLint
import android.content.Intent
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
import androidx.compose.material3.ModalBottomSheet
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
import com.hfut.schedule.R
import com.hfut.schedule.activity.MainActivity
import com.hfut.schedule.logic.enums.CardBarItems
import com.hfut.schedule.logic.enums.FixBarItems
import com.hfut.schedule.logic.utils.Starter.startWebUrl
import com.hfut.schedule.logic.utils.VersionUtils
import com.hfut.schedule.ui.activity.home.cube.items.subitems.MyAPIItem
import com.hfut.schedule.ui.activity.home.cube.items.subitems.PersonPart
import com.hfut.schedule.ui.activity.home.cube.items.subitems.update.UpdateUI
import com.hfut.schedule.ui.activity.home.cube.items.subitems.update.VersionInfo
import com.hfut.schedule.ui.activity.home.cube.items.subitems.update.getUpdates
import com.hfut.schedule.ui.activity.home.search.functions.person.getPersonInfo
import com.hfut.schedule.ui.utils.components.BottomSheetTopBar
import com.hfut.schedule.ui.utils.components.DividerTextExpandedWith
import com.hfut.schedule.ui.utils.components.HazeBottomSheetTopBar
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.style.HazeBottomSheet
import com.hfut.schedule.ui.utils.style.bottomSheetRound
import com.hfut.schedule.viewmodel.NetWorkViewModel
import dev.chrisbanes.haze.HazeState


fun apiCheck() : Boolean {
    return try {
        val classes = getPersonInfo().classes
        return classes!!.contains("计算机") && classes.contains("23")
    } catch (e : Exception) {
        false
    }
}
@SuppressLint("SuspiciousIndentation")
@Composable
fun PartOne(vm : NetWorkViewModel,
            showlable : Boolean,
            showlablechanged :(Boolean) -> Unit,
            ifSaved : Boolean,
            blur : Boolean,
            blurchanged :(Boolean) -> Unit,
            navController: NavController) {
    TransplantListItem(
        headlineContent = { Text(text = "界面显示") },
        supportingContent = { Text(text = "实时模糊 运动模糊 转场动画")},
        leadingContent = {
            Icon(painter = painterResource(id = R.drawable.stacks), contentDescription ="" )
        },
        modifier = Modifier.clickable { navController.navigate(Screen.UIScreen.route)  }
    )
    TransplantListItem(
        headlineContent = { Text(text = "应用行为") },
        supportingContent = { Text(text = "快速启动 预加载 主页面")},
        leadingContent = {
            Icon(painter = painterResource(id = R.drawable.empty_dashboard), contentDescription ="" )
        },
        modifier = Modifier.clickable { navController.navigate(Screen.APPScreen.route)  }
    )
    TransplantListItem(
        headlineContent = { Text(text = "网络相关") },
        supportingContent = { Text(text = "云端接口 请求范围 登录状态")},
        leadingContent = {
            Icon(painter = painterResource(id = R.drawable.net), contentDescription ="" )
        },
        modifier = Modifier.clickable { navController.navigate(Screen.NetWorkScreen.route)  }
    )
    TransplantListItem(
        headlineContent = { Text(text = "维护关于") },
        supportingContent = { Text(text = "疑难修复 反馈信息 分享推广")},
        leadingContent = {
            Icon(painter = painterResource(id = R.drawable.responsive_layout), contentDescription ="" )
        },
        modifier = Modifier.clickable { navController.navigate(Screen.FIxAboutScreen.route)  }
    )
}

enum class DetailSettings {
    Home,UI,APP,NetWork,FIxAbout
}
sealed class Screen(val route: String) {
    object HomeScreen : Screen(DetailSettings.Home.name)
    object UIScreen : Screen(DetailSettings.UI.name)
    object NetWorkScreen : Screen(DetailSettings.NetWork.name)
    object APPScreen : Screen(DetailSettings.APP.name)
    object FIxAboutScreen : Screen(DetailSettings.FIxAbout.name)
    object FIxScreen : Screen(FixBarItems.Fix.name)
    object AboutScreen : Screen(FixBarItems.About.name)

    object DebugScreen : Screen("DEBUG")
    object CardBillsScreen : Screen(CardBarItems.BILLS.name)

    object CardCountScreen : Screen(CardBarItems.COUNT.name)
    object CardHomeScreen : Screen(CardBarItems.HOME.name)
    object DownloadScreen : Screen("Download")

}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun HomeSettingScreen(navController: NavController,
                      vm : NetWorkViewModel,
                      showlable : Boolean,
                      showlablechanged: (Boolean) -> Unit,
                      ifSaved : Boolean,
                      innerPaddings : PaddingValues,
                      blur : Boolean,
                      blurchanged : (Boolean) -> Unit,
                      hazeState: HazeState
) {
   //


    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .fillMaxSize()
        .padding(innerPaddings)) {

        Spacer(modifier = Modifier.height(5.dp))

        DividerTextExpandedWith(text = "登录信息") {
            PersonPart()
        }


        MyAPIItem()

        if (VersionUtils.getVersionName() != getUpdates().version) {
            if(!VersionUtils.isPreview()) {
                DividerTextExpandedWith(text = "更新版本") {
                    UpdateUI()
                }
            }
        }

        DividerTextExpandedWith(text = "常驻项目") {
            AlwaysItem(hazeState)
        }


        DividerTextExpandedWith(text = "应用设置") {
            PartOne(vm,showlable,showlablechanged,ifSaved,blur,blurchanged,navController)
        }

        Spacer(modifier = Modifier.height(5.dp))
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlwaysItem(hazeState: HazeState) {
    val version by remember { mutableStateOf(getUpdates()) }
    var showBadge by remember { mutableStateOf(false) }
    if (version.version != VersionUtils.getVersionName()) showBadge = true
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
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
    TransplantListItem(
        headlineContent = { Text(text = "刷新登录状态") },
        supportingContent = { Text(text = "如果一卡通或者考试成绩等无法查询,可能是登陆过期,需重新登录一次") },
        leadingContent = { Icon(painterResource(R.drawable.rotate_right), contentDescription = "Localized description",) },
        modifier = Modifier.clickable {
            val it = Intent(MyApplication.context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra("nologin",false)
            }
            MyApplication.context.startActivity(it) }
    )
    if (VersionUtils.getVersionName() == getUpdates().version)
        TransplantListItem(
            headlineContent = { Text(text = "查看本版本新特性") },
            supportingContent = { Text(text = if(version.version == VersionUtils.getVersionName()) "当前为最新版本 ${VersionUtils.getVersionName()}" else "当前版本  ${VersionUtils.getVersionName()}\n最新版本  ${version.version}") },
            leadingContent = {
                BadgedBox(badge = {
                    if(showBadge)
                        Badge(modifier = Modifier.size(7.dp)) }) {
                    Icon(painterResource(R.drawable.arrow_upward), contentDescription = "Localized description",)
                }
            },
            modifier = Modifier.clickable{
                if (version.version != VersionUtils.getVersionName())
                    startWebUrl(MyApplication.GITEE_UPDATE_URL+ "releases/download/Android/${version.version}.apk")
                else {
                    showBottomSheet = true
                }
            }
        )

}