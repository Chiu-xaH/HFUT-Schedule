package com.hfut.schedule.ui.screen.fix.about

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.network.util.Constant
import com.hfut.schedule.ui.component.button.LargeButton
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.network.UrlImage
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.screen.welcome.arguments
import com.hfut.schedule.ui.style.special.bottomBarBlur
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.align.RowHorizontal
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.style.padding.InnerPaddingHeight
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


private val openSourceProjects = listOf(
    OpenSource("Okhttp","网络请求","${Constant.GITHUB_URL}square/okhttp"),
    OpenSource("Retrofit","网络请求","${Constant.GITHUB_URL}square/retrofit"),
    OpenSource("Gson", "JSON解析","${Constant.GITHUB_URL}google/gson"),
    OpenSource("Jsoup", "XML/HTML解析","${Constant.GITHUB_URL}jhy/jsoup"),
    OpenSource("Zxing", "二维码","${Constant.GITHUB_URL}zxing/zxing"),
    OpenSource("Haze" ,"渐进式模糊","${Constant.GITHUB_URL}chrisbanes/haze"),
    OpenSource("Accompanist" ,"扩展工具包","${Constant.GITHUB_URL}google/accompanist"),
    OpenSource("Glide", "图片","${Constant.GITHUB_URL}bumptech/glide"),
    OpenSource("EdDSA Java" ,"供和风天气Api加密","${Constant.GITHUB_URL}str4d/ed25519-java"),
    OpenSource("Konfetti" ,"礼花特效","${Constant.GITHUB_URL}DanielMartinus/Konfetti"),
    OpenSource("Tesseract4Android" ,"Tesseract4(供图片验证码识别)","${Constant.GITHUB_URL}adaptech-cz/Tesseract4Android"),
    OpenSource("DiffUpdater" , "增量更新","${Constant.GITHUB_URL}Chiu-xaH/DiffUpdater"),
    OpenSource("MaterialKolor" , "自定义取色","${Constant.GITHUB_URL}jordond/MaterialKolor"),
    OpenSource("LeakCanary" , "内存泄漏检查","${Constant.GITHUB_URL}square/leakcanary"),
    OpenSource("Reorderable" , "列表拖拽","${Constant.GITHUB_URL}Calvin-LL/Reorderable"),
    OpenSource("SharedNav" , "页面管理&容器共享","${Constant.GITHUB_URL}Chiu-xaH/SharedNav"),
    OpenSource("AndroidLiquidGlass" , "液态玻璃","${Constant.GITHUB_URL}Kyant0/AndroidLiquidGlass"),
    OpenSource("Mirror-Android" , "镜面效果","${Constant.GITHUB_URL}Chiu-xaH/Mirror-Android"),
)

private data class OpenSource(val name : String,val description: String,val url : String?)

