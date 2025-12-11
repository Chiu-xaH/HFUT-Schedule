package com.hfut.schedule.ui.screen.welcome

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.file.killApp
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.text.AnimatedTextCarousel
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.style.special.bottomBarBlur
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.navigation.navigateAndClear
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.color.topBarTransplantColor
import com.xah.uicommon.style.padding.InnerPaddingHeight
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


val arguments = listOf(
    "本应用所使用权限为：网络、日历(用于向日历写入日程)、存储(用于导入导出文件)、相机(用于扫码)、通知，均由用户自由决定授予",
    "本应用已在Github开源，F-Droid上架，无广告、恶意等行为",
    "本应用推荐但不限于合肥工业大学宣城校区在校生使用，但有部分功能必须登录才可以体验",
    "本应用不代表学校官方，若因使用本应用而造成实际损失，概不负责",
    "本应用存在托管的服务端，用于及时发布信息和收集一些不敏感的数据帮助改善本应用，开发者承诺不会泄露数据",
    "本应用几乎完全端侧运行，无论是逻辑还是界面均写在应用内，无需租赁服务器等经济成本",
    "欢迎用户向开发者反馈、建议或寻求帮助，也欢迎其他开发者借鉴、指正或参与，个人的测试范围有限，需要大家发现问题",
    "最后编辑于 2025-09-03 13:42 第7版"
)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun UseAgreementScreen(
    navController : NavHostController,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            LargeTopAppBar(
                colors = topBarTransplantColor(),
                title = {
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(text = "用户协议", modifier = Modifier.padding(start = 3.5.dp),)
                    }
                },
                actions = {
                    val height = MaterialTheme.typography.headlineMedium.lineHeight.value.dp
                    Row(modifier = Modifier.padding(end = APP_HORIZONTAL_DP)) {
                        Icon(
                            painterResource(R.drawable.partner_exchange),
                            null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier =  Modifier
                                .size(height)
                        )
                    }
                },
                navigationIcon = {
                    AnimatedTextCarousel(
                        welcomeTexts,
                        modifier = Modifier.padding(start = APP_HORIZONTAL_DP),
                        textStyle = MaterialTheme. typography. headlineLarge. copy(color = MaterialTheme.colorScheme.secondaryContainer)
                    )
                },
                modifier = Modifier.topBarBlur(hazeState, )
            )
        },
        bottomBar = {
//            val route = remember { AppNavRoute.Empty.withArgs(AppNavRoute.Home.route) }
            Column () {
                Box(Modifier.bottomBarBlur(hazeState)) {
                    Row(modifier = Modifier
                        .padding(APP_HORIZONTAL_DP)
                        .navigationBarsPadding(),horizontalArrangement = Arrangement.Center) {
                        Button(
                            onClick = {
                                scope.launch {
                                    async {
                                        launch { SharedPrefs.saveString("versionName", AppVersion.getVersionName()) }
                                        launch { SharedPrefs.saveBoolean("canUse", default = false, save = true) }
                                    }.await()
                                    navController.navigateAndClear(AppNavRoute.Home.route)
                                }
                            },
                            shape = MaterialTheme.shapes.extraLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(.5f)
//                                .containerShare(route, MaterialTheme.shapes.extraLarge)
                        ) {
                            Text("同意")
                        }
                        Spacer(modifier = Modifier.width(APP_HORIZONTAL_DP*2/3))
                        FilledTonalButton(
                            onClick = {
                                showToast("已关闭APP")
                                killApp()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(.5f)
                        ) {
                            SharedPrefs.saveBoolean("canUse", default = false, save = false)
                            Text("拒绝")
                        }
                    }
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
            LazyColumn {
                item { InnerPaddingHeight(innerPadding,true) }
                items(arguments.size) { index ->
                    val item = arguments[index]
                    TransplantListItem(
                        headlineContent = { Text(item, modifier = Modifier.padding(start = CARD_NORMAL_DP)) },
//                        leadingContent = { Text((index+1).toString()) }
                    )
                }
                item { InnerPaddingHeight(innerPadding,false) }
            }
        }
    }
}


