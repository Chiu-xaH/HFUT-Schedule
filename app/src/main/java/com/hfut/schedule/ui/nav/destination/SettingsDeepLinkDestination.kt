package com.hfut.schedule.ui.nav.destination

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.nav.deepLinks
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.common.ui.util.text
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

object SettingsDeepLinkDestination : NavDestination() {
    override val key: String = "settings_deeplink"
    override val title: UiText = text("深度链接")
    override val icon = R.drawable.link_2

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
        val hazeState = rememberHazeState(blurEnabled = blur)

        Scaffold(
            containerColor = MaterialTheme.colorScheme.surface,
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
                LazyColumn {
                    item { InnerPaddingHeight(innerPadding,true) }
                    items(deepLinks.size, key = { deepLinks[it].host }) { index ->
                        val item = deepLinks[index]
                        CardListItem(
                            headlineContent = { Text(item.host)},
                            leadingContent = {
                                Icon(painterResource(R.drawable.link_2),null)
                            }
                        )
                    }
                    item { InnerPaddingHeight(innerPadding,false) }
                }
            }
        }
    }
}