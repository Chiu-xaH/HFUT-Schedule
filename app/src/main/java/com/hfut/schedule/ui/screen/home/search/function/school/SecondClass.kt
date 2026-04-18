package com.hfut.schedule.ui.screen.home.search.function.school

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.network.util.Constant
import com.hfut.schedule.network.util.StatusCode
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.icon.DepartmentIcons
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.network.UrlImage
import com.hfut.schedule.ui.component.screen.pager.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.pager.PageController
import com.hfut.schedule.ui.nav.destination.SecondClassDestination
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.component.text.ScrollText
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.navigation.util.LocalNavController
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
fun SecondClass() {
    val navController = LocalNavController.current

    TransplantListItem(
        headlineContent = { ScrollText(text = SecondClassDestination.title.asString()) },
        leadingContent = {
            Icon(painterResource(SecondClassDestination.icon), contentDescription = null)
        },
        modifier = Modifier.clickable {
            navController.push(SecondClassDestination)
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SecondClassScreen(
    vm : NetWorkViewModel
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    var page by remember { mutableIntStateOf(1) }
    val cookie = remember { prefs.getString(SharedPrefs.SECOND_CLASS_TOKEN,null) }
    val refreshNetwork : suspend () -> Unit =  {
        cookie?.let {
            vm.secondClassActivitiesResp.clear()
            vm.getSecondClassActivities(cookie,page)
        } ?: vm.secondClassActivitiesResp.emitError(null, StatusCode.UNAUTHORIZED.code)
    }
    val uiState by vm.secondClassActivitiesResp.state.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val listState = rememberLazyListState()

    LaunchedEffect(page) {
        refreshNetwork()
    }

    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                modifier = Modifier.topBarBlur(hazeState),
                colors = topBarTransplantColor(),
                title = { Text(SecondClassDestination.title.asString()) },
                navigationIcon = {
                    TopBarNavigationIcon()
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
            CommonNetworkScreen(uiState, onReload = refreshNetwork) {
                val list = (uiState as UiState.Success).data
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(state = listState) {
                        item { InnerPaddingHeight(innerPadding,true) }
                        items(list.size, key = { list[it].id }) { index ->
                            val item = list[index]
                            val url = Constant.SECOND_CLASS_URL + "scReports/activity/item_detail/" + item.id
                            CustomCard(
                                color = cardNormalColor(),
                                modifier = Modifier.clickable {
                                    scope.launch {
                                        Starter.startWebView(context,url, title = item.name, cookie = cookie)
                                    }
                                },
                            ) {
                                TransplantListItem(
                                    headlineContent = {
                                        Text(item.name)
                                    },
                                    overlineContent = {
                                        Text("${item.beginTime.substringBefore(" ")} ~ ${item.endTime.substringBefore(" ")}")
                                    },
                                    leadingContent = {
                                        UrlImage(
                                            Constant.SECOND_CLASS_URL + item.activePhoto
                                        )
                                    },
                                )
                                PaddingHorizontalDivider()
                                Row {
                                    TransplantListItem(
                                        headlineContent = {
                                            Text(item.module)
                                        },
                                        overlineContent = {
                                            Text("类型")
                                        },
                                        leadingContent = {
                                            Icon(painterResource(R.drawable.kid_star),null)
                                        },
                                        modifier = Modifier.weight(.5f)
                                    )
                                    TransplantListItem(
                                        headlineContent = {
                                            Text(item.form)
                                        },
                                        overlineContent = {
                                            Text("形式")
                                        },
                                        leadingContent = {
                                            Icon(painterResource(R.drawable.directions_alt),null)
                                        },
                                        modifier = Modifier.weight(.5f)
                                    )
                                }
                                Row {
                                    TransplantListItem(
                                        headlineContent = {
                                            Text(item.getCampus().description)
                                        },
                                        overlineContent = {
                                            Text("校区")
                                        },
                                        leadingContent = {
                                            Icon(painterResource(R.drawable.near_me),null)
                                        },
                                        modifier = Modifier.weight(.5f)
                                    )
                                    TransplantListItem(
                                        headlineContent = {
                                            Text(item.peopleNum.toString())
                                        },
                                        overlineContent = {
                                            Text("额定人数")
                                        },
                                        leadingContent = {
                                            Icon(painterResource(R.drawable.group),null)
                                        },
                                        modifier = Modifier.weight(.5f)
                                    )
                                }
                                TransplantListItem(
                                    headlineContent = {
                                        Text(item.sponsor)
                                    },
                                    overlineContent = {
                                        Text("主办单位")
                                    },
                                    leadingContent = {
                                        DepartmentIcons(item.sponsor)
                                    }
                                )
                                if(item.keynoteSpeaker != null || item.theVenue != null || item.lectureStartTime != null) {
                                    PaddingHorizontalDivider()
                                }
                                item.keynoteSpeaker?.let {
                                    TransplantListItem(
                                        headlineContent = {
                                            Text(it)
                                        },
                                        overlineContent = {
                                            Text("主讲人")
                                        },
                                        leadingContent = {
                                            Icon(painterResource(R.drawable.voice_selection),null)
                                        }
                                    )
                                }
                                item.theVenue?.let {
                                    TransplantListItem(
                                        headlineContent = {
                                            Text(it)
                                        },
                                        overlineContent = {
                                            Text("地点")
                                        },
                                        leadingContent = {
                                            Icon(painterResource(R.drawable.near_me),null)
                                        }
                                    )
                                }
                                item.lectureStartTime?.let { startTime ->
                                    TransplantListItem(
                                        headlineContent = {
                                            Text(startTime.dropLast(3) + (item.lectureEndTime?.let { endTime -> " ~ ${endTime.dropLast(3)}" } ?: ")"))
                                        },
                                        overlineContent = {
                                            Text("时间")
                                        },
                                        leadingContent = {
                                            Icon(painterResource(R.drawable.schedule),null)
                                        }
                                    )
                                }
                            }
                        }
                        item { PaddingForPageControllerButton() }
                        item { InnerPaddingHeight(innerPadding,false) }
                    }
                    PageController(listState,page, onNextPage = { page = it }, onPreviousPage = { page = it })
                }
            }
        }
    }
}