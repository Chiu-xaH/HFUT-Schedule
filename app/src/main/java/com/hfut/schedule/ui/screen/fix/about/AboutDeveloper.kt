package com.hfut.schedule.ui.screen.fix.about

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.LargeButton
import com.hfut.schedule.ui.component.container.CardListItem
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.screen.Party
import com.xah.uicommon.component.text.ScrollText
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.divider.ScrollHorizontalBottomDivider
import com.hfut.schedule.ui.component.divider.ScrollHorizontalTopDivider
import com.hfut.schedule.ui.component.network.UrlImage
import com.hfut.schedule.ui.screen.welcome.arguments

import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import kotlinx.coroutines.launch

private data class Build(
    val languages : List<String>,
    val build : List<String>,
    val ui : String,
    val jetpack : String
)
private val backgroundArticle = listOf(
    "我自小被Android吸引，2023年高考后，怀揣着好奇，开启了我的学习路线",
    "简单学习C语言后，跟随《第一行代码》踏上了Android之路，最终于大一10月份完成一本书的学习，跃跃欲试",
    "根据平常的使用频率，决定做一个课表APP以练手，起名：肥工课程表，仓库建立与2023年10月16日",
    "作为我的第一个从0开始、无指导的项目，以及薄弱的知识储备，一些现在看起来很简单的问题还是攻克了好久",
    "在一节大学生心理健康课上成功显示课程表，此时是2023年11月2日",
    "频繁的闪退崩溃使我意识到能力仍很薄弱，这个项目不能就此终止，改名：肥工教务通",
    "在不断更新、迭代、重构的过程中，功能增多，稳定性提高",
    "大一下，更名为：聚在工大",
    "大二上，从地球信息科学与技术专业转入了计算机科学与技术专业",
    "感谢使用，故事仍在继续...",
    "开发者 Chiu-xaH"
)
private val openSourceProjects = listOf(
    OpenSource("Okhttp","网络请求","${MyApplication.GITHUB_URL}square/okhttp"),
    OpenSource("Retrofit","网络请求","${MyApplication.GITHUB_URL}square/retrofit"),
    OpenSource("Gson", "JSON解析","${MyApplication.GITHUB_URL}google/gson"),
    OpenSource("Jsoup", "XML/HTML解析","${MyApplication.GITHUB_URL}jhy/jsoup"),
    OpenSource("Zxing", "二维码","${MyApplication.GITHUB_URL}zxing/zxing"),
    OpenSource("Haze" ,"层级实时模糊","${MyApplication.GITHUB_URL}chrisbanes/haze"),
    OpenSource("Accompanist" ,"用做实现透明状态栏","${MyApplication.GITHUB_URL}google/accompanist"),
    OpenSource("Glide", "加载网络图片","${MyApplication.GITHUB_URL}bumptech/glide"),
    OpenSource("EdDSA Java" ,"加密(供和风天气API使用)","${MyApplication.GITHUB_URL}str4d/ed25519-java"),
    OpenSource("Konfetti" ,"礼花效果","${MyApplication.GITHUB_URL}DanielMartinus/Konfetti"),
    OpenSource("Tesseract4Android" ,"封装Tesseract4(供图片验证码OCR)","${MyApplication.GITHUB_URL}adaptech-cz/Tesseract4Android"),
    OpenSource("Bsdiff-Lib" , "增量更新","${MyApplication.GITHUB_URL}Chiu-xaH/Bsdiff-Lib"),
    OpenSource("MaterialKolor" , "自定义取色","${MyApplication.GITHUB_URL}jordond/MaterialKolor"),
    OpenSource("Reorderable" , "列表拖拽","${MyApplication.GITHUB_URL}Calvin-LL/Reorderable")
)
private val dependencies = Build(
    jetpack = "Jetpack Compose",
    ui = "Material Design 3 (Material You)",
    languages = listOf("Kotlin"),
    build = listOf( "Gradle","OpenJDK 17"),
)
private data class OpenSource(val name : String,val description: String,val url : String?)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun About(vm : NetWorkViewModel) {
    LaunchedEffect(Unit) {
        launch {
            vm.githubStarsData.clear()
            vm.getStarNum()
        }
        launch {
            vm.supabaseTodayVisitResp.clear()
            vm.getTodayVisit()
        }
        launch {
            vm.supabaseUserCountResp.clear()
            vm.getUserCount()
        }
    }
    val context = LocalContext.current
    var starsNum by remember { mutableStateOf("") }
    val uiState by vm.githubStarsData.state.collectAsState()
    var loading = uiState is UiState.Loading
    LaunchedEffect(uiState) {
        if(uiState is UiState.Success) {
            starsNum = (uiState as UiState.Success).data.toString()
        }
    }
    val userCount by vm.supabaseUserCountResp.state.collectAsState()
    val activity = LocalActivity.current
    val todayVisitCount by vm.supabaseTodayVisitResp.state.collectAsState()
    val scrollState = rememberScrollState()
    Box() {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Column {
                    HazeBottomSheetTopBar("关于") {
                        FilledTonalButton(
                            enabled = todayVisitCount is UiState.Success,
                            onClick = { Starter.startWebView(context,"${MyApplication.GITHUB_REPO_URL}/blob/main/docs/CHART.md","统计报表",null,R.drawable.github) }
                        ) {
                            Text("今日流量 ${(todayVisitCount as? UiState.Success)?.data ?: ""}")
                        }
                    }
                    ScrollHorizontalTopDivider(scrollState)
                }
            },
            bottomBar = {
                Column {
                    ScrollHorizontalBottomDivider(scrollState)
                    Row(modifier = Modifier.padding(APP_HORIZONTAL_DP),horizontalArrangement = Arrangement.Center) {
                        Button(
                            onClick = { Starter.startWebView(context,"${MyApplication.GITHUB_URL}${MyApplication.GITHUB_DEVELOPER_NAME}/${MyApplication.GITHUB_REPO_NAME}") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(.5f)
                        ) {
                            Text("Github ⭐ $starsNum")
                        }

                        Spacer(modifier = Modifier.width(APP_HORIZONTAL_DP/2))
                        FilledTonalButton(
                            onClick = { Starter.startWebView(context,"${MyApplication.GITHUB_REPO_URL}/blob/main/docs/CHART.md","统计报表",null,R.drawable.github) },
                            enabled = userCount is UiState.Success,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(.5f)
                        ) {
                            Text("总用户 ${(userCount as? UiState.Success)?.data ?: ""}")
                        }
                    }
                }
            },
        ) { innerPadding ->
            Column(modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(scrollState)) {
                DividerTextExpandedWith("开发者") {
                    CardListItem(
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.clickable {
                            Starter.startWebView(context,"${MyApplication.GITHUB_URL}${MyApplication.GITHUB_DEVELOPER_NAME}")
                        },
                        headlineContent = { ScrollText(MyApplication.GITHUB_DEVELOPER_NAME) },
                        leadingContent = {
                            UrlImage(url = MyApplication.GITHUB_USER_IMAGE_URL + MyApplication.GITHUB_USER_ID, width = 50.dp, height = 50.dp)
                        },
                        supportingContent = {
                            Text("一名热爱安卓的开发者,宣城校区23级本科生")
                        },
                        trailingContent = {
                            FilledTonalIconButton(
                                onClick = {
                                    Starter.emailMe(context)
                                }
                            ) {
                                Icon(painterResource(R.drawable.mail),null)
                            }
                        },
                    )
                }

                DividerTextExpandedWith("构建") {
                    CustomCard (color = MaterialTheme.colorScheme.surface){
                        TransplantListItem(
                            headlineContent = { Text("Kotlin/Java,C") },
                            overlineContent = { Text("语言") }
                        )
                        TransplantListItem(
                            headlineContent = { Text(dependencies.ui) },
                            overlineContent = { Text("UI") }
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
                }

                DividerTextExpandedWith("开源引用") {
                    CustomCard (color = MaterialTheme.colorScheme.surface){
                        for(index in openSourceProjects.indices step 2) {
                            Row {
                                TransplantListItem(
                                    headlineContent = { Text(openSourceProjects[index].name) },
                                    supportingContent = { Text(openSourceProjects[index].description) },
                                    modifier = Modifier.weight(.5f).clickable{
                                        openSourceProjects[index].url?.let { Starter.startWebUrl(context,it) }
                                    }
                                )
                                if(index+1 < openSourceProjects.size)
                                    TransplantListItem(
                                        headlineContent = { Text(openSourceProjects[index+1].name) },
                                        supportingContent = { Text(openSourceProjects[index+1].description) },
                                        modifier = Modifier.weight(.5f).clickable {
                                            openSourceProjects[index+1].url?.let { Starter.startWebUrl(context,it) }
                                        }
                                    )
                            }
                            if(index != openSourceProjects.size - 2)
                                PaddingHorizontalDivider()
                        }
                    }
                }

                DividerTextExpandedWith("用户协议") {
                    CustomCard (color = MaterialTheme.colorScheme.surface) {
                        for(index in arguments.indices) {
                            val item = arguments[index]
                            TransplantListItem(
                                headlineContent = { Text(item) },
                                leadingContent = { Text((index+1).toString()) }
                            )
                        }
                    }
                    LargeButton(
                        onClick = {
                            SharedPrefs.saveBoolean("canUse", default = false, save = false)
                            showToast("已退出APP")
                            activity?.finish()
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP).padding(top = 10.dp),
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error,
                        text = "撤销同意",
                        icon = R.drawable.undo
                    )
                }

                DividerTextExpandedWith("创作背景") {
                    CustomCard (color = MaterialTheme.colorScheme.surface) {
                        for(i in backgroundArticle.indices) {
                            TransplantListItem(
                                headlineContent = {
                                    Text(backgroundArticle[i] + ".")
                                }
                            )
                        }
                    }
                }
            }
        }
        Party(show = !loading)
    }
}