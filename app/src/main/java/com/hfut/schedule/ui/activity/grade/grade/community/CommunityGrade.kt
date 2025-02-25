package com.hfut.schedule.ui.activity.grade.grade.community

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import com.hfut.schedule.ui.utils.components.LoadingUI
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.activity.main.LoginActivity
import com.hfut.schedule.logic.utils.DateTimeManager
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.activity.grade.getGrade
import com.hfut.schedule.ui.utils.components.AppHorizontalDp
import com.hfut.schedule.ui.utils.components.EmptyUI
import com.hfut.schedule.ui.utils.components.MyCustomCard
import com.hfut.schedule.ui.utils.components.StyleCardListItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TotalGrade() {

    val json = SharePrefs.prefs.getString("Grade", MyApplication.NullGrades)
    val result = Gson().fromJson(json,com.hfut.schedule.logic.beans.community.GradeResponse::class.java).result
    val Class = result.classRanking
    val Major = result.majorRanking
    val TotalGPA = result.gpa

    StyleCardListItem(
        headlineContent = {  Text("绩点(GPA)  $TotalGPA") },
        supportingContent = { Text("班级排名: $Class   专业排名: $Major") },
        leadingContent = { Icon(painterResource(R.drawable.flag), contentDescription = "Localized description",) },
        modifier = Modifier.clickable {},
        color = MaterialTheme.colorScheme.primaryContainer
    )

//    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
//        Column() {
//            MyCustomCard{
//
//            }
//        }
//    }
}

@Composable
fun GradeItemUI(vm :NetWorkViewModel, innerPadding : PaddingValues) {

    var loading by remember { mutableStateOf(true) }
    var clicked by remember { mutableStateOf(false) }

    var term  by remember { mutableStateOf( "1") }
    val month = DateTimeManager.Date_MM.toInt()
    if( month >= 9 || month <= 2) term = "1"
    else term = "2"
    var Years by remember { mutableStateOf(
        if (month <= 8) DateTimeManager.Date_yyyy.toInt() - 1
        else DateTimeManager.Date_yyyy.toInt()
    ) }


    var termBoolean by remember { mutableStateOf(prefs.getBoolean("term",true)) }

    if (termBoolean) term = "1"
    else term = "2"

    var showitem_year by remember { mutableStateOf(false) }
    DropdownMenu(expanded = showitem_year, onDismissRequest = { showitem_year = false }, offset = DpOffset(AppHorizontalDp(),0.dp)) {
        DropdownMenuItem(text = { Text(text = "${(DateTimeManager.Date_yyyy.toInt() - 3).toString()} - ${(DateTimeManager.Date_yyyy.toInt() - 2).toString()}" )}, onClick = {
            Years = (DateTimeManager.Date_yyyy.toInt() - 3)
            showitem_year = false})
        DropdownMenuItem(text = { Text(text = "${(DateTimeManager.Date_yyyy.toInt() - 2).toString()} - ${(DateTimeManager.Date_yyyy.toInt() - 1).toString()}" )}, onClick = {
            Years =  (DateTimeManager.Date_yyyy.toInt() - 2)
            showitem_year = false})
        DropdownMenuItem(text = { Text(text = "${(DateTimeManager.Date_yyyy.toInt() - 1).toString()} - ${(DateTimeManager.Date_yyyy.toInt() ).toString()}" )},onClick = {
            Years =  (DateTimeManager.Date_yyyy.toInt() - 1)
            showitem_year = false})
        DropdownMenuItem(text = { Text(text = "${(DateTimeManager.Date_yyyy.toInt() ).toString()} - ${(DateTimeManager.Date_yyyy.toInt() + 1).toString()}" )}, onClick = {
            Years =  DateTimeManager.Date_yyyy.toInt()
            showitem_year = false})
        DropdownMenuItem(text = { Text(text = "${(DateTimeManager.Date_yyyy.toInt() + 1).toString()} - ${(DateTimeManager.Date_yyyy.toInt() + 2).toString()}" )}, onClick = {
            Years =  (DateTimeManager.Date_yyyy.toInt() + 1)
            showitem_year = false})
        DropdownMenuItem(text = { Text(text = "${(DateTimeManager.Date_yyyy.toInt() + 2).toString()} - ${(DateTimeManager.Date_yyyy.toInt() + 3).toString()}" )}, onClick = {
            Years =  (DateTimeManager.Date_yyyy.toInt() + 2)
            showitem_year = false})
        DropdownMenuItem(text = { Text(text = "${(DateTimeManager.Date_yyyy.toInt() + 3).toString()} - ${(DateTimeManager.Date_yyyy.toInt() + 4).toString()}" )}, onClick = {
            Years =  (DateTimeManager.Date_yyyy.toInt() + 3)
            showitem_year = false})
    }

    @SuppressLint("SuspiciousIndentation")
    @Composable
    fun UIS(){
        Column {
            Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppHorizontalDp(), vertical = 0.dp), horizontalArrangement = Arrangement.Start){

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
                val CommuityTOKEN = prefs.getString("TOKEN","")
                AssistChip(
                    onClick = {
                        CoroutineScope(Job()).launch {
                            async {
                                loading = true
                                clicked = true
                                SharePrefs.saveBoolean("term",true,termBoolean)
                            }.await()
                            async { CommuityTOKEN?.let { vm.getGrade(it,Years.toString() + "-"+(Years+1),term) } }
                            async {
                                Handler(Looper.getMainLooper()).post{
                                    vm.GradeData.observeForever { result ->
                                        if (result != null) {
                                            if(result.contains("success")) {
                                                CoroutineScope(Job()).launch {
                                                    async {
                                                        delay(500)
                                                        async { loading = false }
                                                        async { getGrade() }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    label = { Text(text = "搜索") },
                    leadingIcon = { Icon(painter = painterResource(R.drawable.search), contentDescription = "description") }
                )
            }

            if(getGrade().isEmpty()) EmptyUI()
            else
                LazyColumn {
                    item { TotalGrade() }
                    items(getGrade().size) { item ->
                        val pass = getGrade()[item].pass
                        var passs = ""
                        if (pass == true) passs = "通过"
                        else passs = "挂科"
                        StyleCardListItem(
                            headlineContent = { Text(getGrade()[item].courseName) },
                            supportingContent = { Text("学分: " + getGrade()[item].credit + "   绩点: " + getGrade()[item].gpa + "   分数: ${getGrade()[item].score}") },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.article),
                                    contentDescription = "Localized description",
                                )
                            },
                            trailingContent = { Text(passs) },
                            modifier = Modifier.clickable {},
                        )
                    }
                    item {
                        StyleCardListItem(
                            headlineContent = { Text("查看分数详细请点击此处进入教务数据") },
                            supportingContent = { Text(text = "您现在使用的是智慧社区接口,使用教务系统数据可查看详细成绩") },
                            trailingContent = { Icon(Icons.Filled.ArrowForward, contentDescription = "") },
                            modifier = Modifier.clickable {
                                val it = Intent(MyApplication.context, LoginActivity::class.java).apply {
                                    putExtra("nologin",false)
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                MyApplication.context.startActivity(it)
                            },
                        )
                    }
                    item { Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding())) }
                }
        }
    }




        if(clicked){
            Box{
                androidx.compose.animation.AnimatedVisibility(
                    visible = loading,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .padding(innerPadding)
                        .align(Alignment.Center)
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
                        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center)  {
                            LoadingUI()
                        }
                    }
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = !loading,
                    enter = fadeIn(),
                    exit = fadeOut()
                ){ UIS() }
            }
        }
        else { UIS() }


}

