package com.hfut.schedule.ui.Activity.success.search.Search.CourseSearch

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.ui.Activity.success.search.Search.Survey.getSemseter
import com.hfut.schedule.ui.Activity.success.search.Search.Survey.getSemseterCloud
import com.hfut.schedule.ui.Activity.success.search.Search.TotalCourse.CourseTotalUI
import com.hfut.schedule.ui.UIUtils.MyToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun courseSearchUI(vm : LoginSuccessViewModel) {
    var className by remember { mutableStateOf( "") }
    var courseName by remember { mutableStateOf( "") }

    var onclick by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    val cookie = SharePrefs.prefs.getString("redirect", "")
   // var showitem by remember { mutableStateOf(false) }

   // val saveSem = SharePrefs.prefs.getString("semesterId","")?.toInt()
   // var semester by remember { mutableStateOf(saveSem) }

   // var buttonText by remember { mutableStateOf( "本学期") }

   // val radioOptions = listOf("上学期", "本学期", "下学期")
    //val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[1]) }

    var semsters = getSemseterCloud()
    var semester by remember { mutableStateOf(semsters) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("开课查询") },
                actions = {
                    Row(modifier = Modifier.padding(horizontal = 15.dp)) {
                        FilledTonalButton(
                            onClick = {
                                MyToast("按钮已迁移到底部,可快速切换学期")
                            }
                        ){
                            Text(text = "学期切换")
                        }
                        FilledTonalIconButton(
                            // shape = RoundedCornerShape(5.dp),
                            onClick = {
                                CoroutineScope(Job()).launch {
                                    async {
                                        cookie?.let { semester?.let { it1 -> vm.searchCourse(it, className, courseName, it1) } }
                                        loading = true
                                        onclick = true
                                        Handler(Looper.getMainLooper()).post {
                                            vm.courseData.value = "{}"
                                        }
                                    }.await()
                                    async {
                                        Handler(Looper.getMainLooper()).post {
                                            vm.courseData.observeForever { result ->
                                                if (result != null) {
                                                    if (result == "200")
                                                        loading = false
                                                }
                                            }
                                        }
                                    }
                                }
                            }) {
                            Icon(
                                painter = painterResource(R.drawable.search),
                                contentDescription = "description"
                            )
                        }
                    }
                }
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextField(
                        modifier = Modifier
                            .weight(.5f)
                            .padding(horizontal = 7.dp),
                        value = className,
                        onValueChange = {
                            className = it
                        },
                        label = { Text("教学班级" ) },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
                            unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
                        ),
                    )
                    TextField(
                        modifier = Modifier
                            .weight(.5f)
                            .padding(horizontal = 7.dp),
                        value = courseName,
                        onValueChange = {
                            courseName = it
                        },
                        label = { Text("课程名称" ) },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
                            unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
                        ),
                    )
                }

                Spacer(modifier = Modifier.height(7.dp))

                if(onclick){
                    AnimatedVisibility(
                        visible = loading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Spacer(modifier = Modifier.height(5.dp))
                            CircularProgressIndicator()
                        }
                    }////加载动画居中，3s后消失

                    AnimatedVisibility(
                        visible = !loading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        CourseTotalUI(json = vm.courseRsponseData.value)
                    }
                }



                Spacer(modifier = Modifier.height(20.dp))
            }
            AnimatedVisibility(
                visible = !loading,
                enter = scaleIn(),
                exit = scaleOut(),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 15.dp, vertical = 15.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        semester -= 20
                        CoroutineScope(Job()).launch {
                            async {
                                cookie?.let { semester?.let { it1 -> vm.searchCourse(it, className, courseName, it1) } }
                                loading = true
                                onclick = true
                                Handler(Looper.getMainLooper()).post {
                                    vm.courseData.value = "{}"
                                }
                            }.await()
                            async {
                                Handler(Looper.getMainLooper()).post {
                                    vm.courseData.observeForever { result ->
                                        if (result != null) {
                                            if (result == "200")
                                                loading = false
                                        }
                                    }
                                }
                            }
                        }
                    },
                ) { Icon(Icons.Filled.ArrowBack, "Add Button") }
            }


            AnimatedVisibility(
                visible = !loading,
                enter = scaleIn(),
                exit = scaleOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 15.dp, vertical = 15.dp)
            ) {
                ExtendedFloatingActionButton(
                    onClick = {
                        CoroutineScope(Job()).launch {
                            async {
                                cookie?.let { semester?.let { it1 -> vm.searchCourse(it, className, courseName, it1) } }
                                loading = true
                                onclick = true
                                Handler(Looper.getMainLooper()).post {
                                    vm.courseData.value = "{}"
                                }
                            }.await()
                            async {
                                Handler(Looper.getMainLooper()).post {
                                    vm.courseData.observeForever { result ->
                                        if (result != null) {
                                            if (result == "200")
                                                loading = false
                                        }
                                    }
                                }
                            }
                        }
                    },
                ) { Text(text = getSemseter(semester),) }
            }

            AnimatedVisibility(
                visible = !loading,
                enter = scaleIn(),
                exit = scaleOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(horizontal = 15.dp, vertical = 15.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        semester += 20
                        CoroutineScope(Job()).launch {
                            async {
                                cookie?.let { semester?.let { it1 -> vm.searchCourse(it, className, courseName, it1) } }
                                loading = true
                                onclick = true
                                Handler(Looper.getMainLooper()).post {
                                    vm.courseData.value = "{}"
                                }
                            }.await()
                            async {
                                Handler(Looper.getMainLooper()).post {
                                    vm.courseData.observeForever { result ->
                                        if (result != null) {
                                            if (result == "200")
                                                loading = false
                                        }
                                    }
                                }
                            }
                        }
                    },
                ) { Icon(Icons.Filled.ArrowForward, "Add Button") }
            }
        }
    }


}