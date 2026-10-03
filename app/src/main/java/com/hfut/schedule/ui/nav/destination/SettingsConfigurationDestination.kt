package com.hfut.schedule.ui.nav.destination

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.button.BUTTON_END_PADDING
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.screen.home.cube.screen.AppearanceSettingsScreen
import com.hfut.schedule.ui.screen.home.cube.screen.ConfigurationSettingsScreen
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.rememberHazeBlur
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.util.res
import com.xah.navigation.anim.effect.RollTransitionEffect
import com.xah.navigation.util.LocalNavController
import dev.chrisbanes.haze.hazeSource

object SettingsConfigurationDestination : NavDestination() {
    override val key: String = "settings_configurations"
    override val title: UiText = res(R.string.navigation_label_settings_configurations)
    override val icon = R.drawable.joystick

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val hazeState = rememberHazeBlur()
        val backdrop = rememberLayerBackdrop()
        val navController = LocalNavController.current

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
                    },
                    actions = {
                        LiquidButton(
                            onClick = {
                                navController.push(SettingsSearchDestination, effect = RollTransitionEffect())
                            },
                            isCircle = true,
                            backdrop = backdrop,
                            modifier = Modifier.padding(end = BUTTON_END_PADDING)
                        ) {
                            Icon(
                                painterResource(R.drawable.search),
                                null
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(hazeState)
                    .backDropSource(backdrop)
            ) {
                ConfigurationSettingsScreen(innerPadding)
            }
        }
    }
}

