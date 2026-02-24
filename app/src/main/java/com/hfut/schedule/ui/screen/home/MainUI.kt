package com.hfut.schedule.ui.screen.home

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.FriendEntity
import com.hfut.schedule.logic.enumeration.BottomBarItems
import com.hfut.schedule.logic.enumeration.BottomBarItems.COURSES
import com.hfut.schedule.logic.enumeration.BottomBarItems.FOCUS
import com.hfut.schedule.logic.enumeration.BottomBarItems.SEARCH
import com.hfut.schedule.logic.enumeration.BottomBarItems.SETTINGS
import com.hfut.schedule.logic.model.GiteeReleaseResponse
import com.hfut.schedule.logic.model.NavigationBarItemDataDynamic
import com.hfut.schedule.logic.model.NavigationBarItemDynamicIcon
import com.hfut.schedule.logic.network.util.MyApiParse.isNextOpen
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager.SEARCH_DEFAULT_STR
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.saveInt
import com.hfut.schedule.logic.util.sys.LanguageHelper
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.AnimatedIconButton
import com.hfut.schedule.ui.component.button.BUTTON_PADDING
import com.hfut.schedule.ui.component.button.HazeBottomBarDynamic
import com.hfut.schedule.ui.component.button.SpecialBottomBar
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.dialog.LittleDialog
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.divider.ScrollHorizontalTopDivider
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.network.onListenStateHolder
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.screen.pager.CustomTabRow
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.calendar.common.ScheduleTopDate
import com.hfut.schedule.ui.screen.home.calendar.common.numToChinese
import com.hfut.schedule.ui.screen.home.calendar.communtiy.CommunityCourseTableUI
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.JxglstuCourseTableUI
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.JxglstuCourseTableTwo
import com.hfut.schedule.ui.screen.home.calendar.multi.CourseType
import com.hfut.schedule.ui.screen.home.calendar.multi.MultiScheduleSettings
import com.hfut.schedule.ui.screen.home.calendar.uniapp.UniAppCoursesScreen
import com.hfut.schedule.ui.screen.home.calendar.zjgd.ZhiJianCourseTableUI
import com.hfut.schedule.ui.screen.home.cube.SettingsScreen
import com.hfut.schedule.ui.screen.home.cube.screen.CalendarUISettings
import com.hfut.schedule.ui.screen.home.cube.sub.update.getUpdates
import com.hfut.schedule.ui.screen.home.focus.TodayScreen
import com.hfut.schedule.ui.screen.home.focus.funiction.AddEventOrigin
import com.hfut.schedule.ui.screen.home.search.SearchAppBeanLite
import com.hfut.schedule.ui.screen.home.search.SearchFuncs
import com.hfut.schedule.ui.screen.home.search.SearchScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.TotalCourseDataSource
import com.hfut.schedule.ui.screen.home.search.function.my.notification.calculatedReadNotificationCount
import com.hfut.schedule.ui.screen.supabase.login.ApiToSupabase
import com.hfut.schedule.ui.style.color.textFiledTransplant
import com.hfut.schedule.ui.style.special.CustomBottomSheet
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.ui.util.navigation.AppAnimationManager.currentPage
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.hfut.schedule.ui.util.state.GlobalUIStateHolder
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.xah.mirror.shader.glassLayer
import com.xah.mirror.shader.largeStyle
import com.xah.mirror.shader.smallStyle
import com.xah.mirror.util.ShaderState
import com.xah.mirror.util.rememberShaderState
import com.xah.mirror.util.shaderSource
import com.xah.transition.component.containerShare
import com.xah.transition.component.iconElementShare
import com.xah.transition.util.currentRouteWithoutArgs
import com.xah.uicommon.component.text.BottomTip
import com.xah.uicommon.component.text.ScrollText
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.align.RowHorizontal
import com.xah.uicommon.style.color.topBarTransplantColor
import com.xah.uicommon.util.LogUtil
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState
import java.io.File

private val titles = listOf("重要安排","其他事项")

