package com.hfut.schedule.ui.ComposeUI.Saved

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.datamodel.Community.CourseTotalResponse
import com.hfut.schedule.logic.datamodel.Community.courseDetailDTOList
import com.hfut.schedule.logic.utils.GetDate
import com.hfut.schedule.logic.utils.GetDateReconstruction
import com.hfut.schedule.logic.utils.SharePrefs
import java.time.LocalDate
import java.time.temporal.ChronoUnit


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReconstructionUI() {
  ReBuildUI()
}

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
    if(weekday <= 7) {
        val days = getCouses(Week)[weekday - 1]
        for (i in 0 until days.size){
            if(days[i].isNotEmpty())
                days[i].forEach { new.add(days[i]) }
        }
        return new
    } else return new
}


@OptIn(ExperimentalAnimationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReBuildUI() {
    var table_1_1 by rememberSaveable { mutableStateOf("") }
    var table_1_2 by rememberSaveable { mutableStateOf("") }
    var table_1_3 by rememberSaveable { mutableStateOf("") }
    var table_1_4 by rememberSaveable { mutableStateOf("") }
    var table_1_5 by rememberSaveable { mutableStateOf("") }
    var table_2_1 by rememberSaveable { mutableStateOf("") }
    var table_2_2 by rememberSaveable { mutableStateOf("") }
    var table_2_3 by rememberSaveable { mutableStateOf("") }
    var table_2_4 by rememberSaveable { mutableStateOf("") }
    var table_2_5 by rememberSaveable { mutableStateOf("") }
    var table_3_1 by rememberSaveable { mutableStateOf("") }
    var table_3_2 by rememberSaveable { mutableStateOf("") }
    var table_3_3 by rememberSaveable { mutableStateOf("") }
    var table_3_4 by rememberSaveable { mutableStateOf("") }
    var table_3_5 by rememberSaveable { mutableStateOf("") }
    var table_4_1 by rememberSaveable { mutableStateOf("") }
    var table_4_2 by rememberSaveable { mutableStateOf("") }
    var table_4_3 by rememberSaveable { mutableStateOf("") }
    var table_4_4 by rememberSaveable { mutableStateOf("") }
    var table_4_5 by rememberSaveable { mutableStateOf("") }

      //切换周数
    var Bianhuaweeks by rememberSaveable { mutableStateOf(GetDate.weeksBetween) }
    var date by rememberSaveable { mutableStateOf(LocalDate.now()) }

    val table = arrayOf(
        arrayOf(table_1_1, table_1_2, table_1_3, table_1_4, table_1_5),
        arrayOf(table_2_1, table_2_2, table_2_3, table_2_4, table_2_5),
        arrayOf(table_3_1, table_3_2, table_3_3, table_3_4, table_3_5),
        arrayOf(table_4_1, table_4_2, table_4_3, table_4_4, table_4_5)
    )

    fun Updates() {
        table_1_1 = ""
        table_1_2 = ""
        table_1_3 = ""
        table_1_4 = ""
        table_1_5 = ""
        table_2_1 = ""
        table_2_2 = ""
        table_2_3 = ""
        table_2_4 = ""
        table_2_5 = ""
        table_3_1 = ""
        table_3_2 = ""
        table_3_3 = ""
        table_3_4 = ""
        table_3_5 = ""
        table_4_1 = ""
        table_4_2 = ""
        table_4_3 = ""
        table_4_4 = ""
        table_4_5 = ""
        for (j in 0 until 5 ) {
            var info = ""
            val lists = getCourseINFO(j +1 ,Bianhuaweeks.toInt())

            for(i in 0 until lists.size) {
                val text = lists[i][0]
                val name = text.name
                var time = text.classTime
                time = time.substringBefore("-")
                var room = text.place
                room = room.replace("学堂","")
                info = time + "\n" + name + "\n" + room
                when(text.section) {
                    1 -> table[0][j] = info
                    3 -> table[1][j] = info
                    5 -> table[2][j] = info
                    7 -> table[3][j] = info
                }
            }
        }
    }

    Updates()

    Column {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp),horizontalArrangement = Arrangement.Center) {
            LazyColumn {
                item{
                   Row{
                      // Text(text = date.toString().substring(5,10))
                   }
                }
                items(4) { sectionItem ->
                    LazyRow {
                        items(5) { weekDayItem ->
                            Box (modifier = Modifier
                                .weight(0.2f)
                                .padding(4.dp)){
                                Card(
                                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                                    modifier = Modifier
                                        .height(100.dp)
                                        .width(60.dp),
                                    shape = MaterialTheme.shapes.extraSmall,
                                ) {
                                    Text(text = table[sectionItem][weekDayItem],fontSize = 14.sp, textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }
                }
            }
        }



        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),horizontalArrangement = Arrangement.Center) {

            FilledTonalButton(
                onClick = { if (Bianhuaweeks > 1) Bianhuaweeks-- - 1 },
                ) { Text(text = "上一周") }

            Spacer(modifier = Modifier.width(20.dp))

            FilledTonalButton(
                onClick = { Bianhuaweeks = GetDate.Benweeks }

            ) {
                AnimatedContent(
                    targetState = Bianhuaweeks,
                    transitionSpec = {
                        scaleIn(
                            animationSpec = tween(500)
                        ) with scaleOut(animationSpec = tween(500))
                    }, label = ""
                ) { annumber ->
                    Text(text = "第${annumber}周",)
                }

            }
            Spacer(modifier = Modifier.width(20.dp))

            FilledTonalButton(
                onClick = { if (Bianhuaweeks < 20) Bianhuaweeks++ + 1 }
            ) { Text(text = "下一周") }
        }
    }
}