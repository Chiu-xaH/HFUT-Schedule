package com.hfut.schedule.ui.screen.home.search.function.community.appointment

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showDevelopingToast
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.network.api.model.response.json.community.CommunityPlacesAppointmentTimeFrame
import com.hfut.schedule.ui.component.button.BUTTON_END_PADDING
import com.hfut.schedule.ui.component.button.BUTTON_PADDING
import com.hfut.schedule.ui.component.button.HazeBottomBarV2
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.NoPadding
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.dialog.DatePickerDialog
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.network.DEFAULT_IMAGE_SIZE
import com.hfut.schedule.ui.component.network.UrlImage
import com.hfut.schedule.ui.component.screen.RefreshIndicator
import com.hfut.schedule.ui.component.screen.pager.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.pager.PageController
import com.hfut.schedule.ui.model.NavigationBarItemData
import com.hfut.schedule.ui.nav.destination.AppointmentDestination
import com.hfut.schedule.ui.nav.destination.AppointmentPlaceDestination
import com.hfut.schedule.ui.nav.destination.AppointmentSeatDestination
import com.hfut.schedule.ui.nav.window.PlaceAppointmentOccupiedDetailWindow
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.rememberHazeBlur
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.theme.errorColor
import com.hfut.schedule.ui.theme.greenColor
import com.hfut.schedule.ui.theme.warnColor
import com.hfut.schedule.ui.util.nav2Composable
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.ui.util.navigation.currentRouteWithoutArgs
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.sharednav.common.helper.NoneRoundShape
import com.xah.common.logic.state.NetworkUiState
import com.xah.common.logic.util.EMPTY_STRING
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.container.component.base.sharedContainer
import com.xah.floating.util.LocalFloatingController
import com.xah.navigation.util.LocalNavController
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.launch


private enum class AppointmentBarItems(val page : Int) {
    PLACE(0),SEAT(1)
}

private val items = listOf(
    NavigationBarItemData(
        AppointmentBarItems.PLACE.name,
        "场地预约",
        R.drawable.meeting_room,
        R.drawable.meeting_room_filled,
    ),
    NavigationBarItemData(
        AppointmentBarItems.SEAT.name,
        "座位预约",
        R.drawable.table_restaurant,
        R.drawable.table_restaurant_filled,
    )
)