fun smoothToOne(scaleFactor: MutableState<Float>) {
    // 创建一个 ValueAnimator，从当前值平滑过渡到 1f
    val animator = ValueAnimator.ofFloat(scaleFactor.value, 1f)
    animator.duration = AppAnimationManager.ANIMATION_SPEED*1L

    // 更新 scaleFactor 的值
    animator.addUpdateListener { animation ->
        scaleFactor.value = animation.animatedValue as Float
    }

    // 开始动画
    animator.start()
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("SuspiciousIndentation", "CoroutineCreationDuringComposition",
    "UnusedMaterial3ScaffoldPaddingParameter"
)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalGlideComposeApi::class, ExperimentalAnimationGraphicsApi::class
)
@Composable
fun MainScreen(
    vm : NetWorkViewModel,
    vmUI : UIViewModel,
    celebrationText : String?,
    isLogin : Boolean,
    navHostTopController : NavHostController,
) {
    val navController = rememberNavController()
    var isEnabled by rememberSaveable { mutableStateOf(!isLogin) }
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)

    val update by produceState<GiteeReleaseResponse?>(initialValue = null) {
        value = getUpdates(vm)
    }
    val showBadge = update != null

    //判定是否以聚焦作为第一页
    val first  by rememberSaveable { mutableStateOf(
        if(isLogin) COURSES
        else FOCUS
//            when (prefs.getBoolean("SWITCHFOCUS",true)) {
//            true -> FOCUS
//            false -> COURSES
//        }
    ) }
    val targetPage = when(navController.currentRouteWithoutArgs()) {
        COURSES.name -> COURSES
        FOCUS.name -> FOCUS
        SEARCH.name -> SEARCH
        SETTINGS.name -> SETTINGS
        else -> first
    }

    var showAll by rememberSaveable { mutableStateOf(false) }

    var ifSaved by rememberSaveable { mutableStateOf(!isLogin) }

    var swapUI by rememberSaveable { mutableIntStateOf(
        if(ifSaved)
            DataStoreManager.getSyncDefaultCalendar() ?: CourseType.JXGLSTU.code
        else
            CourseType.JXGLSTU.code
    ) }
    

    var showBottomSheet_multi by remember { mutableStateOf(false) }

    val currentAnimationIndex by DataStoreManager.animationType.collectAsState(initial = 0)

    var showUiSettings by remember { mutableStateOf(false) }
    if (showUiSettings) {
        Dialog(
            onDismissRequest = { showUiSettings = false }
        ) {
            CustomCard(
                color= MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.large
            ) {
                CalendarUISettings(true)
            }
        }
    }
    if (showBottomSheet_multi) {
        CustomBottomSheet (
            showBottomSheet = showBottomSheet_multi,
            onDismissRequest = { showBottomSheet_multi = false },
            autoShape = false
        ) {
            Column {
                MultiScheduleSettings(
                    ifSaved = ifSaved,
                    select = swapUI,
                    onSelectedChange = { newSelected ->
                        swapUI = newSelected
                    },
                    onShowUiSettings = {
                        showUiSettings = it
                        showBottomSheet_multi = false
                    },
                    vm = vm,
                )
                Spacer(modifier = Modifier.height(APP_HORIZONTAL_DP))
            }
        }
    }

    var today by rememberSaveable() { mutableStateOf(DateTimeManager.getToday()) }
    val pagerState = rememberPagerState(pageCount = { titles.size })
    var searchText by rememberSaveable() { mutableStateOf("") }
    var showSearch by rememberSaveable() { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        if(!isNextOpen()) {
            //重置
            saveInt("FIRST",0)
        }
        if(!isLogin) {
            onListenStateHolder(vm.bizTypeIdResponse) { data ->
                // 检测是否教务token还有效
                ifSaved = data == -1
            }
        }
        // 等待加载完毕可切换标签
        if(isLogin) {
            if(!GlobalUIStateHolder.webVpn) ifSaved = false
        }
    }
    // 保存上一页页码 用于决定左右动画
    if(currentAnimationIndex == 2) {
        LaunchedEffect(targetPage) {
            currentPage = targetPage.page
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var isNavigationIconVisible by rememberSaveable { mutableStateOf(true) }
    // 监听滚动状态
    if(targetPage == FOCUS) {
        LaunchedEffect(scrollBehavior.state) {
            snapshotFlow { scrollBehavior.state.collapsedFraction }
                .collect { collapsedFraction -> isNavigationIconVisible = collapsedFraction < 0.5f }
        }
    }

    val backGroundSource = rememberShaderState()
    var firstStart by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        firstStart = true
    }
    val customBackground by DataStoreManager.customBackground.collectAsState(initial = "")
    val useCustomBackground = customBackground != ""
    val context = LocalContext.current
    var zhiJianStudentId by rememberSaveable { mutableStateOf(getPersonInfo().studentId ?: "") }

    // 捏合手势
    val scaleFactor = rememberSaveable { mutableFloatStateOf(1f) } // 捏合手势缩放因子

    val count by produceState(initialValue = 0) {
        value = calculatedReadNotificationCount()
    }

    CustomTransitionScaffold (
        navHostController = navHostTopController,
        route = AppNavRoute.Home.route,
        roundShape = RoundedCornerShape(0.dp),
        modifier = Modifier.let {
            if (targetPage != COURSES) {
                it.nestedScroll(scrollBehavior.nestedScrollConnection)
            } else {
                it
            }
        },
        floatingActionButton = {
            val addRoute = remember { AppNavRoute.AddEvent.withArgs(origin = AddEventOrigin.FOCUS_ADD.name) }
            AnimatedVisibility(
                enter = scaleIn(),
                exit = scaleOut(),
                visible = isNavigationIconVisible && (targetPage == FOCUS)
            ) {
                FloatingActionButton(
                    modifier = Modifier
                        .containerShare(
                            addRoute,
                            FloatingActionButtonDefaults.shape
                        ),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
                    onClick = {
                        navHostTopController.navigateForTransition(
                            AppNavRoute.AddEvent,
                            addRoute
                        )
                    },
                ) {
                    Icon(
                        painterResource(AppNavRoute.AddEvent.icon),
                        "Add Button",
                        modifier = Modifier.iconElementShare(addRoute)
                    )
                }
            }
        },
        topBar = {
            Column(
                modifier = Modifier.let {
                    if(targetPage == COURSES && useCustomBackground) {
                       it
                    } else {
                        it.topBarBlur(
                            hazeState,
                            backgroundColor = if(targetPage == SETTINGS) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.surface
                        )
                    }
                }
            ) {
                if (targetPage != COURSES) {
                    MediumTopAppBar(
                        colors = topBarTransplantColor(),
                        navigationIcon = {
                            if (targetPage == FOCUS && isNavigationIconVisible && celebrationText != null) {
                                Box(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP - 7.dp)) {
                                    ScrollText(
                                        text = celebrationText,
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                }
                            }
                        },
                        title = { Text(topBarText(targetPage,context)) },
                        actions = {
                            when (targetPage) {
                                SEARCH -> {
                                    val route = remember { AppNavRoute.FunctionsSort.route }
                                    IconButton(onClick = {
                                        navHostTopController.navigateForTransition(
                                            AppNavRoute.FunctionsSort,
                                            route,
                                            transplantBackground = true
                                        )
                                    }) {
                                        Icon(
                                            painterResource(id = R.drawable.edit),
                                            contentDescription = "",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.iconElementShare(route = route)
                                        )
                                    }
                                    IconButton(onClick = { showSearch = !showSearch }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.search),
                                            contentDescription = "",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                FOCUS -> {
                                    ApiToSupabase(vm)
                                    val iconRoute = remember { AppNavRoute.NotificationBox.route }
                                    IconButton(onClick = {
                                        navHostTopController.navigateForTransition(AppNavRoute.NotificationBox,iconRoute,transplantBackground = true)
                                    }) {
                                        BadgedBox(badge = {
                                            if (count != 0) {
                                                Badge {
                                                    Text(text = count.toString())
                                                }
                                            }
                                        }) {
                                            Icon(painterResource(id = AppNavRoute.NotificationBox.icon), contentDescription = "", tint = MaterialTheme.colorScheme.primary,modifier = Modifier.iconElementShare(route = iconRoute))
                                        }
                                    }
                                    if (ifSaved) {
                                        IconButton(onClick = { refreshLogin(context) }) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.login),
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.width(7.5.dp))
                                        Text(
                                            text = if (GlobalUIStateHolder.webVpn) "WebVpn" else "已登录",
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(APP_HORIZONTAL_DP))
                                    }
                                }
                                else -> {}
                            }
                        },
                        scrollBehavior = scrollBehavior,
                    )
                    when (targetPage) {
                        FOCUS -> CustomTabRow(pagerState, titles)
                        SEARCH -> {
                            if (showSearch) {
                                SearchFuncs(searchText, onShow = {
                                    searchText = ""
                                    showSearch = it
                                }) {
                                    searchText = it
                                }
                            }
                        }

                        else -> {}
                    }
                } else {
                    if(useCustomBackground) {
                        val customBackgroundAlpha by DataStoreManager.customCalendarSquareAlpha.collectAsState(initial = MyApplication.CALENDAR_SQUARE_ALPHA)
                        val enableLiquidGlass by DataStoreManager.enableLiquidGlass.collectAsState(initial = AppVersion.CAN_SHADER)
                        val iconColor = IconButtonDefaults.iconButtonColors().contentColor
                        TopAppBar(
                            colors = topBarTransplantColor(),
                            navigationIcon = {
                                Surface(
                                    shape = CircleShape,
                                    modifier = Modifier
                                        .padding(horizontal = APP_HORIZONTAL_DP - (if (showAll) 1.75.dp else 2.5.dp) * 3)
                                        .clip(CircleShape)
                                        .glassLayer(
                                            backGroundSource,
                                            smallStyle.copy(
                                                blur = 2.dp,
                                                overlayColor = MaterialTheme.colorScheme.surface.copy(
                                                    customBackgroundAlpha
                                                )
                                            ),
                                            enableLiquidGlass
                                        ),
                                    color = Color.Transparent
                                ) {
                                    Text(topBarText(COURSES,context), modifier = Modifier.padding(vertical = CARD_NORMAL_DP*2, horizontal = CARD_NORMAL_DP*3), fontSize = 20.5.sp)
                                }
                            },
                            title = {

                            },
                            actions = {
                                val isFriend = CourseType.entries.all { swapUI > it.code }
                                if (isFriend) {
                                    val route = AppNavRoute.WorkAndRest.withArgs(swapUI.toString())
                                    Surface(
                                        shape = CircleShape,
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .glassLayer(
                                                backGroundSource,
                                                smallStyle.copy(
                                                    blur = 2.dp,
                                                    overlayColor = MaterialTheme.colorScheme.surface.copy(
                                                        customBackgroundAlpha
                                                    )
                                                ),
                                                enableLiquidGlass
                                            )
                                            .clickable {
                                                navHostTopController.navigateForTransition(
                                                    AppNavRoute.WorkAndRest,
                                                    route,
                                                    transplantBackground = true
                                                )
                                            }
                                        ,
                                        color = Color.Transparent
                                    ) {
                                        Icon(
                                            tint = iconColor,
                                            painter = painterResource(id = AppNavRoute.WorkAndRest.icon),
                                            contentDescription = "",
                                            modifier = Modifier
                                                .padding(CARD_NORMAL_DP * 3)
                                                .iconElementShare(route)
                                        )
                                    }
                                } else {
                                    val route = AppNavRoute.TermCourses.withArgs(ifSaved,COURSES.name)
                                    Surface(
                                        shape = CircleShape,
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .glassLayer(
                                                backGroundSource,
                                                smallStyle.copy(
                                                    blur = 2.dp,
                                                    overlayColor = MaterialTheme.colorScheme.surface.copy(
                                                        customBackgroundAlpha
                                                    )
                                                ),
                                                enableLiquidGlass
                                            )
                                            .clickable {
                                                navHostTopController.navigateForTransition(
                                                    AppNavRoute.TermCourses,
                                                    route,
                                                    transplantBackground = true
                                                )
                                            }
                                        ,
                                        color = Color.Transparent
                                    ) {
                                        Icon(
                                            tint = iconColor,
                                            painter = painterResource(id = R.drawable.category),
                                            contentDescription = "",
                                            modifier = Modifier
                                                .padding(CARD_NORMAL_DP * 3)
                                                .iconElementShare(route)
                                        )
                                    }
                                }
                                Spacer(Modifier.width(BUTTON_PADDING))
                                Surface(
                                    shape = CircleShape,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .glassLayer(
                                            backGroundSource,
                                            smallStyle.copy(
                                                blur = 2.dp,
                                                overlayColor = MaterialTheme.colorScheme.surface.copy(
                                                    customBackgroundAlpha
                                                )
                                            ),
                                            enableLiquidGlass
                                        )
                                        .clickable {
                                            showBottomSheet_multi = true
                                        }
                                    ,
                                    color = Color.Transparent
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.tab_inactive),
                                        contentDescription = "",
                                        tint = iconColor,
                                        modifier = Modifier.padding(CARD_NORMAL_DP*3)
                                    )
                                }
                                Spacer(Modifier.width(BUTTON_PADDING))
                                Surface(
                                    shape = CircleShape,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .glassLayer(
                                            backGroundSource,
                                            smallStyle.copy(
                                                blur = 2.dp,
                                                overlayColor = MaterialTheme.colorScheme.surface.copy(
                                                    customBackgroundAlpha
                                                )
                                            ),
                                            enableLiquidGlass
                                        )
                                        .clickable {
                                            showAll = !showAll
                                        }
                                    ,
                                    color = Color.Transparent
                                ) {
                                    val animatedImageVector = AnimatedImageVector.animatedVectorResource(id = R.drawable.avd1) 
                                    val painter = rememberAnimatedVectorPainter(animatedImageVector, showAll)
                                    Icon(
                                        painter = painter,//painterResource(id = if (showAll) R.drawable.collapse_content else R.drawable.expand_content),
                                        contentDescription = "",
                                        modifier = Modifier.padding(CARD_NORMAL_DP*3),
                                        tint = iconColor,
                                    )
                                }
                                Spacer(Modifier.width(APP_HORIZONTAL_DP-(if (showAll) 1.75.dp else 2.5.dp)*3))
                            },
                        )
                        if(swapUI == CourseType.ZHI_JIAN.code) {
                            ZhiJianSearchBar(backGroundSource,customBackgroundAlpha,enableLiquidGlass,zhiJianStudentId,showAll) {
                                zhiJianStudentId = it
                            }
                        }
                        ScheduleTopDate(showAll, today,backGroundSource)
                    } else {
                        TopAppBar(
                            colors = topBarTransplantColor(),
                            title = {
                                Text(topBarText(COURSES,context))
                            },
                            actions = {
                                val isFriend = CourseType.entries.all { swapUI > it.code }
                                if (isFriend) {
                                    val route = AppNavRoute.WorkAndRest.withArgs(swapUI.toString())
                                    IconButton(
                                        onClick = {
                                            navHostTopController.navigateForTransition(AppNavRoute.WorkAndRest, route,transplantBackground = true)
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(id = AppNavRoute.WorkAndRest.icon),
                                            contentDescription = "",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.iconElementShare(route)
                                        )
                                    }
                                } else {
                                    val route = AppNavRoute.TermCourses.withArgs(ifSaved,COURSES.name)
                                    IconButton(onClick = {
                                        navHostTopController.navigateForTransition(AppNavRoute.TermCourses, route,transplantBackground = true)
                                    }) {
                                        Icon(
                                            painter = painterResource(id = AppNavRoute.TermCourses.icon),
                                            contentDescription = "",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.iconElementShare(route)
                                        )
                                    }
                                }

                                IconButton(onClick = {
                                    showBottomSheet_multi = true
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.tab_inactive),
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.primary,
                                    )
                                }
                                // 动画写在内部
                                AnimatedIconButton(
                                    valueState = showAll,
                                    onClick = { showAll = !showAll }
                                )
                            },
                        )
                        if(swapUI == CourseType.ZHI_JIAN.code) {
                            ZhiJianSearchBar(null,1f,false,zhiJianStudentId,showAll) {
                                zhiJianStudentId = it
                            }
                        }
                        ScheduleTopDate(showAll, today)
                    }
                }
            }
        },
        bottomBar = {
            val items = listOf(
                NavigationBarItemDataDynamic(
                    COURSES.name,
                    "课程表",
                    icon = { selected -> NavigationBarItemDynamicIcon(
                        selected,
                        R.drawable.calendar,
                        R.drawable.calendar_month_filled
                    ) },
                ),
                NavigationBarItemDataDynamic(
                    FOCUS.name,
                    "聚焦",
                    icon = { selected -> NavigationBarItemDynamicIcon(
                        selected,
                        R.drawable.lightbulb,
                        R.drawable.lightbulb_filled
                    ) },
                ),
                NavigationBarItemDataDynamic(
                    SEARCH.name,
                    "查询中心",
                    icon = { selected -> NavigationBarItemDynamicIcon(
                        selected,
                        R.drawable.category_search,
                        R.drawable.category_search_filled
                    ) },
                ),
                NavigationBarItemDataDynamic(
                    SETTINGS.name,
                    "选项",
                    icon = { selected -> NavigationBarItemDynamicIcon(
                        selected,
                        if (!showBadge) R.drawable.deployed_code else R.drawable.deployed_code_update,
                        if (!showBadge) R.drawable.deployed_code_filled else R.drawable.deployed_code_update_filled
                    ) },
                    badge = {
                        if (showBadge) Badge { Text("1") }
                    }
                )
            )
            if(useCustomBackground && targetPage == COURSES) {
                SpecialBottomBar(backGroundSource,items,navController,isEnabled)
            } else {
                HazeBottomBarDynamic(hazeState,items,navController,isEnabled)
            }
        },
    ) { innerPadding ->
        val animation = AppAnimationManager.getAnimationType(currentAnimationIndex, targetPage.page)

        NavHost(
            navController = navController,
            startDestination = first.name,
            enterTransition = { animation.enter },
            exitTransition = { animation.exit },
            modifier = Modifier.hazeSource(state = hazeState)
        ) {
            composable(COURSES.name) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // 背景图层
                    if (useCustomBackground) {
                        GlideImage(
                            model = File(customBackground),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .shaderSource(backGroundSource)
                                .fillMaxSize()
                        )
                    }

                    Scaffold(
                        containerColor = if (!useCustomBackground) {
                            MaterialTheme.colorScheme.background
                        } else {
                            Color.Transparent
                        },

                        modifier = Modifier
                            .pointerInput(Unit) {
                                detectTransformGestures { _, pan, zoom, _ ->
                                    scaleFactor.floatValue *= zoom
                                }
                            }

//                    modifier = Modifier.pointerInput(Unit) {
//                            detectTransformGestures { _, _, zoom, _ ->
//                                when {
//                                    zoom > 1f -> showAll = false
//                                    zoom < 1f -> showAll = true
//                                }
//                            }
//                        }
                    ) {
                        val isFriend = CourseType.entries.all { swapUI > it.code }
                        if (!isFriend) {
                            // 非好友课表
                            when (swapUI) {
                                // 下学期
//                                CourseType.NEXT.code -> JxglstuCourseTableUINext(
//                                    showAll,
//                                    vm,
//                                    hazeState,
//                                    navHostTopController,
//                                    innerPadding,
//                                    backGroundHaze = if (useCustomBackground) backGroundSource else null,
//                                    { showAll = it },
//                                )
                                // 社区
                                CourseType.COMMUNITY.code -> CommunityCourseTableUI(
                                    scaleFactor.floatValue,
                                    showAll,
                                    innerPadding,
                                    onDateChange = { new -> today = new },
                                    today = today,
                                    hazeState = hazeState,
                                    backGroundHaze = if (useCustomBackground) backGroundSource else null,
                                    onSwapShowAll = { showAll = it },
                                    navController = navHostTopController,
                                    onRestoreHeight = { smoothToOne(scaleFactor) }
                                )
                                // 合工大教务
                                CourseType.UNI_APP.code -> UniAppCoursesScreen(
                                    scaleFactor.floatValue,
                                    showAll,
                                    innerPadding,
                                    { newDate ->
//                                        LogUtil.info("newDate = " +newDate.format(DateTimeManager.formatter_YYYY_MM_DD))
//                                        LogUtil.info("today = " + today.format(DateTimeManager.formatter_YYYY_MM_DD))
                                        today = newDate
                                    },
                                    today,
                                    hazeState,
                                    navHostTopController,
                                    if (useCustomBackground) backGroundSource else null,
                                    { showAll = it },
                                    { smoothToOne(scaleFactor) }
                                )
                                // 教务
                                CourseType.JXGLSTU.code -> JxglstuCourseTableUI(
                                    scaleFactor.floatValue,
                                    showAll,
                                    vm,
                                    innerPadding,
                                    if (isLogin) GlobalUIStateHolder.webVpn else false,
                                    isLogin,
                                    { newDate -> today = newDate },
                                    today,
                                    hazeState,
                                    navHostTopController,
                                    if (useCustomBackground) backGroundSource else null,
                                    isEnabled,
                                    { isEnabled = it },
                                    { showAll = it },
                                    { smoothToOne(scaleFactor) }
                                )
//                                // 教务2
                                CourseType.JXGLSTU2.code -> JxglstuCourseTableTwo(
                                    showAll,
                                    vm,
                                    hazeState,
                                    innerPadding,
                                    TotalCourseDataSource.MINE,
                                    onDateChange = { new -> today = new },
                                    today = today,
                                    backGroundHaze = if (useCustomBackground) backGroundSource else null,
                                    { showAll = it },
                                )
                                // 指尖工大
                                CourseType.ZHI_JIAN.code -> ZhiJianCourseTableUI(
                                    showAll,
                                    vm,
                                    innerPadding,
                                    zhiJianStudentId,
                                    today = today,
                                    onDateChange = { new -> today = new },
                                    backGroundHaze = if (useCustomBackground) backGroundSource else null,
                                    hazeState,
                                    { showAll = it }
                                )
                                // 自定义导入课表 数据库id+3=swapUI
//                                else -> CustomSchedules(showAll,innerPadding,vmUI,swapUI-4,{newDate-> today = newDate}, today)
                            }
                        } else // 好友课表 swapUI为学号
                            CommunityCourseTableUI(
                                scaleFactor.floatValue,
                                showAll,
                                innerPadding,
                                friendUserName = swapUI.toString(),
                                onDateChange = { new -> today = new },
                                today = today,
                                hazeState,
                                backGroundHaze = if (useCustomBackground) backGroundSource else null,
                                onSwapShowAll = { showAll = it },
                                navController = navHostTopController,
                                onRestoreHeight = { smoothToOne(scaleFactor) }
                            )
                    }
                }
            }
            composable(FOCUS.name) {
                Scaffold {
                    TodayScreen(
                        vm,
                        innerPadding,
                        vmUI,
                        ifSaved,
                        pagerState,
                        hazeState = hazeState,
                        navHostTopController,
                    )
                }
            }
            composable(SEARCH.name) {
                Scaffold {
                    SearchScreen(
                        vm,
                        ifSaved,
                        innerPadding,
                        vmUI,
                        searchText,
                        navController = navHostTopController,
                        hazeState = hazeState,
                    )
                }
            }
            composable(SETTINGS.name) {
                Scaffold {
                    SettingsScreen(
                        vm,
                        ifSaved,
                        innerPadding,
                        hazeState,
                        navHostTopController,
                    )
                }
            }
        }
    }
}


