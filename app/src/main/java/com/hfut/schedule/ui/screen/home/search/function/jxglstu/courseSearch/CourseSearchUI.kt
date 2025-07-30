package com.hfut.schedule.ui.screen.home.search.function.jxglstu.courseSearch

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.SemseterParser.getSemseter
import com.hfut.schedule.logic.util.parse.SemseterParser.parseSemseter
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.status.PrepareSearchUI
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.CourseTotalUI
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.TotalCourseDataSource
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseSearchUI(vm : NetWorkViewModel, hazeState: HazeState) {
    var className by remember { mutableStateOf( getPersonInfo().classes ?: "") }
    var courseName by remember { mutableStateOf("") }
    var courseId by remember { mutableStateOf("") }

    var showSearch by remember { mutableStateOf(true) }

    var semester by remember { mutableStateOf<Int?>(null) }
    LaunchedEffect(Unit) {
        semester = getSemseter()
    }
    var firstSearch by remember { mutableStateOf(true) }

    val refreshNetwork : suspend () -> Unit = {
        if(semester != null) {
            val cookie = getJxglstuCookie(vm)
            cookie?.let {
                vm.courseSearchResponse.clear()
                if(firstSearch) firstSearch = false
                vm.searchCourse(it, className, courseName, semester!!,courseId)
            }
        }
    }
    val scope = rememberCoroutineScope()
    val uiState by vm.courseSearchResponse.state.collectAsState()
    LaunchedEffect(Unit) {
        vm.courseSearchResponse.emitPrepare()
    }
    if(!firstSearch) {
        LaunchedEffect(semester) {
            if(semester != null)
                refreshNetwork()
        }
    }

    val loading = uiState is UiState.Loading
    if(!firstSearch) {
        LaunchedEffect(loading) {
            if(!loading) {
                showSearch = false
            }
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            HazeBottomSheetTopBar("开课查询") {
                AnimatedVisibility(
                    visible = !showSearch,
                    enter = AppAnimationManager.upDownAnimation.enter,
                    exit = AppAnimationManager.upDownAnimation.exit
                ) {
                    FilledTonalButton(
                        onClick = {
                            showSearch = !showSearch
                        },
                    ) {
                        Text("显示搜索框")
                    }
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column {
                AnimatedVisibility(
                    visible = showSearch,
                    enter = AppAnimationManager.downUpAnimation.enter,
                    exit = AppAnimationManager.downUpAnimation.exit
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
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
                                trailingIcon = if(courseId == "") {
                                    {
                                        IconButton(
                                            onClick = {
                                                courseId = ClipBoardUtils.paste()
                                            },
                                        ) {
                                            Icon(painterResource(R.drawable.content_paste),null)
                                        }
                                    }
                                } else null
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
                                trailingIcon = if(courseName == "") {
                                     {
                                        IconButton(
                                            onClick = {
                                                courseName = ClipBoardUtils.paste()
                                            },
                                        ) {
                                            Icon(painterResource(R.drawable.content_paste),null)
                                        }
                                    }
                                } else null
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            val myClass = getPersonInfo().classes
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
                                            Icon(painterResource(R.drawable.person),null)
                                        }
                                    } else {
                                        IconButton(
                                            onClick = {
                                                className = ""
                                            },
                                        ) {
                                            Icon(painterResource(R.drawable.close),null)
                                        }
                                    }
                                }
                            )
                            FilledTonalIconButton(
                                onClick = {
                                    scope.launch{ refreshNetwork() }
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

                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }

                CommonNetworkScreen(uiState, onReload = refreshNetwork, prepareContent = { PrepareSearchUI() }) {
                    CourseTotalUI(dataSource = TotalCourseDataSource.SEARCH, sortType = true,vm, hazeState = hazeState, false)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
            if(semester != null){
                AnimatedVisibility(
                    visible = !loading,
                    enter = scaleIn(),
                    exit = scaleOut(),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = APP_HORIZONTAL_DP, vertical = APP_HORIZONTAL_DP)
                ) {
                    FloatingActionButton(
                        onClick = { semester = semester!! - 20 },
                    ) { Icon(Icons.Filled.ArrowBack, "Add Button") }
                }


                AnimatedVisibility(
                    visible = !loading,
                    enter = scaleIn(),
                    exit = scaleOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = APP_HORIZONTAL_DP, vertical = APP_HORIZONTAL_DP)
                ) {
                    ExtendedFloatingActionButton(
                        onClick = {
                            scope.launch {
                                semester = getSemseter()
                            }
                        },
                    ) { semester?.let { Text(text = parseSemseter(it)) } }
                }

                AnimatedVisibility(
                    visible = !loading,
                    enter = scaleIn(),
                    exit = scaleOut(),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(horizontal = APP_HORIZONTAL_DP, vertical = APP_HORIZONTAL_DP)
                ) {
                    FloatingActionButton(
                        onClick = { semester = semester!! + 20 },
                    ) { Icon(Icons.Filled.ArrowForward, "Add Button") }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiForCourseSearch(vm: NetWorkViewModel, courseName : String?, courseId : String?, showBottomSheet : Boolean, hazeState: HazeState, onDismissRequest :  () -> Unit) {
    if(showBottomSheet) {
        var semester by remember { mutableStateOf<Int?>(null) }
        LaunchedEffect(Unit) {
            semester = getSemseter()
        }
        val refreshNetwork : suspend () -> Unit = {
            if(semester != null) {
                val cookie = getJxglstuCookie(vm)
                cookie?.let {
                    vm.courseSearchResponse.clear()
                    vm.searchCourse(it, null, courseName, semester!!,courseId)
                }
            }

        }
        val uiState by vm.courseSearchResponse.state.collectAsState()
        LaunchedEffect(semester) {
            if(semester != null)
                refreshNetwork()
        }
        val loading = uiState is UiState.Loading
        val scope = rememberCoroutineScope()
        HazeBottomSheet (
            onDismissRequest = onDismissRequest,
            showBottomSheet = showBottomSheet,
            hazeState = hazeState,
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("开课查询 ${courseName ?: courseId}")
                },
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    Column {
                        CommonNetworkScreen(uiState, onReload = refreshNetwork) {
                            CourseTotalUI(dataSource = TotalCourseDataSource.SEARCH, sortType = true,vm, hazeState = hazeState,false)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    if(semester != null) {
                        AnimatedVisibility(
                            visible = !loading,
                            enter = scaleIn(),
                            exit = scaleOut(),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(
                                    horizontal = APP_HORIZONTAL_DP,
                                    vertical = APP_HORIZONTAL_DP
                                )
                        ) {
                            FloatingActionButton(
                                onClick = {
                                    semester = semester!! - 20
                                },
                            ) { Icon(Icons.Filled.ArrowBack, "Add Button") }
                        }


                        AnimatedVisibility(
                            visible = !loading,
                            enter = scaleIn(),
                            exit = scaleOut(),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(
                                    horizontal = APP_HORIZONTAL_DP,
                                    vertical = APP_HORIZONTAL_DP
                                )
                        ) {
                            ExtendedFloatingActionButton(
                                onClick = {
                                    scope.launch{ semester = getSemseter() }
                                },
                            ) { Text(text = parseSemseter(semester!!),) }
                        }

                        AnimatedVisibility(
                            visible = !loading,
                            enter = scaleIn(),
                            exit = scaleOut(),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(
                                    horizontal = APP_HORIZONTAL_DP,
                                    vertical = APP_HORIZONTAL_DP
                                )
                        ) {
                            FloatingActionButton(
                                onClick = {
                                    semester = semester!! + 20
                                },
                            ) { Icon(Icons.Filled.ArrowForward, "Add Button") }
                        }
                    }

                }
            }
        }
    }
}