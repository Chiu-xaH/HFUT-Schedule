package com.hfut.schedule.ui.screen.home.search.function.other.wechat

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.button.StartAppIcon
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.StyleCardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.text.BottomTip
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.fix.about.createQRCodeBitmap
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.hfut.schedule.ui.style.topBarBlur
import com.hfut.schedule.ui.style.topBarTransplantColor
import com.xah.transition.component.TopBarNavigateIcon
import com.xah.transition.component.TransitionScaffold
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

private data class ItemBean2(val name : String,val text : String?,val icon : Int,val url : String)

private data class ItemBean(val name : String,val text : String?,val icon : Int)


private val list1 = listOf(
    ItemBean("校务行","亮点功能: 官方成绩单",R.drawable.article),
    ItemBean("第二课堂",null,R.drawable.school),
    ItemBean("合工大智慧学生社区线上平台","亮点功能: 今日校园(学工平台)",R.drawable.handshake),
    ItemBean("合工大校友服务平台","亮点功能: 校友卡回校",R.drawable.local_library),
    ItemBean("呱呱物联","洗浴用，大多数接口已经被聚在工大集成",R.drawable.bathtub),
    ItemBean("海乐生活","洗衣机用，部分接口已经被聚在工大集成，支付相关功能仍需使用微信",R.drawable.laundry),
    ItemBean("合工大信息查询","大多数接口已经被聚在工大集成",R.drawable.search)
)
private val list2 = listOf(
    ItemBean2("合工大教务","亮点功能: 教室课表、同班同学",R.drawable.search,"https://jwglapp.hfut.edu.cn/uniapp/"),
    ItemBean2("(多多买菜)宣城校区快递","亮点功能: 取件码汇总",R.drawable.package_2,"https://mdkd.pinduoduo.com/weixin/package"),
)

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WeChatScreen(
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    var showedUrl by remember { mutableStateOf("") }
    var showBottomSheetQRCode by remember { mutableStateOf(false) }

    if (showBottomSheetQRCode) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheetQRCode = false },
            showBottomSheet = showBottomSheetQRCode,
            hazeState = hazeState,
            autoShape = false
        ) {
            Column {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = APP_HORIZONTAL_DP, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {
                    val qrPainter = createQRCodeBitmap(showedUrl,1000,1000)
                    qrPainter?.let { Image(it.asImageBitmap(), contentDescription = "") }
                }
                Spacer(modifier = Modifier.height(APP_HORIZONTAL_DP))
            }
        }
    }
    val route = remember { AppNavRoute.Wechat.route }
    with(sharedTransitionScope) {
        CustomTransitionScaffold (
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            topBar = {
                TopAppBar(
                    modifier = Modifier.topBarBlur(hazeState,useTry = true),
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.Wechat.title) },
                    navigationIcon = {
                        TopBarNavigateIcon(navController,animatedContentScope,route, AppNavRoute.Wechat.icon)
                    },
                    actions = {
                        StartAppIcon(
                            Starter.AppPackages.WECHAT,R.drawable.wechat_icon,
                            modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)
                        )
                    }
                )
            },
            ) { innerPadding ->
            Column(
                modifier = Modifier.hazeSource(hazeState)
//                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                InnerPaddingHeight(innerPadding,true)
                TransplantListItem(
                    headlineContent = { Text("由于微信的垄断，以及一些功能只设计了微信登陆方式，无法接入，只能由用户操作")},
                    leadingContent = { Icon(painterResource(R.drawable.info),null)}
                )
                DividerTextExpandedWith("网页") {
                    TransplantListItem(
                        headlineContent = { Text("点击展示二维码，长按复制链接，到微信打开")},
                        leadingContent = { Icon(painterResource(R.drawable.info),null)}
                    )
                    for(i in list2) {
                        with(i) {
                            StyleCardListItem(
                                headlineContent = { Text(name)} ,
                                supportingContent = text?.let { { Text(it) } },
                                overlineContent = { Text(url) },
                                leadingContent = { Icon(painterResource(icon),null)},
                                modifier = Modifier.combinedClickable(
                                    onClick = {
                                        showedUrl = url
                                        showBottomSheetQRCode = true
                                    },
                                    onLongClick = { ClipBoardUtils.copy(url) },
                                )
                            )
                        }
                    }
                }
                DividerTextExpandedWith("小程序") {
                    TransplantListItem(
                        headlineContent = { Text("点击复制名称，自行搜索使用")},
                        leadingContent = { Icon(painterResource(R.drawable.info),null)},
                    )
                    for(i in list1) {
                        with(i) {
                            StyleCardListItem(
                                headlineContent = { Text(name)},
                                supportingContent = text?.let { { Text(it) } },
                                leadingContent = { Icon(painterResource(icon),null)},
                                modifier = Modifier.clickable { ClipBoardUtils.copy(name) }
                            )
                        }
                    }
                }
                BottomTip("若有更多 欢迎来信贡献")
                InnerPaddingHeight(innerPadding,false)
            }
        }
    }
}