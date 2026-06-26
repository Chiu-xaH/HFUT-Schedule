package com.hfut.schedule.ui.nav.destination

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.xah.common.logic.state.NetworkUiState
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.courseSearch.ApiForCourseSearch
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.util.res
import com.xah.container.component.base.SharedContainer
import com.sharednav.common.util.NoneRoundShape
import com.xah.navigation.util.LocalNavController
import com.xah.navigation.util.LocalNavDependencies
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

data class CourseSearchApiDestination(
    val courseName : String?,
    val code : String?,
    val term : Int? = null,
) : NavDestination() {
    override val key = "course_search_api_${code}_$courseName"
    override val description = courseName ?: code
    override val title = TITLE
    override val icon = ICON

    companion object {
        val TITLE = res(R.string.navigation_label_course_search)
        val ICON = R.drawable.search
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
        val hazeState = rememberHazeState(blurEnabled = blur)
        val uiState by vm.courseSearchResponse.state.collectAsState()
        val navController = LocalNavController.current

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    modifier = Modifier.topBarBlur(hazeState),
                    scrollBehavior = scrollBehavior,
                    title = { Text("${TITLE.asString()}: $description") },
                    colors = topBarTransplantColor(),
                    navigationIcon = {
                        TopBarNavigationIcon()
                    },
                    actions = {
                        val canNotUse = courseName == null && code == null
                        val enabled = uiState is NetworkUiState.Success && !canNotUse
                        val dest = remember(enabled) {
                            if(enabled) {
                                val data = (uiState as NetworkUiState.Success).data
                                val term = if(data.isNotEmpty()) {
                                    data[0].semester.id
                                } else {
                                    null
                                }
                                CourseSearchTableDestination(term,courseName,code,null,data)
                            } else {
                                null
                            }
                        }
                        SharedContainer(
                            key = dest?.key,
                            shape = CircleShape,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP),
                        ) {
                            LiquidButton(
                                onClick = {
                                    navController.push(dest!!)
                                },
                                shape = NoneRoundShape,
                                backdrop = rememberLayerBackdrop(),
                                isCircle = true,
                                enabled = enabled
                            ) {
                                Icon(
                                    painterResource(R.drawable.calendar),
                                    null,
                                )
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(hazeState)
            ){
                ApiForCourseSearch(vm,courseName,code,term,innerPadding)
            }
        }
    }
}