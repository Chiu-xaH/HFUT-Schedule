package com.hfut.schedule.ui.activity.home.calendar.jxglstu

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.with
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import com.hfut.schedule.ui.utils.components.LoadingUI
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.viewmodel.LoginViewModel
import com.hfut.schedule.viewmodel.UIViewModel
import com.hfut.schedule.logic.beans.community.LoginCommunityResponse
import com.hfut.schedule.logic.beans.jxglstu.datumResponse
import com.hfut.schedule.logic.utils.DateTimeUtils
import com.hfut.schedule.logic.utils.data.JxglstuParseUtils
import com.hfut.schedule.logic.utils.parse.Semseter.parseSemseter
import com.hfut.schedule.logic.utils.parse.Semseter.getSemseterFromCloud
import com.hfut.schedule.logic.utils.data.SharePrefs
import com.hfut.schedule.logic.utils.data.SharePrefs.saveInt
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.ui.activity.home.calendar.communtiy.CourseDetailApi
import com.hfut.schedule.ui.activity.home.calendar.examToCalendar
import com.hfut.schedule.ui.activity.home.calendar.getScheduleDate
import com.hfut.schedule.ui.activity.home.calendar.next.parseCourseName
import com.hfut.schedule.ui.activity.home.main.saved.isNextOpen
import com.hfut.schedule.ui.activity.home.search.functions.totalCourse.getTotalCourse
import com.hfut.schedule.ui.utils.components.AppHorizontalDp
import com.hfut.schedule.ui.utils.components.CustomTopBar
import com.hfut.schedule.ui.utils.components.LargeCard
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.style.Round
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class, ExperimentalAnimationApi::class)
@SuppressLint("SuspiciousIndentation", "CoroutineCreationDuringComposition")
@Composable
fun CalendarScreen(
    showAll: Boolean,
    vm: NetWorkViewModel,
    innerPadding: PaddingValues,
    vmUI: UIViewModel,
    webVpn: Boolean,
    vm2: LoginViewModel,
    load: Boolean,
    onDateChange: (LocalDate) ->Unit,
    today: LocalDate) {
    val sheetState_totalCourse = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_totalCourse by remember { mutableStateOf(false) }
    val sheetState_multiCourse = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_multiCourse by remember { mutableStateOf(false) }
    var courseName by remember { mutableStateOf("") }


    var courses by remember { mutableStateOf(listOf<String>()) }
    var multiWeekday by remember { mutableStateOf(0) }
    var multiWeek by remember { mutableStateOf(0) }
    if (showBottomSheet_totalCourse) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet_totalCourse = false
            },
            sheetState = sheetState_totalCourse,
            shape = Round(sheetState_totalCourse)
        ) {
            CourseDetailApi(courseName = courseName, vm = vm)
        }
    }
    if (showBottomSheet_multiCourse) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet_multiCourse = false
            },
            sheetState = sheetState_multiCourse,
        ) {
            MultiCourseSheetUI(courses = courses ,weekday = multiWeekday,week = multiWeek,vm = vm)
        }
    }

    var loading by remember { mutableStateOf(load) }

    val table = remember { List(30) { mutableStateListOf<String>() } }
    val tableAll = remember { List(42) { mutableStateListOf<String>() } }



    var Bianhuaweeks by rememberSaveable { mutableStateOf(
        if(DateTimeUtils.weeksBetween > 20) {
            getNewWeek()
        } else DateTimeUtils.weeksBetween
    ) }

    // 去重
    val distinctUnit = { list : List<SnapshotStateList<String>> ->
        for(t in list) {
            val uniqueItems = t.distinct()
            t.clear()
            t.addAll(uniqueItems)
        }
    }
    // 清空
    val clearUnit = { list : List<SnapshotStateList<String>> ->
        for(t in list) {
            t.clear()
        }
    }


    fun refreshUI(showAll : Boolean) {
        // 清空
        if(showAll) {
            clearUnit(tableAll)
        } else {
            clearUnit(table)
        }

        try {
            // 组装
            val json = prefs.getString("json", "")
            val datumResponse = Gson().fromJson(json, datumResponse::class.java)
            val scheduleList = datumResponse.result.scheduleList
            val lessonList = datumResponse.result.lessonList

            for (i in scheduleList.indices) {
                val item = scheduleList[i]
                var startTime = item.startTime.toString()
                startTime =
                    startTime.substring(0, startTime.length - 2) + ":" + startTime.substring(
                        startTime.length - 2
                    )
                var room = item.room.nameZh
                var courseId = item.lessonId.toString()
                room = room.replace("学堂","")


                for (j in lessonList.indices) {
                    if (courseId == lessonList[j].id) {
                        courseId = lessonList[j].courseName
                    }
                }

                val text = startTime + "\n" + courseId + "\n" + room

                if (item.weekIndex == Bianhuaweeks.toInt()) {
                    when(item.weekday) {
                        6 -> { Handler(Looper.getMainLooper()).post { vmUI.findNewCourse.value = text.isNotEmpty() } }
                        7 -> { Handler(Looper.getMainLooper()).post { vmUI.findNewCourse.value = text.isNotEmpty() } }
                    }
                    if(showAll) {
                        if (item.weekday == 1) {
                            if (item.startTime == 800) {
                                tableAll[0].add(text)
                            }
                            if (item.startTime == 1010) {
                                tableAll[7].add(text)
                            }
                            if (item.startTime == 1400) {
                                tableAll[14].add(text)
                            }
                            if (item.startTime == 1600) {
                                tableAll[21].add(text)
                            }
                            if (item.startTime == 1900) {
                                tableAll[28].add(text)
                            }
                        }
                        if (item.weekday == 2) {
                            if (item.startTime == 800) {
                                tableAll[1].add(text)
                            }
                            if (item.startTime == 1010) {
                                tableAll[8].add(text)
                            }
                            if (item.startTime == 1400) {
                                tableAll[15].add(text)
                            }
                            if (item.startTime == 1600) {
                                tableAll[22].add(text)
                            }
                            if (item.startTime == 1900) {
                                tableAll[29].add(text)
                            }
                        }
                        if (item.weekday == 3) {
                            if (item.startTime == 800) {
                                tableAll[2].add(text)
                            }
                            if (item.startTime == 1010) {
                                tableAll[9].add(text)
                            }
                            if (item.startTime == 1400) {
                                tableAll[16].add(text)
                            }
                            if (item.startTime == 1600) {
                                tableAll[23].add(text)
                            }
                            if (item.startTime == 1900) {
                                tableAll[30].add(text)
                            }
                        }
                        if (item.weekday == 4) {
                            if (item.startTime == 800) {
                                tableAll[3].add(text)
                            }
                            if (item.startTime == 1010) {
                                tableAll[10].add(text)
                            }
                            if (item.startTime == 1400) {
                                tableAll[17].add(text)
                            }
                            if (item.startTime == 1600) {
                                tableAll[24].add(text)
                            }
                            if (item.startTime == 1900) {
                                tableAll[31].add(text)
                            }
                        }
                        if (item.weekday == 5) {
                            if (item.startTime == 800) {
                                tableAll[4].add(text)
                            }
                            if (item.startTime == 1010) {
                                tableAll[11].add(text)
                            }
                            if (item.startTime == 1400) {
                                tableAll[18].add(text)
                            }
                            if (item.startTime == 1600) {
                                tableAll[25].add(text)
                            }
                            if (item.startTime == 1900) {
                                tableAll[32].add(text)
                            }
                        }
                        if (item.weekday == 6) {
                            if (item.startTime == 800) {
                                tableAll[5].add(text)
                            }
                            if (item.startTime == 1010) {
                                tableAll[12].add(text)
                            }
                            if (item.startTime == 1400) {
                                tableAll[19].add(text)
                            }
                            if (item.startTime == 1600) {
                                tableAll[26].add(text)
                            }
                            if (item.startTime == 1900) {
                                tableAll[33].add(text)
                            }
                        }
                        if (item.weekday == 7) {
                            if (item.startTime == 800) {
                                tableAll[6].add(text)
                            }
                            if (item.startTime == 1010) {
                                tableAll[13].add(text)
                            }
                            if (item.startTime == 1400) {
                                tableAll[20].add(text)
                            }
                            if (item.startTime == 1600) {
                                tableAll[27].add(text)
                            }
                            if (item.startTime == 1900) {
                                tableAll[34].add(text)
                            }
                        }
                    } else {
                        if (item.weekday == 1) {
                            if (item.startTime == 800) {
                                table[0].add(text)
                            }
                            if (item.startTime == 1010) {
                                table[5].add(text)
                            }
                            if (item.startTime == 1400) {
                                table[10].add(text)
                            }
                            if (item.startTime == 1600) {
                                table[15].add(text)
                            }
                            if (item.startTime == 1900) {
                                table[20].add(text)
                            }
                        }
                        if (item.weekday == 2) {
                            if (item.startTime == 800) {
                                table[1].add(text)
                            }
                            if (item.startTime == 1010) {
                                table[6].add(text)
                            }
                            if (item.startTime == 1400) {
                                table[11].add(text)
                            }
                            if (item.startTime == 1600) {
                                table[16].add(text)
                            }
                            if (item.startTime == 1900) {
                                table[21].add(text)
                            }
                        }
                        if (item.weekday == 3) {
                            if (item.startTime == 800) {
                                table[2].add(text)
                            }
                            if (item.startTime == 1010) {
                                table[7].add(text)
                            }
                            if (item.startTime == 1400) {
                                table[12].add(text)
                            }
                            if (item.startTime == 1600) {
                                table[17].add(text)
                            }
                            if (item.startTime == 1900) {
                                table[22].add(text)
                            }
                        }
                        if (item.weekday == 4) {
                            if (item.startTime == 800) {
                                table[3].add(text)
                            }
                            if (item.startTime == 1010) {
                                table[8].add(text)
                            }
                            if (item.startTime == 1400) {
                                table[13].add(text)
                            }
                            if (item.startTime == 1600) {
                                table[18].add(text)
                            }
                            if (item.startTime == 1900) {
                                table[23].add(text)
                            }
                        }
                        if (item.weekday == 5) {
                            if (item.startTime == 800) {
                                table[4].add(text)
                            }
                            if (item.startTime == 1010) {
                                table[9].add(text)
                            }
                            if (item.startTime == 1400) {
                                table[14].add(text)
                            }
                            if (item.startTime == 1600) {
                                table[19].add(text)
                            }
                            if (item.startTime == 1900) {
                                table[24].add(text)
                            }
                        }
                    }
                }
            }

            // 去重
            if(showAll) {
                distinctUnit(tableAll)
            } else {
                distinctUnit(table)
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }


    LaunchedEffect(showAll,loading) {
        async { refreshUI(showAll) }.await()
    }

//////////////////////////////////////////////////////////////////////////////////

   if(load) {
        val cookie = if (!webVpn) prefs.getString(
            "redirect",
            ""
        ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")
        var num2 = 1
        // val grade = intent.getStringExtra("Grade")
        val ONE = JxglstuParseUtils.casCookies
        val TGC = prefs.getString("TGC", "")
        val cardvalue = prefs.getString("borrow", "")
        val cookies = "$ONE;$TGC"
        val ticket = prefs.getString("TICKET", "")
        // val jsons = prefs.getString("LoginCommunity",MyApplication.NullLoginCommunity)
        val CommuityTOKEN = prefs.getString("TOKEN", "")
        var a by rememberSaveable { mutableStateOf(0) }
        val job = Job()
        val job2 = Job()
        val scope = CoroutineScope(job)

       if(!webVpn) {
           CoroutineScope(job2).launch {
               val token = prefs.getString("bearer", "")

               //检测若登陆成功（200）则解析出CommunityTOKEN
               val LoginCommunityObserver = Observer<String?> { result ->
                   if (result != null) {
                       if (result.contains("200") && result.contains("token")) {
                           val result = Gson().fromJson(result, LoginCommunityResponse::class.java)
                           val token = result.result.token
                           SharePrefs.saveString("TOKEN", token)
                           if (num2 == 1) {
                               MyToast("Community登陆成功")
                               num2++
                           }
                       }
                   }
               }

               //检测CommunityTOKEN的可用性
               val ExamObserver = Observer<Int> { result ->
//                   Log.d("result",(result == 500).toString())
                   if (result == 500) {
                       CoroutineScope(Job()).async {
                           async { vm.GotoCommunity(cookies) }.await()
                           async {
                               delay(1000)
                               ticket?.let { vm.LoginCommunity(it) }
                           }.await()
                           async {
                               Handler(Looper.getMainLooper()).post {
                                   vm.LoginCommunityData.observeForever(
                                       LoginCommunityObserver
                                   )
                               }
                           }
                       }
                   }
               }


               //检测慧新易校可用性
               val auth = prefs.getString("auth", "")
               if (prefs.getString("auth", "") == "") vm.OneGotoCard("$ONE;$TGC")

               async { vm.OneGotoCard("$ONE;$TGC") }
               async { CommuityTOKEN?.let { vm.Exam(it) } }

               Handler(Looper.getMainLooper()).post { vm.ExamCodeData.observeForever(ExamObserver) }


               //登录信息门户的接口,还没做重构（懒）
               if (token != null) {
                   if (token.contains("AT") && cardvalue != "未获取") {
                       async { vm.getSubBooks("Bearer $token") }
                       async { vm.getBorrowBooks("Bearer $token") }
                   } else {
                       async {
                           async { vm.OneGoto(cookies) }.await()
                           async {
                               delay(500)
                               vm.getToken()
                           }.await()
                       }
                   }
               }
           }
       }

        val nextBoolean = isNextOpen()
        if (nextBoolean) saveInt("FIRST", 1)


        scope.launch {
//加载其他教务信息////////////////////////////////////////////////////////////////////////////////////////////////////
            async {
                val studentIdObserver = Observer<Int> { result ->
                    if (result != 0) {
                        //Log.d("result",result.toString())
                        SharePrefs.saveString("studentId", result.toString())
                        CoroutineScope(Job()).launch {
                            async { vm.getBizTypeId(cookie!!) }.await()
//                            async {
//                                bizTypeId?.let {
//                                    vm.getLessonIds(
//                                        cookie!!,
//                                        it,
//                                        result.toString()
//                                    )
//                                }
//                            }
//                            if (nextBoolean) {
//                                async {
//                                    bizTypeId?.let {
//                                        vm.getLessonIdsNext(
//                                            cookie!!,
//                                            it,
//                                            result.toString()
//                                        )
//                                    }
//                                }
//                            }
                            async { vm.getInfo(cookie!!) }
                            if(prefs.getString("photo","") == null || prefs.getString("photo","") == "")
                            async { cookie?.let { vm.getPhoto(it) } }
                        }
                    }
                }
                val getBizTypeIdObserver = Observer<String?> { result ->
                    if(result != null) {
                        // 开始解析
                        val bizTypeId = JxglstuParseUtils.bizTypeId ?: JxglstuParseUtils.getBizTypeId(result)
                        if(bizTypeId != null) {
                            vm.getLessonIds(cookie!!,bizTypeId,vm.studentId.value.toString())
                            if(nextBoolean) {
                                vm.getLessonIdsNext(cookie!!,bizTypeId,vm.studentId.value.toString())
                            }
                        }
                    }
                }
                val lessonIdObserver = Observer<List<Int>> { result ->
                    if (result.toString() != "") {
                        val lessonIdsArray = JsonArray()
                        result?.forEach { lessonIdsArray.add(JsonPrimitive(it)) }
                        val jsonObject = JsonObject().apply {
                            add("lessonIds", lessonIdsArray)//课程ID
                            addProperty("studentId", vm.studentId.value)//学生ID
                            addProperty("weekIndex", "")
                        }
                        vm.getDatum(cookie!!, jsonObject)
                        vm.bizTypeIdResponse.removeObserver(getBizTypeIdObserver)
                        vm.studentId.removeObserver(studentIdObserver)
                    }
                }
                val lessonIdObserverNext = Observer<List<Int>> { result ->
                    if (result.toString() != "") {
                        val lessonIdsArray = JsonArray()
                        result?.forEach { lessonIdsArray.add(JsonPrimitive(it)) }
                        val jsonObject = JsonObject().apply {
                            add("lessonIds", lessonIdsArray)//课程ID
                            addProperty("studentId", vm.studentId.value)//学生ID
                            addProperty("weekIndex", "")
                        }
                        vm.getDatumNext(cookie!!, jsonObject)
                        // vm.lessonIdsNext.removeObserver(lessonIdObserver)
                    }
                }

                val datumObserver = Observer<String?> { result ->
                    if (result != null) {
                        if (result.contains("result")) {
                            CoroutineScope(Job()).launch {
//                                async { if (showAll) updateAll() else update() }.await()
                                async {
                                    Handler(Looper.getMainLooper()).post {
                                        vm.lessonIds.removeObserver(
                                            lessonIdObserver
                                        )
                                    }
                                }
                                async {
                                    delay(200)
                                    a++
                                    loading = false
                                }
                            }
                        } else MyToast("数据为空,尝试刷新")
                    }
                }

                async { vm.getStudentId(cookie!!) }.await()

                Handler(Looper.getMainLooper()).post {
                    vm.studentId.observeForever(studentIdObserver)
                    vm.bizTypeIdResponse.observeForever(getBizTypeIdObserver)
                    vm.lessonIds.observeForever(lessonIdObserver)
                    vm.datumData.observeForever(datumObserver)
                    if (nextBoolean)
                        vm.lessonIdsNext.observeForever(lessonIdObserverNext)
                }
            }
        }

        if (a > 0) job.cancel()
        if (prefs.getString("tip", "0") != "0") loading = false
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    val dateList  = getScheduleDate(showAll,today)
    val examList  = examToCalendar()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AnimatedVisibility(
                visible = loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column() { LoadingUI() }
                }
            }//加载动画居中，3s后消失

            AnimatedVisibility(
                visible = !loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                //在这里插入课程表布局
                Column {
                    Box( modifier = Modifier
                        .fillMaxHeight()
                    ) {
                        val scrollstate = rememberLazyGridState()
                        val shouldShowAddButton by remember { derivedStateOf { scrollstate.firstVisibleItemScrollOffset == 0 } }

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(if(showAll)7 else 5),
                            modifier = Modifier.padding(10.dp),
                            state = scrollstate
                        ) {
                            items(if(showAll)7 else 5) { Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding())) }
                            items(if(showAll)42 else 30) { cell ->
                                val texts = if(showAll)tableAll[cell].toMutableList() else table[cell].toMutableList()
                                Card(
                                    shape = MaterialTheme.shapes.extraSmall,
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                                    modifier = Modifier
                                        .height(125.dp)
                                        .padding(if (showAll) 1.dp else 2.dp)
                                        .clickable {
                                            // 只有一节课
                                            if(texts.size == 1) {
                                                val name = parseCourseName(if(showAll)tableAll[cell][0] else table[cell][0])
                                                if(name != null) {
                                                    courseName = name
                                                    showBottomSheet_totalCourse = true
                                                }
                                            } else if(texts.size > 1) {
                                                multiWeekday = if(showAll) (cell+1)%7 else (cell+1)%5
                                                multiWeek = Bianhuaweeks.toInt()
                                                courses = texts
                                                showBottomSheet_multiCourse = true
                                            }
                                                // 空数据
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
                                                    texts.add(it.startTime + "\n" + it.course + "\n" + it.place)
                                                } else if(hour in 10..12 && j == 1) {
                                                    texts.add(it.startTime + "\n" + it.course + "\n" + it.place)
                                                } else if(hour in 14..15  && j == 2) {
                                                    texts.add(it.startTime + "\n" + it.course + "\n" + it.place)
                                                } else if(hour in 16..17  && j == 3) {
                                                    texts.add(it.startTime + "\n" + it.course + "\n" + it.place)
                                                } else if(hour >= 18  && j == 4) {
                                                    texts.add(it.startTime + "\n" + it.course + "\n" + it.place)
                                                }
                                            }
                                        }
                                    }
                                    Column(
                                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                                    ) {
                                        Text(
                                            text =
                                                if(texts.size == 1) texts[0]
                                                else if(texts.size > 1) "${texts[0].substringBefore("\n")}\n" + "${texts.size}节课冲突\n点击查看"
                                                else "",
                                            fontSize = if(showAll)12.sp else 14.sp, textAlign = TextAlign.Center)
                                    }
                                }
                            }
                            item {  Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding())) }
                        }
                        androidx.compose.animation.AnimatedVisibility(
                            visible = shouldShowAddButton,
                            enter = scaleIn(),
                            exit = scaleOut(),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(innerPadding)
                                .padding(horizontal = AppHorizontalDp(), vertical = AppHorizontalDp())
                        ) {
                            if (shouldShowAddButton) {
                                FloatingActionButton(
                                    onClick = {
                                        if (Bianhuaweeks > 1) {
                                            Bianhuaweeks-- - 1
                                            refreshUI(showAll)
                                            onDateChange(today.minusDays(7))
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
                                .padding(innerPadding)
                                .padding(horizontal = AppHorizontalDp(), vertical = AppHorizontalDp())
                        ) {
                            if (shouldShowAddButton) {
                                ExtendedFloatingActionButton(
                                    onClick = {
                                        Bianhuaweeks = DateTimeUtils.Benweeks
                                        refreshUI(showAll)
                                        onDateChange(LocalDate.now())
                                    },
                                ) {
                                    AnimatedContent(
                                        targetState = Bianhuaweeks,
                                        transitionSpec = {  scaleIn(animationSpec = tween(500)
                                        ) with scaleOut(animationSpec = tween(500))
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
                                .padding(innerPadding)
                                .padding(horizontal = AppHorizontalDp(), vertical = AppHorizontalDp())
                        ) {
                            TextButton(onClick = {  }) {
                                Text(
                                    text = parseSemseter(getSemseterFromCloud()),
                                    style = TextStyle(shadow = Shadow(
                                        color = Color.Gray,
                                        offset = Offset(5.0f,5.0f),
                                        blurRadius = 10.0f
                                    )
                                    )
                                )
                            }
                        }
                        androidx.compose.animation.AnimatedVisibility(
                            visible = shouldShowAddButton,
                            enter = scaleIn(),
                            exit = scaleOut(),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(innerPadding)
                                .padding(horizontal = AppHorizontalDp(), vertical = AppHorizontalDp())
                        ) {
                            if (shouldShowAddButton) {
                                FloatingActionButton(
                                    onClick = {
                                        if (Bianhuaweeks < 20) {
                                            Bianhuaweeks++ + 1
                                            refreshUI(showAll)
                                            onDateChange(today.plusDays(7))
                                        }
                                    },
                                ) { Icon(Icons.Filled.ArrowForward, "Add Button") }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

}

fun getNewWeek() : Long {
    return try {
        val jxglstuJson = prefs.getString("courses","")
        val resultJxglstu = getTotalCourse(jxglstuJson)[0].semester.startDate
        val firstWeekStartJxglstu: LocalDate = LocalDate.parse(resultJxglstu)
        val weeksBetweenJxglstu = ChronoUnit.WEEKS.between(firstWeekStartJxglstu, DateTimeUtils.today) + 1
        weeksBetweenJxglstu  //固定本周
    } catch (e : Exception) {
        DateTimeUtils.Benweeks
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiCourseSheetUI(week : Int,weekday : Int,courses : List<String>,vm: NetWorkViewModel) {
    var courseName by remember { mutableStateOf("") }
    val sheetState_totalCourse = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_totalCourse by remember { mutableStateOf(false) }
    if (showBottomSheet_totalCourse) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet_totalCourse = false
            },
            sheetState = sheetState_totalCourse,
            shape = Round(sheetState_totalCourse)
        ) {
            CourseDetailApi(courseName = courseName, vm = vm)
        }
    }
    Column {
        CustomTopBar("第${week}周 周${numToChinese(weekday)}")
        LargeCard(
            title = "${courses.size}节课冲突"
        ) {
            for(index in courses.indices) {
                val course = courses[index]
                val list = course.split("\n")
                val startTime = list[0]
                val name = list[1]
                val place =
                    if(list.size > 2) {
                        list[2]
                    } else null
                TransplantListItem(
                    headlineContent = {
                        Text(name)
                    },
                    supportingContent = {
                        Text("$place $startTime")
                    },
                    leadingContent = {
                        Text((index+1).toString())
                    },
                    modifier = Modifier.clickable {
                        courseName = name
                        showBottomSheet_totalCourse = true
                    }
                )
            }
        }
        Spacer(Modifier.height(30.dp))
    }

}

fun numToChinese(num : Int) : String {
    return when(num) {
        1 -> "一"
        2 -> "二"
        3 -> "三"
        4 -> "四"
        5 -> "五"
        6 -> "六"
        7 -> "日"
        else -> ""
    }
}