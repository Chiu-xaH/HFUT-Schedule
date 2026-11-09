package com.hfut.schedule.ui.nav.destination

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.hfut.schedule.R
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.icon.DepartmentIcons
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.style.special.rememberHazeBlur
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.logic.state.NetworkUiState
import com.xah.common.logic.util.EMPTY_STRING
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.common.ui.util.text
import com.xah.container.component.base.sharedContainer
import com.xah.navigation.util.LocalNavDependencies
import dev.chrisbanes.haze.hazeSource

data class StudentIdSelectDestination(
    val targetDestination : NavDestination,
    val onSelected : (Int) -> Unit,
): NavDestination() {
    override val key = "select_student_id_${targetDestination.key}"
    override val title = text("选择身份")
    override val icon = R.drawable.beach_access

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()

        val hazeState = rememberHazeBlur()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val uiState by vm.studentId.state.collectAsState()

        val refreshNetwork : suspend () -> Unit = {
            getJxglstuCookie()?.let { cookie ->
                vm.studentId.clear()
                vm.getStudentId(cookie)
            }
        }

        Scaffold (
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    modifier = Modifier.topBarBlur(hazeState),
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(title.asString()) },
                    navigationIcon = {
                        TopBarNavigationIcon()
                    }
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .hazeSource(hazeState)
                    .fillMaxSize()
            ) {
                CommonNetworkScreen(uiState, onReload = refreshNetwork) {
                    val list = (uiState as NetworkUiState.Success).data
                    LazyColumn {
                        item { InnerPaddingHeight(innerPadding,true) }
                        items(list.size,key = { list[it].studentId }) { index ->
                            val item = list[index]
                            CardListItem(
                                headlineContent = {
                                    item.major?.let {
                                        Text(
                                            it + (
                                                item.direction?.let { dir -> "(方向: $dir)" } ?: EMPTY_STRING
                                            )
                                        )
                                    }
                                },
                                supportingContent = {
                                    item.department?.let {
                                        Text(it)
                                    }
                                },
                                leadingContent = {
                                    DepartmentIcons(item.department ?: EMPTY_STRING)
                                },
                                overlineContent = {
                                    Text("${item.type} |${item.level} | ${item.status}")
                                },
                                modifier = Modifier.clickable {
                                    onSelected(item.studentId)
                                },
                                cardModifier = Modifier.sharedContainer(
                                    key = targetDestination.key,
                                    containerColor = cardNormalColor(),
                                    shape = MaterialTheme.shapes.medium
                                )
                            )
                        }
                        item { InnerPaddingHeight(innerPadding,false) }
                    }
                }
            }
        }
    }
}