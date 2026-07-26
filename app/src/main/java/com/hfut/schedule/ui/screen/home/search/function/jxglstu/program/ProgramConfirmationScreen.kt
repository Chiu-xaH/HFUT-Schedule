package com.hfut.schedule.ui.screen.home.search.function.jxglstu.program

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.network.api.model.response.json.jxglstu.JxglstuSelectedCourseConfirmationType
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.pager.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.nav.destination.ProgramConfirmationDestination
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.logic.state.NetworkUiState
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.style.padding.InnerPaddingHeight
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramConfirmationScreen(
    vm : NetWorkViewModel
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val uiState by vm.jxglstuSelectedCourseConfirmationResp.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = m@ {
        val cookie = getJxglstuCookie() ?: return@m
        vm.jxglstuSelectedCourseConfirmationResp.clear()
        vm.getSelectCourseConfirmation(cookie)
    }

    LaunchedEffect(Unit) {
        if(uiState is NetworkUiState.Success) {
            return@LaunchedEffect
        }
        refreshNetwork()
    }

    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState),
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = {
                        Text(ProgramConfirmationDestination.title.asString())
                    },
                    navigationIcon = {
                        TopBarNavigationIcon()
                    },
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
            CommonNetworkScreen(uiState, onReload = refreshNetwork) {
                val list = (uiState as NetworkUiState.Success).data.toList()
                LazyColumn {
                    item { InnerPaddingHeight(innerPadding,true) }
                    items(list.size, key = { list[it].first.name }) { index ->
                        val item = list[index]
                        val type = item.first
                        DividerTextExpandedWith(type.desc) {
                            val subList = item.second
                            subList.forEach { subItem ->
                                CardListItem(
                                    headlineContent = {
                                        Text(subItem.courseName)
                                    },
                                    overlineContent = {
                                        Text(subItem.courseCode)
                                    },
                                    trailingContent = {
                                        Text("学分 ${subItem.credits}")
                                    },
                                    leadingContent = {
                                        Icon(
                                            painterResource(
                                                when(type) {
                                                    JxglstuSelectedCourseConfirmationType.NAVER_NOT_PASSED -> R.drawable.error
                                                    else -> R.drawable.circle
                                                }
                                            ),
                                            null,
                                            tint = when(type) {
                                                JxglstuSelectedCourseConfirmationType.NAVER_NOT_PASSED -> MaterialTheme.colorScheme.error
                                                else -> LocalContentColor.current
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                    item { InnerPaddingHeight(innerPadding,false) }
                }
            }
        }
    }
}