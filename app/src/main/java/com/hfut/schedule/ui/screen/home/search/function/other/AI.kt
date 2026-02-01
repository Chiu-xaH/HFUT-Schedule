package com.hfut.schedule.ui.screen.home.search.function.other

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.status.DevelopingIcon
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.xah.transition.component.iconElementShare
import com.xah.uicommon.component.text.ScrollText
import com.xah.uicommon.style.align.CenterScreen
import com.xah.uicommon.style.color.topBarTransplantColor
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
fun AI(
    navController : NavHostController,
) {
    val route = remember { AppNavRoute.AI.route }

    TransplantListItem(
        headlineContent = { ScrollText(text = AppNavRoute.AI.label) },
        leadingContent = {
            Box() {
//                RotatingRainbowGlow(
//                    modifier = Modifier.size(24.dp)
//                )
                Icon(painterResource(AppNavRoute.AI.icon), contentDescription = null,modifier = Modifier.iconElementShare( route = route))

            }
        },
        modifier = Modifier.clickable {
            navController.navigateForTransition(AppNavRoute.AI,route)
        }
    )
}

@Composable
fun RotatingRainbowGlow(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2f

        // 角度方向彩虹渐变
        val sweepBrush = Brush.sweepGradient(
            colors = listOf(
                Color.Red.copy(.2f),
                Color.Yellow.copy(.2f),
                Color.Green.copy(.2f),
                Color.Cyan.copy(.2f),
                Color.Blue.copy(.2f),
                Color.Magenta.copy(.2f),
                Color.Red.copy(.2f) // 闭环
            ),
            center = center
        )

        // 画彩色光晕
        drawCircle(
            brush = sweepBrush,
            radius = radius,
            center = center
        )

        // 叠加一层“向外透明”的遮罩
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.Black
                ),
                center = center,
                radius = radius
            ),
            radius = radius,
            center = center,
            blendMode = BlendMode.DstIn
        )
    }
}



@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AIScreen(
    navController : NavHostController,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val route = remember { AppNavRoute.AI.route }
    CustomTransitionScaffold (
        route = route,
        navHostController = navController,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                modifier = Modifier.topBarBlur(hazeState),
                colors = topBarTransplantColor(),
                title = { Text(AppNavRoute.AI.label) },
                navigationIcon = {
                    TopBarNavigationIcon(route, AppNavRoute.AI.icon)
                },
            )
        },
    ) { innerPadding ->
        CenterScreen {
            DevelopingIcon()
        }
    }
}