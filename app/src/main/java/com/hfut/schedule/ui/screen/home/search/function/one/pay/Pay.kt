package com.hfut.schedule.ui.screen.home.search.function.one.pay

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.PayData
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.container.LoadingLargeCard
import com.hfut.schedule.ui.component.text.ScrollText
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Pay(vm : NetWorkViewModel, hazeState: HazeState) {
    var showBottomSheet by remember { mutableStateOf(false) }

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
//                                modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)
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
//                    Text("学费", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(APP_HORIZONTAL_DP), fontSize = 22.sp)
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
    val uiState by vm.payFeeResponse.state.collectAsState()
    var loading = uiState !is UiState.Success

    var data by remember { mutableStateOf(PayData("0.00","0.00","0.00","0.00","0.00")) }
    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            val response = (uiState as UiState.Success).data
            data = response
        }
    }
    val refreshNetwork : suspend () -> Unit = {
        vm.payFeeResponse.clear()
        vm.getPay()
    }
    LaunchedEffect(Unit) {
        refreshNetwork()
    }
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
                ClipBoardUtils.copy(url)
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

//
//fun getPay(vm: NetWorkViewModel) : PayData {
//    return try {
//        val json = vm.payFeeResponse.value
//        val data = Gson().fromJson(json,PayResponse::class.java).data
//        data
//    } catch (e : Exception) {
//        PayData("0.0","0.0","0.0","0.0","0.0")
//    }
//}


