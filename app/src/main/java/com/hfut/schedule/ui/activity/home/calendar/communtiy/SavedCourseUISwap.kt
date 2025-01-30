package com.hfut.schedule.ui.activity.home.calendar.communtiy

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.viewmodel.UIViewModel
import com.hfut.schedule.logic.beans.community.courseDetailDTOList
import com.hfut.schedule.logic.utils.DateTimeManager
import com.hfut.schedule.logic.utils.DateTimeManager.Benweeks
import com.hfut.schedule.logic.utils.Semseter.getSemseter
import com.hfut.schedule.logic.utils.Semseter.getSemseterCloud
import com.hfut.schedule.ui.activity.home.calendar.examToCalendar
import com.hfut.schedule.ui.activity.home.calendar.getScheduleDate
import com.hfut.schedule.ui.activity.home.search.functions.exam.getExam
import com.hfut.schedule.ui.activity.home.search.functions.exam.getExamJXGLSTU
import com.hfut.schedule.ui.utils.components.MyToast
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class,
    ExperimentalMaterialApi::class
)
@SuppressLint("SuspiciousIndentation", "CoroutineCreationDuringComposition")
@Composable
fun SaveCourse(
    showAll: Boolean,
    innerPaddings: PaddingValues,
    vmUI : UIViewModel,
    friendUserName : String? = null,
    onDateChange : (LocalDate) ->Unit,
    today: LocalDate,
    vm : NetWorkViewModel) {

    val table_1_1 by rememberSaveable { mutableStateOf("") }
    val table_1_2 by rememberSaveable { mutableStateOf("") }
    val table_1_3 by rememberSaveable { mutableStateOf("") }
    val table_1_4 by rememberSaveable { mutableStateOf("") }
    val table_1_5 by rememberSaveable { mutableStateOf("") }
    val table_2_1 by rememberSaveable { mutableStateOf("") }
    val table_2_2 by rememberSaveable { mutableStateOf("") }
    val table_2_3 by rememberSaveable { mutableStateOf("") }
    val table_2_4 by rememberSaveable { mutableStateOf("") }
    val table_2_5 by rememberSaveable { mutableStateOf("") }
    val table_3_1 by rememberSaveable { mutableStateOf("") }
    val table_3_2 by rememberSaveable { mutableStateOf("") }
    val table_3_3 by rememberSaveable { mutableStateOf("") }
    val table_3_4 by rememberSaveable { mutableStateOf("") }
    val table_3_5 by rememberSaveable { mutableStateOf("") }
    val table_4_1 by rememberSaveable { mutableStateOf("") }
    val table_4_2 by rememberSaveable { mutableStateOf("") }
    val table_4_3 by rememberSaveable { mutableStateOf("") }
    val table_4_4 by rememberSaveable { mutableStateOf("") }
    val table_4_5 by rememberSaveable { mutableStateOf("") }
    val table_5_1 by rememberSaveable { mutableStateOf("") }
    val table_5_2 by rememberSaveable { mutableStateOf("") }
    val table_5_3 by rememberSaveable { mutableStateOf("") }
    val table_5_4 by rememberSaveable { mutableStateOf("") }
    val table_5_5 by rememberSaveable { mutableStateOf("") }
    val table_6_1 by rememberSaveable { mutableStateOf("") }
    val table_6_2 by rememberSaveable { mutableStateOf("") }
    val table_6_3 by rememberSaveable { mutableStateOf("") }
    val table_6_4 by rememberSaveable { mutableStateOf("") }
    val table_6_5 by rememberSaveable { mutableStateOf("") }
    val table_1_6 by rememberSaveable { mutableStateOf("") }
    val table_1_7 by rememberSaveable { mutableStateOf("") }
    val table_2_6 by rememberSaveable { mutableStateOf("") }
    val table_2_7 by rememberSaveable { mutableStateOf("") }
    val table_3_6 by rememberSaveable { mutableStateOf("") }
    val table_3_7 by rememberSaveable { mutableStateOf("") }
    val table_4_6 by rememberSaveable { mutableStateOf("") }
    val table_4_7 by rememberSaveable { mutableStateOf("") }
    val table_5_6 by rememberSaveable { mutableStateOf("") }
    val table_5_7 by rememberSaveable { mutableStateOf("") }
    val table_6_6 by rememberSaveable { mutableStateOf("") }
    val table_6_7 by rememberSaveable { mutableStateOf("") }


    val sheet_1_1 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_1_2  = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_1_3 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_1_4 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_1_5 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_2_1 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_2_2 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_2_3 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_2_4 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_2_5 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_3_1 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_3_2  = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_3_3 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_3_4  = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_3_5  = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_4_1  = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_4_2  = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_4_3 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_4_4 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_4_5 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_5_1 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_5_2  = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_5_3 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_5_4 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_5_5 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_6_1 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_6_2  = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_6_3 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_6_4 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_6_5 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_1_6= courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_1_7= courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_2_6 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_2_7= courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_3_6= courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_3_7= courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_4_6 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_4_7= courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_5_6 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_5_7 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_6_6= courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    val sheet_6_7= courseDetailDTOList(0,0,"","","", listOf(0),0,"")



    //切换周数
    var Bianhuaweeks by rememberSaveable { mutableStateOf(DateTimeManager.weeksBetween) }
    //var date by rememberSaveable { mutableStateOf(LocalDate.now()) }
  //  Log.d("本周", GetDate.a.toString())

    val tableall = arrayOf(
        table_1_1, table_1_2, table_1_3, table_1_4, table_1_5,table_1_6,table_1_7,
        table_2_1, table_2_2, table_2_3, table_2_4, table_2_5,table_2_6,table_2_7,
        table_3_1, table_3_2, table_3_3, table_3_4, table_3_5,table_3_6,table_3_7,
        table_4_1, table_4_2, table_4_3, table_4_4, table_4_5,table_4_6,table_4_7,
        table_5_1, table_5_2, table_5_3, table_5_4, table_5_5,table_5_6,table_5_7,
        table_6_1, table_6_2, table_6_3, table_6_4, table_6_5,table_6_6,table_6_7,
    )

    val sheetall = arrayOf(
        sheet_1_1, sheet_1_2, sheet_1_3, sheet_1_4, sheet_1_5,sheet_1_6,sheet_1_7,
        sheet_2_1, sheet_2_2, sheet_2_3, sheet_2_4, sheet_2_5,sheet_2_6,sheet_2_7,
        sheet_3_1, sheet_3_2, sheet_3_3, sheet_3_4, sheet_3_5,sheet_3_6,sheet_3_7,
        sheet_4_1, sheet_4_2, sheet_4_3, sheet_4_4, sheet_4_5,sheet_4_6,sheet_4_7,
        sheet_5_1, sheet_5_2, sheet_5_3, sheet_5_4, sheet_5_5,sheet_5_6,sheet_5_7,
        sheet_6_1, sheet_6_2, sheet_6_3, sheet_6_4, sheet_6_5,sheet_6_6,sheet_6_7,
    )

    val table = arrayOf(
        table_1_1, table_1_2, table_1_3, table_1_4, table_1_5,
        table_2_1, table_2_2, table_2_3, table_2_4, table_2_5,
        table_3_1, table_3_2, table_3_3, table_3_4, table_3_5,
        table_4_1, table_4_2, table_4_3, table_4_4, table_4_5,
        table_5_1, table_5_2, table_5_3, table_5_4, table_5_5,
        table_6_1, table_6_2, table_6_3, table_6_4, table_6_5,
    )

    val sheet = arrayOf(
        sheet_1_1, sheet_1_2, sheet_1_3, sheet_1_4, sheet_1_5,
        sheet_2_1, sheet_2_2, sheet_2_3, sheet_2_4, sheet_2_5,
        sheet_3_1, sheet_3_2, sheet_3_3, sheet_3_4, sheet_3_5,
        sheet_4_1, sheet_4_2, sheet_4_3, sheet_4_4, sheet_4_5,
        sheet_5_1, sheet_5_2, sheet_5_3, sheet_5_4, sheet_5_5,
        sheet_6_1, sheet_6_2, sheet_6_3, sheet_6_4, sheet_6_5
    )


    //填充UI与更新
    fun UpdatesAll() {
        try {
            for(i in tableall.indices)
                tableall[i] = ""
            for(i in sheetall.indices)
                sheetall[i] = courseDetailDTOList(0,0,"","","", listOf(0),0,"")

            for (j in 0 until 7 ) {
                var info = ""
                val lists = getCourseINFO(j +1 ,Bianhuaweeks.toInt(),friendUserName)

                for(i in 0 until lists.size) {
                    val text = lists[i][0]
                    val name = text.name
                    var time = text.classTime
                    time = time.substringBefore("-")
                    var room = text.place
                    if (room != null) {
                        room = room.replace("学堂","")
                    }
                    info = time + "\n" + name + "\n" + room


                    when (j) {
                        0 -> {
                            when(text.section) {
                                1 -> {
                                    tableall[0] = info
                                    sheetall[0] = text
                                }
                                3 -> {
                                    tableall[7] = info
                                    sheetall[7] = text
                                }
                                5 -> {
                                    tableall[14] = info
                                    sheetall[14] = text
                                }
                                7 -> {
                                    tableall[21] = info
                                    sheetall[21] = text
                                }
                                9 -> {
                                    tableall[28] = info
                                    sheetall[28] = text
                                }
                            }
                        }
                        1 -> {
                            when(text.section) {
                                1 -> {
                                    tableall[1] = info
                                    sheetall[1] = text
                                }
                                3 -> {
                                    tableall[8] = info
                                    sheetall[8] = text
                                }
                                5 -> {
                                    tableall[15] = info
                                    sheetall[15] = text
                                }
                                7 -> {
                                    tableall[22] = info
                                    sheetall[22] = text
                                }
                                9 -> {
                                    tableall[29] = info
                                    sheetall[29] = text
                                }
                            }
                        }
                        2 -> {
                            when(text.section) {
                                1 -> {
                                    tableall[2] = info
                                    sheetall[2] = text
                                }
                                3 -> {
                                    tableall[9] = info
                                    sheetall[9] = text
                                }
                                5 -> {
                                    tableall[16] = info
                                    sheetall[16] = text
                                }
                                7 -> {
                                    tableall[23] = info
                                    sheetall[23] = text
                                }
                                9 -> {
                                    tableall[30] = info
                                    sheetall[30] = text
                                }
                            }
                        }
                        3 -> {
                            when(text.section) {
                                1 -> {
                                    tableall[3] = info
                                    sheetall[3] = text
                                }
                                3 -> {
                                    tableall[10] = info
                                    sheetall[10] = text
                                }
                                5 -> {
                                    tableall[17] = info
                                    sheetall[17] = text
                                }
                                7 -> {
                                    tableall[24] = info
                                    sheetall[24] = text
                                }
                                9 -> {
                                    tableall[31] = info
                                    sheetall[31] = text
                                }
                            }
                        }
                        4 -> {
                            when(text.section) {
                                1 -> {
                                    tableall[4] = info
                                    sheetall[4] = text
                                }
                                3 -> {
                                    tableall[11] = info
                                    sheetall[11] = text
                                }
                                5 -> {
                                    tableall[18] = info
                                    sheetall[18] = text
                                }
                                7 -> {
                                    tableall[25] = info
                                    sheetall[25] = text
                                }
                                9 -> {
                                    tableall[32] = info
                                    sheetall[32] = text
                                }
                            }
                        }
                        5 -> {
                            vmUI.findNewCourse.value = info.isNotEmpty()
                            when(text.section) {
                                1 -> {
                                    tableall[5] = info
                                    sheetall[5] = text
                                }
                                3 -> {
                                    tableall[12] = info
                                    sheetall[12] = text
                                }
                                5 -> {
                                    tableall[19] = info
                                    sheetall[19] = text
                                }
                                7 -> {
                                    tableall[26] = info
                                    sheetall[26] = text
                                }
                                9 -> {
                                    tableall[33] = info
                                    sheetall[33] = text
                                }
                            }
                        }
                        6 -> {
                            vmUI.findNewCourse.value = info.isNotEmpty()
                            when(text.section) {
                                1 -> {
                                    tableall[6] = info
                                    sheetall[6] = text
                                }
                                3 -> {
                                    tableall[13] = info
                                    sheetall[13] = text
                                }
                                5 -> {
                                    tableall[20] = info
                                    sheetall[20] = text
                                }
                                7 -> {
                                    tableall[27] = info
                                    sheetall[27] = text
                                }
                                9 -> {
                                    tableall[34] = info
                                    sheetall[34] = text
                                }
                            }
                        }
                    }
                }
            }
        } catch (e:Exception) {
            e.printStackTrace()
        }

    }
    fun Updates() {
        try {
            for(i in table.indices)
                table[i] = ""
            for(i in sheet.indices)
                sheet[i] = courseDetailDTOList(0,0,"","","", listOf(0),0,"")

            for (j in 0 until 5 ) {
                var info = ""
                val lists = getCourseINFO(j +1 ,Bianhuaweeks.toInt(),friendUserName)

                for(i in 0 until lists.size) {
                    val text = lists[i][0]
                    val name = text.name
                    var time = text.classTime
                    time = time.substringBefore("-")
                    var room = text.place
                    room = room?.replace("学堂","")
                    info = time + "\n" + name + "\n" + if(room == null) "" else room


                    when (j) {
                        0 -> {
                            when(text.section) {
                                1 -> {
                                    table[0] = info
                                    sheet[0] = text
                                }
                                3 -> {
                                    table[5] = info
                                    sheet[5] = text
                                }
                                5 -> {
                                    table[10] = info
                                    sheet[10] = text
                                }
                                7 -> {
                                    table[15] = info
                                    sheet[15] = text
                                }
                                9 -> {
                                    table[20] = info
                                    sheet[20] = text
                                }
                            }
                        }
                        1 -> {
                            when(text.section) {
                                1 -> {
                                    table[1] = info
                                    sheet[1] = text
                                }
                                3 -> {
                                    table[6] = info
                                    sheet[6] = text
                                }
                                5 -> {
                                    table[11] = info
                                    sheet[11] = text
                                }
                                7 -> {
                                    table[16] = info
                                    sheet[16] = text
                                }
                                9 -> {
                                    table[21] = info
                                    sheet[21] = text
                                }
                            }
                        }
                        2 -> {
                            when(text.section) {
                                1 -> {
                                    table[2] = info
                                    sheet[2] = text
                                }
                                3 -> {
                                    table[7] = info
                                    sheet[7] = text
                                }
                                5 -> {
                                    table[12] = info
                                    sheet[12] = text
                                }
                                7 -> {
                                    table[17] = info
                                    sheet[17] = text
                                }
                                9 -> {
                                    table[22] = info
                                    sheet[22] = text
                                }
                            }
                        }
                        3 -> {
                            when(text.section) {
                                1 -> {
                                    table[3] = info
                                    sheet[3] = text
                                }
                                3 -> {
                                    table[8] = info
                                    sheet[8] = text
                                }
                                5 -> {
                                    table[13] = info
                                    sheet[13] = text
                                }
                                7 -> {
                                    table[18] = info
                                    sheet[18] = text
                                }
                                9 -> {
                                    table[23] = info
                                    sheet[23] = text
                                }
                            }
                        }
                        4 -> {
                            when(text.section) {
                                1 -> {
                                    table[4] = info
                                    sheet[4] = text
                                }
                                3 -> {
                                    table[9] = info
                                    sheet[9] = text
                                }
                                5 -> {
                                    table[14] = info
                                    sheet[14] = text
                                }
                                7 -> {
                                    table[19] = info
                                    sheet[19] = text
                                }
                                9 -> {
                                    table[24] = info
                                    sheet[24] = text
                                }
                            }
                        }
                    }
                }
            }
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    //装载数组和信息

    UpdatesAll()
    Updates()




    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
    val json = prefs.getString("json", "")
    if (json?.contains("result") == true) {
        UpdatesAll()//填充UI与更新
    }// else Toast.makeText(MyApplication.context,"本地数据为空,请登录以更新数据",Toast.LENGTH_SHORT).show()

  //  var showAlls by remember { mutableStateOf(false) }
    //showAlls = showAll


    //刷新
    var refreshing by remember { mutableStateOf(false) }
    // 用协程模拟一个耗时加载
    val scope = rememberCoroutineScope()
    val states = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        scope.launch {
            async {
                refreshing = true
                if(showAll) UpdatesAll() else Updates()
            }.await()
            async {
                refreshing = false
            }
        }
    })


    //课程详情
    var num by remember { mutableStateOf(0) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            //shape = Round(sheetState)
        ) {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text(if(showAll) sheetall[num].name else sheet[num].name) },
            )
            DetailInfos(if(showAll) sheetall[num] else sheet[num],friendUserName != null, vm = vm)
        }
    }

