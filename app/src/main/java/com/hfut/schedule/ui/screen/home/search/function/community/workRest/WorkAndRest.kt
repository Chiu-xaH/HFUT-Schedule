package com.hfut.schedule.ui.screen.home.search.function.community.workRest

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
import androidx.compose.material3.FilledTonalButton
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
import com.hfut.schedule.logic.util.network.ParseJsons.getMy
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.container.LargeCard
import com.hfut.schedule.ui.component.container.MyCustomCard
import com.hfut.schedule.ui.component.container.StyleCardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.webview.WebDialog
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getFormCommunity
import com.hfut.schedule.ui.style.HazeBottomSheet
import dev.chrisbanes.haze.HazeState

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
                    val percent = DateTimeManager.getPercent(startDate,endDate)

                    if(percent < 0) {
                        // 未开始
                        TransplantListItem(
                            headlineContent = { Text("${DateTimeManager.daysBetween(startDate)}天后") },
                            overlineContent = { Text("未开学")},
                            modifier = Modifier.weight(.5f)
                        )
                    } else if(percent >= 100.toDouble()) {
                        // 已结束
                        TransplantListItem(
                            headlineContent = { Text("本学期已结束")},
                            modifier = Modifier.weight(.5f)
                        )
                    } else {
                        TransplantListItem(
                            headlineContent = { Text("${formatDecimal(percent,1)}%") },
                            overlineContent = { Text("已过")},
                            modifier = Modifier.weight(.5f)
                        )
                    }
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
fun WorkAndRest(hazeState: HazeState) {
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
        var showDialog by remember { mutableStateOf(false) }
        val url = try {
            getMy()!!.SchoolCalendar
        } catch (e:Exception) {
            null
        }
        url?.let { WebDialog(showDialog,{showDialog = false}, it,"校历") }

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
                    HazeBottomSheetTopBar("作息") {
                        FilledTonalButton(onClick = {
                            if(url == null) {
                                showToast("正在从云端获取数据")
                            } else {
                                showDialog = true
                                showToast("即将打开网页链接,可自行下载或保存图片")
                            }
                        }) {
                            Text("校历")
                        }
                    }
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
