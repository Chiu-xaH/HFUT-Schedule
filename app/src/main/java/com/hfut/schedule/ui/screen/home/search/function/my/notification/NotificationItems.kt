package com.hfut.schedule.ui.screen.home.search.function.my.notification

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalSharedTransitionApi
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
import androidx.compose.runtime.LaunchedEffect
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
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CardBottomButton
import com.hfut.schedule.ui.component.container.CardBottomButtons
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.status.EmptyIcon
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.uicommon.style.color.topBarTransplantColor
import com.xah.uicommon.style.padding.InnerPaddingHeight
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


/**
 * 计算未读信息的小红点
 */
suspend fun calculatedReadNotificationCount() : Int {
    val readIds = DataStoreManager.readNotifications.first()
        .split(",")
        .mapNotNull { it.toIntOrNull() }
        .toSet()
    val allIds = getNotifications().map { it.id }.toSet()
    // 计算未读的数量
    return (allIds - readIds).size
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun NotificationItems() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val list = remember { getNotifications() }
    val readNotifications by DataStoreManager.readNotifications.collectAsState(initial = "")
    val readIds = readNotifications.split(",").mapNotNull { it.toIntOrNull() }.toSet()
    // 清理保存的id：readIds中存在list中没有的id，就删除
    LaunchedEffect(readIds, list) {
        if (list.isEmpty() || readIds.isEmpty()) return@LaunchedEffect

        val validIds = list.map { it.id }.toSet()
        val cleanedReadIds = readIds.filter { it in validIds }

        // 只有真的发生变化才写回，避免无限触发
        if (cleanedReadIds.size != readIds.size) {
            DataStoreManager.saveReadNotifications(cleanedReadIds)
        }
    }

    if(list.isEmpty()) {
        EmptyIcon()
    } else {
        list.forEach { item ->
            val id = item.id
            val read = id in readIds
            val clickAction = {
                scope.launch {
                    item.url?.let {
                        Starter.startWebView(context,it,item.title, icon = AppNavRoute.Notifications.icon)
                    } ?: showToast("暂无点击操作")
                }
            }
            CustomCard(color = cardNormalColor()) {
                Column {
                    TransplantListItem(
                        headlineContent = { Text(text = item.title) },
                        supportingContent = { Text(text = item.info) },
                        leadingContent = {
                            Icon(
                                painter = painterResource(id =
                                    if(read) R.drawable.check
                                    else R.drawable.notifications_unread
                                ),
                                contentDescription = ""
                            )
                        },
                        modifier = Modifier.clickable {
                            clickAction()
                        }
                    )
                    CardBottomButtons(
                        listOf(
                            CardBottomButton(item.remark, clickable = null),
                            CardBottomButton("含网页", show = item.url != null) {
                                clickAction()
                            },
                            CardBottomButton(if(!read) "标记为已读" else "标记为未读") {
                                scope.launch {
                                    if(!read) {
                                        // 将 id 追加保存
                                        DataStoreManager.saveReadNotifications(
                                            (readIds + id).toList()
                                        )
                                    } else {
                                        // 删除此 id
                                        DataStoreManager.saveReadNotifications(
                                            (readIds - id).toList()
                                        )
                                    }
                                }
                            }
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
                    TopBarNavigationIcon(route, AppNavRoute.Notifications.icon)
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .hazeSource(hazeState)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            InnerPaddingHeight(innerPadding,true)
            NotificationItems()
            InnerPaddingHeight(innerPadding,false)
        }
    }
}