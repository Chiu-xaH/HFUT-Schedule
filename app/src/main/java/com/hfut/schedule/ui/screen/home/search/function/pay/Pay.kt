package com.hfut.schedule.ui.screen.home.search.function.pay

import android.icu.text.CaseMap.Title
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.logic.model.PayData
import com.hfut.schedule.logic.model.PayResponse
import com.hfut.schedule.logic.util.sys.ClipBoard
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.ActiveTopBar
import com.hfut.schedule.ui.component.appHorizontalDp
import com.hfut.schedule.ui.component.BottomSheetTopBar
import com.hfut.schedule.ui.style.CardForListColor
import com.hfut.schedule.ui.component.DividerText
import com.hfut.schedule.ui.component.DividerTextExpandedWith
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.LoadingLargeCard
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.style.bottomSheetRound
import com.hfut.schedule.ui.component.ScrollText
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.style.HazeBottomSheet
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Pay(ifSaved : Boolean, vm : NetWorkViewModel, hazeState: HazeState) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
 //   var showDialog by remember { mutableStateOf(false) }

    val url = "http://pay.hfut.edu.cn/payment/mobileOnlinePay"
    TransplantListItem(
        headlineContent = { Text(text = "学费") },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.paid), contentDescription = "") },
        modifier = Modifier.clickable {
            showBottomSheet = true
        }
    )
    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet,
//            sheetState = sheetState,
//            shape = bottomSheetRound(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
//                    TopAppBar(
//                        colors = TopAppBarDefaults.mediumTopAppBarColors(
//                            containerColor = Color.Transparent,
//                            titleContentColor = MaterialTheme.colorScheme.primary,
//                        ),
//                        title = { Text("学费") },
//                        actions = {
//                            FilledTonalButton(
//                                onClick = { Starter.startWebUrl(url) },
//                                modifier = Modifier.padding(horizontal = AppHorizontalDp())
//                            ) {
//                                Text(text = "缴费")
//                            }
//                        }
//                    )
                    HazeBottomSheetTopBar("学费") {
                        FilledTonalButton(
                                onClick = { Starter.startWebUrl(url) },
                            ) {
                                Text(text = "缴费")
                            }
                    }
//                    Text("学费", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(AppHorizontalDp()), fontSize = 22.sp)
//                    TopAppBar(
//                        title = { ScrollText(text = "学费") },
//                        colors = TopAppBarDefaults.mediumTopAppBarColors(
//                            containerColor = MaterialTheme.colorScheme.primaryContainer,
//                            titleContentColor = MaterialTheme.colorScheme.primary),
//                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ) {
                    PayUI(url,vm)
                }
            }
        }
    }
}

@Composable
fun PayUI(url : String,vm: NetWorkViewModel) {
    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }

    if(refresh) {
        loading = true
        CoroutineScope(Job()).launch{
            async{ vm.getPay() }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.PayData.observeForever { result ->
                        if (result != null) {
                            if(result.contains("{")) {
                                loading = false
                                refresh = false
                            }
                        }
                    }
                }
            }
        }
    }
    val scale = animateFloatAsState(
        targetValue = if (loading) 0.9f else 1f, // 按下时为0.9，松开时为1
        //animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        animationSpec = tween(MyApplication.ANIMATION_SPEED / 2, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )
    val scale2 = animateFloatAsState(
        targetValue = if (loading) 0.97f else 1f, // 按下时为0.9，松开时为1
        //animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        animationSpec = tween(MyApplication.ANIMATION_SPEED / 2, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )

    val blurSize by animateDpAsState(
        targetValue = if (loading) 10.dp else 0.dp, label = ""
        ,animationSpec = tween(MyApplication.ANIMATION_SPEED / 2, easing = LinearOutSlowInEasing),
    )


    val data = getPay(vm)
    DividerTextExpandedWith(text = "欠缴费用",false) {
        LoadingLargeCard(
            title = "￥${if(!loading) data.total else "0.0"}",
            loading = loading
        ) {
            Row {
                TransplantListItem(
                    headlineContent = { ScrollText(text = "学费 ￥${if(!loading)data.xf else "0.0"}") },
                    modifier = Modifier.weight(.5f)
                )
                TransplantListItem(
                    headlineContent = { ScrollText(text = "体检费 ￥${if(!loading) data.dstjf else "0.0"}") },
                    modifier = Modifier.weight(.5f)
                )

            }
            Row {
                TransplantListItem(
                    headlineContent = { ScrollText(text = "住宿费 ￥${if(!loading) data.zsf else "0.0"}") },
                    modifier = Modifier.weight(.5f)
                )
                TransplantListItem(
                    headlineContent = { ScrollText(text = "军训费 ￥${if(!loading) data.dsjxf else "0.0"}") },
                    modifier = Modifier.weight(.5f)
                )
            }
        }
    }

    DividerTextExpandedWith(text = "缴费方式") {

        TransplantListItem(
            headlineContent = { Text(text = "提前在中国农业银行卡预存费用,学校到期自动扣取") },
            leadingContent = { Icon(
                painter = painterResource(id = R.drawable.credit_card),
                contentDescription = ""
            )})
        TransplantListItem(
            headlineContent = { Text(text = "点击右上角打开链接即可调用电子支付(Apple Pay通道)") },
            leadingContent = {Icon(
                painter = painterResource(id = R.drawable.net),
                contentDescription = ""
            ) },
            modifier = Modifier.clickable { Starter.startWebUrl(url) }
        )
        TransplantListItem(
            headlineContent = { Text(text = "点击此处复制链接到剪切板，在微信/支付宝等中打开链接即可走对应的软件支付") },
            leadingContent = { Icon(
                painter = painterResource(id = R.drawable.barcode),
                contentDescription = ""
            )},
            modifier = Modifier.clickable {
                ClipBoard.copy(url)
            }
        )
    }

  //  }
    DividerTextExpandedWith(text = "防骗警告") {
        TransplantListItem(
            headlineContent = { Text(text = "电子支付只能通过学校缴费平台官方链接(右上角按钮提供)发起,其余线上途径均需谨慎甄别!") },
            leadingContent = {Icon(
                painter = painterResource(id = R.drawable.error),
                contentDescription = ""
            )}
        )
    }


}


fun getPay(vm: NetWorkViewModel) : PayData {
    return try {
        val json = vm.PayData.value
        val data = Gson().fromJson(json,PayResponse::class.java).data
        data
    } catch (e : Exception) {
        PayData("0.0","0.0","0.0","0.0","0.0")
    }
}


