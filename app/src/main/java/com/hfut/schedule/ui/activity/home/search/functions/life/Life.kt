package com.hfut.schedule.ui.activity.home.search.functions.life

import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.Starter.startLife
import com.hfut.schedule.ui.utils.ScrollText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Life() {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }


    ListItem(
        headlineContent = { ScrollText(text = "生活服务") },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.near_me), contentDescription = null) },
     //       overlineContent = { ScrollText(text = "高德地图SDK测试") },
        modifier = Modifier.clickable {
//            MyToast("暂未开发")
//                //showBottomSheet = true
            startLife()
        }
    )


//    if (showBottomSheet) {
//        ModalBottomSheet(
//            onDismissRequest = { showBottomSheet = false },
//            sheetState = sheetState
//        ) {
//            Scaffold(
//                modifier = Modifier.fillMaxSize(),
//                topBar = {
//                    TopAppBar(
//                        colors = TopAppBarDefaults.mediumTopAppBarColors(
//                            containerColor = Color.Transparent,
//                            titleContentColor = MaterialTheme.colorScheme.primary,
//                        ),
//                        title = { Text("高德地图SDK测试") }
//                    )
//                },) { innerPadding ->
//                Column(
//                    modifier = Modifier
//                        .padding(innerPadding)
//                        .fillMaxSize()
//                ) {
//                    Spacer(modifier = Modifier.height(20.dp))
//                }
//            }
//        }
//    }
}