package com.hfut.schedule.ui.destination

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.hfut.schedule.logic.model.community.courseFailRateDTOList
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.screen.home.search.function.community.failRate.FailRateDetailScreen
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.NavDestination
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.util.res
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

data class FailRateDetailDestination(
    val courseName : String,
    val lessonId : String,
    val bean : List<courseFailRateDTOList>
) : NavDestination() {
    override val key = "fail_rate_detail_${courseName}_${lessonId}"
    override val title = res(R.string.navigation_label_fail_rate)
    override val icon = R.drawable.radio_button_partial

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
        val hazeState = rememberHazeState(blurEnabled = blur)

        Scaffold(
            modifier = Modifier.Companion.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    modifier = Modifier.Companion.topBarBlur(hazeState),
                    scrollBehavior = scrollBehavior,
                    title = { Text("${title.asString()}: $courseName") },
                    colors = topBarTransplantColor(),
                    navigationIcon = {
                        TopBarNavigationIcon()
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(hazeState)
            ) {
                FailRateDetailScreen(innerPadding,bean)
            }
        }
    }
}