// TODO 重新设计
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
    var starsNum by remember { mutableStateOf<String?>(null) }
    val uiState by vm.githubStarsData.state.collectAsState()
    LaunchedEffect(uiState) {
        if(uiState is UiState.Success) {
            starsNum = (uiState as UiState.Success).data.toString()
        }
    }
    val scope = rememberCoroutineScope()
    val userCount by vm.supabaseUserCountResp.state.collectAsState()
    val todayVisitCount by vm.supabaseTodayVisitResp.state.collectAsState()
    val scrollState = rememberScrollState()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                modifier = Modifier.topBarBlur(hazeState),
                scrollBehavior = scrollBehavior,
                title = { Text("关于") },
                colors = topBarTransplantColor(),
                navigationIcon = {
                    TopBarNavigationIcon()
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .hazeSource(hazeState)
        ) {
            InnerPaddingHeight(innerPadding,true)
            DividerTextExpandedWith("开发人员") {
                CustomCard(
                    color = cardNormalColor(),
                ) {
                    TransplantListItem(
                        modifier = Modifier.clickable {
                            scope.launch {
                                Starter.startWebView(context,"${Constant.GITHUB_URL}${Constant.GITHUB_DEVELOPER_NAME}")
                            }
                        },
                        headlineContent = {
                            //一名热爱Android的开发者,宣城校区23级本科生
                            Text(Constant.GITHUB_DEVELOPER_NAME)
                        },
                        overlineContent = { Text("开发者") },
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
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        modifier = Modifier.clickable {
                        },
                        overlineContent = { Text("贡献者(按时间顺序)") },
                        headlineContent = {
                            Text(MyApplication.contributors.map { it.key }.drop(1).joinToString(", "))
                        },
                    )
                    PaddingHorizontalDivider()
                    RowHorizontal  (
                        modifier = Modifier.padding(APP_HORIZONTAL_DP)
                    ) {
                        OverlappingAvatars(
                            MyApplication.contributors.map {
                                Constant.GITHUB_USER_IMAGE_URL + it.value
                            },
                        )
                    }
                }
            }

            DividerTextExpandedWith("关于本应用") {
                CustomCard(
                    color = cardNormalColor(),
                ) {
                    Row(
                        modifier = Modifier.clickable {
                            scope.launch {
                                Starter.startWebView(context,"${Constant.GITHUB_REPO_URL}/blob/main/docs/CHART.md","统计报表",null,R.drawable.github)
                            }
                        }
                    ) {
                        TransplantListItem(
                            modifier = Modifier.weight(.5f),
                            headlineContent = {
                                Text("${(todayVisitCount as? UiState.Success)?.data ?: "--"}")
                            },
                            leadingContent = {
                                Icon(painterResource(R.drawable.leaderboard),null)
                            },
                            overlineContent = {
                                Text("日流量")
                            }
                        )
                        TransplantListItem(
                            modifier = Modifier.weight(.5f),
                            headlineContent = {
                                Text("${(userCount as? UiState.Success)?.data ?: "--"}")
                            },
                            leadingContent = {
                                Icon(painterResource(R.drawable.group),null)
                            },
                            overlineContent = {
                                Text("用户量")
                            }
                        )
                    }
                    Row {
                        TransplantListItem(
                            modifier = Modifier.weight(.5f).clickable {
                                scope.launch {
                                    Starter.startWebView(context,"${Constant.GITHUB_URL}${Constant.GITHUB_DEVELOPER_NAME}/${Constant.GITHUB_REPO_NAME}")
                                }
                            },
                            headlineContent = {
                                Text(starsNum ?: "--")
                            },
                            leadingContent = {
                                Icon(painterResource(R.drawable.kid_star),null)
                            },
                            overlineContent = {
                                Text("Star")
                            },
                        )
                        TransplantListItem(
                            modifier = Modifier.weight(.5f).clickable {

                            },
                            headlineContent = {
                                Text("--")
                            },
                            leadingContent = {
                                Icon(painterResource(R.drawable.target),null)
                            },
                            overlineContent = {
                                Text("Issue")
                            }
                        )
                    }
                    Row {
                        TransplantListItem(
                            modifier = Modifier.weight(.5f).clickable {
                                scope.launch {
                                    Starter.startWebView(context,"${Constant.GITHUB_URL}${Constant.GITHUB_DEVELOPER_NAME}/${Constant.GITHUB_REPO_NAME}")
                                }
                            },
                            headlineContent = {
                                Text("--")
                            },
                            leadingContent = {
                                Icon(painterResource(R.drawable.commit),null)
                            },
                            overlineContent = {
                                Text("Commit")
                            }
                        )
                        TransplantListItem(
                            modifier = Modifier.weight(.5f).clickable {

                            },
                            headlineContent = {
                                Text("--")
                            },
                            leadingContent = {
                                Icon(painterResource(R.drawable.sell),null)
                            },
                            overlineContent = {
                                Text("发行版")
                            }
                        )
                    }
                }
                // star数量 issue数量 最新release 本版本新特性 历史更新日志 commit次数
                LargeButton(
                    onClick = {
                        scope.launch {
                            Starter.startWebView(context,"${Constant.GITHUB_URL}${Constant.GITHUB_DEVELOPER_NAME}/${Constant.GITHUB_REPO_NAME}")
                        }
                    },
                    text = "Github",
                    icon = R.drawable.github,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP, vertical = CARD_NORMAL_DP*2)
                )
            }

            DividerTextExpandedWith("开源引用") {
                CustomCard (color = cardNormalColor()){
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
                    }
                }
            }

            DividerTextExpandedWith("用户协议") {
                CustomCard (color = cardNormalColor()) {
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
                        scope.launch {
                            async { SharedPrefs.saveBoolean("canUse", default = false, save = false) }.await()
                            launch {
                                showToast("已退出APP")
                                MyApplication.exitAppSafely()
//                                    activity?.finish()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP, vertical = CARD_NORMAL_DP*2),
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.error,
                    text = "撤销同意",
                    icon = R.drawable.undo
                )
            }

            InnerPaddingHeight(innerPadding,false)
        }
    }
}

@Composable
private fun OverlappingAvatars(
    imageUrls: List<String>,
    modifier: Modifier = Modifier,
    avatarSize: Dp = 45.dp,
    overlapFraction: Float = 0.35f,
) {
    if (imageUrls.isEmpty()) return

    BoxWithConstraints(modifier = modifier) {
        val maxWidth = constraints.maxWidth.toFloat().dp
        val totalAvatars = imageUrls.size
        val defaultSpacing = avatarSize * (1f - overlapFraction)

        // 动态计算每个头像间距，保证全部头像在屏幕内显示
        val spacing = if (totalAvatars > 1) {
            val neededWidth = defaultSpacing * (totalAvatars - 1) + avatarSize
            if (neededWidth > maxWidth) {
                ((maxWidth - avatarSize) / (totalAvatars - 1)).coerceAtLeast(0.dp)
            } else defaultSpacing
        } else 0.dp

        imageUrls.forEachIndexed { index, url ->
            Box(
                modifier = Modifier
                    .offset(x = spacing * index)
                    .size(avatarSize)
                    .shadow(elevation = APP_HORIZONTAL_DP, shape = CircleShape, clip = false)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                UrlImage(
                    url = url,
                    width = avatarSize,
                    height = avatarSize,
                )
            }
        }
    }
}
