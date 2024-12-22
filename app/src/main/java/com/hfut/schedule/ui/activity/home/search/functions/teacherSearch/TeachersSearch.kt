package com.hfut.schedule.ui.activity.home.search.functions.teacherSearch

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.utils.reEmptyLiveDta
import com.hfut.schedule.ui.activity.home.search.functions.failRate.permit
import com.hfut.schedule.ui.utils.Round
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherSearch(vm: NetWorkViewModel) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text(text = "教师检索") },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.group), contentDescription = "") },
        modifier = Modifier.clickable { showBottomSheet = true }
    )

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            shape = Round(sheetState)
        ) {
            SearchTeachersUI(vm)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTeachersUI(vm : NetWorkViewModel) {
    var loading by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var direction by remember { mutableStateOf("") }


    fun onClick() {
        CoroutineScope(Job()).launch{
            async { loading = true }.await()
            async { reEmptyLiveDta(vm.teacherSearchData) }
            async{ vm.getProgramPerformance(name, direction) }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.teacherSearchData.observeForever { result ->
                        if (result != null) {
                            if(result.contains("{")) {
                                loading = false
                            }
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("教师") },
                actions = {
                    FilledTonalIconButton(
                        onClick = { onClick() },
                        modifier = Modifier.padding(horizontal = 15.dp)
                    ) {
                        Icon(painterResource(R.drawable.search), contentDescription = "")
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){
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
                        value = name,
                        onValueChange = {
                            name = it
                        },
                        label = { Text("姓名" ) },
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
                        value = direction,
                        onValueChange = {
                            direction = it
                        },
                        label = { Text("研究方向" ) },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
                            unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
                        ),
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Box {
                    androidx.compose.animation.AnimatedVisibility(
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
                    }
                    androidx.compose.animation.AnimatedVisibility(
                        visible = !loading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        TeacherListUI(vm)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ApiToTeacherSearch(input : String,vm: NetWorkViewModel) {
    var loading by remember { mutableStateOf(false) }
    if(permit == 1) {
        CoroutineScope(Job()).launch{
            async { loading = true }.await()
            async { reEmptyLiveDta(vm.teacherSearchData) }
            async{ vm.getProgramPerformance(name = input) }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.teacherSearchData.observeForever { result ->
                        if (result != null) {
                            if(result.contains("{")) {
                                loading = false
                                permit++
                            }
                        }
                    }
                }
            }
        }
    }
    Box {
        androidx.compose.animation.AnimatedVisibility(
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
        }
        androidx.compose.animation.AnimatedVisibility(
            visible = !loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            TeacherListUI(vm)
        }
    }
}