//    var today by rememberSaveable { mutableStateOf(LocalDate.now()) }
//    val mondayOfCurrentWeek = today.minusDays(today.dayOfWeek.value - 1L)
//
//
    val dateList  = getScheduleDate(showAll,today)
    val examList  = examToCalendar()

        Column(modifier = Modifier.fillMaxSize()
        ) {
           // Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding()))
           // Spacer(modifier = Modifier.height(5.dp))

            //ScheduleTopDate(showAll,today)

            Box(
                modifier = Modifier
                    .pullRefresh(states)

            ) {
                val scrollstate = rememberLazyGridState()
                val shouldShowAddButton by remember { derivedStateOf { scrollstate.firstVisibleItemScrollOffset == 0 } }


                LazyVerticalGrid(
                    columns = GridCells.Fixed(if(showAll)7 else 5),
                    modifier = Modifier.padding(7.dp),
                    state = scrollstate
                ) {
                    items(if(showAll)7 else 5) { Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding())) }
                    items(if(showAll)42 else 30) { cell ->
                        var texts = if(showAll)tableall[cell] else table[cell]
                        var click by remember { mutableStateOf(false) }

                        Card(
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            shape = MaterialTheme.shapes.extraSmall,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                            modifier = Modifier
                                .height(125.dp)
                                .padding(if (showAll) 1.dp else 2.dp)
                                .clickable {
                                    click = true
                                    num = cell
                                    if ((if (showAll) sheetall[cell].name else sheet[cell].name) != "")
                                        showBottomSheet = true
                                }
                        ) {

                            //存在待考时
                            if(examList.isNotEmpty()){
                                val numa = if(showAll) 7 else 5
                                val i = cell % numa
                                val j = cell / numa
                                val date = dateList[i]
                                examList.forEach {
                                    if(date == it.day) {
                                        val hour = it.startTime?.substringBefore(":")?.toIntOrNull() ?: 99

                                        if(hour in 7..9 && j == 0) {
                                            texts = it.startTime + "\n" + it.course + "\n" + it.place
                                        } else if(hour in 10..12 && j == 1) {
                                            texts = it.startTime + "\n" + it.course + "\n" + it.place
                                        } else if(hour in 14..15  && j == 2) {
                                            texts = it.startTime + "\n" + it.course + "\n" + it.place
                                        } else if(hour in 16..17  && j == 3) {
                                            texts = it.startTime + "\n" + it.course + "\n" + it.place
                                        } else if(hour >= 18  && j == 4) {
                                            texts = it.startTime + "\n" + it.course + "\n" + it.place
                                        }
                                    }
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    //.padding(8.dp)
                            ) {
                                Text(
                                    text = texts,
                                    fontSize = if (showAll) 12.sp else 14.sp,
                                    textAlign = TextAlign.Center
                                )
                            }

                           // Text(text = texts,fontSize = if(showAll)12.sp else 14.sp, textAlign = TextAlign.Center)
                        }
                    }
                    item {  Spacer(modifier = Modifier.height(innerPaddings.calculateBottomPadding())) }
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = shouldShowAddButton,
                    enter = scaleIn(),
                    exit = scaleOut(),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(innerPaddings)
                        .padding(horizontal = 15.dp, vertical = 15.dp)
                ) {
                    if (shouldShowAddButton) {
                        FloatingActionButton(
                            onClick = {
                                if (Bianhuaweeks > 1) {
                                    Bianhuaweeks-- - 1
                                    onDateChange(today.minusDays(7))
                                    //today = today.minusDays(7)
                                }
                            },
                        ) { Icon(Icons.Filled.ArrowBack, "Add Button") }
                    }
                }


                androidx.compose.animation.AnimatedVisibility(
                    visible = shouldShowAddButton,
                    enter = scaleIn(),
                    exit = scaleOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(innerPaddings)
                        .padding(horizontal = 15.dp, vertical = 15.dp)
                ) {
                    if (shouldShowAddButton) {
                        ExtendedFloatingActionButton(
                            onClick = {
                                Bianhuaweeks = Benweeks
                                onDateChange(LocalDate.now())
                                //today = LocalDate.now()
                            },
                        ) {
                            AnimatedContent(
                                targetState = Bianhuaweeks,
                                transitionSpec = {
                                    scaleIn(animationSpec = tween(500)
                                    ) togetherWith scaleOut(animationSpec = tween(500))
                                }, label = ""
                            ){annumber ->
                                Text(text = "第 $annumber 周",)
                            }
                        }
                    }
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = !shouldShowAddButton,
                    enter = scaleIn(),
                    exit = scaleOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(innerPaddings)
                        .padding(horizontal = 15.dp, vertical = 15.dp)
                ) {
                    TextButton(onClick = {  }) {
                        Text(
                            text = getSemseter(getSemseterCloud()),
                            style = TextStyle(shadow = Shadow(
                                color = Color.Gray,
                                offset = Offset(5.0f,5.0f),
                                blurRadius = 10.0f
                            ))
                        )
                    }
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = shouldShowAddButton,
                    enter = scaleIn(),
                    exit = scaleOut(),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(innerPaddings)
                        .padding(horizontal = 15.dp, vertical = 15.dp)
                ) {
                    if (shouldShowAddButton) {
                        FloatingActionButton(
                            onClick = {
                                if (Bianhuaweeks < 20) {
                                    Bianhuaweeks++ + 1
                                    onDateChange(today.plusDays(7))
                                }
                            },
                        ) { Icon(Icons.Filled.ArrowForward, "Add Button") }
                    }
                }
                PullRefreshIndicator(refreshing, states,
                    Modifier
                        .align(Alignment.TopCenter)
                        .padding(innerPaddings))
            }
        }
}

@Composable
fun ScheduleTopDate(showAll: Boolean,today : LocalDate,blur : Boolean) {
    val mondayOfCurrentWeek = today.minusDays(today.dayOfWeek.value - 1L)
    Column(modifier = Modifier.background(Color.Transparent)) {
        Spacer(modifier = Modifier.height(5.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(if(showAll)7 else 5),modifier = Modifier.padding(horizontal = 10.dp)){
            items(if(showAll)7 else 5) { item ->
                val date = mondayOfCurrentWeek.plusDays(item.toLong()).toString() //YYYY-MM-DD 与考试对比
                val isToday = date == DateTimeManager.Date_yyyy_MM_dd
                if (Benweeks > 0)
                    Text(
                        text = date.substringAfter("-"),
                        textAlign = TextAlign.Center,
                        fontSize = if(showAll)12.sp else 14.sp,
                        color = if(isToday) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary,
                        fontWeight = if(isToday) FontWeight.Bold else FontWeight.Normal
                    )
                else Text(
                    text = "未开学",
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    fontSize = if(showAll)12.sp else 14.sp
                )
            }
        }
    }
}