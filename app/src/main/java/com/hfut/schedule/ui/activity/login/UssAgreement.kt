package com.hfut.schedule.ui.activity.login

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.AndroidVersion.canBlur
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.utils.NavigateManager
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.style.bottomBarBlur
import com.hfut.schedule.ui.utils.style.topBarBlur
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
//@Preview
fun UseAgreementUI(navController : NavHostController) {
    val hazeState = remember { HazeState() }
    val switchblur = prefs.getBoolean("SWITCHBLUR", canBlur)
    var blur by remember { mutableStateOf(switchblur) }

    val argeements = listOf(
        "本应用所使用权限为：网络、日历(用于向日历写入聚焦日程)、存储(用于导入导出课程表文件)、相机(用于洗浴扫码)，均由用户自由决定是否授予",
        "本应用已在Github开源，F-Droid上架，无任何盈利、广告、恶意等行为",
        "本应用不代表学校官方，如侵害到您的权益请联系邮件zsh0908@outlook.com",
        "本应用推荐但不限于合肥工业大学宣城校区在校生使用，不对未登录用户做强制要求，可通过游客模式进入",
        "本应用存在开发者自己的服务端，会收集一些不敏感的数据帮助改善使用体验，开发者承诺不会泄露数据，且用户可自由选择开启与否",
        "开发者只负责分发由自己签名的版本(签名为O=Chiu xaH,ST=Anhui,L=Xuancheng)，其他签名版本不对此负责",
        "最后编辑于 2025-02-13 22:00 v2"
    )

    val context = LocalContext.current


    androidx.compose.material3.Scaffold(
        topBar = {
            LargeTopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "用户协议",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            //style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                actions = {
                    //  Row {
                    IconButton(onClick = {
                        (context as? Activity)?.finish()
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
                modifier = Modifier.topBarBlur(hazeState,blur)
            )
        },
        bottomBar = {
            Box(modifier = Modifier.bottomBarBlur(hazeState, blur)) {
                Row(modifier = Modifier.padding(15.dp),horizontalArrangement = Arrangement.Center) {
                    Button(
                        onClick = {
                            SharePrefs.saveBoolean("canUse", default = false, save = true)
                            NavigateManager.turnToAndClear(navController, First.HOME.name)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f)
                    ) {
                        Text("同意")
                    }
                    Spacer(modifier = Modifier.width(15.dp))
                    FilledTonalButton(
                        onClick = {
                            MyToast("已关闭APP")
                            (context as Activity).finish()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f)
                    ) {
                        SharePrefs.saveBoolean("canUse", default = false, save = false)
                        Text("拒绝")
                    }
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize().haze(hazeState)
        ) {
            LazyColumn {
                item { Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding())) }
                items(argeements.size) { index ->
                    val item = argeements[index]
                    ListItem(
                        headlineContent = { Text(item) },
                        leadingContent = { Text((index+1).toString()) }
                    )
                }
                item { Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding())) }
            }
        }
    }
}
