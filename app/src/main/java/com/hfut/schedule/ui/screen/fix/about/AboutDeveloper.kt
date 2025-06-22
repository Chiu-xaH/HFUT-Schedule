package com.hfut.schedule.ui.screen.fix.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.DividerTextExpandedWith
import com.hfut.schedule.ui.component.custom.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.Party
import com.hfut.schedule.ui.component.custom.ScrollText
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.URLImage
 
import com.hfut.schedule.viewmodel.network.NetWorkViewModel

private data class Build(
    val languages : List<String>,
    val build : List<String>,
    val ui : String,
    val jetpack : String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun About(vm : NetWorkViewModel) {
    val openSourceProjects = listOf(
        "Okhttp" to "网络请求",
        "Retrofit" to "网络请求",
        "Gson" to "JSON解析",
        "Jsoup" to "XML/HTML解析",
        "Zxing" to "二维码",
        "Haze" to "实时模糊(SDK>=33)",
        "Accompanist" to "用做实现透明状态栏",
        "Glide" to "网络图片",
        "EdDSA Java" to "加密(供和风天气API使用)",
        "Konfetti" to "礼花动画",
        "Tesseract4Android" to "封装Tesseract4(供图片验证码识别)",
        "Bsdiff-Lib" to "增量更新"
    )
    val dependencies = Build(
        jetpack = "Jetpack Compose",
        ui = "Material Design 3 (Material You)",
        languages = listOf("Kotlin","Java"),
        build = listOf( "Gradle 8.3 With Groovy","OpenJDK 17"),
    )

    LaunchedEffect(Unit) {
        vm.githubStarsData.clear()
        vm.getStarNum()
    }
    var starsNum by remember { mutableStateOf("") }
    val uiState by vm.githubStarsData.state.collectAsState()
    var loading = uiState is UiState.Loading
    LaunchedEffect(uiState) {
        if(uiState is UiState.Success) {
            starsNum = (uiState as UiState.Success).data.toString()
        }
    }

    Party(
        show = !loading
    ) {
        androidx.compose.material3.Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                HazeBottomSheetTopBar("关于")
            },
            bottomBar = {
                Row(modifier = Modifier.padding(APP_HORIZONTAL_DP),horizontalArrangement = Arrangement.Center) {
                    Button(
                        onClick = { Starter.startWebUrl("https://github.com/${MyApplication.GITHUB_DEVELOPER_NAME}/${MyApplication.GITHUB_REPO_NAME}") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f)
                    ) {
                        Text("Stars ⭐ $starsNum")
                    }

                    Spacer(modifier = Modifier.width(APP_HORIZONTAL_DP))
                    FilledTonalButton(
                        onClick = { Starter.startWebUrl("https://github.com/${MyApplication.GITHUB_DEVELOPER_NAME}/${MyApplication.GITHUB_REPO_NAME}/releases/latest") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f)
                    ) {
                        Text("最新版本")
                    }
                }
            },
        ) { innerPadding ->
            Column(modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())) {
                DividerTextExpandedWith("开发者") {
                    TransplantListItem(
                        modifier = Modifier.clickable {
                            Starter.startWebUrl("https://github.com/${MyApplication.GITHUB_DEVELOPER_NAME}")
                        },
                        headlineContent = { ScrollText(MyApplication.GITHUB_DEVELOPER_NAME) },
                        leadingContent = {
                            URLImage(url = MyApplication.GITHUB_USER_IMAGE_URL + MyApplication.GITHUB_USER_ID, width = 50.dp, height = 50.dp)
                        },
                        supportingContent = {
                            Text("一名热爱安卓的开发者,宣城校区23级计算机科学与技术专业(转)本科生")
                        },
                        trailingContent = {
                            FilledTonalIconButton(
                                onClick = {
                                    Starter.emailMe()
                                }
                            ) {
                                Icon(painterResource(R.drawable.mail),null)
                            }
                        }
                    )

                }

                DividerTextExpandedWith("构建") {
                    TransplantListItem(
                        headlineContent = { Text("Kotlin / Java") },
                        overlineContent = { Text("主语言") }
                    )
                    TransplantListItem(
                        headlineContent = { Text(dependencies.ui) },
                        overlineContent = { Text("UI设计") }
                    )
                    TransplantListItem(
                        headlineContent = { Text(dependencies.jetpack) },
                    )
                    val builds = dependencies.build
                    for(index in builds.indices) {
                        TransplantListItem(
                            headlineContent = { Text(builds[index]) },
                            overlineContent = { Text("构建 打包") }
                        )
                    }
                }

                DividerTextExpandedWith("开源引用") {
                    for(index in openSourceProjects.indices step 2) {
                        Row {
                            TransplantListItem(
                                headlineContent = { Text(openSourceProjects[index].first) },
                                supportingContent = { Text(openSourceProjects[index].second) },
                                modifier = Modifier.weight(.5f)
                            )
                            if(index+1 < openSourceProjects.size)
                                TransplantListItem(
                                    headlineContent = { Text(openSourceProjects[index+1].first) },
                                    supportingContent = { Text(openSourceProjects[index+1].second) },
                                    modifier = Modifier.weight(.5f)
                                )
                        }

                    }
                }
            }
        }
    }
}