package com.hfut.schedule.ui.activity.fix.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.ui.utils.components.DividerTextExpandedWith
import com.hfut.schedule.ui.utils.components.ScrollText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun About() {
    data class Build(
        val languages : List<String>,
        val build : List<String>,
        val ui : String,
        val jetpack : String
    )
    val openSourceProjects = listOf(
        "Okhttp" to "网络请求",
        "Retrofit" to "网络请求",
        "Gson" to "JSON解析",
        "Jsoup" to "XML/HTML解析",
        "Zxing" to "二维码",
        "Haze" to "实时模糊(SDK>=33)",
        "Accompanist" to "用做实现透明状态栏",
        "Monet" to "莫奈取色(供SDK<32不支持MY取色平替)",
        "Dagger" to "Hilt注入,辅助莫奈取色功能",
        "Glide" to "网络图片",
        "EdDSA Java" to "加密(供和风天气API使用)",
        "Konfetti" to "礼花动画",
    )
    val dependencies = Build(
        jetpack = "Jetpack Compose",
        ui = "Material Design 3 (Material You)",
        languages = listOf("Kotlin","Java"),
        build = listOf( "Gradle 8.3 With Groovy","OpenJDK 17"),
    )
//    var showBottomSheet by remember { mutableStateOf(false) }
//    UseAgreement(showBottomSheet, showBottomSheetChange = { showBottomSheet = false })

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
                            text = "关于",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            //style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                navigationIcon = {
                    Column(modifier = Modifier
                        .padding(horizontal = 23.dp)) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "聚在工大",
                            fontSize = 38.sp,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        )
                    }
                },
            )
        },
        bottomBar = {
            Row(modifier = Modifier.padding(15.dp),horizontalArrangement = Arrangement.Center) {
                Button(
                    onClick = { Starter.startWebUrl("https://github.com/Chiu-xaH/HFUT-Schedule") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f)
                ) {
                    Text("给颗⭐")
                }

                Spacer(modifier = Modifier.width(15.dp))
                FilledTonalButton(
                    onClick = { Starter.startWebUrl("https://github.com/Chiu-xaH") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f)
                ) {
                    Text("我的Github")
                }
            }
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).verticalScroll(rememberScrollState())) {
            DividerTextExpandedWith("开发者") {
                ListItem(
                    modifier = Modifier.clickable {
                        Starter.startWebUrl("https://github.com/Chiu-xaH")
                    },
                    headlineContent = { ScrollText("Chiu-xaH") },
                    leadingContent = { Icon(painterResource(R.drawable.github), null) },
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
                val lans = dependencies.languages
                for(index in lans.indices) {
                    ListItem(
                        headlineContent = { Text(lans[index]) },
                        overlineContent = { Text("主体语言") }
                    )
                }
                ListItem(
                    headlineContent = { Text(dependencies.ui) },
                    overlineContent = { Text("UI设计") }
                )
                ListItem(
                    headlineContent = { Text(dependencies.jetpack) },
                )
                val builds = dependencies.build
                for(index in builds.indices) {
                    ListItem(
                        headlineContent = { Text(builds[index]) },
                        overlineContent = { Text("构建 打包") }
                    )
                }
            }

            DividerTextExpandedWith("开源") {
                for(index in openSourceProjects.indices) {
                    ListItem(
                        headlineContent = { Text(openSourceProjects[index].first) },
                        supportingContent = { Text(openSourceProjects[index].second) }
                    )
                }
            }
        }
    }
}