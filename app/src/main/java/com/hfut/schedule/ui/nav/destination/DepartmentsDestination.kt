package com.hfut.schedule.ui.nav.destination

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
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.screen.news.department.SchoolsUI
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.util.res
import com.xah.navigation.util.LocalNavDependencies
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

object DepartmentsDestination : NavDestination() {
    override val key = "departments"
    override val title = res(R.string.navigation_label_departments)
    override val icon = R.drawable.school

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
        val hazeState = rememberHazeState(blurEnabled = blur)

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    modifier = Modifier.topBarBlur(hazeState),
                    scrollBehavior = scrollBehavior,
                    title = { Text(title.asString()) },
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
                SchoolsUI(vm,innerPadding)
            }
        }
    }
}