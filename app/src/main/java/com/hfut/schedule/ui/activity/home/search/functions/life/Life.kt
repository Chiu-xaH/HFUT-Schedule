package com.hfut.schedule.ui.activity.home.search.functions.life

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.hfut.schedule.ui.utils.Round
import com.hfut.schedule.ui.utils.ScrollText
import com.hfut.schedule.viewmodel.NetWorkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Life(vm : NetWorkViewModel) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }


    ListItem(
        headlineContent = { ScrollText(text = "生活服务") },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.near_me), contentDescription = null) },
     //       overlineContent = { ScrollText(text = "高德地图SDK测试") },
        modifier = Modifier.clickable {
//            MyToast("暂未开发")
            countFunc = 0
                showBottomSheet = true
//            startLife()
        }
    )


    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            shape = Round(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("生活服务") }
                    )
                },) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    LifeUIS(vm)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiFromLife(vm: NetWorkViewModel) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    IconButton(onClick = {
        countFunc = 0
        showBottomSheet = true
    }) {
        Icon(painterResource(id = R.drawable.near_me), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            shape = Round(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("生活服务") }
                    )
                },) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    LifeUIS(vm)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}