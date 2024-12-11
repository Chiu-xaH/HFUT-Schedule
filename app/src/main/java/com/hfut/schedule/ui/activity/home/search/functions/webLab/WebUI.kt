package com.hfut.schedule.ui.activity.home.search.functions.webLab

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
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
import com.hfut.schedule.ui.activity.news.departments.SchoolsUI
import com.hfut.schedule.ui.utils.BottomTip
import com.hfut.schedule.ui.utils.DividerText
import com.hfut.schedule.ui.utils.Round


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebUI() {
    val sheetState_Web = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_Web by remember { mutableStateOf(false) }



    ListItem(
        headlineContent = { Text(text = "网址导航") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.explore),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable { showBottomSheet_Web = true }
    )

    if (showBottomSheet_Web) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_Web = false },
            sheetState = sheetState_Web,
            shape = Round(sheetState_Web)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("网址导航") },
                        actions = {
                            Row(modifier = Modifier.padding(horizontal = 15.dp),) {
                                Schools()
                            }
                        }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ) {
                    DividerText(text = "固定项")
                    WebItem()
                    DividerText(text = "实验室")
                    LabUI()
                    BottomTip(str = "有需求? 欢迎反馈")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Schools() {
    val sheetState_School = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_School by remember { mutableStateOf(false) }

    if (showBottomSheet_School) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_School = false },
            sheetState = sheetState_School,
            shape = Round(sheetState_School)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("学院") },
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    SchoolsUI(null)
                }
            }
        }
    }

    FilledTonalButton(
        onClick = { showBottomSheet_School = true }
    ) {
        Text(text = "学院集锦")
    }
}
