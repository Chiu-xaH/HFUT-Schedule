package com.hfut.schedule.ui.screen.grade.grade.community

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.DateTimeUtils
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.network.reEmptyLiveDta
import com.hfut.schedule.ui.screen.grade.grade.getGrade
import com.hfut.schedule.ui.component.AnimationCardListItem
import com.hfut.schedule.ui.component.EmptyUI
import com.hfut.schedule.ui.component.LoadingUI
import com.hfut.schedule.ui.component.appHorizontalDp
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@Composable
fun TotalGrade(vm : NetWorkViewModel) {

    val result = getTotalGrade(vm)
    if(result != null) {
        val Class = result.classRanking
        val Major = result.majorRanking
        val TotalGPA = result.gpa

        AnimationCardListItem(
            headlineContent = {  Text("绩点(GPA)  $TotalGPA") },
            supportingContent = { Text("班级排名: $Class   专业排名: $Major") },
            leadingContent = { Icon(painterResource(R.drawable.flag), contentDescription = "Localized description",) },
            modifier = Modifier.clickable {},
            color = MaterialTheme.colorScheme.primaryContainer,
            index = 0
        )
    }
}

@Composable
fun GradeItemUI(vm : NetWorkViewModel, innerPadding : PaddingValues) {

    val CommuityTOKEN = prefs.getString("TOKEN","")
    var refresh by remember { mutableStateOf(true) }
    var loading by remember { mutableStateOf(true) }

    var term  by remember { mutableStateOf( "1") }
    val month = DateTimeUtils.Date_MM.toInt()
    if( month >= 9 || month <= 2) term = "1"
    else term = "2"
    var Years by remember { mutableStateOf(
        if (month <= 8) DateTimeUtils.Date_yyyy.toInt() - 1
        else DateTimeUtils.Date_yyyy.toInt()
    ) }

    var termBoolean by remember { mutableStateOf(prefs.getBoolean("term",true)) }

    if (termBoolean) term = "1"
    else term = "2"
    val cor = rememberCoroutineScope()

    fun refresh() = cor.launch {
        async {
            reEmptyLiveDta(vm.gradeFromCommunityResponse)
            loading = true
            SharedPrefs.saveBoolean("term",true,termBoolean)
        }.await()
        async { CommuityTOKEN?.let { vm.getGrade(it,Years.toString() + "-"+(Years+1),term) } }.await()
        launch {
            Handler(Looper.getMainLooper()).post{
                vm.gradeFromCommunityResponse.observeForever { result ->
                    if (result != null && result.contains("success")) {
                        refresh = false
                        loading = false
                    }
                }
            }
        }
    }
    if(refresh) refresh()

    var showitem_year by remember { mutableStateOf(false) }
    DropdownMenu(expanded = showitem_year, onDismissRequest = { showitem_year = false }, offset = DpOffset(appHorizontalDp(),0.dp)) {
        DropdownMenuItem(text = { Text(text = "${(DateTimeUtils.Date_yyyy.toInt() - 3).toString()} - ${(DateTimeUtils.Date_yyyy.toInt() - 2).toString()}" )}, onClick = {
            Years = (DateTimeUtils.Date_yyyy.toInt() - 3)
            showitem_year = false})
        DropdownMenuItem(text = { Text(text = "${(DateTimeUtils.Date_yyyy.toInt() - 2).toString()} - ${(DateTimeUtils.Date_yyyy.toInt() - 1).toString()}" )}, onClick = {
            Years =  (DateTimeUtils.Date_yyyy.toInt() - 2)
            showitem_year = false})
        DropdownMenuItem(text = { Text(text = "${(DateTimeUtils.Date_yyyy.toInt() - 1).toString()} - ${(DateTimeUtils.Date_yyyy.toInt() ).toString()}" )},onClick = {
            Years =  (DateTimeUtils.Date_yyyy.toInt() - 1)
            showitem_year = false})
        DropdownMenuItem(text = { Text(text = "${(DateTimeUtils.Date_yyyy.toInt() ).toString()} - ${(DateTimeUtils.Date_yyyy.toInt() + 1).toString()}" )}, onClick = {
            Years =  DateTimeUtils.Date_yyyy.toInt()
            showitem_year = false})
        DropdownMenuItem(text = { Text(text = "${(DateTimeUtils.Date_yyyy.toInt() + 1).toString()} - ${(DateTimeUtils.Date_yyyy.toInt() + 2).toString()}" )}, onClick = {
            Years =  (DateTimeUtils.Date_yyyy.toInt() + 1)
            showitem_year = false})
        DropdownMenuItem(text = { Text(text = "${(DateTimeUtils.Date_yyyy.toInt() + 2).toString()} - ${(DateTimeUtils.Date_yyyy.toInt() + 3).toString()}" )}, onClick = {
            Years =  (DateTimeUtils.Date_yyyy.toInt() + 2)
            showitem_year = false})
        DropdownMenuItem(text = { Text(text = "${(DateTimeUtils.Date_yyyy.toInt() + 3).toString()} - ${(DateTimeUtils.Date_yyyy.toInt() + 4).toString()}" )}, onClick = {
            Years =  (DateTimeUtils.Date_yyyy.toInt() + 3)
            showitem_year = false})
    }

    Column {
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
        if(loading) {
            LoadingUI()
        } else {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = appHorizontalDp(), vertical = 0.dp), horizontalArrangement = Arrangement.Start){

                AssistChip(
                    onClick = { showitem_year = true },
                    label = { Text(text = "${Years} - ${Years + 1}") }
                )

                Spacer(modifier = Modifier.width(10.dp))

                AssistChip(
                    onClick = {termBoolean = !termBoolean},
                    label = { Text(text = "第 $term 学期") },
                )

                Spacer(modifier = Modifier.width(10.dp))
                AssistChip(
                    onClick = {
                        refresh()
                    },
                    label = { Text(text = "搜索") },
                    leadingIcon = { Icon(painter = painterResource(R.drawable.search), contentDescription = "description") }
                )
            }


            val list = getGrade(vm)
            if(list.isEmpty()) EmptyUI()
            else {
                LazyColumn {
                    item { TotalGrade(vm) }
                    items(list.size) { index ->
                        val item = list[index]
                        val pass = item.pass
                        val passs = if (pass) "通过" else "挂科"
                        AnimationCardListItem(
                            headlineContent = { Text(item.courseName) },
                            supportingContent = { Text("学分: " + item.credit + "   绩点: " + item.gpa + "   分数: ${item.score}") },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.article),
                                    contentDescription = "Localized description",
                                )
                            },
                            trailingContent = { Text(passs) },
                            index = index
                        )
                    }
                    item {
                        AnimationCardListItem(
                            headlineContent = { Text("查看分数详细请点击此处进入教务数据") },
                            supportingContent = { Text(text = "您现在使用的是智慧社区接口,使用教务系统数据可查看详细成绩") },
                            trailingContent = { Icon(Icons.Filled.ArrowForward, contentDescription = "") },
                            modifier = Modifier.clickable {
                                refreshLogin()
                            },
                            index = 0
                        )
                    }
                    item { Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding())) }
                }

            }
        }
    }
}

