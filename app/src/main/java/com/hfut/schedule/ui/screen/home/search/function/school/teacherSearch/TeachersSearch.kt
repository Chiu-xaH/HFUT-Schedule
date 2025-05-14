package com.hfut.schedule.ui.screen.home.search.function.school.teacherSearch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.ui.component.CommonNetworkScreen
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.PrepareSearchUI
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.appHorizontalDp
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherSearch(vm: NetWorkViewModel, hazeState: HazeState) {
    var showBottomSheet by remember { mutableStateOf(false) }

    TransplantListItem(
        headlineContent = { Text(text = "教师检索") },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.group), contentDescription = "") },
        modifier = Modifier.clickable { showBottomSheet = true }
    )

    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ) {
            SearchTeachersUI(vm)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTeachersUI(vm : NetWorkViewModel) {
    var name by remember { mutableStateOf("") }
    var direction by remember { mutableStateOf("") }

    val uiState by vm.teacherSearchData.state.collectAsState()

    val scope = rememberCoroutineScope()
    val refreshNetwork: suspend () -> Unit = {
        vm.teacherSearchData.clear()
        vm.searchTeacher(name,direction)
    }
    LaunchedEffect(Unit) {
        vm.teacherSearchData.emitPrepare()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            HazeBottomSheetTopBar("教师") {
                FilledTonalIconButton(
                    onClick = { scope.launch { refreshNetwork() } },
                ) {
                    Icon(painterResource(R.drawable.search), contentDescription = "")
                }
            }
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
                        .padding(horizontal = appHorizontalDp()),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextField(
                        modifier = Modifier
                            .weight(.5f),
//                            .padding(horizontal = 7.dp),
                        value = name,
                        onValueChange = {
                            name = it
                        },
                        label = { Text("姓名" ) },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = textFiledTransplant(),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        modifier = Modifier
                            .weight(.5f),
//                            .padding(horizontal = 7.dp),
                        value = direction,
                        onValueChange = {
                            direction = it
                        },
                        label = { Text("研究方向" ) },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = textFiledTransplant(),
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                CommonNetworkScreen(uiState, onReload = refreshNetwork, prepareContent = { PrepareSearchUI() }) {
//                    val response = (uiState as SimpleUiState.Success).data
//                    val list = response?.teacherData ?: emptyList()
                    TeacherListUI(vm)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ApiToTeacherSearch(input : String,vm: NetWorkViewModel) {
    val uiState by vm.teacherSearchData.state.collectAsState()

    val refreshNetwork: suspend () -> Unit = {
        vm.teacherSearchData.clear()
        vm.searchTeacher(input)
    }
    LaunchedEffect(input) {
        refreshNetwork()
    }
    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
//        val response = (uiState as SimpleUiState.Success).data
//        val list = response?.teacherData ?: emptyList()
        TeacherListUI(vm)
    }
}