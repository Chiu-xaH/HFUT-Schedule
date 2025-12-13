package com.hfut.schedule.ui.screen.grade.grade.community

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.AnimationCardListItem
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.status.EmptyIcon
import com.hfut.schedule.ui.component.status.PrepareSearchIcon
import com.xah.uicommon.style.padding.InnerPaddingHeight

import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import kotlinx.coroutines.launch

@Composable
private fun TotalGrade(vm : NetWorkViewModel) {
    val uiState by vm.gradeFromCommunityResponse.state.collectAsState()
    val result = (uiState as UiState.Success).data
    with(result) {
        AnimationCardListItem(
            headlineContent = {  Text("绩点(GPA)  $gpa") },
            supportingContent = { Text("班级排名: $classRanking   专业排名: $majorRanking") },
            leadingContent = { Icon(painterResource(R.drawable.flag), contentDescription = "Localized description",) },
            color = MaterialTheme.colorScheme.secondaryContainer,
            index = 0
        )
    }
}

@Composable
fun GradeItemUI(vm : NetWorkViewModel, innerPadding : PaddingValues) {
    var year by remember {
        mutableIntStateOf(
            if (DateTimeManager.Date_MM.toInt() <= 8)
                DateTimeManager.Date_yyyy.toInt() - 1
            else
                DateTimeManager.Date_yyyy.toInt()
        )
    }

    var isUpTerm by remember { mutableStateOf(
        DateTimeManager.Date_MM.toInt().let { it >= 9 || it <= 2 }
    ) }
    val uiState by vm.gradeFromCommunityResponse.state.collectAsState()

    LaunchedEffect(Unit) {
        if(uiState !is UiState.Success)
            vm.gradeFromCommunityResponse.emitPrepare()
    }

    val refreshNetwork : suspend () -> Unit = {
        prefs.getString("TOKEN","")?.let {
            vm.gradeFromCommunityResponse.clear()
            vm.getGrade(it,year.toString() + "-"+(year+1),(if(isUpTerm) 1 else 2).toString())
        }
    }
    val scope = rememberCoroutineScope()
    var showItemYear by remember { mutableStateOf(false) }
    DropdownMenu(expanded = showItemYear, onDismissRequest = { showItemYear = false }, offset = DpOffset(APP_HORIZONTAL_DP,0.dp)) {
        DropdownMenuItem(text = { Text(text = "${(DateTimeManager.Date_yyyy.toInt() - 3).toString()} - ${(DateTimeManager.Date_yyyy.toInt() - 2).toString()}" )}, onClick = {
            year = (DateTimeManager.Date_yyyy.toInt() - 3)
            showItemYear = false})
        DropdownMenuItem(text = { Text(text = "${(DateTimeManager.Date_yyyy.toInt() - 2).toString()} - ${(DateTimeManager.Date_yyyy.toInt() - 1).toString()}" )}, onClick = {
            year =  (DateTimeManager.Date_yyyy.toInt() - 2)
            showItemYear = false})
        DropdownMenuItem(text = { Text(text = "${(DateTimeManager.Date_yyyy.toInt() - 1).toString()} - ${(DateTimeManager.Date_yyyy.toInt() ).toString()}" )},onClick = {
            year =  (DateTimeManager.Date_yyyy.toInt() - 1)
            showItemYear = false})
        DropdownMenuItem(text = { Text(text = "${(DateTimeManager.Date_yyyy.toInt() ).toString()} - ${(DateTimeManager.Date_yyyy.toInt() + 1).toString()}" )}, onClick = {
            year =  DateTimeManager.Date_yyyy.toInt()
            showItemYear = false})
        DropdownMenuItem(text = { Text(text = "${(DateTimeManager.Date_yyyy.toInt() + 1).toString()} - ${(DateTimeManager.Date_yyyy.toInt() + 2).toString()}" )}, onClick = {
            year =  (DateTimeManager.Date_yyyy.toInt() + 1)
            showItemYear = false})
        DropdownMenuItem(text = { Text(text = "${(DateTimeManager.Date_yyyy.toInt() + 2).toString()} - ${(DateTimeManager.Date_yyyy.toInt() + 3).toString()}" )}, onClick = {
            year =  (DateTimeManager.Date_yyyy.toInt() + 2)
            showItemYear = false})
        DropdownMenuItem(text = { Text(text = "${(DateTimeManager.Date_yyyy.toInt() + 3).toString()} - ${(DateTimeManager.Date_yyyy.toInt() + 4).toString()}" )}, onClick = {
            year =  (DateTimeManager.Date_yyyy.toInt() + 3)
            showItemYear = false})
    }
    Column {
        InnerPaddingHeight(innerPadding,true)
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP, vertical = 0.dp), horizontalArrangement = Arrangement.Start){

            AssistChip(
                onClick = { showItemYear = true },
                label = { Text(text = "$year - ${year + 1}") }
            )

            Spacer(modifier = Modifier.width(10.dp))

            AssistChip(
                onClick = {isUpTerm = !isUpTerm},
                label = { Text(text = "第 ${if(isUpTerm) 1 else 2} 学期") },
            )

            Spacer(modifier = Modifier.width(10.dp))
            AssistChip(
                onClick = {
                    scope.launch { refreshNetwork() }
                },
                label = { Text(text = "搜索") },
                leadingIcon = { Icon(painter = painterResource(R.drawable.search), contentDescription = "description") }
            )
        }

        CommonNetworkScreen(uiState, onReload = refreshNetwork, prepareContent = { PrepareSearchIcon() }) {
            val context = LocalContext.current
            val bean = (uiState as UiState.Success).data
            val list = bean.scoreInfoDTOList
            if(list.isEmpty()) EmptyIcon()
            else {
                LazyColumn {
                    item { TotalGrade(vm) }
                    items(list.size) { index ->
                        val item = list[index]
                        AnimationCardListItem(
                            headlineContent = { Text(item.courseName) },
                            supportingContent = { Text("学分: " + item.credit + "   绩点: " + item.gpa + "   分数: ${item.score}") },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.article),
                                    contentDescription = "Localized description",
                                )
                            },
                            trailingContent = { Text(if (item.pass) "通过" else "未通过") },
                            index = index
                        )
                    }
                    item {
                        AnimationCardListItem(
                            headlineContent = { Text("查看分数详细请点击此处进入教务数据") },
                            supportingContent = { Text(text = "您现在使用的是智慧社区接口,使用教务系统数据可查看详细成绩") },
                            trailingContent = { Icon(Icons.Filled.ArrowForward, contentDescription = "") },
                            modifier = Modifier.clickable {
                                refreshLogin(context)
                            },
                            index = 0
                        )
                    }
                    item { InnerPaddingHeight(innerPadding,false) }
                }
            }
        }
    }
}

