package com.hfut.schedule.ui.nav.destination

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.AppNotificationManager
import com.hfut.schedule.logic.util.sys.PermissionSet
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.common.ui.util.text
import com.xah.navigation.util.LocalNavDependencies
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

object TestDestination : NavDestination() {
    override val key = "test"
    override val title = text("内部调试")
    override val icon = R.drawable.build

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
        val hazeState = rememberHazeState(blurEnabled = blur)
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val backdrop = rememberLayerBackdrop()

        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        val context = LocalContext.current
        val activity = LocalActivity.current
        val scope = rememberCoroutineScope()

        Scaffold (
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                Column(
                    modifier = Modifier.topBarBlur(hazeState),
                ) {
                    MediumTopAppBar(
                        scrollBehavior = scrollBehavior,
                        colors = topBarTransplantColor(),
                        title = { Text(title.asString()) },
                        navigationIcon = {
                            TopBarNavigationIcon()
                        },
                    )
                }
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .backDropSource(backdrop)
                    .hazeSource(hazeState)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                InnerPaddingHeight(innerPadding,true)
                LaunchedEffect(activity) {
                    if(activity == null) {
                        return@LaunchedEffect
                    }
                    PermissionSet.checkAndRequestNotificationPermission(activity)
                }
                CardListItem(
                    headlineContent = {
                        Text("发送实时通知")
                    },
                    modifier = Modifier.clickable {
                        AppNotificationManager.updateCourseProgress("课程","08:00","12:00")
                    }
                )
                InnerPaddingHeight(innerPadding,false)
            }
        }
    }
}