package com.hfut.schedule.ui.screen.home.search.function.jxglstu.courseSearch

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.SemesterParser.getSemester
import com.hfut.schedule.logic.util.parse.SemesterParser.parseSemester
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.button.BUTTON_PADDING
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.pager.PageController
import com.hfut.schedule.ui.component.status.PrepareSearchIcon
import com.hfut.schedule.ui.nav.destination.CourseSearchDestination
import com.hfut.schedule.ui.nav.destination.CourseSearchTableDestination
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.CourseTotalUI
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.TotalCourseDataSource
import com.hfut.schedule.ui.style.color.textFiledTransplant
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.navigation.util.LocalNavController
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CourseSearchScreen(
    vm : NetWorkViewModel,
) {
    val navController = LocalNavController.current
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)

    var showSearch by rememberSaveable() { mutableStateOf(true) }
    var className by rememberSaveable { mutableStateOf( getPersonInfo().className ?: "") }
    var courseName by rememberSaveable { mutableStateOf("") }
    var courseId by rememberSaveable { mutableStateOf("") }

    val uiState by vm.courseSearchResponse.state.collectAsState()
    var semester by rememberSaveable { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        if(uiState is UiState.Success) {
            val data = (uiState as UiState.Success).data
            if(data.isEmpty()) {
                return@LaunchedEffect
            }
            semester = data[0].semester.id
            return@LaunchedEffect
        } else {
            semester = getSemester()
            vm.courseSearchResponse.emitPrepare()
        }
    }

    val refreshNetwork : suspend (Boolean) -> Unit = m@ { skip ->
        if(uiState is UiState.Success && skip) return@m
        if(semester == null) return@m
        val cookie = getJxglstuCookie() ?: return@m

        vm.courseSearchResponse.clear()
        vm.searchCourse(cookie, className, courseName, semester!!,courseId)
    }

    val scope = rememberCoroutineScope()

