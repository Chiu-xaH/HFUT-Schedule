package com.hfut.schedule.ui.screen.home.search.function.my.webLab

import android.util.Patterns
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.WebURLType
import com.hfut.schedule.logic.database.entity.WebUrlDTO
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.cube.sub.MyAPIItem
import com.hfut.schedule.ui.screen.home.search.function.my.notification.NotificationItems
import com.hfut.schedule.ui.screen.home.search.function.my.notification.getNotifications
import com.hfut.schedule.ui.screen.news.department.SchoolsUI
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.hfut.schedule.ui.style.RowHorizontal
import com.hfut.schedule.ui.style.topBarBlur
import com.hfut.schedule.ui.style.topBarTransplantColor
import com.hfut.schedule.ui.util.AppAnimationManager
import com.xah.transition.component.TopBarNavigateIcon
import com.xah.transition.component.TransitionScaffold
import com.xah.transition.component.iconElementShare
import com.xah.transition.util.navigateAndSaveForTransition
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun WebUI(
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val route = remember { AppNavRoute.WebNavigation.route }

    TransplantListItem(
        headlineContent = { Text(text = AppNavRoute.WebNavigation.title) },
        leadingContent = {
            with(sharedTransitionScope) {
                Icon(painterResource(AppNavRoute.WebNavigation.icon), contentDescription = null,modifier = iconElementShare(animatedContentScope = animatedContentScope, route = route))
            }
        },
        modifier = Modifier.clickable {
            navController.navigateAndSaveForTransition(route)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Schools(hazeState: HazeState) {
    var showBottomSheet_School by remember { mutableStateOf(false) }

    if (showBottomSheet_School) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_School = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_School
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("学院")
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    SchoolsUI(null)
                }
            }
        }
    }

    FilledTonalButton(
        onClick = { showBottomSheet_School = true }
    ) {
        Text(text = "学院集锦")
    }
}

fun isValidWebUrl(url: String, strict : Boolean = false): Boolean {
    val checkUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
        if(strict) {
            return false
        }
        "http://$url"
    } else url
    return Patterns.WEB_URL.matcher(checkUrl).matches()
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun WebNavigationScreen(
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.WebNavigation.route }
    var showBottomSheet_Add by remember { mutableStateOf(false) }

    if(showBottomSheet_Add) {
        val scope = rememberCoroutineScope()
        var inputName by remember { mutableStateOf("") }
        var inputUrl by remember { mutableStateOf("") }
        Dialog(onDismissRequest = { showBottomSheet_Add = false}) {
            Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)) {
                Column(modifier = Modifier.padding(vertical = APP_HORIZONTAL_DP)) {
                    CustomTextField(
                        input = inputName,
                        label = { Text("名称") },
                        singleLine = false
                    ) { inputName = it }
                    Spacer(Modifier.height(APP_HORIZONTAL_DP))

                    CustomTextField(
                        input = inputUrl,
                        label = { Text("链接") },
                        singleLine = false,
                        supportingText = { Text("请输入http://或https://开头的完整链接")}
                    ) { inputUrl = it }
                    Spacer(Modifier.height(APP_HORIZONTAL_DP/2))

                    RowHorizontal(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                        FilledTonalButton (
                            onClick = {
                                showBottomSheet_Add = false
                            },
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth().weight(.5f)
                        ) {
                            Text("取消")
                        }
                        Spacer(Modifier.width(APP_HORIZONTAL_DP/2))

                        Button(
                            onClick = {
                                scope.launch {
                                    if(!isValidWebUrl(inputUrl)) {
                                        showToast("不合理链接")
                                        return@launch
                                    }
                                    if(inputName.isEmpty() || inputName.isBlank()) {
                                        showToast("空名")
                                        return@launch
                                    }
                                    DataBaseManager.webUrlDao.insert(WebUrlDTO(
                                        name = inputName,
                                        url = inputUrl,
                                        type = WebURLType.ADDED
                                    ).toEntity())
                                    showToast("添加完成")
                                    showBottomSheet_Add = false
                                }
                            },
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth().weight(.5f)
                        ) {
                            Text("保存")
                        }
                    }
                }
            }
        }
    }
    with(sharedTransitionScope) {
        CustomTransitionScaffold (
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            topBar = {
                TopAppBar(
                    modifier = Modifier.topBarBlur(hazeState, ),
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.WebNavigation.title) },
                    navigationIcon = {
                        TopBarNavigateIcon(navController,animatedContentScope,route, AppNavRoute.WebNavigation.icon)
                    },
                    actions = {
                        Box(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                            Schools(hazeState)
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showBottomSheet_Add = true }
                ) {
                    Icon(painterResource(R.drawable.add_2),null)
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.hazeSource(hazeState)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                InnerPaddingHeight(innerPadding,true)
                DividerTextExpandedWith(text = "固定项") {
                    WebItem()
                }

                DividerTextExpandedWith(text = "实验室") {
                    LabUI()
                }
                DividerTextExpandedWith(text = "本地收藏夹(点击刷新)") {
                    StorageWeb(hazeState)
                }
                InnerPaddingHeight(innerPadding,false)
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NotificationBoxScreen(
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.NotificationBox.route }

    LaunchedEffect(Unit) {
        saveString("Notifications", getNotifications().size.toString())
    }

    with(sharedTransitionScope) {
        CustomTransitionScaffold (
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            topBar = {
                TopAppBar(
                    modifier = Modifier.topBarBlur(hazeState, ),
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.NotificationBox.title) },
                    navigationIcon = {
                        TopBarNavigateIcon(navController,animatedContentScope,route, AppNavRoute.NotificationBox.icon)
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
                MyAPIItem()
                DividerTextExpandedWith("通知") {
                    NotificationItems()
                }
                DividerTextExpandedWith("实验室") {
                    LabUI()
                }
                InnerPaddingHeight(innerPadding,false)
            }
        }
    }
}