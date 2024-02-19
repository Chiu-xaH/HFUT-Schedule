package com.hfut.schedule.ui.ComposeUI.Settings.Test

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.R
import com.hfut.schedule.ui.UIUtils.DialogSample
import com.hfut.schedule.ui.UIUtils.Tests
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun debug() {

    val scaffoldState = rememberBottomSheetScaffoldState()


    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {
        ModalBottomSheet(
            containerColor = Color.Transparent,
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
        ) { DialogSample() }
    }


    ListItem(
        headlineContent = { Text(text = "调试模式") },
        modifier = Modifier.clickable { showBottomSheet = true },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.api), contentDescription ="" ) }
    )
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestUI() {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ), title = { Text("UI 调试") },
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) { Tests() }
    }
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetExample() {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val bottomSheetState = scaffoldState.bottomSheetState

    val scope = rememberCoroutineScope()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            Column(modifier = Modifier
                .padding(16.dp)
                .height(3000.dp)) {
                Text(text = "This is a bottom sheet")
                Button(
                    onClick = {
                        // 处理按钮点击事件
                        scope.launch { bottomSheetState.requireOffset()}
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(text = "关闭")
                }
                LazyColumn {
                    items(10) {
                        androidx.compose.material3.ListItem(headlineContent = { Text(text = "11") })
                    }
                }
            }
        },
        sheetPeekHeight = 0.dp
    ) {
        // 页面内容
        Button(
            onClick = { scope.launch { bottomSheetState.expand() } },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "打开底部表单")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun  ss() {
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            Row(
                modifier = Modifier.height(128.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.hotel_class),
                    contentDescription = null,
                    modifier = Modifier
                        .size(38.dp)
                        .padding(start = 10.dp)
                        //.testTag(POINT_ICON)
                )
                Text(
                    text = "Test",
                    fontSize = 28.sp,
                   // modifier = Modifier.testTag(POINT_TITLE)
                )
            }
        }
    ) { paddingValues ->
    }
}