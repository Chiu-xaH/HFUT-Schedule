package com.hfut.schedule.ui.screen.home.search.function.other.life

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.hfut.schedule.logic.model.community.NodeV
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.network.UrlImageNoCrop

import com.hfut.schedule.ui.component.screen.pager.CustomTabRow
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.CampusDetail
import com.xah.uicommon.style.align.RowHorizontal
import com.hfut.schedule.viewmodel.network.NetWorkViewModel

fun getCampusDetail() : CampusDetail? {
    val campusText = getPersonInfo().campus ?: return null
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


    CustomTabRow(pagerState,titles)
    HorizontalPager(state = pagerState) { pager ->
        val campus = t[pager]
        CommonNetworkScreen(uiState = uiState, onReload = refreshNetwork, isFullScreen = false) {
            val list = (uiState as UiState.Success).data
            // 从list里取出符合条件的一项
            val bean = list.find { it.name.contains(campus.description) } ?: return@CommonNetworkScreen
            val cUrl = bean.currentMap
            val name = bean.name
            val nodes = bean.nodeVOList.toMutableList()
            nodes.add(NodeV("..."))
            Column {
                RowHorizontal {
                    UrlImageNoCrop(cUrl)
                }
//                for(j in nodes.indices step 2) {
//                    val item1 = nodes[j]
//                    Row(Modifier.padding(horizontal = 12.dp)) {
//                        SmallCard(modifier = Modifier.padding(horizontal = 3.dp, vertical = 3.dp).weight(.5f)) {
//                            TransplantListItem(
//                                leadingContent = {
//                                    Text((j+1).toString())
//                                },
//                                headlineContent = { ScrollText(item1.name) },
//                            )
//                        }
//                        if(j + 1 < nodes.size) {
//                            val item2 = nodes[j+1]
//                            SmallCard(modifier = Modifier.padding(horizontal = 3.dp, vertical = 3.dp).weight(.5f)) {
//                                TransplantListItem(
//                                    leadingContent = {
//                                        Text((j+1+1).toString())
//                                    },
//                                    headlineContent = { ScrollText(item2.name) },
//                                )
//                            }
//                        } else {
//                            Spacer(Modifier.width(1.dp).weight(.5f))
//                        }
//                    }
//                }
            }
        }
    }
}
