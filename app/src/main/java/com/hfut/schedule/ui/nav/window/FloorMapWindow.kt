package com.hfut.schedule.ui.nav.window

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.model.BuildingMapFloorBean
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.ui.component.button.NoPadding
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.network.UrlImage
import com.hfut.schedule.ui.nav.window.base.FloatingWindow
import com.hfut.schedule.ui.screen.home.search.function.other.life.RoomMap
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.align.RowHorizontal
import com.xah.common.ui.util.text
import com.xah.container.component.base.SharedContent
import com.xah.container.model.ContentStrategy
import com.xah.floating.util.LocalFloatingController

data class FloorMapWindow(
    val floor : BuildingMapFloorBean,
    val vm : NetWorkViewModel,
) : FloatingWindow() {
    override val key = "floor_map_${floor.floor}"
    override val title = text("楼层导向图")

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    override fun BoxScope.Content() {
        val controller = LocalFloatingController.current
        val selectedRooms = remember { mutableStateSetOf<String>() }
        val uiState by vm.githubFloorXmlResp.state.collectAsState()

        val refreshNetwork: suspend () -> Unit = {
            vm.githubFloorXmlResp.clear()
            vm.getFloorXml(floor.xmlUrl)
        }

        LaunchedEffect(floor) {
            refreshNetwork()
        }


        Box(modifier = Modifier.fillMaxSize()) {
            SharedContent(
                shape = MaterialTheme.shapes.largeIncreased,
                key = key,
                contentStrategy = ContentStrategy.Layer(isFloating = true),
                modifier = Modifier
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(vertical = APP_HORIZONTAL_DP, horizontal = APP_HORIZONTAL_DP)
                    .align(Alignment.Center)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(0.dp)
                ) {

                    if(uiState is UiState.Success) {
                        val buildings = (uiState as UiState.Success).data
                        RowHorizontal {
                            NoPadding {
                                FlowRow(
                                    modifier = Modifier
                                        .padding(horizontal = APP_HORIZONTAL_DP)
                                        .padding(bottom = APP_HORIZONTAL_DP - CARD_NORMAL_DP * 3),
                                ) {
                                    val rooms = buildings.rooms.map { it.id }.distinct()
                                    rooms.forEach {
                                        val selected = selectedRooms.contains(it)
                                        FilterChip(
                                            selected = selected,
                                            onClick = {
                                                if(selected) {
                                                    selectedRooms.remove(it)
                                                } else {
                                                    selectedRooms.add(it)
                                                }
                                            },
                                            label = { Text(it) },
                                            modifier = Modifier
                                                .padding(end = CARD_NORMAL_DP * 3)
                                                .padding(bottom = CARD_NORMAL_DP * 3)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Box() {
                        UrlImage(
                            "https://raw.githubusercontent.com/Chiu-xaH/HFUT-Schedule/dev/src/source/building/${floor.imageUrl}",
                            contentScale = ContentScale.FillWidth,
                            shape = RoundedCornerShape(0.dp)
                        )

                        if(uiState is UiState.Success) {
                            val buildings = (uiState as UiState.Success).data

                            RoomMap(buildings, selectedIds = selectedRooms)
                        }
                    }
                }
            }
        }
    }
}