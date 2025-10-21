package com.hfut.schedule.ui.screen.card

//import com.hfut.schedule.ui.activity.card.function.main.turnToBottomBar
//import com.hfut.schedule.ui.utils.NavigateAndAnimationManager.turnTo

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.CardBarItems
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.logic.model.NavigationBarItemData
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.button.BUTTON_PADDING
import com.hfut.schedule.ui.component.button.HazeBottomBar
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.screen.pager.CustomTabRow
import com.hfut.schedule.ui.screen.card.bill.main.BillScreen
import com.hfut.schedule.ui.screen.card.count.BillAnalysisScreen
import com.hfut.schedule.ui.screen.card.function.SearchBillsUI
import com.hfut.schedule.ui.screen.card.function.main.CardHomeScreen
import com.hfut.schedule.ui.screen.home.focus.funiction.initCardNetwork
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.ui.util.navigation.AppAnimationManager.currentPage
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.transition.util.currentRouteWithoutArgs
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.color.topBarTransplantColor
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

private val items = listOf(
    NavigationBarItemData(
        CardBarItems.HOME.name,"卡包", R.drawable.credit_card,  R.drawable.credit_card_filled
    ),
    NavigationBarItemData(
        CardBarItems.BILLS.name,"账单", R.drawable.receipt_long, R.drawable.receipt_long_filled
    ),
    NavigationBarItemData(
        CardBarItems.COUNT.name,"统计",R.drawable.leaderboard,R.drawable.leaderboard_filled
    )
)
//private fun BillItem(vm : NetWorkViewModel) :List<records> {
//    val billjson = vm.BillsData.value
//    try {
//        if(billjson?.contains("操作成功") == true){
//            val bill = Gson().fromJson(billjson, BillResponse::class.java)
//            val data = bill.data.records
//            val msg = bill.data.msg
//            val totalpage = bill.data.pages
//            SharedPrefs.saveString("totalpage",totalpage.toString())
//            if (msg != null) {
//                if (msg.contains("成功")) {
//                    val cardAccount = bill.data.records[0].fromAccount
//                    SharedPrefs.saveString("cardAccount", cardAccount)
//                } else { Toast.makeText(MyApplication.context,msg, Toast.LENGTH_SHORT).show() }
//            }
//            return data
//        } else {
//            return emptyList()
//        }
//    } catch (e:Exception) {
//        return emptyList()
//    }
//}

@SuppressLint("SuspiciousIndentation", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardUI(vm : NetWorkViewModel, vmUI : UIViewModel) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val navController = rememberNavController()
    val bottomBarItems = when(navController.currentRouteWithoutArgs()) {
        CardBarItems.HOME.name ->CardBarItems.HOME
        CardBarItems.COUNT.name->CardBarItems.COUNT
        CardBarItems.BILLS.name->CardBarItems.BILLS
        else ->CardBarItems.HOME
    }
    val titles = remember { listOf("概况", "日", "月","年") }

    val pagerState = rememberPagerState(pageCount = { titles.size })

    var refresh by remember { mutableStateOf(false) }

    LaunchedEffect(refresh) {
        if(!refresh) {
            async { initCardNetwork(vm,vmUI) }.await()
            launch { refresh = true }
        }
    }

    var showBottomSheet_Search by remember { mutableStateOf(false) }
    if (showBottomSheet_Search) {
        HazeBottomSheet (
            onDismissRequest = {
                showBottomSheet_Search = false
            },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_Search
        ) { SearchBillsUI(vm,hazeState) }
    }

    val currentAnimationIndex by DataStoreManager.animationType.collectAsState(initial = 0)
// 保存上一页页码 用于决定左右动画
    if(currentAnimationIndex == 2) {
        LaunchedEffect(bottomBarItems) {
            currentPage = bottomBarItems.page
        }
    }
    var sorted by remember { mutableStateOf(true) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val backdrop = rememberLayerBackdrop()
    val context = LocalActivity.current
    Scaffold(
//        modifier = Modifier.fillMaxSize(),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column(modifier = Modifier.topBarBlur(hazeState)) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text("一卡通") },
                    navigationIcon = {
                        IconButton(onClick = {
                            context?.finish()
                        }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                        }
                    },
                    actions = {
                        if(bottomBarItems == CardBarItems.BILLS) {
                            Row (
                                modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)
                            ){
                                LiquidButton(
                                    onClick = {
                                        showBottomSheet_Search = true
                                    },
                                    backdrop = backdrop,
                                    isCircle = true,
                                ) {
                                    Icon(painterResource(R.drawable.search),null)
                                }
                                Spacer(Modifier.width(BUTTON_PADDING))
                                LiquidButton(
                                    onClick = {
                                        sorted = !sorted
                                    },
                                    backdrop = backdrop,
                                ) {
                                    Text(if(sorted)"按交易时间" else "按入账时间")
                                }
                            }
                        }
                    }
                )
                if(bottomBarItems == CardBarItems.COUNT) {
                    CustomTabRow(pagerState, titles)
                }
            }
        },
        bottomBar = {
            HazeBottomBar(hazeState,items,navController)
        }
    ) {innerPadding ->
        val animation = AppAnimationManager.getAnimationType(currentAnimationIndex,bottomBarItems.page)

        NavHost(navController = navController,
            startDestination = CardBarItems.HOME.name,
            enterTransition = { animation.enter },
            exitTransition = { animation.exit },
            modifier = Modifier
                .backDropSource(backdrop)
                .hazeSource(
                    state = hazeState
                )
        ) {
            composable(CardBarItems.HOME.name) {
                Scaffold(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    CardHomeScreen(innerPadding,vm,navController,vmUI,hazeState)
                }
            }
            composable(CardBarItems.BILLS.name) {
                Scaffold {
                    BillScreen(vm,innerPadding,vmUI, hazeState,sorted)
                }
            }
            composable(CardBarItems.COUNT.name) {
                Scaffold {
                    BillAnalysisScreen(innerPadding,vm,pagerState,hazeState)
                }
            }
        }
    }
}

