package com.hfut.schedule.ui.screen.home.search.function.community.failRate

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.status.PrepareSearchUI
import com.hfut.schedule.ui.component.container.TransplantListItem

import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FailRate(vm: NetWorkViewModel, hazeState: HazeState) {
    var showBottomSheet by remember { mutableStateOf(false) }

    TransplantListItem(
        headlineContent = { Text(text = "挂科率") },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.monitoring), contentDescription = "")},
        modifier = Modifier.clickable { showBottomSheet = true }
    )

    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("挂科率")
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    FailRateSearch(vm)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FailRateSearch(vm: NetWorkViewModel) {
    LaunchedEffect(Unit) {
        vm.failRateData.emitPrepare()
    }
    val uiState by vm.failRateData.state.collectAsState()
    var input by remember { mutableStateOf( "") }
    var firstUse by remember { mutableStateOf(true) }
    var page by remember { mutableIntStateOf(1) }
    val refreshNetwork : suspend () -> Unit = {
        SharedPrefs.prefs.getString("TOKEN","")?.let {
            vm.failRateData.clear()
            vm.searchFailRate(it,input,page)
            firstUse = false
        }
    }
    LaunchedEffect(page) {
        if(!firstUse) {
            refreshNetwork()
        }
    }
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        TextField(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = APP_HORIZONTAL_DP),
            value = input,
            onValueChange = {
                input = it
            },
            label = { Text("输入科目名称" ) },
            singleLine = true,
            trailingIcon = {
                IconButton(
                    onClick = {
                        scope.launch { refreshNetwork() }
                    }) {
                    Icon(painter = painterResource(R.drawable.search), contentDescription = "description")
                }
            },
            shape = MaterialTheme.shapes.medium,
            colors = textFiledTransplant(),
        )
    }
    Spacer(modifier = Modifier.height(3.dp))
    CommonNetworkScreen(uiState, onReload = refreshNetwork, prepareContent = { PrepareSearchUI() }) {
        FailRateUI(vm,page,nextPage = { page = it }, previousPage = { page = it })
    }
}
var permit = 1
@Composable
fun ApiToFailRate(input : String, vm: NetWorkViewModel, hazeState: HazeState) {
    val uiState by vm.failRateData.state.collectAsState()
    var page by remember { mutableIntStateOf(1) }
    val refreshNetwork : suspend () -> Unit = {
        SharedPrefs.prefs.getString("TOKEN","")?.let {
            vm.failRateData.clear()
            vm.searchFailRate(it,input,page)
        }
    }
    LaunchedEffect(page) {
        refreshNetwork()
    }
    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        FailRateUI(vm,page,nextPage = { page = it }, previousPage = { page = it })
    }
}