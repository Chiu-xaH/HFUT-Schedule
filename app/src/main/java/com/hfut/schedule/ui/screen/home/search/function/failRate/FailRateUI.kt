package com.hfut.schedule.ui.screen.home.search.function.failRate

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.ui.component.AnimationCardListItem
import com.hfut.schedule.ui.component.BottomSheetTopBar
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.ui.component.MyCustomCard
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.style.bottomSheetRound

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FailRateUI(vm : NetWorkViewModel) {

//    getFailRate(vm)

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    var num by remember { mutableStateOf(0) }

    LazyColumn {
        items(getFailRate(vm).size){ item ->
            AnimationCardListItem(
                headlineContent = {  Text(getFailRate(vm)[item].courseName) },
                leadingContent = { Icon(painterResource(R.drawable.monitoring), contentDescription = "Localized description",) },
                trailingContent = { Icon(Icons.Filled.ArrowForward, contentDescription = "")},
                modifier = Modifier.clickable {
                    showBottomSheet = true
                    num = item },
                index = item
            )
        }
    }


    if (showBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            shape = bottomSheetRound(sheetState)
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    BottomSheetTopBar(getFailRate(vm)[num].courseName)
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    LazyColumn{
                        items(getLists(num,vm).size){ item ->
                            val rate = (1 - getLists(num,vm)[item].successRate) * 100
                            val formattedNumber = String.format("%.2f", rate)
                            AnimationCardListItem(
                                headlineContent = {  Text("平均分 ${getLists(num,vm)[item].avgScore}") },
                                supportingContent = { Text("人数: 挂科 ${getLists(num,vm)[item].failCount} | 总 ${getLists(num,vm)[item].totalCount}") },
                                overlineContent = { Text(text = "${getLists(num,vm)[item].xn}年 第${getLists(num,vm)[item].xq}学期")},
                                leadingContent = { Icon(painterResource(R.drawable.article), contentDescription = "Localized description",) },
                                trailingContent = { Text("挂科率 $formattedNumber %") },
                                index = item
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}
