package com.hfut.schedule.ui.destination

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.screen.home.cube.sub.DeveloperScreen
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.util.text
import com.xah.navigation.util.LocalNavDependencies
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

object SettingsDeveloperDestination : NavDestination() {
    override val key: String = "settings_developer"
    override val title: UiText = text("开发者选项")
    override val icon = R.drawable.code

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
        val hazeState = rememberHazeState(blurEnabled = blur)
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()

        Scaffold(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    modifier = Modifier.topBarBlur(hazeState, MaterialTheme.colorScheme.surfaceContainer),
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
                DeveloperScreen(vm,innerPadding)
            }
        }
    }
}

