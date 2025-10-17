package com.hfut.schedule.ui.component.button

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.hfut.schedule.logic.model.NavigationBarItemData
import com.hfut.schedule.logic.model.NavigationBarItemDataDynamic
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.style.special.bottomBarBlur
import com.hfut.schedule.ui.util.navigateForBottomBar
import com.xah.transition.util.isCurrentRouteWithoutArgs
import com.xah.uicommon.style.padding.NavigationBarSpacer
import dev.chrisbanes.haze.HazeState

// 按索引顺序添加badge
@Composable
private fun BottomBarContent(
    list : List<NavigationBarItemData>,
    navController : NavController,
    enabled : Boolean = true,
) {
    val showLabel by DataStoreManager.showBottomBarLabel.collectAsState(initial = true)
    Row {
        list.forEachIndexed { index,item ->
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale = animateFloatAsState(
                targetValue = if (isPressed) 0.8f else 1f, // 按下时为0.9，松开时为1
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                label = "" // 使用弹簧动画
            )
            val route = item.route
            val selected = navController.isCurrentRouteWithoutArgs(route)
            NavigationBarItem(
                enabled = enabled,
                alwaysShowLabel = showLabel,
                selected = selected,
                modifier = Modifier.let {
                    if(showLabel) it.scale(scale.value)
                    else it
                },
                interactionSource = interactionSource,
                onClick = {
                    if (!selected) {
                        navController.navigateForBottomBar(route)
                    }
                },
                label = { Text(text = item.label) },
                icon = {
                    Icon(painterResource(if(selected)item.filledIcon else item.icon), contentDescription = item.label)
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = .9f),
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}


@Composable
fun HazeBottomBar(
    hazeState : HazeState,
    list : List<NavigationBarItemData>,
    navController : NavController,
    enabled : Boolean = true,
) {
    Column(modifier = Modifier.bottomBarBlur(hazeState)) {
        NavigationBarSpacer()
        NavigationBar(containerColor = Color.Transparent) {
            BottomBarContent(list,navController,enabled)
        }
    }
}



// 按索引顺序添加badge
@Composable
private fun BottomBarContentDynamic(
    list : List<NavigationBarItemDataDynamic>,
    navController : NavController,
    enabled : Boolean = true,
) {
    val showLabel by DataStoreManager.showBottomBarLabel.collectAsState(initial = true)
    Row {
        list.forEachIndexed { index,item ->
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale = animateFloatAsState(
                targetValue = if (isPressed) 0.8f else 1f, // 按下时为0.9，松开时为1
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                label = "" // 使用弹簧动画
            )
            val route = item.route
            val selected = navController.isCurrentRouteWithoutArgs(route)
            NavigationBarItem(
                enabled = enabled,
                alwaysShowLabel = showLabel,
                selected = selected,
                modifier = Modifier.let {
                    if(showLabel) it.scale(scale.value)
                    else it
                },
                interactionSource = interactionSource,
                onClick = {
                    if (!selected) {
                        navController.navigateForBottomBar(route)
                    }
                },
                label = { Text(text = item.label) },
                icon = {
                    BadgedBox(
                        badge = { item.badge?.invoke(this) },
                        content = { item.icon(selected) }
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = .9f),
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}


@Composable
fun HazeBottomBarDynamic(
    hazeState : HazeState,
    list : List<NavigationBarItemDataDynamic>,
    navController : NavController,
    enabled : Boolean = true,
) {
    Column(modifier = Modifier.bottomBarBlur(hazeState)) {
        NavigationBarSpacer()
        NavigationBar(containerColor = Color.Transparent) {
            BottomBarContentDynamic(list,navController,enabled)
        }
    }
}