@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AppointmentScreen(
    vm: NetWorkViewModel
) {
    val hazeState = rememberHazeBlur()
    val backdrop = rememberLayerBackdrop()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val appointmentNavController = rememberNavController()
    val targetPage = when(appointmentNavController.currentRouteWithoutArgs()) {
        AppointmentBarItems.PLACE.name -> AppointmentBarItems.PLACE
        else -> AppointmentBarItems.SEAT
    }

    var date by rememberSaveable { mutableStateOf(DateTimeManager.Date_yyyy_MM_dd) }
    var showSelectDateDialog by remember { mutableStateOf(false) }
    if(showSelectDateDialog) {
        DatePickerDialog(
            onConfirm = {
                date = it
            },
            allowSelectPrevious = false
        ) {
            showSelectDateDialog = false
        }
    }

    var filterAvailable by rememberSaveable { mutableStateOf(false) }

    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState),
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(AppointmentDestination.title.asString()) },
                    navigationIcon = {
                        TopBarNavigationIcon()
                    },
                    actions = {
                        when(targetPage) {
                            AppointmentBarItems.PLACE -> {
                                LiquidButton(
                                    onClick = {
                                        filterAvailable = !filterAvailable
                                    },
                                    isCircle = true,
                                    backdrop = backdrop,
                                    modifier = Modifier.padding(end = BUTTON_PADDING)
                                ) {
                                    Icon(
                                        painterResource(
                                            if(filterAvailable) {
                                                R.drawable.filter_alt_filled
                                            } else {
                                                R.drawable.filter_alt
                                            }
                                        ),
                                        null,
                                        tint = if(filterAvailable) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            LocalContentColor.current
                                        }
                                    )
                                }
                                LiquidButton(
                                    onClick = {
                                        // TODO
                                        showDevelopingToast()
//                                        showSelectDateDialog = true
                                    },
                                    backdrop = backdrop,
                                    modifier = Modifier.padding(end = BUTTON_END_PADDING)
                                ) {
                                    Text(date)
                                }
                            }
                            else -> Unit
                        }
                    }
                )
            }
        },
        bottomBar = {
            HazeBottomBarV2(
                hazeState,
                backdrop,
                items,
                appointmentNavController
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = appointmentNavController,
            startDestination = AppointmentBarItems.PLACE.name,
            enterTransition = {
                AppAnimationManager.centerAnimation.enter
            },
            exitTransition = {
                AppAnimationManager.centerAnimation.exit
            },
            modifier = Modifier
                .hazeSource(state = hazeState)
                .backDropSource(backdrop)
        ) {
            nav2Composable(AppointmentBarItems.PLACE.name) {
                AppointmentPlacesScreen(vm,innerPadding,date,filterAvailable)
            }
            nav2Composable(AppointmentBarItems.SEAT.name) {
                AppointmentSeatsScreen(vm,innerPadding)
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AppointmentSeatsScreen(
    vm : NetWorkViewModel,
    innerPadding : PaddingValues
) {
    var page by rememberSaveable { mutableIntStateOf(1) }

    val navController = LocalNavController.current
    val uiState by vm.communitySeatsAppointmentResp.state.collectAsState()
    val refreshNetwork: suspend(Boolean) -> Unit =  m@ { skip : Boolean ->
        if(skip && uiState is NetworkUiState.Success) {
            return@m
        }
        prefs.getString("TOKEN","")?.let {
            vm.communitySeatsAppointmentResp.clear()
            vm.getCommunitySeatsAppointment(it,page)
        }
    }
    val enableNewBottomBar by DataStoreManager.enableNewBottomBar.collectAsState(initial = false)
    val refreshing = uiState is NetworkUiState.Loading
    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = {
            scope.launch {
                refreshNetwork(false)
            }
        }
    )

    LaunchedEffect(Unit) {
        refreshNetwork(true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        CommonNetworkScreen(uiState, onReload = { refreshNetwork(false) }) {
            val list = (uiState as NetworkUiState.Success).data
            val listState = rememberLazyListState()
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(state = listState) {
                    item { InnerPaddingHeight(innerPadding,true) }
                    items(list.size, key = { list[it].communityId }) { index ->
                        val item = list[index]
                        val title = "${item.campus}-${item.communityName}"
                        val dest = AppointmentSeatDestination(item.communityId,title)
                        CardListItem(
                            headlineContent = {
                                Text(title)
                            },
                            overlineContent = {
                                Text(item.startTime + "~" + item.endTime + " | 座位数: ${item.seatNum}")
                            },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.near_me),
                                    null
                                )
                            },
                            modifier = Modifier.clickable {
                                navController.push(dest)
                            },
                            cardModifier = Modifier.sharedContainer(
                                dest.key,
                                MaterialTheme.shapes.medium,
                                cardNormalColor()
                            ),
                            shape = NoneRoundShape
                        )
                    }
                    item { PaddingForPageControllerButton() }
                    item { InnerPaddingHeight(innerPadding,false) }
                }
                PageController(
                    listState,
                    page,
                    onNextPage = {
                        scope.launch {
                            page = it
                            refreshNetwork(false)
                        }
                    },
                    onPreviousPage = {
                        scope.launch {
                            page = it
                            refreshNetwork(false)
                        }
                    },
                    paddingBottom = false,
                    paddingSafely = enableNewBottomBar,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
        RefreshIndicator(
            refreshing,
            pullRefreshState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(innerPadding)
        )
    }
}

// TODO date
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AppointmentPlacesScreen(
    vm : NetWorkViewModel,
    innerPadding : PaddingValues,
    date : String,
    filterAvailable : Boolean
) {
    var page by rememberSaveable { mutableIntStateOf(1) }

    val navController = LocalNavController.current
    val floatingController = LocalFloatingController.current
    val uiState by vm.communityPlacesAppointmentResp.state.collectAsState()
    val refreshNetwork: suspend(Boolean) -> Unit =  m@ { skip : Boolean ->
        if(skip && uiState is NetworkUiState.Success) {
            return@m
        }
        prefs.getString("TOKEN","")?.let {
            vm.communityPlacesAppointmentResp.clear()
            vm.getCommunityPlacesAppointment(it,date,page)
        }
    }
    val enableNewBottomBar by DataStoreManager.enableNewBottomBar.collectAsState(initial = false)
    val refreshing = uiState is NetworkUiState.Loading
    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = {
            scope.launch {
                refreshNetwork(false)
            }
        }
    )

    LaunchedEffect(Unit) {
        refreshNetwork(true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        CommonNetworkScreen(uiState, onReload = { refreshNetwork(false) }) {
            val list = (uiState as NetworkUiState.Success).data
            val listState = rememberLazyListState()
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(state = listState) {
                    item { InnerPaddingHeight(innerPadding,true) }
                    items(list.size, key = { list[it].id }) { index ->
                        val item = list[index]
                        val timeFrames = item.voList.filter { frame ->
                            if(filterAvailable) {
                                frame.available()
                            } else {
                                true
                            }
                        }
                        if(timeFrames.isEmpty()) {
                            return@items
                        }
                        val title = "${item.campus}-${item.communityName}"
                        val dest = AppointmentPlaceDestination(item)
                        CustomCard(
                            color = cardNormalColor(),
                            modifier = Modifier
                                .sharedContainer(
                                    dest.key,
                                    MaterialTheme.shapes.medium,
                                    cardNormalColor()
                                )
                                .clickable {
                                    navController.push(dest)
                                }
                            ,
                            shape = NoneRoundShape
                        ) {
                            TransplantListItem(
                                headlineContent = {
                                    Text(item.name, fontWeight = FontWeight.Bold)
                                },
                                overlineContent = {
                                    Text(item.startTime + "~" + item.endTime + " | $title")
                                },
                                leadingContent = {
                                    UrlImage(
                                        item.logo ?: EMPTY_STRING,
                                        modifier = Modifier.size(DEFAULT_IMAGE_SIZE),
                                        shape = MaterialTheme.shapes.medium,
                                        contentScale = ContentScale.Fit
                                    )
                                },
                                supportingContent = {
                                    Text(item.address)
                                }
                            )
                            LazyRow(modifier = Modifier.padding(bottom = APP_HORIZONTAL_DP)) {
                                item { Spacer(Modifier.width(APP_HORIZONTAL_DP)) }
                                items(timeFrames.size,key = { it }) { index ->
                                    val frame = timeFrames[index]
                                    val available = frame.available()
                                    val subDest = AppointmentPlaceDestination(item,frame)
                                    val subWindow = frame.appointmentId?.let { PlaceAppointmentOccupiedDetailWindow(it,index) }
                                    NoPadding {
                                        AssistChip(
                                            onClick = {
                                                if(available) {
                                                    // 可预约
                                                    navController.push(subDest)
                                                } else {
                                                    if(frame.appointmentId == null) {
                                                        showToast("该时间段暂不可用")
                                                    } else {
                                                        // 占用详情
                                                        floatingController.push(subWindow!!)
                                                    }
                                                }
                                            },
                                            label = {
                                                Text(frame.timeFrame)
                                            },
                                            shape = NoneRoundShape,
                                            border = null,
                                            colors = AssistChipDefaults.assistChipColors(
                                                containerColor = MaterialTheme.colorScheme.surface,
                                                labelColor = if(available) {
                                                    greenColor()
                                                } else {
                                                    if(frame.appointmentId == null) {
                                                        warnColor()
                                                    } else {
                                                        MaterialTheme.colorScheme.error
                                                    }
                                                }
                                            ),
                                            modifier = Modifier
                                                .padding(end = if (index == timeFrames.size - 1) 0.dp else CARD_NORMAL_DP * 2)
                                                .sharedContainer(
                                                    subWindow?.key ?: subDest.key,
                                                    (AssistChipDefaults.shape as? CornerBasedShape) ?: MaterialTheme.shapes.small,
                                                    MaterialTheme.colorScheme.surface
                                                )
                                        )
                                    }
                                }
                                item { Spacer(Modifier.width(APP_HORIZONTAL_DP)) }
                            }
                        }
                    }
                    item { PaddingForPageControllerButton() }
                    item { InnerPaddingHeight(innerPadding,false) }
                }
                PageController(
                    listState,
                    page,
                    onNextPage = {
                        scope.launch {
                            page = it
                            refreshNetwork(false)
                        }
                    },
                    onPreviousPage = {
                        scope.launch {
                            page = it
                            refreshNetwork(false)
                        }
                    },
                    paddingBottom = false,
                    paddingSafely = enableNewBottomBar,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
        RefreshIndicator(
            refreshing,
            pullRefreshState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(innerPadding)
        )
    }
}

private fun CommunityPlacesAppointmentTimeFrame.available() = this.appointmentFlag == "1"