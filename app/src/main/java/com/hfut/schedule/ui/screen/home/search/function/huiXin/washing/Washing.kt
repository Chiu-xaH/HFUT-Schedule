package com.hfut.schedule.ui.screen.home.search.function.huiXin.washing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.zjgd.FeeType
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.component.CustomTabRow
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.StatusUI
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.WebDialog
import com.hfut.schedule.ui.component.appHorizontalDp
import com.hfut.schedule.ui.screen.home.search.function.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.transfer.getCampus
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Washing(hazeState: HazeState) {
    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            autoShape = false,
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ) {
            WashingUI()
        }
    }


    TransplantListItem(
        headlineContent = { Text(text = "洗衣机") },
        leadingContent = {
            Icon(painterResource(id = R.drawable.local_laundry_service), contentDescription = "")
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
fun WashingUI() {
    val pagerState = rememberPagerState(pageCount = { 2 }, initialPage =
        when(getCampus()) {
            Campus.XUANCHENG -> XUANCHENG_TAB
            Campus.HEFEI -> HEFEI_TAB
        }
    )
    val auth = prefs.getString("auth","")
    var showDialogWeb by remember { mutableStateOf(false) }
    WebDialog(showDialogWeb, url = MyApplication.HUIXIN_URL + "charge-app/?name=pays&appsourse=ydfwpt&id=${FeeType.WASHING_HEFEI.code}&name=pays&paymentUrl=${MyApplication.HUIXIN_URL}plat&token=" + auth, title = "慧新易校",showChanged = { showDialogWeb = false }, showTop = false)
    val titles = remember { listOf("合肥","宣城") }


    //布局///////////////////////////////////////////////////////////////////////////
    Column() {
        HazeBottomSheetTopBar("洗衣机", isPaddingStatusBar = false)
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
                        StatusUI(R.drawable.manga,"暂时未收集到洗衣机有充值入口")
                    }
                }
                Spacer(Modifier.height(appHorizontalDp()*3))
            }
        }
    }
}



