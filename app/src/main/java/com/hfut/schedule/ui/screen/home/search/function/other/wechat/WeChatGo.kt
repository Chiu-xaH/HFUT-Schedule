package com.hfut.schedule.ui.screen.home.search.function.other.wechat

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.text.BottomTip
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.container.StyleCardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
 
import com.hfut.schedule.ui.screen.fix.about.createQRCodeBitmap
import com.hfut.schedule.ui.style.HazeBottomSheet
import dev.chrisbanes.haze.HazeState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WeChatGo(hazeState : HazeState) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var showBottomSheetQRCode by remember { mutableStateOf(false) }

    TransplantListItem(
        headlineContent = { Text(text = "微信专区") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.wechat),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
            showBottomSheet = true
        }
    )
    var showedUrl by remember { mutableStateOf("") }

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

    val list1 = listOf(
        ItemBean("校务行","亮点功能: 官方成绩单",R.drawable.article),
        ItemBean("第二课堂",null,R.drawable.school),
        ItemBean("合工大智慧学生社区线上平台","亮点功能: 今日校园(学工平台)",R.drawable.handshake),
        ItemBean("合工大校友服务平台","亮点功能: 校友卡回校",R.drawable.local_library),
        ItemBean("呱呱物联","洗浴用，大多数接口已经被聚在工大集成",R.drawable.bathtub),
        ItemBean("海乐生活","洗衣机用，部分接口已经被聚在工大集成，支付相关功能仍需使用微信",R.drawable.laundry),
        ItemBean("合工大信息查询","大多数接口已经被聚在工大集成",R.drawable.search)
    )
    val list2 = listOf(
        ItemBean2("合工大教务","亮点功能: 教室课表、同班同学",R.drawable.search,"https://jwglapp.hfut.edu.cn/uniapp/"),
        ItemBean2("(多多买菜)宣城校区快递","亮点功能: 取件码汇总",R.drawable.package_2,"https://mdkd.pinduoduo.com/weixin/package"),
    )

    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState,
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("微信专区") {
                        FilledTonalButton(
                            onClick = {
                                Starter.startLaunchAPK("com.tencent.mm","微信")
                            }
                        ) {
                            Text("打开微信")
                        }
                    }
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ) {
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
                }
            }
        }
    }
}

private data class ItemBean2(val name : String,val text : String?,val icon : Int,val url : String)

private data class ItemBean(val name : String,val text : String?,val icon : Int)