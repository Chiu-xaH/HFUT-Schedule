package com.hfut.schedule.ui.screen.home.search.function.other.life

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.xah.common.logic.model.Campus
import com.hfut.schedule.logic.util.helper.getCampus
import com.xah.common.logic.state.NetworkUiState
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.network.UrlImage
import com.hfut.schedule.ui.component.screen.pager.CustomTabRow
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.align.RowHorizontal

@Composable
fun SchoolMapScreen(vm : NetWorkViewModel) {
    val t = remember { Campus.entries }
    val titles = remember { t.map { it.description } }
    val defaultCampus = remember { getCampus() }
    val pagerState = rememberPagerState(pageCount = { titles.size }, initialPage =
        when(defaultCampus) {
            Campus.TXL -> 0
            Campus.FCH -> 1
            Campus.XC -> 2
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

    CustomTabRow(pagerState,titles)
    HorizontalPager(state = pagerState) { pager ->
        val campus = t[pager]
        CommonNetworkScreen(uiState = uiState, onReload = refreshNetwork, isFullScreen = false) {
            val list = (uiState as NetworkUiState.Success).data
            // 从list里取出符合条件的一项
            val bean = list.find { it.name.contains(campus.description) } ?: return@CommonNetworkScreen
            val cUrl = bean.imageUrl
            Column {
                RowHorizontal {
                    UrlImage(
                        cUrl,
                        modifier = Modifier.padding(APP_HORIZONTAL_DP),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}
