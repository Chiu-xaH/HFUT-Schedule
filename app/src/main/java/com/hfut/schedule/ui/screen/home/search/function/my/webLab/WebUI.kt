package com.hfut.schedule.ui.screen.home.search.function.my.webLab

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.news.department.SchoolsUI
import com.hfut.schedule.ui.component.BottomTip
import com.hfut.schedule.ui.component.DividerTextExpandedWith
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.style.HazeBottomSheet
import dev.chrisbanes.haze.HazeState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebUI(hazeState: HazeState) {
    var showBottomSheet_Web by remember { mutableStateOf(false) }


    TransplantListItem(
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
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_Web = false },
            showBottomSheet = showBottomSheet_Web,
            hazeState = hazeState,
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("网址导航") {
                        Schools(hazeState)
                    }
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ) {
                    DividerTextExpandedWith(text = "固定项") {
                        WebItem()
                    }

                    DividerTextExpandedWith(text = "实验室") {
                        LabUI()
                        BottomTip(str = "有需求? 欢迎反馈")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Schools(hazeState: HazeState) {
    val sheetState_School = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_School by remember { mutableStateOf(false) }

    if (showBottomSheet_School) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_School = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_School
//            sheetState = sheetState_School,
//            shape = bottomSheetRound(sheetState_School)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("学院")
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
