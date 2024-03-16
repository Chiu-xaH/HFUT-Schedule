package com.hfut.schedule.ui.Activity.success.calendar.nonet

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.datamodel.Community.CourseTotalResponse
import com.hfut.schedule.logic.datamodel.Community.courseDetailDTOList
import com.hfut.schedule.logic.utils.SharePrefs


@RequiresApi(Build.VERSION_CODES.O)
fun getCouses(Week : Int) : Array<Array<List<courseDetailDTOList>>> {
    val dayArray = Array(7) { Array<List<courseDetailDTOList>>(12) { emptyList() } }
    val json = SharePrefs.prefs.getString("Course", MyApplication.NullTotal)
    val result = Gson().fromJson(json, CourseTotalResponse::class.java).result.courseBasicInfoDTOList
    for (i in 0 until result.size){
        val Name = result[i].courseName
        val List = result[i].courseDetailDTOList
        for(j in 0 until List.size) {
            val section = List[j].section
            val weekCount = List[j].weekCount
            val week = List[j].week
            weekCount.forEach { item ->
                if(item == Week) {
                    List[j].name = Name
                    dayArray[week - 1][section - 1] = listOf(List[j])
                }
            }
        }
    }
    return dayArray
}

@RequiresApi(Build.VERSION_CODES.O)
fun getCourseINFO(weekday : Int,Week : Int) : MutableList<List<courseDetailDTOList>> {
    val new = mutableListOf<List<courseDetailDTOList>>()
    return try {
        if(weekday <= 7) {
            val days = getCouses(Week)[weekday - 1]
            for (i in days.indices){
                if(days[i].isNotEmpty())
                    days[i].forEach { new.add(days[i]) }
            }
            new
        } else new
    } catch (e : Exception) {
        e.printStackTrace()
        new
    }
}


@Composable
fun DetailInfos(sheet : courseDetailDTOList) {
    LazyColumn {
        item{
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Column() {
                    Card(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 3.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 15.dp,
                                vertical = 5.dp
                            ),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        ListItem(
                            headlineContent = { Text(sheet.place ) },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.near_me),
                                    contentDescription = "Localized description",
                                )
                            }
                        )
                        ListItem(
                            headlineContent = { Text(sheet.classTime ) },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.schedule),
                                    contentDescription = "Localized description",
                                )
                            }
                        )
                        ListItem(
                                headlineContent = { Text(sheet.teacher ) },
                        leadingContent = {
                            Icon(
                                painterResource(R.drawable.person),
                                contentDescription = "Localized description",
                            )
                        }
                        )
                        ListItem(
                            headlineContent = { Text("周${sheet.week} 第${sheet.section.toString()}节" ) },
                            supportingContent = { Text(text = "周数 ${sheet.weekCount.toString()} ")},
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.calendar),
                                    contentDescription = "Localized description",
                                )
                            }
                        )
                    }
                    Card(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 3.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 15.dp,
                                vertical = 5.dp
                            )
                            .clickable { },
                        shape = MaterialTheme.shapes.medium,
                    ){
                        ListItem(
                            headlineContent = { Text( "更多信息请到课程汇总中查看") },
                            leadingContent = {
                                Icon(
                                    Icons.Filled.ArrowForward,
                                    contentDescription = "Localized description",
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}