package com.hfut.schedule.ui.screen.login

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.component.APP_HORIZONTAL_DP

import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.util.navigateAndClear
import com.hfut.schedule.ui.style.bottomBarBlur
import com.hfut.schedule.ui.style.topBarBlur
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
//@Preview
fun UseAgreementScreen(navController : NavHostController) {
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)

    val argeements = listOf(
        "本应用所使用权限为：网络、日历(用于向日历写入聚焦日程)、存储(用于导入导出课程表文件)、相机(用于洗浴扫码)、通知(用于提醒更新包已准备好)，均由用户自由决定是否授予",
        "本应用已在Github开源，F-Droid上架，无任何盈利、广告、恶意等行为",
        "本应用不代表学校官方，如出现任何损失与开发者无责任，联系邮件为zsh0908@outlook.com",
        "本应用推荐但不限于合肥工业大学宣城校区在校生使用，不对未登录用户做强制要求，可通过游客模式进入",
        "本应用存在开发者自己的服务端，会收集一些不敏感的数据帮助改善使用体验，开发者承诺不会泄露数据，且用户可自由选择开启与否",
        "开发者只负责分发由自己签名的版本(签名为O=Chiu xaH,ST=Anhui,L=Xuancheng)，其他签名版本不对此负责",
        "最后编辑于 2025-03-23 16:42 v4"
    )

    val context = LocalActivity.current


    androidx.compose.material3.Scaffold(
        topBar = {
            LargeTopAppBar(
                colors = topAppBarColors(
        containerColor = Color.Transparent,
        titleContentColor = MaterialTheme.colorScheme.primary
        ),
                title = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "用户协议  ",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            //style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                actions = {
                    //  Row {
                    IconButton(onClick = {
                        context?.finish()
                    }) {
                        Icon(
                            painterResource(id = R.drawable.close),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    //    Text(text = "   ")
                    /// }

                },
                navigationIcon = {
                    AnimatedWelcomeScreen()
                },
                modifier = Modifier.topBarBlur(hazeState)
            )
        },
        bottomBar = {
            Row(modifier = Modifier.padding(APP_HORIZONTAL_DP).statusBarsPadding().bottomBarBlur(hazeState),horizontalArrangement = Arrangement.Center) {
                Button(
                    onClick = {
                        SharedPrefs.saveBoolean("canUse", default = false, save = true)
                        navController.navigateAndClear(MainNav.HOME.name)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f)
                ) {
                    Text("同意")
                }
                Spacer(modifier = Modifier.width(APP_HORIZONTAL_DP))
                FilledTonalButton(
                    onClick = {
                        showToast("已关闭APP")
                        context?.finish()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f)
                ) {
                    SharedPrefs.saveBoolean("canUse", default = false, save = false)
                    Text("拒绝")
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.hazeSource(hazeState).fillMaxSize()
        ) {
            LazyColumn {
                item { Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding())) }
                items(argeements.size) { index ->
                    val item = argeements[index]
                    TransplantListItem(
                        headlineContent = { Text(item) },
                        leadingContent = { Text((index+1).toString()) }
                    )
                }
                item { Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding())) }
            }
        }
    }
}
