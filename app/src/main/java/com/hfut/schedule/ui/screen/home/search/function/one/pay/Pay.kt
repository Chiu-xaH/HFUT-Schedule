package com.hfut.schedule.ui.screen.home.search.function.one.pay

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
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.PayData
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.logic.util.sys.Starter
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.LoadingLargeCard
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.xah.uicommon.component.text.ScrollText
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.fix.about.createQRCodeBitmap
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.uicommon.style.color.topBarTransplantColor
import com.hfut.schedule.ui.util.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.xah.transition.component.iconElementShare
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun Pay(
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val route = remember { AppNavRoute.Fee.route }

    TransplantListItem(
        headlineContent = { ScrollText(text = AppNavRoute.Fee.label) },
        leadingContent = {
            Icon(painterResource(AppNavRoute.Fee.icon), contentDescription = null,modifier = Modifier.iconElementShare(sharedTransitionScope,animatedContentScope = animatedContentScope, route = route))
        },
        modifier = Modifier.clickable {
            navController.navigateForTransition(AppNavRoute.Fee,route)
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FeeScreen(
    vm : NetWorkViewModel,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val route = remember { AppNavRoute.Fee.route }
    with(sharedTransitionScope) {
        CustomTransitionScaffold (
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            topBar = {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.topBarBlur(hazeState, ),
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.Fee.label) },
                    navigationIcon = {
                        TopBarNavigationIcon(navController,animatedContentScope,route,AppNavRoute.Fee.icon)
                    },
                    actions = {
                        FilledTonalButton(
                            onClick = { Starter.startWebUrl(MyApplication.PAY_FEE_URL) },
                            modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)
                            ) { Text(text = "缴费") }
                    }
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.hazeSource(hazeState)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                InnerPaddingHeight(innerPadding,true)
                PayUI(vm,hazeState)
                InnerPaddingHeight(innerPadding,false)
            }
        }
    }
}

@Composable
fun PayUI(vm: NetWorkViewModel,hazeState : HazeState) {
    val uiState by vm.payFeeResponse.state.collectAsState()
    var successLoad = uiState is UiState.Success
    var data by remember { mutableStateOf(PayData("0.00","0.00","0.00","0.00","0.00")) }
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
                    val qrPainter = createQRCodeBitmap(MyApplication.PAY_FEE_URL,1000,1000)
                    qrPainter?.let { Image(it.asImageBitmap(), contentDescription = "") }
                }
                Spacer(modifier = Modifier.height(APP_HORIZONTAL_DP))
            }
        }
    }
    val refreshNetwork : suspend () -> Unit = {
        vm.payFeeResponse.clear()
        vm.getPay()
    }
    LaunchedEffect(uiState) {
        if(successLoad) {
            data = (uiState as UiState.Success).data
        }
    }
    LaunchedEffect(Unit) {
        refreshNetwork()
    }
    DividerTextExpandedWith(text = "欠缴费用",false) {
        LoadingLargeCard(
            title = "￥${if(successLoad) data.total else "0.0"}",
            loading = !successLoad
        ) {
            Row {
                TransplantListItem(
                    headlineContent = { ScrollText(text = "￥${if(successLoad)data.xf else "0.0"}") },
                    overlineContent = { Text("学费") },
                    modifier = Modifier.weight(.5f)
                )
                TransplantListItem(
                    headlineContent = { ScrollText(text = "￥${if(successLoad) data.dstjf else "0.0"}") },
                    overlineContent = { Text("体检费") },
                    modifier = Modifier.weight(.5f)
                )

            }
            Row {
                TransplantListItem(
                    headlineContent = { ScrollText(text = "￥${if(successLoad) data.zsf else "0.0"}") },
                    overlineContent = { Text("住宿费") },
                    modifier = Modifier.weight(.5f)
                )
                TransplantListItem(
                    headlineContent = { ScrollText(text = "￥${if(successLoad) data.dsjxf else "0.0"}") },
                    overlineContent = { Text("军训费") },
                    modifier = Modifier.weight(.5f)
                )
            }
        }
    }

    DividerTextExpandedWith(text = "缴费方式") {
        CustomCard(color = cardNormalColor()) {
            TransplantListItem(
                headlineContent = { Text(text = "提前在中国农业银行卡预存费用,自动扣取") },
                leadingContent = { Icon(painter = painterResource(id = R.drawable.credit_card), contentDescription = "")},
                modifier = Modifier.clickable{}
            )
            PaddingHorizontalDivider()
            TransplantListItem(
                headlineContent = { Text(text = "点击右上角打开链接登录后可调用支付") },
                leadingContent = {Icon(
                    painter = painterResource(id = R.drawable.net),
                    contentDescription = ""
                ) },
                modifier = Modifier.clickable { Starter.startWebUrl(MyApplication.PAY_FEE_URL) }
            )
            PaddingHorizontalDivider()
            TransplantListItem(
                headlineContent = { Text(text = "点击展示二维码，长按复制链接，在微信/支付宝等扫码或打开链接后，即可支付") },
                leadingContent = { Icon(
                    painter = painterResource(id = R.drawable.barcode),
                    contentDescription = ""
                )},
                modifier= Modifier.combinedClickable(
                    onClick = {
                        showBottomSheetQRCode = true
                    },
                    onDoubleClick = {},
                    onLongClick = {
                        ClipBoardUtils.copy(MyApplication.PAY_FEE_URL)
                    }
                )
            )
        }

    }
    DividerTextExpandedWith(text = "防骗警告") {
        CardListItem(
            headlineContent = { Text(text = "电子支付只能通过学校缴费平台官方链接(右上角按钮提供)发起,其余线上途径均需谨慎甄别!") },
            leadingContent = {
                Icon(painter = painterResource(id = R.drawable.error), contentDescription = "")
            },
        )
    }
}