fun topBarText(num : BottomBarItems,context: Context) : String = when(num) {
    SEARCH -> MyApplication.context.getString(R.string.functions_center_title)
    SETTINGS -> context.getString(R.string.settings_title)
    else -> {
        val chineseNumber  =
//            "周${numToChinese(DateTimeManager.dayWeek)}"
        if(LanguageHelper.isChineseLanguage(context)) {
            "周${numToChinese(DateTimeManager.dayWeek)}"
        } else {
            when(DateTimeManager.dayWeek) {
                1 -> "Mon."
                2 -> "Tue."
                3 -> "Wed."
                4 -> "Thur."
                5 -> "Fri."
                6 -> "Sat."
                0,7 -> "Sun."
                else -> ""
            }
        }
        context.getString(R.string.focus_and_calendar_title, DateTimeManager.Date_MM_dd, DateTimeManager.currentWeek, chineseNumber)
    }
}

fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    if (index1 in indices && index2 in indices) {
        val tmp = this[index1]
        this[index1] = this[index2]
        this[index2] = tmp
    }
}


// 按 List<Int> 排序，并把未出现的新元素追加到末尾
private fun MutableList<SearchAppBeanLite>.reorderByIds(idOrder: List<Int>): MutableList<SearchAppBeanLite> {
    val map = this.associateBy { it.id }

    // 按顺序取出原有元素
    val sorted = idOrder.mapNotNull { map[it] }.toMutableList()

    // 追加未在 idOrder 中的新元素
    val remaining = this.filter { it.id !in idOrder }
    sorted.addAll(remaining)

    this.clear()
    this.addAll(sorted)
    return this
}

