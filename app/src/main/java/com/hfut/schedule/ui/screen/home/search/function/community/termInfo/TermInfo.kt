package com.hfut.schedule.ui.screen.home.search.function.community.termInfo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.ui.component.DividerTextExpandedWith
import com.hfut.schedule.ui.component.custom.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.LargeCard
import com.hfut.schedule.ui.component.MyCustomCard
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.cardNormalColor
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getFormCommunity
import com.hfut.schedule.ui.style.HazeBottomSheet
import dev.chrisbanes.haze.HazeState

@Composable
fun TimeTable(startTime: List<String>, endTime: List<String>) {
    Column {
        // 表头
        Row(Modifier.padding(vertical = 8.dp)) {
            Text("上课时间", modifier = Modifier.weight(0.5f))
            Text("下课时间", modifier = Modifier.weight(0.5f))
        }

        HorizontalDivider() // 表头与内容分隔

        // 表格内容
        for (i in startTime.indices) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(startTime[i], modifier = Modifier.weight(0.5f))
                Text(endTime[i], modifier = Modifier.weight(0.5f))
            }
            HorizontalDivider() // 每行之间的分隔线
        }
    }
}

@Composable
fun TermInfoUI(friendUserName : String? = null) {
    val data = remember { getFormCommunity(friendUserName) } ?: return
    with(data) {
        val startDate = start.substringBefore(" ")
        val endDate = end.substringBefore(" ")

        DividerTextExpandedWith("学期",openBlurAnimation = false) {
            LargeCard(title = xn + "学年 第${xq}学期") {
                Row {
                    TransplantListItem(
                        headlineContent = { Text(startDate) },
                        overlineContent = { Text("开始")},
                        modifier = Modifier.weight(.5f),
                    )
                    TransplantListItem(
                        overlineContent = { Text("结束")},
                        headlineContent = { Text(endDate) },
                        modifier = Modifier.weight(.5f)
                    )
                }
                Row {
                    TransplantListItem(
                        headlineContent = { Text("$currentWeek / $totalWeekCount") },
                        overlineContent = { Text("教学周")},
                        modifier = Modifier.weight(.5f)
                    )
                    TransplantListItem(
                        headlineContent = { Text("${formatDecimal(DateTimeManager.getPercent(startDate,endDate),1)}%") },
                        overlineContent = { Text("已过")},
                        modifier = Modifier.weight(.5f)
                    )
                }
            }
        }
        DividerTextExpandedWith("作息") {
            MyCustomCard(containerColor = cardNormalColor()) {
                Column {
                    if(startTime.size == endTime.size) {
                        for(i in startTime.indices step 2) {
                            Row {
                                TransplantListItem(
                                    headlineContent = { Text(startTime[i] + " ~ " + endTime[i]) },
                                    overlineContent = { Text("第${i+1}节")},
                                    modifier = Modifier.weight(.5f)
                                )
                                if(i+1 < startTime.size)
                                    TransplantListItem(
                                        headlineContent = { Text(startTime[i+1] + " ~ " + endTime[i+1]) },
                                        overlineContent = { Text("第${i+2}节")},
                                        modifier = Modifier.weight(.5f)
                                    )}
                        }
                    } else {
                        Row {
                            TransplantListItem(
                                headlineContent = { Text(startTime.joinToString("\n")) },
                                overlineContent = { Text("上课时间")},
                                modifier = Modifier.weight(.5f)
                            )
                            TransplantListItem(
                                headlineContent = { Text(endTime.joinToString("\n")) },
                                overlineContent = { Text("下课时间")},
                                modifier = Modifier.weight(.5f)
                            )
                        }
                    }
                }
            }
        }
        DividerTextExpandedWith("线下课程(社区数据源 可能有更新延迟)") {
            courseBasicInfoDTOList.forEachIndexed { index, item ->
                val type = item.trainingCategoryName_dictText
                val str = type?.let { " | $it" } ?: ""
                val id = item.courseId
                StyleCardListItem(
                    headlineContent = { Text(item.courseName) },
                    supportingContent = { Text(item.className)},
                    overlineContent = { Text("学分 ${item.credit}" + " | $id"+ str)},
                    leadingContent = { Text((index+1).toString())},
                    modifier = Modifier.clickable {
                        ClipBoardUtils.copy(id)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermInfo(hazeState: HazeState) {
    var showBottomSheet by remember { mutableStateOf(false) }

    TransplantListItem(
        headlineContent = { Text(text = "作息") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.schedule),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
            showBottomSheet = true
        }
    )
    if (showBottomSheet) {
        HazeBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("作息")
                },) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ){
                    TermInfoUI()
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiForTermInfo(friendUserName: String,hazeState: HazeState) {
    var showBottomSheet by remember { mutableStateOf(false) }

    IconButton(
        onClick = { showBottomSheet = true }
    ) {
        Icon(painterResource(R.drawable.info),null, tint = MaterialTheme.colorScheme.primary)
    }
    if (showBottomSheet) {
        HazeBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("$friendUserName 作息")
                },) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ){
                    TermInfoUI(friendUserName)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
