package com.hfut.schedule.ui.activity.home.search.functions.courseSearch

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Divider
import com.hfut.schedule.ui.utils.components.LoadingUI
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.ClipBoard
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.utils.parse.SemseterParser.parseSemseter
import com.hfut.schedule.logic.utils.parse.SemseterParser.getSemseter
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.logic.utils.data.reEmptyLiveDta
import com.hfut.schedule.ui.activity.home.search.functions.person.getPersonInfo
import com.hfut.schedule.ui.activity.home.search.functions.totalCourse.CourseTotalUI
import com.hfut.schedule.ui.utils.NavigateAnimationManager
import com.hfut.schedule.ui.utils.components.AppHorizontalDp
import com.hfut.schedule.ui.utils.components.CardNormalDp
import com.hfut.schedule.ui.utils.components.CustomTopBar
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.style.Round
import com.hfut.schedule.ui.utils.style.textFiledTransplant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseSearchUI(vm : NetWorkViewModel) {
    var className by remember { mutableStateOf( getPersonInfo().classes ?: "") }
    var courseName by remember { mutableStateOf("") }
    var courseId by remember { mutableStateOf("") }

    var onclick by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    val cookie = if (!vm.webVpn) prefs.getString(
        "redirect",
        ""
    ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")


    var showSearch by remember { mutableStateOf(true) }

    val semsters = getSemseter()
    var semester by remember { mutableIntStateOf(semsters) }

    fun refresh() {
        CoroutineScope(Job()).launch {
            async {
                reEmptyLiveDta(vm.courseData)
                loading = true
                onclick = true
            }.await()
            async {
                cookie?.let { semester.let { it1 -> vm.searchCourse(it, className, courseName, it1,courseId) } }
            }.await()
            async {
                Handler(Looper.getMainLooper()).post {
                    vm.courseData.observeForever { result ->
                        if (result != null && result == "200") {
                            showSearch = false
                            loading = false
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            CustomTopBar("开课查询") {
                androidx.compose.animation.AnimatedVisibility(
                    visible = !showSearch,
                    enter = NavigateAnimationManager.upDownAnimation.enter,
                    exit = NavigateAnimationManager.upDownAnimation.exit
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
                    enter = NavigateAnimationManager.downUpAnimation.enter,
                    exit = NavigateAnimationManager.downUpAnimation.exit
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
                                                courseId = ClipBoard.paste()
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
                                                courseName = ClipBoard.paste()
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
                                    refresh()
                                },
                                modifier = Modifier.weight(.5f).height(56.dp).padding(horizontal = 3.dp),
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

                if(onclick){
                    Box() {
                        androidx.compose.animation.AnimatedVisibility(
                            visible = loading,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Box(modifier = Modifier.align(Alignment.Center)) {
                                LoadingUI()
                            }
                        }////加载动画居中，3s后消失

                        androidx.compose.animation.AnimatedVisibility(
                            visible = !loading,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            CourseTotalUI(json = vm.courseRsponseData.value, isSearch = true, sortType = true,vm)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = !loading,
                enter = scaleIn(),
                exit = scaleOut(),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = AppHorizontalDp(), vertical = AppHorizontalDp())
            ) {
                FloatingActionButton(
                    onClick = {
                        semester -= 20
                        refresh()
                    },
                ) { Icon(Icons.Filled.ArrowBack, "Add Button") }
            }


            androidx.compose.animation.AnimatedVisibility(
                visible = !loading,
                enter = scaleIn(),
                exit = scaleOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = AppHorizontalDp(), vertical = AppHorizontalDp())
            ) {
                ExtendedFloatingActionButton(
                    onClick = {
                        refresh()
                    },
                ) { Text(text = parseSemseter(semester),) }
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = !loading,
                enter = scaleIn(),
                exit = scaleOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(horizontal = AppHorizontalDp(), vertical = AppHorizontalDp())
            ) {
                FloatingActionButton(
                    onClick = {
                        semester += 20
                        refresh()
                    },
                ) { Icon(Icons.Filled.ArrowForward, "Add Button") }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiForCourseSearch(vm: NetWorkViewModel,courseName : String?,courseId : String?,showBottomSheet : Boolean,onDismissRequest :  () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val cookie = if (!vm.webVpn) prefs.getString(
        "redirect",
        ""
    ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")

    if(showBottomSheet) {
        var onclick by remember { mutableStateOf(false) }
        var loading by remember { mutableStateOf(true) }
        val semsters = getSemseter()
        var semester by remember { mutableIntStateOf(semsters) }

        fun refresh() {
            CoroutineScope(Job()).launch {
                async {
                    reEmptyLiveDta(vm.courseData)
                    loading = true
                    onclick = true
                }.await()
                async {
                    cookie?.let { semester.let { it1 -> vm.searchCourse(it, null, courseName, it1,courseId) } }
                }.await()
                async {
                    Handler(Looper.getMainLooper()).post {
                        vm.courseData.observeForever { result ->
                            if (result != null && result == "200") {
                                loading = false
                            }
                        }
                    }
                }
            }
        }

        if(loading) { refresh() }

        ModalBottomSheet(
            onDismissRequest,
            sheetState = sheetState,
            shape = Round(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    CustomTopBar("开课查询 ${courseName ?: courseId}")
                },
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    Column {
                        if(onclick){
                            Box() {
                                androidx.compose.animation.AnimatedVisibility(
                                    visible = loading,
                                    enter = fadeIn(),
                                    exit = fadeOut()
                                ) {
                                    Box(modifier = Modifier.align(Alignment.Center)) {
                                        LoadingUI()
                                    }
                                }////加载动画居中，3s后消失

                                androidx.compose.animation.AnimatedVisibility(
                                    visible = !loading,
                                    enter = fadeIn(),
                                    exit = fadeOut()
                                ) {
                                    CourseTotalUI(json = vm.courseRsponseData.value, isSearch = true, sortType = true,vm)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    androidx.compose.animation.AnimatedVisibility(
                        visible = !loading,
                        enter = scaleIn(),
                        exit = scaleOut(),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(horizontal = AppHorizontalDp(), vertical = AppHorizontalDp())
                    ) {
                        FloatingActionButton(
                            onClick = {
                                semester -= 20
                                refresh()
                            },
                        ) { Icon(Icons.Filled.ArrowBack, "Add Button") }
                    }


                    androidx.compose.animation.AnimatedVisibility(
                        visible = !loading,
                        enter = scaleIn(),
                        exit = scaleOut(),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = AppHorizontalDp(), vertical = AppHorizontalDp())
                    ) {
                        ExtendedFloatingActionButton(
                            onClick = {
                                refresh()
                            },
                        ) { Text(text = parseSemseter(semester),) }
                    }

                    androidx.compose.animation.AnimatedVisibility(
                        visible = !loading,
                        enter = scaleIn(),
                        exit = scaleOut(),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(horizontal = AppHorizontalDp(), vertical = AppHorizontalDp())
                    ) {
                        FloatingActionButton(
                            onClick = {
                                semester += 20
                                refresh()
                            },
                        ) { Icon(Icons.Filled.ArrowForward, "Add Button") }
                    }
                }
            }
        }
    }
}