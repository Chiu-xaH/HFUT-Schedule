package com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer


import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.AnimationCardListItem
import com.hfut.schedule.ui.component.container.AnimationCustomCard
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.mixedCardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.icon.DepartmentIcons
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.screen.RefreshIndicator
import com.hfut.schedule.ui.component.status.StatusUI2
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.screen.home.search.function.other.life.countFunc
import com.hfut.schedule.ui.style.color.textFiledTransplant
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.component.containerShare
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalSharedTransitionScope
import com.xah.uicommon.component.text.ScrollText
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.align.ColumnVertical
import com.xah.uicommon.style.color.topBarTransplantColor
import com.xah.uicommon.style.padding.InnerPaddingHeight
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)
@Composable
fun TransferScreen(
    vm : NetWorkViewModel,
    navController : NavHostController,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val route = remember { AppNavRoute.Transfer.route }
    val scope = rememberCoroutineScope()
    val uiState by vm.transferListData.state.collectAsState()
    val refreshing = uiState is UiState.Loading

    val refreshNetwork : suspend(Boolean) -> Unit =  m@ { skip : Boolean ->
        if(skip && uiState is UiState.Success) {
            return@m
        }
        val cookie = getJxglstuCookie()
        cookie?.let {
            vm.transferListData.clear()
            vm.getTransferList(it)
        }
    }


    val pullRefreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        scope.launch {
            refreshNetwork(false)
        }
    })
        CustomTransitionScaffold (
            route = route,
            
            navHostController = navController,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.topBarBlur(hazeState),
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.Transfer.label) },
                    navigationIcon = {
                        TopBarNavigationIcon(navController,route, AppNavRoute.Transfer.icon)
                    }
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .hazeSource(hazeState)
                    .fillMaxSize()
            ) {
                var input by remember { mutableStateOf("") }
                LaunchedEffect(Unit) {
                    refreshNetwork(true)
                }

                Box(modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState)) {
                    RefreshIndicator(
                        refreshing,
                        pullRefreshState,
                        Modifier
                            .align(Alignment.TopCenter)
                            .zIndex(1f)
                            .padding(innerPadding)
                    )
                    CommonNetworkScreen(uiState, onReload = { refreshNetwork(false) }) {
                        val transferList = (uiState as UiState.Success).data
                        LazyColumn {
                            item { InnerPaddingHeight(innerPadding,true) }
                            items(transferList.size, key = { it }) { index ->
                                val data = transferList[index]
                                val batchId = data.batchId
                                val name = data.title
                                val route = AppNavRoute.TransferDetail.withArgs(false,batchId,name)
                                var expand by remember { mutableStateOf(false) }
                                CustomCard (
                                    modifier = Modifier
                                        .clickable {
                                            navController.navigateForTransition(
                                                AppNavRoute.TransferDetail,
                                                route
                                            )
                                        }
                                        .containerShare(route),
                                    color = mixedCardNormalColor()
                                ) {
                                    TransplantListItem(
                                        headlineContent = { Text(text = name) },
                                        trailingContent = {
                                            ColumnVertical {
                                                FilledTonalIconButton(onClick = { expand = !expand }) {
                                                    Icon(painter = painterResource(id = if(expand) R.drawable.collapse_content else R.drawable.expand_content), contentDescription = "")
                                                }
                                                Text("代号 $batchId")
                                            }
                                        },
                                    )
                                    androidx.compose.animation.AnimatedVisibility(
                                        visible = expand,
                                        enter = slideInVertically(
                                            initialOffsetY = { -40 }
                                        ) + expandVertically(
                                            expandFrom = Alignment.Top
                                        ) + scaleIn(
                                            transformOrigin = TransformOrigin(0.5f, 0f)
                                        ) + fadeIn(initialAlpha = 0.3f),
                                        exit = slideOutVertically() + shrinkVertically() + fadeOut() + scaleOut(targetScale = 1.2f)
                                    ) {
                                        Column {
                                            PaddingHorizontalDivider()
                                            TransplantListItem(
                                                headlineContent = { Text(text = "申请日期") },
                                                supportingContent = {
                                                    Text(text = data.applicationDate )
                                                }
                                            )
                                            TransplantListItem(
                                                headlineContent = { Text(text = "转专业时期") },
                                                supportingContent = {
                                                    Text(data.admissionDate)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            item {
                                AnimationCardListItem(
                                    index = transferList.size,
                                    headlineContent = {
                                        Text("手动输入代号查看被隐藏掉的转专业入口")
                                    },
                                    supportingContent = {
                                        Column {
                                            Text("合肥校区和宣城校区之间转专业入口互相不可见，但可以通过输入代号进入，代号位于右上角\n示例：1,3,21,42,43,61,101,81等...")
                                            Spacer(Modifier.height(APP_HORIZONTAL_DP/2))
                                            Row {
                                                TextField(
                                                    modifier = Modifier
                                                        .weight(1f),
                                                    value = input,
                                                    onValueChange = { input = it },
                                                    label = { Text("输入数字代号") },
                                                    trailingIcon = {
                                                        IconButton(onClick = {
                                                            if(input.toIntOrNull() != null) {
                                                                navController.navigateForTransition(AppNavRoute.TransferDetail,AppNavRoute.TransferDetail.withArgs(true,input,"入口$input"))
                                                            } else {
                                                                showToast("必须为数字")
                                                            }
                                                        }) {
                                                            Icon(Icons.Default.ArrowForward,null)
                                                        }
                                                    },
                                                    shape = MaterialTheme.shapes.medium,
                                                    colors = textFiledTransplant(),
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                            item { InnerPaddingHeight(innerPadding,false) }
                        }
                    }
                }
            }
        }
//    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TransferDetailScreen(
    isHidden : Boolean,
    batchId: String,
    title : String,
    vm : NetWorkViewModel,
    navController : NavHostController,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val route = remember { AppNavRoute.TransferDetail.withArgs(isHidden,batchId,title) }
    var showBottomSheet_apply by remember { mutableStateOf(false) }

    if (showBottomSheet_apply) {
        HazeBottomSheet(
            onDismissRequest = { showBottomSheet_apply = false },
            hazeState = hazeState,
            isFullExpand = false,
            showBottomSheet = showBottomSheet_apply
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("我的申请", isPaddingStatusBar = false)
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    MyApplyListUI(vm,batchId,hazeState)
                }
            }
        }
    }
    var input by remember { mutableStateOf("") }

        CustomTransitionScaffold (
            route = route,
            
            navHostController = navController,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                Column (
                    modifier = Modifier.topBarBlur(hazeState),

                ){
                    MediumTopAppBar(
                        scrollBehavior = scrollBehavior,
                        colors = topBarTransplantColor(),
                        title = { Text(title) },
                        navigationIcon = {
                            TopBarNavigationIcon(
                                navController,
                                route,
                                AppNavRoute.TransferDetail.icon
                            )
                        },
                        actions = {
                            Row(modifier = Modifier.padding(end = APP_HORIZONTAL_DP)) {
                                FilledTonalButton(
                                    onClick = { showBottomSheet_apply = true },
                                ) {
                                    Text("我的申请")
                                }
                            }
                        }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextField(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = APP_HORIZONTAL_DP),
                            value = input,
                            onValueChange = {
                                input = it
                            },
                            label = { Text("搜索学院或专业") },
                            singleLine = true,
                            trailingIcon = {
                                IconButton(
                                    onClick = {}) {
                                    Icon(
                                        painter = painterResource(R.drawable.search),
                                        contentDescription = "description"
                                    )
                                }
                            },
                            shape = MaterialTheme.shapes.medium,
                            colors = textFiledTransplant(),
                        )
                    }
                    Spacer(Modifier.height(CARD_NORMAL_DP))
                }
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .hazeSource(hazeState)
                    .fillMaxSize()
            ) {
                TransferUI(vm,batchId,hazeState,isHidden,input,innerPadding)
            }
        }
//    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransferUI(
    vm: NetWorkViewModel,
    batchId: String,
    hazeState: HazeState,
    isHidden : Boolean = false,
    input : String,
    innerPadding: PaddingValues
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var showBottomSheet_select by remember { mutableStateOf(false) }
    var telephone by remember { mutableStateOf("") }

    var id by remember { mutableIntStateOf(0) }
    val uiState by vm.transferData.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = {
        val cookie = getJxglstuCookie()
        cookie?.let {
            vm.transferData.clear()
            vm.getTransfer(it,batchId)
        }
    }

    LaunchedEffect(batchId) {
        refreshNetwork()
    }

    if(showBottomSheet) {
        countFunc = 0
        HazeBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet,
            isFullExpand = false
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("结果", isPaddingStatusBar = false)
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    TransferStatusUI(vm,batchId,id,telephone)
                }
            }
        }
    }

    if(showBottomSheet_select) {
        HazeBottomSheet (
            onDismissRequest = {
                showBottomSheet_select = false
            },
            hazeState = hazeState,
            isFullExpand = false,
            showBottomSheet = showBottomSheet_select
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("手机号 (教务要求)", isPaddingStatusBar = false)
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ) {
                    val personInfo = getPersonInfo()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextField(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = APP_HORIZONTAL_DP),
                            value = telephone,
                            onValueChange = {
                                telephone = it
                            },
                            label = { Text("自行输入手机号" ) },
                            singleLine = true,
                            trailingIcon = {
                               IconButton(
                                   onClick = {
                                       showBottomSheet_select = false
                                       showBottomSheet = true
                                   }
                               ) {
                                   Icon(Icons.Filled.Check, null)
                               }
                            },
                            shape = MaterialTheme.shapes.medium,
                            colors = textFiledTransplant(),
                        )
                    }
                    Spacer(Modifier.height(5.dp))
                    personInfo.mobile?.let {
                        if(it.isNotEmpty()) {
                            CardListItem(
                                    headlineContent = { Text(it) },
                                    overlineContent = { Text("教务系统预留手机号") },
                                    modifier = Modifier.clickable {
                                        telephone = it
                                        showBottomSheet_select = false
                                        showBottomSheet = true
                                    }
                                )
                        }
                    }
                    personInfo.phone?.let {
                        if(it.isNotEmpty()) {
                                CardListItem(
                                    headlineContent = { Text(it) },
                                    overlineContent = { Text("教务系统预留电话号") },
                                    modifier = Modifier.clickable {
                                        telephone = it
                                        showBottomSheet_select = false
                                        showBottomSheet = true
                                    }
                                )
                        }
                    }
                    prefs.getString("PHONENUM","")?.let {
                        if(it.isNotEmpty()) {
                                CardListItem(
                                    headlineContent = { Text(it) },
                                    overlineContent = { Text("呱呱物联登录手机号") },
                                    modifier = Modifier.clickable {
                                        telephone = it
                                        showBottomSheet_select = false
                                        showBottomSheet = true
                                    }
                                )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        val response = (uiState as UiState.Success).data
        val list = response.data.let {
            if(input.isEmpty() || input.isBlank()) {
                it
            } else {
                it.filter { item ->
                    item.department.nameZh.contains(input) || item.major.nameZh.contains(input)
                }
            }
        }

        LazyColumn() {
            item { InnerPaddingHeight(innerPadding,true) }
            if(response.data.isNotEmpty()) {
                item {
                    val item = list[0].changeMajorBatch ?: return@item
                    AnimationCardListItem(
                        index = 0,
                        headlineContent = {
                            Text(if(isHidden) item.nameZh else "申请提示")
                        },
                        trailingContent = {
                            Text("代号 $batchId")
                        },
                        supportingContent = {
                            item.bulletin?.let {
                                Text(it)
                            }
                        },
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        overlineContent = { Text(item.applyStartTime.replace("T"," ").substringBefore(".") + " ~ " + item.applyEndTime.replace("T"," ").substringBefore(".") ) },
                    )
                }
            }
            items(list.size, key = { it }) {item ->
                val dataItem = list[item]
                var department = dataItem.department.nameZh
                if(department.contains("（")) department = department.substringBefore("（")
                if(department.contains("(")) department = department.substringBefore("(")
                val count = dataItem.applyStdCount
                val limit = dataItem.preparedStdCount
                val isFull = count > limit
                AnimationCardListItem(
                    headlineContent = { Text(text = dataItem.major.nameZh, fontWeight = FontWeight.Bold) },
                    supportingContent = { dataItem.registrationConditions?.let { Text(text = it) } },
                    overlineContent = { ScrollText(text = "已申请 $count / $limit $department") },
                    leadingContent = { DepartmentIcons(dataItem.department.nameZh) },
                    trailingContent = {  FilledTonalIconButton(onClick = {
                        id = dataItem.id
                        showBottomSheet_select = true
                    },
                        colors = if(!isFull) IconButtonDefaults.filledTonalIconButtonColors() else IconButtonDefaults.filledTonalIconButtonColors(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                    ) { Icon(painter = painterResource(id = R.drawable.add_2), contentDescription = "") } },
                    color = if(isFull) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        null
                    },
                    index = item+1
                )
            }
            item { InnerPaddingHeight(innerPadding,false) }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun TransferStatusUI(vm : NetWorkViewModel, batchId: String, id: Int, phoneNumber : String) {
    val refreshNetwork : suspend () -> Unit = {
        val cookie = getJxglstuCookie()
        cookie?.let {
            vm.postTransferResponse.clear()
            vm.fromCookie.clear()
            vm.getFormCookie(it,batchId,id.toString())
            val preferCookie = (vm.fromCookie.state.value as? UiState.Success)?.data ?: return@let
            vm.postTransfer("$cookie;$preferCookie",batchId,id.toString(),phoneNumber)
        }
    }
    val uiState by vm.postTransferResponse.state.collectAsState()
    LaunchedEffect(Unit) {
        refreshNetwork()
    }

    CommonNetworkScreen(uiState, isFullScreen = false , onReload = refreshNetwork) {
        val msg = (uiState as UiState.Success).data
        StatusUI2(painter = if(msg == "成功" ) Icons.Filled.Check else Icons.Filled.Close, text = msg)
    }
}
