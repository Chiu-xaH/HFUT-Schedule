package com.hfut.schedule.ui.component.button

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.text.DIVIDER_TEXT_VERTICAL_PADDING
import com.hfut.schedule.ui.screen.animationOpen
import com.kyant.backdrop.Backdrop
import com.xah.transition.component.awaitTransition
import com.xah.transition.component.iconElementShare
import com.xah.transition.state.LocalAppNavController
import com.xah.transition.state.LocalSharedTransitionScope
import com.xah.transition.state.TransitionConfig
import com.xah.transition.style.DefaultTransitionStyle
import com.xah.transition.style.TransitionLevel
import com.xah.transition.util.popBackStackForTransition
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 备选方案，实在无法解决横划手势才采用
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CustomTopBarNavigationIcon(
    route : String,
    icon :  Int,
    restoreIcon : Boolean = TransitionConfig.transitionBackgroundStyle.level == TransitionLevel.NONE_ALL
) {
    val navController = LocalAppNavController.current
    val drawerState = LocalAppControlCenter.current
    val scope = rememberCoroutineScope()
    val sharedTransitionScope = LocalSharedTransitionScope.current
    var show by remember { mutableStateOf(true) }
    if(!TransitionConfig.transplantBackground) {
        LaunchedEffect(Unit) {
            show = true
            sharedTransitionScope.awaitTransition()
            delay(1500L)
            show = false
            if(restoreIcon) {
                delay(3000L)
                show = true
            }
        }
    }


    FakeIconButton(
        onClick = {
            navController.popBackStackForTransition()
        },
        onLongClick = {
            scope.launch {
                drawerState.animationOpen()
            }
        }
    ) {
        Box() {
            AnimatedVisibility(
                visible = show,
                enter = DefaultTransitionStyle.centerAllAnimation.enter,
                exit = DefaultTransitionStyle.centerAllAnimation.exit
            ) {
                Icon(painterResource(icon), contentDescription = null, tint = MaterialTheme.colorScheme.primary,modifier = Modifier.iconElementShare(route = route))
            }
            AnimatedVisibility(
                visible = !show,
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
fun CustomTopBarNavigationIcon() {
    val navController = LocalAppNavController.current
    val drawerState = LocalAppControlCenter.current
    val scope = rememberCoroutineScope()
    FakeIconButton(
        onClick = {
            navController.popBackStackForTransition()
        },
        onLongClick = {
            scope.launch {
                drawerState.animationOpen()
            }
        }
    ) {
        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
    }
}


@Composable
private fun FakeIconButton(
    onClick : () -> Unit,
    onLongClick : () -> Unit,
    content : @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(horizontal = DIVIDER_TEXT_VERTICAL_PADDING)
            .clip(CircleShape)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )


    ) {
        content()
    }
}