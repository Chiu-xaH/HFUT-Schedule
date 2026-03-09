package com.hfut.schedule.ui.component.button

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
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.text.DIVIDER_TEXT_VERTICAL_PADDING
import com.hfut.schedule.ui.screen.animationOpen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.ui.util.state.GlobalUIStateHolder
import com.kyant.backdrop.Backdrop
import com.xah.mirror.util.ShaderState
import com.xah.navigation.model.action.ActionType
import com.xah.navigation.model.action.LaunchMode
import com.xah.navigation.utils.LocalNavController
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import kotlinx.coroutines.launch


// APP的根导航
val LocalAppControlCenter = staticCompositionLocalOf<DrawerState> {
    error("未提供根DrawerState")
}

@Composable
fun TopBarNavigationIcon() {
    FakeBackButton() {
        Icon(painterResource(R.drawable.arrow_back), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FakeBackButton(
    content : @Composable () -> Unit,
) {
    val navController = LocalNavController.current
    val drawerState = LocalAppControlCenter.current
    val enableControlCenter by DataStoreManager.enableControlCenterGesture.collectAsState(initial = false)
    val scope = rememberCoroutineScope()
    val queue = navController.stack.reversed()
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
                        modifier = Modifier.weight(1f, fill = false),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        LazyColumn {
                            items(queue.size) { index ->
                                val item = queue[index]
                                val dest = item.destination as NavDestination
                                val desc = dest.description
                                val title = dest.title.asString()
                                val isCurrent = index == 0
                                TransplantListItem(
                                    headlineContent = {
                                        Text(title ,fontWeight = if(isCurrent) FontWeight.Bold else FontWeight.Normal)
                                    },
                                    supportingContent = {
                                        desc?.let {
                                            Text(it)
                                        }
                                    },
                                    leadingContent = {
                                        Icon(painterResource(dest.icon),null, tint = if(isCurrent) MaterialTheme.colorScheme.primary else  LocalContentColor. current)
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
                                                navController.push(item.destination, LaunchMode.PopToExisting)
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
                    if(navController.stack.lastOrNull()?.destination == navController.startDestination) {
                        LargeButton(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            onClick = {
                                scope.launch {
                                    navController.push(navController.startDestination, LaunchMode.Single(reuse = true, actionType = ActionType.POP))
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
                    navController.pop()
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
) {
    val navController = LocalNavController.current
    LiquidButton (
        onClick = { navController.pop() },
        backdrop = backdrop,
        isCircle = true,
        modifier = Modifier.padding(start = APP_HORIZONTAL_DP-2.5.dp, end = 9.dp)
    ) {
        Icon(painterResource(R.drawable.arrow_back), contentDescription = null)
    }
}