// 按字符串排序
fun MutableList<SearchAppBeanLite>.reorderByIdsStr(idOrder: String): MutableList<SearchAppBeanLite> {
    return try {
        val order = idOrder.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
        reorderByIds(order)
    } catch (e: Exception) {
        LogUtil.error(e)
        reorderByIds(GlobalUIStateHolder.funcDefault.map { it.id })
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchEditScreen(
    navController : NavHostController,
) {
    val searchSort by DataStoreManager.searchSort.collectAsState(initial = SEARCH_DEFAULT_STR)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val route = remember { AppNavRoute.FunctionsSort.route }
    val funcMaps by produceState(initialValue = GlobalUIStateHolder.funcMaps, key1 = searchSort, key2 = GlobalUIStateHolder.funcMaps) {
        if(searchSort.isNotEmpty() && searchSort.isNotBlank()) {
            value = GlobalUIStateHolder.funcMaps.reorderByIdsStr(searchSort) as SnapshotStateList<SearchAppBeanLite>
        }
    }
    val scope = rememberCoroutineScope()
    var inEdit by remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current
    val state = rememberLazyGridState()
    val reorderableLazyGridState = rememberReorderableLazyGridState(state,) { from, to ->
        // 交换
        funcMaps.swap(from.index, to.index)
        // 保存
        DataStoreManager.saveSearchSort(funcMaps.map { it.id })
        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
    }
    val transition = rememberInfiniteTransition(label = "shake")

    // 在 -3° 到 3° 之间来回旋转
    val rotation by transition.animateFloat(
        initialValue = -.75f,
        targetValue = .75f,
        animationSpec = infiniteRepeatable(
            animation = tween(100, easing = LinearEasing), // 越短越快
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    var showDialog by remember { mutableStateOf(false) }
    if(showDialog) {
        LittleDialog(
            onDismissRequest = { showDialog = false },
            onConfirmation = {
                scope.launch {
                    DataStoreManager.saveSearchSort(GlobalUIStateHolder.funcDefault.map { it.id })
                    showDialog = false
                    showToast("已恢复")
                }
            },
            dialogText = "恢复为初始顺序",
            hazeState = hazeState
        )
    }
    var show by remember { mutableStateOf(false) }
    Column (modifier = Modifier
        .fillMaxSize()
        .hazeSource(hazeState)) {
        MediumTopAppBar(
            modifier = Modifier.let {
                if(show) it
                else {
                    it.onSizeChanged {
                        if(it.height != 0) {
                            show = true
                        }
                    }
                }
            },
            scrollBehavior = scrollBehavior,
            colors = topBarTransplantColor(),
            title = { Text(stringResource(AppNavRoute.FunctionsSort.label)) },
            navigationIcon = {
                TopBarNavigationIcon(route, AppNavRoute.FunctionsSort.icon)
            },
            actions = {
                Row(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                    FilledTonalIconButton(
                        onClick = {
                            showDialog = true
                        }
                    ) {
                        Icon(painterResource(R.drawable.rotate_right),null)
                    }
                    Spacer(Modifier.width(APP_HORIZONTAL_DP/5))
                    if(!inEdit) {
                        FilledTonalButton(
                            onClick = {
                                inEdit = true
                            }
                        ) {
                            Text("编辑")
                        }
                    } else {
                        FilledTonalButton(
                            onClick = {
                                inEdit = false
                            }
                        ) {
                            Text("完成")
                        }
                    }
                }
            }
        )
        ScrollHorizontalTopDivider(state,startPadding = false,endPadding = false)
        AnimatedVisibility(
            visible = show,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LazyVerticalGrid (
                columns = GridCells.Fixed(2),
                state = state,
                modifier = Modifier
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .padding(horizontal = APP_HORIZONTAL_DP - 3.dp),
            ) {
                items(funcMaps.size, key = { funcMaps[it].id }) { index->
                    val item = funcMaps[index]
                    ReorderableItem (reorderableLazyGridState, key = item.id, enabled = inEdit) { isDragging ->
                        val elevation by animateDpAsState(
                            targetValue = if (isDragging) APP_HORIZONTAL_DP else 0.dp,
                        )
                        Surface (
                            shadowElevation = elevation,
                            modifier = Modifier
                                .padding(horizontal = 3.dp, vertical = 3.dp)
                                .let {
                                    if(inEdit) {
                                        if(isDragging) it else it.graphicsLayer { rotationZ = rotation }
                                    } else {
                                        it
                                    }
                                },
                            color = cardNormalColor(),
                            shape = MaterialTheme.shapes.small
                        ) {
                            TransplantListItem(
                                headlineContent = { ScrollText(stringResource(item.name)) },
                                leadingContent = {
                                    Icon(painterResource(item.icon),null)
                                },
                                modifier = Modifier
                                    .combinedClickable(
                                        onClick = {
                                            if (!inEdit) {
                                                showToast("长按卡片开始编辑")
                                            } else {
                                                showToast("双击卡片结束编辑")
                                            }
                                        },
                                        onDoubleClick = {
                                            inEdit = false
                                        },
                                        onLongClick = {
                                            inEdit = true
                                        }
                                    )
                                    .longPressDraggableHandle(
                                        enabled = inEdit,
                                        onDragStarted = {
                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                                        },
                                        onDragStopped = {
                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                        },
                                    )
                            )
                        }
                    }
                }
                items(2) { Spacer(Modifier
                    .navigationBarsPadding()
                    .height(APP_HORIZONTAL_DP)) }
            }
        }

    }
}

@Composable
private fun ZhiJianSearchBar(
    shaderState: ShaderState? = null,
    customBackgroundAlpha : Float,
    enableLiquidGlass : Boolean,
    input : String,
    showAll : Boolean,
    onValueChange : (String) -> Unit,
) {
    var showDelDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showSelectDialog by remember { mutableStateOf(false) }
    val savedList by produceState(initialValue = emptyList(), key1 = showAddDialog, key2 = showDelDialog) {
        value = withContext(Dispatchers.IO) {
            DataBaseManager.friendDao.getAll()
        }
    }
    var delId by remember { mutableIntStateOf(-1) }
    val scope = rememberCoroutineScope()
    val savedData = savedList.find { it.studentId == input }
    val saved = savedData != null

    if(showDelDialog && delId > 0) {
        LittleDialog(
            onDismissRequest = { showDelDialog = false },
            dialogText = "是否删除",
            onConfirmation = {
                scope.launch {
                    val result = DataBaseManager.friendDao.del(delId)
                    if(result <= 0) {
                        showToast("执行失败")
                    } else {
                        showToast("执行成功")
                    }
                    showDelDialog = false
                }
            }
        )
    }
    if(showAddDialog) {
        Dialog(
            onDismissRequest = { showAddDialog = false }
        ) {
            var inputName by remember { mutableStateOf("") }
            var inputRemark by remember { mutableStateOf("") }
            Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)) {
                Column(modifier = Modifier.padding(vertical = APP_HORIZONTAL_DP)) {
                    CustomTextField(
                        input = inputName,
                        label = { Text("姓名") },
                        singleLine = true
                    ) { inputName = it }
                    Spacer(Modifier.height(APP_HORIZONTAL_DP))

                    CustomTextField(
                        input = inputRemark,
                        label = { Text("备注(可填专业或班级等)") },
                        singleLine = false,
                    ) { inputRemark = it }

                    Spacer(Modifier.height(APP_HORIZONTAL_DP/2))

                    RowHorizontal(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                        FilledTonalButton (
                            onClick = {
                                showAddDialog = false
                            },
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(.5f)
                        ) {
                            Text("取消")
                        }
                        Spacer(Modifier.width(APP_HORIZONTAL_DP/2))

                        Button(
                            onClick = {
                                scope.launch {
                                    if(inputName.isEmpty() || inputName.isBlank()) {
                                        showToast("空名")
                                        return@launch
                                    }
                                    if(input.length != 10) {
                                        showToast("学号长度不规范(10位)")
                                        return@launch
                                    }
                                    val result = DataBaseManager.friendDao.insert(FriendEntity(
                                        name = inputName,
                                        studentId = input,
                                        major = inputRemark
                                    ))
                                    if(result <= 0) {
                                        showToast("执行失败")
                                    } else {
                                        showToast("执行成功")
                                    }
                                    showAddDialog = false
                                }
                            },
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(.5f)
                        ) {
                            Text("保存")
                        }
                    }
                }
            }
        }
    }
    if(showSelectDialog) {
        Dialog(
            onDismissRequest = { showSelectDialog = false }
        ) {
            Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)) {
                Column(modifier = Modifier.padding(vertical = APP_HORIZONTAL_DP)) {
                    LazyColumn {
                        items(savedList.size,key = { savedList[it].id }) { index ->
                            val item = savedList[index]
                            TransplantListItem(
                                headlineContent = {
                                    Text(item.name)
                                },
                                supportingContent = {
                                    item.major?.let { Text(it) }
                                },
                                leadingContent = {
                                    Icon(painterResource(
                                        if(input == item.studentId)
                                            R.drawable.check
                                        else R.drawable.person
                                    ),null)
                                },
                                overlineContent = {
                                    Text(item.studentId)
                                },
                                trailingContent = {
                                    FilledTonalIconButton(
                                        onClick = {
                                            delId = item.id
                                            showDelDialog = true
                                        }
                                    ) {
                                        Icon(painterResource(R.drawable.delete),null)
                                    }
                                },
                                modifier = Modifier.clickable {
                                    onValueChange(item.studentId)
                                    showSelectDialog = false
                                }
                            )
                                PaddingHorizontalDivider()
                        }
                        item {
                            Spacer(Modifier.height(10.dp))
                            BottomTip("新增请先将学号输入到输入框后再点击右侧保存")
                        }
                    }
                }
            }
        }
    }


    Column {
        Row(modifier = Modifier.padding(horizontal =
            APP_HORIZONTAL_DP - (
//                    if(shaderState == null) {
//                        (if (showAll) 1.75.dp else 2.5.dp)*3
//                    } else {
                        if (showAll) 1.75.dp else 2.5.dp
//                    }
            )
        )) {
            TextField(
                modifier = Modifier
                    .let {
                        shaderState?.let { state ->
                            it
                                .clip(MaterialTheme.shapes.medium)
                                .glassLayer(
                                    state,
                                    largeStyle.copy(
                                        blur = 2.5.dp,
                                        overlayColor = MaterialTheme.colorScheme.surface.copy(
                                            customBackgroundAlpha
                                        )
                                    ),
                                    enableLiquidGlass
                                )
                        } ?: it
                    }
                    .weight(1f),
                value = input,
                onValueChange = onValueChange,
                leadingIcon = {
                    if(savedList.isEmpty()) {
                        Icon(painterResource(R.drawable.person),null)
                    } else {
                        IconButton(
                            onClick = {
                                showSelectDialog = true
                            }
                        ) {
                            Icon(painterResource(R.drawable.swap_vert),null)
                        }
                    }
                },
                trailingIcon = {
                    if(saved) {
                        IconButton(
                            onClick = {
                                delId = savedData.id
                                showDelDialog = true
                            }
                        ) {
                            Icon(painterResource(R.drawable.delete),null)
                        }
                    } else {
                        IconButton(
                            onClick = {
                                showAddDialog = true
                            },
                            enabled = input.length == 10
                        ) {
                            Icon(painterResource(R.drawable.save),null)
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = textFiledTransplant()
            )
        }
        Spacer(Modifier.height(CARD_NORMAL_DP*2))
    }
}
