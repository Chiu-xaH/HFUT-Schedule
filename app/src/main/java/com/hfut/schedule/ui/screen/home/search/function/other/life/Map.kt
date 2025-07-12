package com.hfut.schedule.ui.screen.home.search.function.other.life

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.other.loadImage
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.component.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.CommonNetworkScreen
import com.hfut.schedule.ui.component.WebDialog
import com.hfut.schedule.ui.component.custom.CustomTabRow
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.CampusDetail
import com.hfut.schedule.ui.style.RowHorizontal
import com.hfut.schedule.viewmodel.network.NetWorkViewModel

fun getCampusDetail() : CampusDetail? {
    val campusText = getPersonInfo().school ?: return null
    return if(campusText.contains(CampusDetail.XC.description) == true) {
        CampusDetail.XC
    } else if(campusText.contains(CampusDetail.FCH.description) == true) {
        CampusDetail.FCH
    } else if(campusText.contains(CampusDetail.TXL.description) == true) {
        CampusDetail.TXL
    } else {
        null
    }
}

@Composable
fun SchoolMapScreen(vm : NetWorkViewModel) {
    val t = remember { CampusDetail.entries }
    val titles = remember { t.map { it.description } }
    val defaultCampus = remember { getCampusDetail() }
    val pagerState = rememberPagerState(pageCount = { titles.size }, initialPage =
        when(defaultCampus) {
            CampusDetail.TXL -> 0
            CampusDetail.FCH -> 1
            CampusDetail.XC -> 2
            else -> 0
        }
    )
    val refreshNetwork : suspend () -> Unit = {
        prefs.getString("TOKEN","")?.let {
            vm.mapsResponse.clear()
            vm.getMaps(it)
        }
    }
    LaunchedEffect(Unit) {
        refreshNetwork()
    }
    val uiState by vm.mapsResponse.state.collectAsState()

//    var showBottomSheet by remember { mutableStateOf(false) }
//    if (showBottomSheet) {
//        HazeBottomSheet (
//            onDismissRequest = { showBottomSheet = false },
//            showBottomSheet = showBottomSheet,
//            hazeState = hazeState,
//        ) {
//
//        }
//    }
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("校区地图") }
    var url by remember { mutableStateOf("") }
    WebDialog(showDialog,{ showDialog = false },url,title)

    CustomTabRow(pagerState,titles)
    HorizontalPager(state = pagerState) { pager ->
        val campus = t[pager]
        CommonNetworkScreen(uiState = uiState, onReload = refreshNetwork, isFullScreen = false) {
            val list = (uiState as UiState.Success).data
            // 从list里取出符合条件的一项
            val bean = list.find { it.name.contains(campus.description) } ?: return@CommonNetworkScreen
            val cUrl = bean.currentMap
            val name = bean.name
            val imageState = loadImage(cUrl)
            imageState.value?.let { bitmap ->
                RowHorizontal {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.padding(APP_HORIZONTAL_DP).clickable {
                            // 点击全屏预览
                            url = cUrl
                            title = name
                            showDialog = true
                        },
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}
