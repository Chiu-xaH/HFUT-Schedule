package com.hfut.schedule.ui.screen.home.search.function.huiXin.washing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.hfut.schedule.logic.model.zjgd.FeeType
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.screen.CustomTabRow
import com.hfut.schedule.ui.component.status.EmptyUI
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.container.StyleCardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.webview.WebDialog

import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.getCampus
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Washing(vm: NetWorkViewModel,hazeState: HazeState) {
    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            autoShape = false,
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ) {
            WashingUI(vm,hazeState)
        }
    }

    var showBottomSheetHaiLe by remember { mutableStateOf(false) }

    if (showBottomSheetHaiLe) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheetHaiLe = false },
            showBottomSheet = showBottomSheetHaiLe,
            hazeState = hazeState
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("海乐生活")
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    HaiLeScreen(vm,hazeState)
                }
            }
        }
    }

    TransplantListItem(
        headlineContent = { Text(text = "洗衣机") },
        leadingContent = {
            Icon(painterResource(id = R.drawable.local_laundry_service), contentDescription = "")
        },
        trailingContent = {
            FilledTonalIconButton(
                modifier = Modifier.size(30.dp),
                onClick = {
                    showBottomSheetHaiLe = true
                },
            ) { Icon( painterResource(R.drawable.search), contentDescription = null, modifier = Modifier.size(20.dp)) }
        },
        modifier = Modifier.clickable {
            showBottomSheet = true
        }
    )
}

private const val HEFEI_TAB = 0
private const val XUANCHENG_TAB = 1
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WashingUI(vm : NetWorkViewModel,hazeState : HazeState) {
    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("海乐生活")
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    HaiLeScreen(vm,hazeState)
                }
            }
        }
    }
    val titles = remember { listOf("合肥","宣城") }

    val pagerState = rememberPagerState(pageCount = { titles.size }, initialPage =
        when(getCampus()) {
            Campus.XUANCHENG -> XUANCHENG_TAB
            Campus.HEFEI -> HEFEI_TAB
        }
    )
    val auth = prefs.getString("auth","")
    var showDialogWeb by remember { mutableStateOf(false) }
    WebDialog(showDialogWeb, url = MyApplication.HUIXIN_URL + "charge-app/?name=pays&appsourse=ydfwpt&id=${FeeType.WASHING_HEFEI.code}&name=pays&paymentUrl=${MyApplication.HUIXIN_URL}plat&token=" + auth, title = "慧新易校",showChanged = { showDialogWeb = false }, showTop = false)


    //布局///////////////////////////////////////////////////////////////////////////
    Column() {
        HazeBottomSheetTopBar("洗衣机", isPaddingStatusBar = false) {
            FilledTonalButton(onClick = {
                showBottomSheet = true
            }) {
                Text("海乐生活")
            }
        }
        CustomTabRow(pagerState,titles)
        HorizontalPager(state = pagerState) { page ->
            Column() {
                when(page) {
                    HEFEI_TAB -> {
                        StyleCardListItem(
                            headlineContent = { Text("官方充值查询入口") },
                            modifier = Modifier.clickable {
                                showDialogWeb = true
                            },
                            trailingContent = {
                                Icon(Icons.Default.ArrowForward,null)
                            },
                            leadingContent = {
                                Icon(painterResource(R.drawable.search),null)
                            },
                            color = MaterialTheme.colorScheme.secondaryContainer
                        )
                    }
                    XUANCHENG_TAB -> {
                        EmptyUI("请使用海乐生活")
                    }
                }
                Spacer(Modifier.height(APP_HORIZONTAL_DP*3))
            }
        }
    }
}



