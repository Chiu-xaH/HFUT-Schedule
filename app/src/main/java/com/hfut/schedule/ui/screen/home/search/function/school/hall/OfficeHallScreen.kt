package com.hfut.schedule.ui.screen.home.search.function.school.hall

import android.content.Context
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.OfficeHallSearchBean
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.network.UrlImage
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.screen.pager.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.pager.PageController
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.isValidWebUrl
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.uicommon.style.color.topBarTransplantColor
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.containerBackDrop
import com.hfut.schedule.ui.util.GlobalUIStateHolder
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalSharedTransitionScope
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

private enum class OfficeHallType(val serviceMode : String,val description: String) {
    GUIDANCE("13","指南"),
    SEARCH("11","查找"),
    HANDLE("12","办理")
}
private suspend fun openDetail(context: Context,bean : OfficeHallSearchBean,needLogin : Boolean) = with(bean) {
    val finalUrl = if(url != null && isValidWebUrl(url)) {
        url
    } else {
        MyApplication.OFFICE_HALL_URL + "ServiceHall/ServiceDetail/" + id
    }
    if(!needLogin || GlobalUIStateHolder.globalWebVpn == true) {
        Starter.startWebView(context,finalUrl,name,null, AppNavRoute.OfficeHall.icon)
    } else {
        Starter.startWebUrl(context,finalUrl)
    }
}
@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OfficeHallScreen(
    vm : NetWorkViewModel,
    navController : NavHostController,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)
    val route = remember { AppNavRoute.OfficeHall.route }
    var input by remember { mutableStateOf("") }
    var page by remember { mutableIntStateOf(1) }
    val refreshNetwork : suspend () -> Unit =  {
        vm.officeHallSearchResponse.clear()
        vm.officeHallSearch(input,page)
    }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scope = rememberCoroutineScope()
    val imageSize = remember { 25.dp }
    val backdrop = rememberLayerBackdrop()
    CustomTransitionScaffold (
        route = route,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        navHostController = navController,
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState),
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.OfficeHall.label) },
                    navigationIcon = {
                        TopBarNavigationIcon(
                            navController,
                            route,
                            AppNavRoute.OfficeHall.icon
                        )
                    },
                )
                CustomTextField(
                    modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP).containerBackDrop(backdrop, MaterialTheme.shapes.medium),
                    input = input,
                    label = { Text("搜索") },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                scope.launch { refreshNetwork() }
                            }
                        ) {
                            Icon(painterResource(R.drawable.search),null)
                        }
                    }
                ) { input = it }
                Spacer(Modifier.height(CARD_NORMAL_DP))
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .backDropSource(backdrop)
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
            val context = LocalContext.current
            val uiState by vm.officeHallSearchResponse.state.collectAsState()
            LaunchedEffect(page) {
                refreshNetwork()
            }
            val listState = rememberLazyListState()
            CommonNetworkScreen(uiState, onReload = refreshNetwork) {
                val list = (uiState as UiState.Success).data
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(state = listState) {
                        item { InnerPaddingHeight(innerPadding,true) }
                        items(list.size, key = { list[it].id }) { index ->
                            val item = list[index]
                            with(item) {
                                val needLogin = serviceMode == OfficeHallType.HANDLE.serviceMode
                                CustomCard (
                                    color = cardNormalColor(),
                                    modifier = Modifier.clickable {
                                        scope.launch {
                                            openDetail(context,item,needLogin)
                                        }
                                    }
                                ){
                                    TransplantListItem(
                                        headlineContent = {
                                            Text(name)
                                        },
                                        overlineContent = {
                                            Text(serviceDpt)
                                        },
                                        leadingContent = {
                                            UrlImage(photoUrl, width = imageSize, height = imageSize, useCut = false, roundSize = 0.dp)
                                        },
                                        trailingContent = if(needLogin) {
                                            { Text("需登录") }
                                        } else null
                                    )
                                    if(serviceTime == null && processingPlace == null) {
                                        return@CustomCard
                                    }
                                    PaddingHorizontalDivider()
                                    serviceTime?.let {
                                        TransplantListItem(
                                            headlineContent = {
                                                Text(it)
                                            },
                                            overlineContent = { Text("时间") },
                                            leadingContent = {
                                                Icon(painterResource(R.drawable.schedule),null)
                                            }
                                        )
                                    }
                                    processingPlace?.let {
                                        TransplantListItem(
                                            headlineContent = {
                                                Text(it)
                                            },
                                            overlineContent = { Text("地点") },
                                            leadingContent = {
                                                Icon(painterResource(R.drawable.near_me),null)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        item { PaddingForPageControllerButton() }
                        item { InnerPaddingHeight(innerPadding,false) }
                    }
                    PageController(listState,page, nextPage = { page = it }, previousPage = { page = it })
                }
            }
        }
    }
//    }
}