package com.hfut.schedule.ui.component.button

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.text.DIVIDER_TEXT_VERTICAL_PADDING
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.animationOpen
import com.hfut.schedule.ui.screen.util.getLabel
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.hfut.schedule.ui.util.state.GlobalUIStateHolder
import com.kyant.backdrop.Backdrop
import com.xah.transition.component.awaitTransition
import com.xah.transition.component.iconElementShare
import com.xah.transition.state.LocalAppNavController
import com.xah.transition.state.LocalSharedTransitionScope
import com.xah.transition.state.TransitionConfig
import com.xah.transition.style.DefaultTransitionStyle
import com.xah.transition.style.TransitionLevel
import com.xah.transition.util.currentRouteWithArgWithoutValues
import com.xah.transition.util.popBackStackForTransition
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 备选方案，实在无法解决横划手势才采用
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TopBarNavigationIcon(
    route : String,
    icon :  Int,
    restoreIcon : Boolean = TransitionConfig.transitionBackgroundStyle.level == TransitionLevel.NONE_ALL
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    var displayRouteIcon by remember { mutableStateOf(true) }
    if(!TransitionConfig.transplantBackground) {
        LaunchedEffect(Unit) {
            displayRouteIcon = true
            sharedTransitionScope.awaitTransition()
            delay(1500L)
            displayRouteIcon = false
            if(restoreIcon) {
                delay(3000L)
                displayRouteIcon = true
            }
        }
    }

    FakeBackButton() {
        Box() {
            AnimatedVisibility(
                visible = displayRouteIcon,
                enter = DefaultTransitionStyle.centerAllAnimation.enter,
                exit = DefaultTransitionStyle.centerAllAnimation.exit
            ) {
                Icon(painterResource(icon), contentDescription = null, tint = MaterialTheme.colorScheme.primary,modifier = Modifier.iconElementShare(route = route))
            }
            AnimatedVisibility(
                visible = !displayRouteIcon,
                enter = DefaultTransitionStyle.centerAllAnimation.enter,
                exit = DefaultTransitionStyle.centerAllAnimation.exit
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}


// APP的根导航
val LocalAppControlCenter = staticCompositionLocalOf<DrawerState> {
    error("未提供根DrawerState")
}


@Composable
fun TopBarNavigationIcon() {
    FakeBackButton() {
        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FakeBackButton(
    content : @Composable () -> Unit,
) {
    val navController = LocalAppNavController.current
    val drawerState = LocalAppControlCenter.current
    val enableControlCenter by DataStoreManager.enableControlCenterGesture.collectAsState(initial = false)
    val currentRoute = navController.currentRouteWithArgWithoutValues()?.substringBefore("?")
    val scope = rememberCoroutineScope()
    val queue = GlobalUIStateHolder.routeQueue
    var displayDialog by remember { mutableStateOf(false) }

    if(displayDialog) {
        Dialog(
            onDismissRequest = { displayDialog = false }
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .clickable(
                    // 去掉水波纹
                    interactionSource = null,
                    indication = null
                ){
                    displayDialog = false
                }
            ) {
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(vertical = APP_HORIZONTAL_DP)
                        .align(Alignment.TopCenter)
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        LazyColumn {
                            items(queue.size) { index ->
                                val item = queue[index]
                                val label = getLabel(item.route)
                                val isCurrent = currentRoute == item.app.route && index == 0
                                TransplantListItem(
                                    headlineContent = {
                                        Text(item.app.label ,fontWeight = if(isCurrent) FontWeight.Bold else FontWeight.Normal)
                                    },
                                    supportingContent = {
                                        label?.let {
                                            Text(it)
                                        }
                                    },
                                    leadingContent = {
                                        Icon(painterResource(item.app.icon),null, tint = if(isCurrent) MaterialTheme.colorScheme.primary else  LocalContentColor. current)
                                    },

                                    trailingContent = {
                                        if(isCurrent) {
                                            Icon(painterResource(R.drawable.arrow_upward),null, modifier = Modifier.rotate(-90f))
                                        }
                                    },
                                    modifier = Modifier.clickable {
                                        if(isCurrent) {
                                            displayDialog = false
                                        } else {
                                            scope.launch {
                                                navController.navigateForTransition(item.app,item.route)
                                                displayDialog = false
                                            }
                                        }
                                    }
                                )
                                if(index != queue.size-1) {
                                    PaddingHorizontalDivider()
                                }
                            }
                        }
                    }
                    if(currentRoute !in TransitionConfig.firstStartRoute) {
                        LargeButton(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            onClick = {
                                scope.launch {
                                    navController.navigate(AppNavRoute.Home.route) {
                                        popUpTo(0) {
                                            inclusive = true
                                        }
                                    }
                                    showToast("已回到首页")
                                    displayDialog = false
                                }
                            },
                            modifier = Modifier
                                .padding(top = APP_HORIZONTAL_DP)
                                .fillMaxWidth()
                            ,
                            text = "回到首页",
                            icon = R.drawable.home
                        )
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .padding(horizontal = CARD_NORMAL_DP/2)
            .clip(CircleShape)
            .combinedClickable(
                onClick = {
                    navController.popBackStackForTransition()
                },
                // TODO 预留唤出启动台
                onDoubleClick = null,
                onLongClick = {
                    if(!enableControlCenter) {
                        displayDialog = true
                    } else {
                        scope.launch {
                            drawerState.animationOpen()
                        }
                    }
                }
            )
    ) {
        Box(
            modifier = Modifier.padding(DIVIDER_TEXT_VERTICAL_PADDING)
        ) {
            content()
        }
    }
}




@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LiquidTopBarNavigateIcon(
    backdrop: Backdrop,
    navController : NavHostController,
    route : String,
    icon :  Int,
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    var show by remember { mutableStateOf(true) }
    if(!TransitionConfig.transplantBackground) {
        LaunchedEffect(Unit) {
            show = true
            sharedTransitionScope.awaitTransition()
            delay(1500L)
            show = false
        }
    }


    LiquidButton (
        onClick = { navController.popBackStackForTransition() },
        backdrop = backdrop,
        isCircle = true,
        modifier = Modifier.padding(start = APP_HORIZONTAL_DP-2.5.dp, end = 9.dp)
    ) {
        Box() {
            androidx.compose.animation.AnimatedVisibility(
                visible = show,
                enter = DefaultTransitionStyle.centerAllAnimation.enter,
                exit = DefaultTransitionStyle.centerAllAnimation.exit
            ) {
                Icon(painterResource(icon), contentDescription = null, modifier = Modifier.iconElementShare(route = route))
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = !show,
                enter = DefaultTransitionStyle.centerAllAnimation.enter,
                exit = DefaultTransitionStyle.centerAllAnimation.exit
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        }
    }
}
