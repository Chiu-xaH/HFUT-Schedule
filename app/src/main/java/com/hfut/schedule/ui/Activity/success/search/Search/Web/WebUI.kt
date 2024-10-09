package com.hfut.schedule.ui.Activity.success.search.Search.Web

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.hfut.schedule.R
import com.hfut.schedule.ui.UIUtils.BottomTip
import com.hfut.schedule.ui.UIUtils.DividerText
import com.hfut.schedule.ui.UIUtils.Round


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
                        title = { Text("网址导航") }
                    )
                },) { innerPadding ->
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
