package com.hfut.schedule.ui.screen.home.search.function.other.wechat

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.button.StartAppIconButton
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.status.DevelopingIcon
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.fix.about.createQRCodeBitmap
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.topBarBlur
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.uicommon.component.text.BottomTip
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.align.CenterScreen
import com.xah.uicommon.style.color.topBarTransplantColor
import com.xah.uicommon.style.padding.InnerPaddingHeight
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState


@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AlumniScreen(
    navController : NavHostController,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val backDrop = rememberLayerBackdrop()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val route = remember { AppNavRoute.Alumni.route }
    CustomTransitionScaffold (
        route = route,
        navHostController = navController,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                modifier = Modifier.topBarBlur(hazeState, ),
                colors = topBarTransplantColor(),
                title = { Text(AppNavRoute.Alumni.label) },
                navigationIcon = {
                    TopBarNavigationIcon(navController,route, AppNavRoute.Alumni.icon)
                },
//                actions = {
//                    StartAppIconButton(
//                        backDrop,
//                        Starter.AppPackages.WECHAT,
//                        modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)
//                    )
//                }
            )
        },
    ) { innerPadding ->
        CenterScreen {
            DevelopingIcon()
        }
//        Column(
//            modifier = Modifier
//                .backDropSource(backDrop)
//                .hazeSource(hazeState)
//                .verticalScroll(rememberScrollState())
//                .fillMaxSize()
//        ) {
//
//        }
    }
}