package com.hfut.schedule.ui.Activity.success.search.Search.Grade

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.datamodel.Community.ExamResponse
import com.hfut.schedule.logic.datamodel.Community.GradeAllResponse
import com.hfut.schedule.logic.datamodel.Community.GradeAllResult
import com.hfut.schedule.logic.datamodel.Community.GradeAvgResponse
import com.hfut.schedule.logic.datamodel.Community.examArrangementList
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.UIUtils.DividerText
import com.hfut.schedule.ui.UIUtils.MyToast

@Composable
fun GradeCountUI(innerPadding : PaddingValues) {
    Column {
        //Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
        Spacer(modifier = Modifier.height(5.dp))
        DividerText(text = "总成绩")
        AvgGrade()
       // Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
        DividerText(text = "学期统计")
        AllGrade()
        DividerText(text = "统计图")
    }
}

@Composable
fun AvgGrade() {
    val jsonAvg = SharePrefs.prefs.getString("Avg", MyApplication.NullGrades)
    val resultAvg = Gson().fromJson(jsonAvg, GradeAvgResponse::class.java).result
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 5.dp),
        shape = MaterialTheme.shapes.medium,
    ){
        Row {
            ListItem(
                headlineContent = { Text("绩点(GPA)") },
                supportingContent = { Text("我的平均 ${resultAvg.myAvgGpa}\n专业平均 ${resultAvg.majorAvgGpa}\n专业排名 ${resultAvg.majorAvgGpaRanking}") },
                leadingContent = { Icon(painterResource(R.drawable.award_star), contentDescription = "Localized description",) },
                modifier = Modifier

                    .fillMaxWidth()
                    .weight(.5f),
                colors = ListItemDefaults.colors(MaterialTheme.colorScheme.primaryContainer)
            )

            ListItem(
                headlineContent = { Text("分数") },
                supportingContent = { Text("我的平均 ${resultAvg.myAvgScore}\n专业平均 ${resultAvg.majorAvgScore}\n专业排名 ${resultAvg.majorAvgScoreRanking}") },
                leadingContent = { Icon(painterResource(R.drawable.filter_vintage), contentDescription = "Localized description",) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(.5f),
                colors = ListItemDefaults.colors(MaterialTheme.colorScheme.primaryContainer)
            )
        }
    }
}

@Composable
fun AllGrade() {
    val chineseList = listOf("大一上","大一下","大二上","大二下","大三上","大三下","大四上","大四下")
    LazyColumn {
        items(getAllGrade().size) { item ->
            val list = getAllGrade()[item]
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium,
            ){
                Text(text = chineseList[item] + "学期", modifier = Modifier.padding(horizontal = 15.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.primary)

                Row {
                    ListItem(
                        headlineContent = { Text("绩点(GPA)") },
                        supportingContent = { Text("我的平均 ${list.myAvgGpa}\n专业平均 ${list.majorAvgGpa}\n最高绩点 ${list.maxAvgGpa}") },
                        leadingContent = { Icon(painterResource(R.drawable.award_star), contentDescription = "Localized description",) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )

                    ListItem(
                        headlineContent = { Text("分数") },
                        supportingContent = { Text("我的平均 ${list.myAvgScore}\n专业平均 ${list.majorAvgScore}\n最高分数 ${list.maxAvgScore}") },
                        leadingContent = { Icon(painterResource(R.drawable.filter_vintage), contentDescription = "Localized description",) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }
    }
}


fun getAllGrade() : MutableList<GradeAllResult> {
    val json = prefs.getString("AvgAll","")
    var addList = mutableListOf<GradeAllResult>()
    return try {
        val list = Gson().fromJson(json,GradeAllResponse::class.java).result

        for(i in list.indices) {
            if(list[i].myAvgGpa != null)
                addList.add(GradeAllResult(list[i].myAvgScore,list[i].myAvgGpa,list[i].majorAvgScore,list[i].majorAvgGpa,list[i].maxAvgScore,list[i].maxAvgGpa))
        }
        addList
    } catch (e : Exception) {
        addList
    }

}