package com.hfut.schedule.ui.screen.home.search.function.my.notification

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.Notifications
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.StyleCardListItem
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.status.EmptyUI
import com.hfut.schedule.ui.component.webview.WebDialog
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.hfut.schedule.ui.style.topBarBlur
import com.hfut.schedule.ui.style.topBarTransplantColor
import com.xah.transition.component.TopBarNavigateIcon
import com.xah.transition.component.TransitionScaffold
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun NotificationItems() {
    val list = getNotifications()
    var showDialog by remember { mutableStateOf(false) }
    var notice : Notifications? by remember { mutableStateOf(null) }

    notice?.let { it.url?.let { it1 -> WebDialog(showDialog,{ showDialog = false }, it1,it.title) } }
    if(list.isEmpty()) EmptyUI() else {
        for(item in list.indices) {
            StyleCardListItem(
                headlineContent = { Text(text = list[item].title) },
                supportingContent = { Text(text = list[item].info) },
                overlineContent = { Text(text = list[item].remark) },
                leadingContent = { Icon(painter = painterResource(id = R.drawable.notifications), contentDescription = "") },
                modifier = Modifier.clickable {
                    if(list[item].url != null) {
                        notice = list[item]
                        showDialog = true
                    } else {
                        showToast("暂无点击操作")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.Notifications.route }
    with(sharedTransitionScope) {
        CustomTransitionScaffold (
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            topBar = {
                TopAppBar(
                    modifier = Modifier.topBarBlur(hazeState,useTry = true),
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.Notifications.title) },
                    navigationIcon = {
                        TopBarNavigateIcon(navController,animatedContentScope,route, AppNavRoute.Notifications.icon)
                    },
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.hazeSource(hazeState)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                InnerPaddingHeight(innerPadding,true)
                NotificationItems()
                InnerPaddingHeight(innerPadding,false)
            }
        }
    }
}