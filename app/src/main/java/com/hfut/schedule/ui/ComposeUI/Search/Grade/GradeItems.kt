package com.hfut.schedule.ui.ComposeUI.Search.Grade

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.SharePrefs

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
                    headlineContent = {  Text("绩点(GPA)  ${TotalGPA}") },
                    supportingContent = { Text("班级排名: ${Class}   专业排名: ${Major}") },
                    leadingContent = { Icon(painterResource(R.drawable.flag), contentDescription = "Localized description",) },
                    modifier = Modifier.clickable {},
                    colors = ListItemDefaults.colors(MaterialTheme.colorScheme.errorContainer)
                )
            }
        }
    }
}
@Composable
fun GradeItemUI() {

    val json = SharePrefs.prefs.getString("Grade", MyApplication.NullGrades)
    val result = Gson().fromJson(json,com.hfut.schedule.logic.datamodel.Community.GradeResponse::class.java).result
    val Class = result?.classRanking
    val Major = result?.majorRanking
    val TotalGPA = result?.gpa

    LazyColumn{
        item { TotaGrade() }
        items(getGrade().size) { item ->
            val pass = getGrade()[item].pass
            var passs = ""
            if(pass==true) passs = "通过"
            else passs = "挂科"
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
                            headlineContent = {  Text(getGrade()[item].courseName) },
                            supportingContent = { Text("学分: " + getGrade()[item].credit  + "   绩点: " + getGrade()[item].gpa   + "   分数: ${getGrade()[item].score}") },
                            leadingContent = { Icon(painterResource(R.drawable.article), contentDescription = "Localized description",) },
                            trailingContent = { Text(passs ) },
                            modifier = Modifier.clickable {},
                        )
                    }
                }
            }
        }
        item {
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
                            headlineContent = {  Text("想查看更多详细信息请登录") },
                            supportingContent = { Text(text = "您现在使用的是Community接口,登陆后使用教务系统数据可查看详细成绩")},
                            leadingContent = { Icon(Icons.Filled.ArrowForward, contentDescription = "")},
                            modifier = Modifier.clickable {},
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GradeItemUIJXGLSTU() {

    LazyColumn{
        item { TotaGrade() }
        items(getGrade().size) { item ->
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