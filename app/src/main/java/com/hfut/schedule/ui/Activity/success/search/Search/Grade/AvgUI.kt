package com.hfut.schedule.ui.Activity.success.search.Search.Grade

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.datamodel.Community.GradeAllResponse
import com.hfut.schedule.logic.datamodel.Community.GradeAllResult
import com.hfut.schedule.logic.datamodel.Community.GradeAvgResponse
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.UIUtils.DividerText
import com.hfut.schedule.ui.UIUtils.MyCard
import com.hfut.schedule.ui.UIUtils.ScrollText

@SuppressLint("SuspiciousIndentation")
@Composable
fun GradeCountUI(innerPadding : PaddingValues) {

    Column (modifier =   Modifier.verticalScroll(rememberScrollState())){
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
        Spacer(modifier = Modifier.height(5.dp))


        DividerText(text = "总成绩 (整个大学阶段)")
        AvgGrade()
       // Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
        DividerText(text = "学期统计 (各学期)")
        AllGrade()
        DividerText(text = "统计图")
        MyCard {
            ListItem(
                headlineContent = { Text("正在开发") },
                leadingContent = { Icon(painterResource(R.drawable.info), contentDescription = "Localized description",) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
    }
}

@Composable
fun AvgGrade() {
    val jsonAvg = SharePrefs.prefs.getString("Avg", MyApplication.NullGrades)
    val resultAvg = Gson().fromJson(jsonAvg, GradeAvgResponse::class.java).result
    MyCard {
        Text(text = "我的水平", modifier = Modifier.padding(horizontal = 15.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.primary)

        Row {
            ListItem(
                headlineContent = { Text("绩点 ${resultAvg.myAvgGpa}") },
                supportingContent = { Text("排名 ${resultAvg.majorAvgGpaRanking}") },
                leadingContent = { Icon(painterResource(R.drawable.award_star), contentDescription = "Localized description",) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(.5f),
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )

            ListItem(
                headlineContent = { Text("分数 ${resultAvg.myAvgScore}") },
                supportingContent = { Text("排名 ${resultAvg.majorAvgScoreRanking}") },
                leadingContent = { Icon(painterResource(R.drawable.filter_vintage), contentDescription = "Localized description",) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(.5f),
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
        }
        Divider()
        Text(text = "专业水平", modifier = Modifier.padding(horizontal = 15.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.primary)

        Row {
            ListItem(
                headlineContent = { Text("绩点") },
                supportingContent = { Text("平均 ${resultAvg.majorAvgGpa}\n最高 (待开发)") },
                leadingContent = { Icon(painterResource(R.drawable.award_star), contentDescription = "Localized description",) },
                modifier = Modifier

                    .fillMaxWidth()
                    .weight(.5f),
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )

            ListItem(
                headlineContent = { Text("分数") },
                supportingContent = { Text("平均 ${resultAvg.majorAvgScore}\n最高 (待开发)") },
                leadingContent = { Icon(painterResource(R.drawable.filter_vintage), contentDescription = "Localized description",) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(.5f),
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
        }
    }
}

@Composable
fun AllGrade() {
    val chineseList = listOf("大一上","大一下","大二上","大二下","大三上","大三下","大四上","大四下")
    LazyRow (modifier = Modifier.padding(horizontal = 12.dp)){
        items(getAllGrade().size) { item ->
            val list = getAllGrade()[item]
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 1.75.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 3.dp, vertical = 5.dp)
                    .width(382.dp),
                shape = MaterialTheme.shapes.medium,
            ){
                Text(text = chineseList[item] + "学期", modifier = Modifier.padding(horizontal = 15.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.primary)
                Text(text = "我的水平", modifier = Modifier.padding(horizontal = 15.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.primary)

                Row {
                    ListItem(
                        headlineContent = { Text("绩点 ${list.myAvgGpa}") },
                        leadingContent = { Icon(painterResource(R.drawable.award_star), contentDescription = "Localized description",) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )

                    ListItem(
                        headlineContent = { Text("分数 ${list.myAvgScore}") },
                        leadingContent = { Icon(painterResource(R.drawable.filter_vintage), contentDescription = "Localized description",) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
                Divider()
                Text(text = "专业水平", modifier = Modifier.padding(horizontal = 15.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.primary)
                Row {
                    ListItem(
                        headlineContent = { Text("绩点(GPA)") },
                        supportingContent = { Text("平均 ${list.majorAvgGpa}\n最高 ${list.maxAvgGpa}") },
                        leadingContent = { Icon(painterResource(R.drawable.award_star), contentDescription = "Localized description",) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )

                    ListItem(
                        headlineContent = { Text("分数") },
                        supportingContent = { Text("平均 ${list.majorAvgScore}\n最高 ${list.maxAvgScore}") },
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