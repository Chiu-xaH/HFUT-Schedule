package com.hfut.schedule.ui.screen.home.search.function.school.hall

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.OfficeHallSearchBean
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.MyCustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.network.URLImage
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.screen.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.PagingController
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.isValidWebUrl
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.hfut.schedule.ui.style.topBarBlur
import com.hfut.schedule.ui.style.topBarTransplantColor
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.component.TopBarNavigateIcon
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

private enum class OfficeHallType(val serviceMode : String,val description: String) {
    GUIDANCE("13","指南"),
    SEARCH("11","查找"),
    HANDLE("12","办理")
}
private fun openDetail(bean : OfficeHallSearchBean,needLogin : Boolean) = with(bean) {
    val finalUrl = if(url != null && isValidWebUrl(url)) {
        url
    } else {
        MyApplication.OFFICE_HALL_URL + "ServiceHall/ServiceDetail/" + id
    }
    if(needLogin) {
        Starter.startWebUrl(finalUrl)
    } else {
        Starter.startWebView(finalUrl,name,null, AppNavRoute.OfficeHall.icon)
    }
}
@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OfficeHallScreen(
    vm : NetWorkViewModel,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.OfficeHall.route }
    var input by remember { mutableStateOf("") }
    var page by remember { mutableIntStateOf(1) }
    val refreshNetwork : suspend () -> Unit =  {
        vm.officeHallSearchResponse.clear()
        vm.officeHallSearch(input,page)
    }

    val scope = rememberCoroutineScope()
    val imageSize = remember { 25.dp }
    with(sharedTransitionScope) {
        CustomTransitionScaffold (
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            topBar = {
                Column(
                    modifier = Modifier.topBarBlur(hazeState),
                ) {
                    TopAppBar(
                        colors = topBarTransplantColor(),
                        title = { Text(AppNavRoute.OfficeHall.label) },
                        navigationIcon = {
                            TopBarNavigateIcon(
                                navController,
                                animatedContentScope,
                                route,
                                AppNavRoute.OfficeHall.icon
                            )
                        },
                    )
                    CustomTextField(
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
                    .hazeSource(hazeState)
                    .fillMaxSize()
            ) {

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
                                    MyCustomCard (
                                        containerColor = cardNormalColor(),
                                        modifier = Modifier.clickable {
                                            openDetail(item,needLogin)
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
                                                URLImage(photoUrl, width = imageSize, height = imageSize, useCut = false, roundSize = 0.dp)
                                            },
                                            trailingContent = if(needLogin) {
                                                { Text("需登录") }
                                            } else null
                                        )
                                        if(serviceTime == null && processingPlace == null) {
                                            return@MyCustomCard
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
                        PagingController(listState,page,showUp = true, nextPage = { page = it }, previousPage = { page = it })
                    }
                }
            }
        }
    }
}