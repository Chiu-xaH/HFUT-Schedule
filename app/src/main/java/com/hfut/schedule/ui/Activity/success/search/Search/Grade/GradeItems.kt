package com.hfut.schedule.ui.Activity.success.search.Search.Grade

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.activity.LoginActivity
import com.hfut.schedule.logic.utils.GetDate
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.UIUtils.EmptyUI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TotaGrade() {

    val json = SharePrefs.prefs.getString("Grade", MyApplication.NullGrades)
    val result = Gson().fromJson(json,com.hfut.schedule.logic.datamodel.Community.GradeResponse::class.java).result
    val Class = result?.classRanking
    val Major = result?.majorRanking
    val TotalGPA = result?.gpa

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Column() {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium,
            ){
                ListItem(
                    headlineContent = {  Text("绩点(GPA)  $TotalGPA") },
                    supportingContent = { Text("班级排名: $Class   专业排名: $Major") },
                    leadingContent = { Icon(painterResource(R.drawable.flag), contentDescription = "Localized description",) },
                    modifier = Modifier.clickable {},
                    colors = ListItemDefaults.colors(MaterialTheme.colorScheme.primaryContainer)
                )
            }
        }
    }
}
@Composable
fun GradeItemUI(vm :LoginSuccessViewModel) {

    var loading by remember { mutableStateOf(true) }
    var clicked by remember { mutableStateOf(false) }

    var term  by remember { mutableStateOf( "1") }
    val month = GetDate.Date_MM.toInt()
    if( month >= 9 || month <= 2) term = "1"
    else term = "2"
    var Years by remember { mutableStateOf(
        if (month <= 8) GetDate.Date_yyyy.toInt() - 1
        else GetDate.Date_yyyy.toInt()
    ) }


    var termBoolean by remember { mutableStateOf(prefs.getBoolean("term",true)) }

    if (termBoolean) term = "1"
    else term = "2"

    var showitem_year by remember { mutableStateOf(false) }
    DropdownMenu(expanded = showitem_year, onDismissRequest = { showitem_year = false }, offset = DpOffset(15.dp,0.dp)) {
        DropdownMenuItem(text = { Text(text = "${(GetDate.Date_yyyy.toInt() - 3).toString()} - ${(GetDate.Date_yyyy.toInt() - 2).toString()}" )}, onClick = {
            Years = (GetDate.Date_yyyy.toInt() - 3)
            showitem_year = false})
        DropdownMenuItem(text = { Text(text = "${(GetDate.Date_yyyy.toInt() - 2).toString()} - ${(GetDate.Date_yyyy.toInt() - 1).toString()}" )}, onClick = {
            Years =  (GetDate.Date_yyyy.toInt() - 2)
            showitem_year = false})
        DropdownMenuItem(text = { Text(text = "${(GetDate.Date_yyyy.toInt() - 1).toString()} - ${(GetDate.Date_yyyy.toInt() ).toString()}" )},onClick = {
            Years =  (GetDate.Date_yyyy.toInt() - 1)
            showitem_year = false})
        DropdownMenuItem(text = { Text(text = "${(GetDate.Date_yyyy.toInt() ).toString()} - ${(GetDate.Date_yyyy.toInt() + 1).toString()}" )}, onClick = {
            Years =  GetDate.Date_yyyy.toInt()
            showitem_year = false})
        DropdownMenuItem(text = { Text(text = "${(GetDate.Date_yyyy.toInt() + 1).toString()} - ${(GetDate.Date_yyyy.toInt() + 2).toString()}" )}, onClick = {
            Years =  (GetDate.Date_yyyy.toInt() + 1)
            showitem_year = false})
        DropdownMenuItem(text = { Text(text = "${(GetDate.Date_yyyy.toInt() + 2).toString()} - ${(GetDate.Date_yyyy.toInt() + 3).toString()}" )}, onClick = {
            Years =  (GetDate.Date_yyyy.toInt() + 2)
            showitem_year = false})
        DropdownMenuItem(text = { Text(text = "${(GetDate.Date_yyyy.toInt() + 3).toString()} - ${(GetDate.Date_yyyy.toInt() + 4).toString()}" )}, onClick = {
            Years =  (GetDate.Date_yyyy.toInt() + 3)
            showitem_year = false})
    }

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 15.dp, vertical = 0.dp), horizontalArrangement = Arrangement.Start){

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
                        SharePrefs.SaveBoolean("term",true,termBoolean)
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



    @SuppressLint("SuspiciousIndentation")
    @Composable
    fun UIS(){
        if(getGrade().size == 0) EmptyUI()
        else
        LazyColumn {
            item { TotaGrade() }
            items(getGrade().size) { item ->
                val pass = getGrade()[item].pass
                var passs = ""
                if (pass == true) passs = "通过"
                else passs = "挂科"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column() {
                        Card(
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp, vertical = 5.dp),
                            shape = MaterialTheme.shapes.medium,
                        ) {
                            ListItem(
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
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column() {
                        Card(
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp, vertical = 5.dp),
                            shape = MaterialTheme.shapes.medium,
                        ) {
                            ListItem(
                                headlineContent = { Text("想查看更多详细信息请登录") },
                                supportingContent = { Text(text = "您现在使用的是Community接口,登陆后使用教务系统数据可查看详细成绩") },
                                leadingContent = {
                                    Icon(
                                        Icons.Filled.ArrowForward,
                                        contentDescription = ""
                                    )
                                },
                                modifier = Modifier.clickable {
                                    val it = Intent(MyApplication.context, LoginActivity::class.java).apply {
                                        putExtra("nologin",false)
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    MyApplication.context.startActivity(it)
                                },
                            )
                        }
                    }
                }
            }
        }
    }


    if(clicked){
        AnimatedVisibility(
            visible = loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center)  {
                Spacer(modifier = Modifier.height(5.dp))
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(50.dp))
            }
        }

        AnimatedVisibility(
            visible = !loading,
            enter = fadeIn(),
            exit = fadeOut()
        ){ UIS() }
    }
    else { UIS() }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun GradeItemUIJXGLSTU() {
    if(getGradeJXGLSTU().size == 0) EmptyUI()
    else
    LazyColumn{
        item { TotaGrade() }
        items(getGradeJXGLSTU().size) { item ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Column() {
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp, vertical = 5.dp),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        ListItem(
                            headlineContent = {  Text(getGradeJXGLSTU()[item].title) },
                            overlineContent = { Text( "成绩  "+ getGradeJXGLSTU()[item].totalGrade + "  |  绩点  " + getGradeJXGLSTU()[item].GPA +  "  |  学分  " + getGradeJXGLSTU()[item].score) },
                            leadingContent = { Icon(painterResource(R.drawable.article), contentDescription = "Localized description",) },
                            supportingContent = { Text(getGradeJXGLSTU()[item].grade) },
                            modifier = Modifier.clickable {},
                        )
                    }
                }
            }
        }
    }
}