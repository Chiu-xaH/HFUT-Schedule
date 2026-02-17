package com.hfut.schedule.ui.screen.home.search.function.school.student

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.logic.model.community.getTodayCampusApps
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.button.BUTTON_PADDING
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.StartAppIconButton
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.button.containerBackDrop
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.SmallCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.network.UrlImage
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.style.color.textFiledAllTransplant
import com.hfut.schedule.ui.style.special.backDropSource

import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.mirror.shader.GlassStyle
import com.xah.mirror.shader.glassLayer
import com.xah.mirror.util.ShaderState
import com.xah.mirror.util.rememberShaderState
import com.xah.mirror.util.shaderSource
import com.xah.transition.component.containerShare
import com.xah.transition.component.iconElementShare
import com.xah.uicommon.component.text.ScrollText
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.color.topBarTransplantColor
import com.xah.uicommon.style.padding.InnerPaddingHeight
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ToadyCampus(
    navController : NavHostController,
){
    val route = remember { AppNavRoute.StuTodayCampus.route }

    TransplantListItem(
        headlineContent = { ScrollText(text = stringResource(AppNavRoute.StuTodayCampus.label)) },
        leadingContent = {
            Icon(painterResource(AppNavRoute.StuTodayCampus.icon), contentDescription = null,modifier = Modifier.iconElementShare( route = route))
        },
        modifier = Modifier.clickable {
            navController.navigateForTransition(AppNavRoute.StuTodayCampus,route)
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StuTodayCampusScreen(
    vm: NetWorkViewModel,
    navController : NavHostController,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.StuTodayCampus.route }
    val context = LocalContext.current
    val backDrop = rememberLayerBackdrop()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var input by remember { mutableStateOf("") }

    CustomTransitionScaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        route = route,
        navHostController = navController,
        topBar = {
            Column (
                modifier = Modifier.topBarBlur(hazeState),
            ){
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(stringResource(AppNavRoute.StuTodayCampus.label)) },
                    navigationIcon = {
                        TopBarNavigationIcon(route, AppNavRoute.StuTodayCampus.icon)
                    },
                    actions = {
                        Row(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                            StartAppIconButton(backDrop,Starter.AppPackages.TODAY_CAMPUS)
                            Spacer(Modifier.width(BUTTON_PADDING))
                            LiquidButton(
                                backdrop = backDrop,
                                onClick = {
                                    Starter.startWebUrl(context,MyApplication.STU_URL)
                                },
                            ) {
                                Text("学工系统")
                            }
                        }
                    }
                )
                val s = MaterialTheme.shapes.medium
                CustomTextField(
                    modifier = Modifier
                        .padding(horizontal = APP_HORIZONTAL_DP)
//                        .clip(s)
                        .containerBackDrop(backDrop, MaterialTheme.shapes.medium)
                    ,
//                        ,
                    colors = textFiledAllTransplant(),
                    
                    input = input,
                    label = { Text("检索功能") },
                    leadingIcon = { Icon(painterResource(R.drawable.search),null) },
                ) {
                    input = it
                }
                Spacer(Modifier.height(CARD_NORMAL_DP))
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .backDropSource(backDrop)
//                .shaderSource(shaderState)
                .hazeSource(hazeState)
        ) {
            StuAppsScreen(vm,input,innerPadding,navController)
        }
    }
//    }
}


