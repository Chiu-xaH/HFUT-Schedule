package com.hfut.schedule.ui.nav.destination

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.hfut.schedule.R
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.status.DevelopingIcon
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.style.special.rememberHazeBlur
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.style.align.CenterScreen
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.common.ui.util.text
import dev.chrisbanes.haze.hazeSource

object SettingsSearchDestination : NavDestination() {
    override val key: String = "settings_search"
    override val title: UiText = text("搜索设置项")
    override val icon = R.drawable.search

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val hazeState = rememberHazeBlur()

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
                CenterScreen {
                    DevelopingIcon()
                }
            }
        }
    }

    private data class Node(
        val destination : NavDestination,
//        val keywords : List<String>,
        var children : List<Node>
    )

    private val head = Node(
        destination = SettingsSearchDestination,
        children = listOf(
            Node(
                destination = SettingsAppearanceDestination,
                children = listOf(
                    Node(
                        destination = SettingsScreenCornerDestination,
                        children = emptyList()
                    )
                )
            ),
            Node(
                destination = SettingsConfigurationDestination,
                children = listOf(
                    Node(
                        destination = SettingsFocusCardDestination,
                        children = emptyList()
                    ),
                    Node(
                        destination = SettingsCalendarDestination,
                        children = emptyList()
                    ),
                    Node(
                        destination = SettingsOcrDestination,
                        children = emptyList()
                    ),
                    Node(
                        destination = SettingsShortcutEditDestination,
                        children = emptyList()
                    ),
                    Node(
                        destination = SettingsLabDestination,
                        children = emptyList()
                    ),
                    Node(
                        destination = SettingsBackupDestination,
                        children = emptyList()
                    ),
                    Node(
                        destination = SettingsFocusWidgetDestination,
                        children = emptyList()
                    ),
                )
            ),
            Node(
                destination = SettingsNetworkDestination,
                children = listOf(
                    Node(
                        destination = SettingsHuiXinPasswordDestination,
                        children = emptyList()
                    ),
                    Node(
                        destination = SettingsJxglstuPasswordDestination,
                        children = emptyList()
                    ),
                    Node(
                        destination = SettingsAiApiKeyDestination,
                        children = emptyList()
                    ),
                )
            ),
            Node(
                destination = SettingsAboutDestination,
                children = listOf(
                    Node(
                        destination = VersionInfoDestination, // 公有
                        children = emptyList()
                    ),
                    Node(
                        destination = SettingsAboutDeveloperDestination,
                        children = emptyList()
                    ),
                    Node(
                        destination = TipsDestination, // 公有
                        children = emptyList()
                    ),
                    Node(
                        destination = SettingsDeveloperDestination,
                        children = emptyList()
                    ),
                )
            )
        )
    )
}

