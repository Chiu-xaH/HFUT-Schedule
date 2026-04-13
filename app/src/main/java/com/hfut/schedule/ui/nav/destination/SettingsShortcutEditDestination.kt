package com.hfut.schedule.ui.nav.destination

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
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.screen.home.cube.sub.ShortcutSortScreen
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.util.res
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState


object SettingsShortcutEditDestination : NavDestination() {
    override val key: String = "settings_shortcut"
    override val title: UiText = res(R.string.navigation_label_shortcut)
    override val icon = R.drawable.keyboard_command_key

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        ShortcutSortScreen()
    }
}

