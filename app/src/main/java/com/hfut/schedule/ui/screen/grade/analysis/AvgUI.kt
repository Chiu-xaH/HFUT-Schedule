package com.hfut.schedule.ui.screen.grade.analysis

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.SimpleUiState
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.component.CommonNetworkScreen
import com.hfut.schedule.ui.component.DividerTextExpandedWith
import com.hfut.schedule.ui.component.MyCustomCard
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.appHorizontalDp
import com.hfut.schedule.ui.component.cardNormalColor
import com.hfut.schedule.viewmodel.network.NetWorkViewModel

@SuppressLint("SuspiciousIndentation")
@Composable
fun GradeCountUI(vm : NetWorkViewModel, innerPadding : PaddingValues) {

    Column (modifier =   Modifier.verticalScroll(rememberScrollState())){
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
        Spacer(modifier = Modifier.height(5.dp))


        DividerTextExpandedWith(text = "总成绩 (整个大学阶段)") {
            AvgGrade(vm)
        }

        DividerTextExpandedWith(text = "学期统计 (各学期)") {
            AllGrade(vm)
        }

        DividerTextExpandedWith(text = "统计图") {
            StyleCardListItem(
                    headlineContent = { Text("正在开发") },
                    leadingContent = { Icon(painterResource(R.drawable.info), contentDescription = "Localized description",) },
                    modifier = Modifier.fillMaxWidth(),
                )
        }

        Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
    }
}


@Composable
fun AvgGrade(vm: NetWorkViewModel) {
    val refreshNetwork : suspend () -> Unit = {
        prefs.getString("TOKEN","")?.let {
            vm.avgData.clear()
            vm.getAvgGrade(it)
        }
    }
    val uiState by vm.avgData.state.collectAsState()

    LaunchedEffect(Unit) {
        if(uiState !is SimpleUiState.Success) {
            refreshNetwork()
        }
    }
    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        val resultAvg = (uiState as SimpleUiState.Success).data
        MyCustomCard(hasElevation = false, containerColor = cardNormalColor()) {
            Text(text = "我的水平", modifier = Modifier.padding(horizontal = appHorizontalDp(), vertical = 8.dp), color = MaterialTheme.colorScheme.primary)

            Row {
                TransplantListItem(
                    headlineContent = { Text("绩点 ${resultAvg.myAvgGpa}") },
                    supportingContent = { Text("排名 ${resultAvg.majorAvgGpaRanking}") },
                    leadingContent = { Icon(painterResource(R.drawable.award_star), contentDescription = "Localized description",) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
//                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )

                TransplantListItem(
                    headlineContent = { Text("分数 ${resultAvg.myAvgScore}") },
                    supportingContent = { Text("排名 ${resultAvg.majorAvgScoreRanking}") },
                    leadingContent = { Icon(painterResource(R.drawable.filter_vintage), contentDescription = "Localized description",) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
//                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
            Divider()
            Text(text = "专业水平", modifier = Modifier.padding(horizontal = appHorizontalDp(), vertical = 8.dp), color = MaterialTheme.colorScheme.primary)

            Row {
                TransplantListItem(
                    headlineContent = { Text("绩点") },
                    supportingContent = { Text("平均 ${resultAvg.majorAvgGpa}\n最高 (待开发)") },
                    leadingContent = { Icon(painterResource(R.drawable.award_star), contentDescription = "Localized description",) },
                    modifier = Modifier

                        .fillMaxWidth()
                        .weight(.5f),
//                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )

                TransplantListItem(
                    headlineContent = { Text("分数") },
                    supportingContent = { Text("平均 ${resultAvg.majorAvgScore}\n最高 (待开发)") },
                    leadingContent = { Icon(painterResource(R.drawable.filter_vintage), contentDescription = "Localized description",) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
//                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }
    }
}

@Composable
fun AllGrade(vm: NetWorkViewModel) {
    val chineseList = remember { listOf("大一上","大一下","大二上","大二下","大三上","大三下","大四上","大四下") }

    val refreshNetwork : suspend () -> Unit = {
        prefs.getString("TOKEN","")?.let {
            vm.allAvgData.clear()
            vm.getAllAvgGrade(it)
        }
    }
    val uiState by vm.allAvgData.state.collectAsState()

    LaunchedEffect(Unit) {
        if(uiState !is SimpleUiState.Success) {
            refreshNetwork()
        }
    }
    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        val list = (uiState as SimpleUiState.Success).data
        Column {
            for (index in list.indices) {
                val item = list[index]
                if(item.myAvgGpa != null || item.myAvgScore != null || item.majorAvgGpa != null || item.majorAvgScore != null)
                    MyCustomCard(hasElevation = false, containerColor = cardNormalColor()) {
                        Text(text = chineseList[index] + "学期", modifier = Modifier.padding(horizontal = appHorizontalDp(), vertical = 8.dp), color = MaterialTheme.colorScheme.primary)
                        Text(text = "我的水平", modifier = Modifier.padding(horizontal = appHorizontalDp(), vertical = 8.dp), color = MaterialTheme.colorScheme.primary)
                        Row {
                            TransplantListItem(
                                headlineContent = { Text("绩点 ${item.myAvgGpa}") },
                                leadingContent = { Icon(painterResource(R.drawable.award_star), contentDescription = "Localized description",) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(.5f),
//                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            )

                            TransplantListItem(
                                headlineContent = { Text("分数 ${item.myAvgScore}") },
                                leadingContent = { Icon(painterResource(R.drawable.filter_vintage), contentDescription = "Localized description",) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(.5f),
//                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            )
                        }
                        Divider()
                        Text(text = "专业水平", modifier = Modifier.padding(horizontal = appHorizontalDp(), vertical = 8.dp), color = MaterialTheme.colorScheme.primary)
                        Row {
                            TransplantListItem(
                                headlineContent = { Text("绩点(GPA)") },
                                supportingContent = { Text("平均 ${item.majorAvgGpa}\n最高 ${item.maxAvgGpa}") },
                                leadingContent = { Icon(painterResource(R.drawable.award_star), contentDescription = "Localized description",) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(.5f),
//                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            )

                            TransplantListItem(
                                headlineContent = { Text("分数") },
                                supportingContent = { Text("平均 ${item.majorAvgScore}\n最高 ${item.maxAvgScore}") },
                                leadingContent = { Icon(painterResource(R.drawable.filter_vintage), contentDescription = "Localized description",) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(.5f),
//                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            )
                        }
                    }
            }
        }
    }
}