@Composable
fun StuAppsScreen(
    vm : NetWorkViewModel,
    input : String,
    innerPadding : PaddingValues,
    navController: NavHostController
) {
    val refreshNetwork : suspend () -> Unit = {
        prefs.getString("TOKEN","")?.let {
            vm.stuAppsResponse.clear()
            vm.getStuApps(it)
        }
    }
    val cookie = remember { prefs.getString("stu","") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val size = remember { 30.dp }
    val uiState by vm.stuAppsResponse.state.collectAsState()
    val localList = remember { getTodayCampusApps(context) }
    LaunchedEffect(Unit) {
        if(uiState is UiState.Success) {
            return@LaunchedEffect
        }
        refreshNetwork()
    }
    val l = localList.flatMap { it.apps }.map { it.openUrl.substringAfter("stu.hfut.edu.cn/").substringBefore("?")  }
    val dataCommunity = (uiState as? UiState.Success)?.data?.filter {
        val filteredUrl = it.url?.substringAfter("stu.hfut.edu.cn/")?.substringBefore("?")
        it.name.contains(input) && (filteredUrl !in l)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if(input.isNotEmpty() || input.isNotBlank()) {
            val data = localList.flatMap { it.apps }.filter { it.name.contains(input) }
            LazyVerticalGrid(columns = GridCells.Fixed(2),modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP-3.dp)) {
                item(span = { GridItemSpan(maxLineSpan) }) { InnerPaddingHeight(innerPadding, true) }
                items(data.size, key = { it }) { index ->
                    val item = data[index]
                    with(item) {
                        val route = AppNavRoute.WebView.shareRoute(openUrl)
                        SmallCard(
                            color = cardNormalColor(),
                            modifier = Modifier.padding(horizontal = 3.dp, vertical = 3.dp).containerShare(route)
                        ) {
                            TransplantListItem(
                                leadingContent = {
                                    UrlImage(iconUrl, width = size, height = size)
                                },
                                headlineContent = { ScrollText(name) },
                                modifier = Modifier.clickable {
                                    scope.launch {
                                        Starter.startWebView(navController,openUrl, title = name, cookie =cookie, icon = AppNavRoute.StuTodayCampus.icon)
                                    }
                                }
                            )
                        }
                    }
                }
                dataCommunity?.let { list ->
                    items(list.size, key = { it }) { index ->
                        val item = list[index]
                        with(item) {
                            if(url == null) {
                                return@with
                            }
                            val route = AppNavRoute.WebView.shareRoute(url)
                            SmallCard(
                                color = cardNormalColor(),
                                modifier = Modifier.padding(horizontal = 3.dp, vertical = 3.dp).containerShare(route)
                            ) {
                                TransplantListItem(
                                    leadingContent = {
                                        UrlImage(logo, width = size, height = size)
                                    },
                                    headlineContent = { ScrollText(name) },
                                    modifier = Modifier.clickable {
                                        scope.launch {
                                            Starter.startWebView(navController,url, title = name, cookie =cookie, icon = AppNavRoute.StuTodayCampus.icon)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                item(span = { GridItemSpan(maxLineSpan) }) { InnerPaddingHeight(innerPadding, false) }
            }
        } else {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                InnerPaddingHeight(innerPadding,true)
                for(i in localList) {
                    val list = i.apps
                    if(list.isNotEmpty()) {
                        DividerTextExpandedWith(i.categoryName) {
                            for(j in list.indices step 2) {
                                val item1 = list[j]
                                Row(Modifier.padding(horizontal = APP_HORIZONTAL_DP-3.dp)) {
                                    with(item1) {
                                        val route = AppNavRoute.WebView.shareRoute(openUrl)
                                        SmallCard(
                                            color = cardNormalColor(),
                                            modifier = Modifier.padding(horizontal = 3.dp, vertical = 3.dp).weight(.5f).containerShare(route)
                                        ) {
                                            TransplantListItem(
                                                leadingContent = {
                                                    UrlImage(iconUrl, width = size, height = size)
                                                },
                                                headlineContent = { ScrollText(name) },
                                                modifier = Modifier.clickable {
                                                    scope.launch {
                                                        Starter.startWebView(navController,openUrl, title = name, cookie =cookie, icon = AppNavRoute.StuTodayCampus.icon)
                                                    }
                                                }
                                            )
                                        }
                                    }
                                    if(j + 1 < i.apps.size) {
                                        val item2 = list[j+1]
                                        with(item2) {
                                            val route = AppNavRoute.WebView.shareRoute(openUrl)
                                            SmallCard(
                                                color = cardNormalColor(),
                                                modifier = Modifier.padding(horizontal = 3.dp, vertical = 3.dp).weight(.5f).containerShare(route)
                                            ) {
                                                TransplantListItem(
                                                    leadingContent = {
                                                        UrlImage(iconUrl, width = size, height = size)
                                                    },
                                                    headlineContent = { ScrollText(name) },
                                                    modifier = Modifier.clickable {
                                                        scope.launch {
                                                            Starter.startWebView(navController,openUrl, title = name, cookie =cookie, icon = AppNavRoute.StuTodayCampus.icon)
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    } else {
                                        Spacer(Modifier.width(1.dp).weight(.5f))
                                    }
                                }
                            }
                        }
                    }
                }
                DividerTextExpandedWith("智慧社区") {
                    CommonNetworkScreen(uiState, onReload = refreshNetwork, isFullScreen = false) {
                        val data = dataCommunity!!
                        for(j in data.indices step 2) {
                            val item1 = data[j]
                            Row(Modifier.padding(horizontal = APP_HORIZONTAL_DP-3.dp)) {
                                with(item1) {
                                    if(url == null) {
                                        return@with
                                    }
                                    val route = AppNavRoute.WebView.shareRoute(url)
                                    SmallCard(
                                        color = cardNormalColor(),
                                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 3.dp).weight(.5f).containerShare(route)
                                    ) {
                                        TransplantListItem(
                                            leadingContent = {
                                                UrlImage(logo, width = size, height = size)
                                            },
                                            headlineContent = { ScrollText(name) },
                                            modifier = Modifier.clickable {
                                                scope.launch {
                                                    Starter.startWebView(navController,url, title = name, cookie =cookie, icon = AppNavRoute.StuTodayCampus.icon)
                                                }
                                            }
                                        )
                                    }
                                }
                                if(j + 1 < data.size) {
                                    val item2 = data[j+1]
                                    with(item2) {
                                        if(url == null) {
                                            return@with
                                        }
                                        val route = AppNavRoute.WebView.shareRoute(url)
                                        SmallCard(
                                            color = cardNormalColor(),
                                            modifier = Modifier.padding(horizontal = 3.dp, vertical = 3.dp).weight(.5f).containerShare(route)
                                        ) {
                                            TransplantListItem(
                                                leadingContent = {
                                                    UrlImage(logo, width = size, height = size)
                                                },
                                                headlineContent = { ScrollText(name) },
                                                modifier = Modifier.clickable {
                                                    scope.launch {
                                                        Starter.startWebView(navController,url, title = name, cookie =cookie, icon = AppNavRoute.StuTodayCampus.icon)
                                                    }
                                                }
                                            )
                                        }
                                    }
                                } else {
                                    Spacer(Modifier.width(1.dp).weight(.5f))
                                }
                            }
                        }
                    }
                }
//                DividerTextExpandedWith("学工系统") {
//
//                }
                InnerPaddingHeight(innerPadding,false)
            }
        }
    }
}