//    LaunchedEffect(semester) {
//        refreshNetwork(true)
//    }

    LaunchedEffect(uiState) {
        showSearch = when(uiState) {
            is UiState.Loading -> false
            is UiState.Error -> true
            is UiState.Prepare -> true
            is UiState.Success -> false
        }
    }

    val listState = rememberLazyListState()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val backdrop = rememberLayerBackdrop()
    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                colors = topBarTransplantColor(),
                title = { Text(CourseSearchDestination.title.asString()) },
                navigationIcon = {
                    TopBarNavigationIcon()
                },
                actions = {
                    Row(modifier = Modifier
                        .padding(horizontal = APP_HORIZONTAL_DP)
                        .animateContentSize()) {
                        val classNameNil = className.let { it.ifEmpty { null } }
                        val courseCodeNil = courseId.let { it.ifEmpty { null } }
                        val courseNameNil = courseName.let { it.ifEmpty { null } }
                        val canNotUse = courseNameNil == null && courseCodeNil == null && classNameNil == null
                        LiquidButton(
                            onClick = {
                                semester?.let { term ->
                                    navController.push(
                                        CourseSearchTableDestination(
                                            term,
                                            classNameNil,
                                            courseCodeNil,
                                            courseNameNil,
                                            (uiState as UiState.Success).data
                                        )
                                    )
                                }
                            },
                            isCircle = true,
                            enabled = uiState is UiState.Success && !canNotUse,
                            backdrop = backdrop,
                        ) {
                            Icon(
                                painterResource(R.drawable.calendar),
                                null,
                            )
                        }

                        AnimatedVisibility(
                            visible = !showSearch,
                            enter = AppAnimationManager.upDownAnimation.enter,
                            exit = AppAnimationManager.upDownAnimation.exit,
                        ) {
                            LiquidButton(
                                modifier = Modifier.padding(start = BUTTON_PADDING),
                                onClick = {
                                    showSearch = !showSearch
                                },
                                isCircle = false,
                                enabled = uiState is UiState.Success && !canNotUse,
                                backdrop = backdrop
                            ) {
                                Text("开始搜索")
                            }
                        }
                    }
                }
            )
        },
    ) { innerPadding ->
        Box(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(modifier = Modifier.hazeSource(hazeState)) {
                AnimatedVisibility(
                    visible = showSearch,
                    enter = AppAnimationManager.downUpAnimation.enter,
                    exit = AppAnimationManager.downUpAnimation.exit
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = APP_HORIZONTAL_DP - 3.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            TextField(
                                modifier = Modifier
                                    .weight(.5f)
                                    .padding(horizontal = 3.dp),
                                value = courseId,
                                onValueChange = {
                                    courseId = it
                                },
                                label = { Text("课程代码" ) },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                colors = textFiledTransplant(),
                            )
                            TextField(
                                modifier = Modifier
                                    .weight(.5f)
                                    .padding(horizontal = 3.dp),
                                value = courseName,
                                onValueChange = {
                                    courseName = it
                                },
                                label = { Text("课程名称" ) },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                colors = textFiledTransplant(),
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = APP_HORIZONTAL_DP - 3.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            val myClass = getPersonInfo().className
                            TextField(
                                modifier = Modifier
                                    .weight(.5f)
                                    .padding(horizontal = 3.dp),
                                value = className,
                                onValueChange = {
                                    className = it
                                },
                                label = { Text("教学班级" ) },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                colors = textFiledTransplant(),
                                trailingIcon = {
                                    if(myClass != className){
                                        IconButton(
                                            onClick = {
                                                myClass?.let { className = it }
                                            },
                                        ) {
                                            Icon(painterResource(R.drawable.person), null)
                                        }
                                    } else {
                                        IconButton(
                                            onClick = {
                                                className = ""
                                            },
                                        ) {
                                            Icon(painterResource(R.drawable.close), null)
                                        }
                                    }
                                }
                            )
                            FilledTonalIconButton(
                                onClick = {
                                    scope.launch{ refreshNetwork(false) }
                                },
                                modifier = Modifier
                                    .weight(.5f)
                                    .height(56.dp)
                                    .padding(horizontal = 3.dp),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.search),
                                    contentDescription = "description"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                    }
                }

                CommonNetworkScreen(uiState, onReload = { refreshNetwork(false) }, prepareContent = { PrepareSearchIcon() }) {
                    CourseTotalUI(
                        dataSource = TotalCourseDataSource.SEARCH,
                        sortType = true,
                        vm,
                        false,
                        listState
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            semester?.let { page ->
                val currentSemester by produceState<Int?>(initialValue = null) {
                    value = getSemester()
                }
                PageController(
                    listState = listState,
                    currentPage = page,
                    onNextPage = {
                        semester = it
                        scope.launch { refreshNetwork(false) }
                    },
                    onPreviousPage = {
                        semester = it
                        scope.launch { refreshNetwork(false) }
                    },
                    gap = 20,
                    text = parseSemester(page),
                    range = Pair(null,null),
                    paddingSafely = false,
                    resetPage = currentSemester ?: -1
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiForCourseSearch(
    vm: NetWorkViewModel,
    courseName : String?,
    code : String?,
    term : Int?,
    innerPadding : PaddingValues
) {
    var semester by remember { mutableStateOf<Int?>(null) }
    LaunchedEffect(term) {
        semester = term ?: getSemester()
    }
    val uiState by vm.courseSearchResponse.state.collectAsState()

    val refreshNetwork : suspend () -> Unit = m@ {
        if(uiState is UiState.Success) {
            return@m
        }
        if(semester != null) {
            val cookie = getJxglstuCookie()
            cookie?.let {
                vm.courseSearchResponse.clear()
                vm.searchCourse(it, null, courseName, semester!!,code)
            }
        }
    }

    LaunchedEffect(semester) {
        if(semester != null)
            refreshNetwork()
    }

    val listState = rememberLazyListState()
    Box(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {
        Column {
            CommonNetworkScreen(uiState, onReload = refreshNetwork) {
                CourseTotalUI(
                    dataSource = TotalCourseDataSource.SEARCH,
                    sortType = true,
                    vm,
                    false,
                    listState
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        semester?.let { page ->
            val currentSemester by produceState<Int?>(initialValue = null) {
                value = getSemester()
            }
            PageController(
                listState = listState,
                currentPage = page,
                onNextPage = { semester = it },
                onPreviousPage = { semester = it },
                gap = 20,
                text = parseSemester(page),
                range = Pair(null,null),
                paddingSafely = false,
                resetPage = currentSemester ?: -1
            )
        }
    }
}