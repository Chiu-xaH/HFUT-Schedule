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
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.status.EmptyIcon

import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.logic.util.parse.parseWifiQrCode
import com.hfut.schedule.logic.util.sys.ClipBoardHelper
import com.hfut.schedule.ui.component.button.BottomButton
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.uicommon.style.color.topBarTransplantColor
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CardBottomButton
import com.hfut.schedule.ui.component.container.CardBottomButtons
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalSharedTransitionScope
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun NotificationItems() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val list = getNotifications()
    if(list.isEmpty()) EmptyIcon() else {
        for(item in list.indices) {
            val clickAction = {
                if(list[item].url != null) {
                    scope.launch {
                        list[item].url?.let { Starter.startWebView(context,it,list[item].title, icon = AppNavRoute.Notifications.icon) }
                    }
                } else {
                    showToast("暂无点击操作")
                }
            }
            CustomCard(color = cardNormalColor()) {
                Column {
                    TransplantListItem(
                        headlineContent = { Text(text = list[item].title) },
                        supportingContent = { Text(text = list[item].info) },
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.notifications), contentDescription = "") },
                        modifier = Modifier.clickable {
                            clickAction()
                        }
                    )
                    CardBottomButtons(
                        listOf(
                            CardBottomButton(list[item].remark, clickable = null),
                            CardBottomButton("已读") {
                                // TODO 折叠
                            },
                            CardBottomButton("含网页", show = list[item].url != null) {
                                clickAction()
                            },
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    navController : NavHostController,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.Notifications.route }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    CustomTransitionScaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        route = route,

        navHostController = navController,
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                modifier = Modifier.topBarBlur(hazeState, ),
                colors = topBarTransplantColor(),
                title = { Text(AppNavRoute.Notifications.label) },
                navigationIcon = {
                    TopBarNavigationIcon(navController,route, AppNavRoute.Notifications.icon)
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