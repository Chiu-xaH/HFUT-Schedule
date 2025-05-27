package com.hfut.schedule.ui.screen.home.calendar.jxglstu

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.hfut.schedule.logic.model.community.LoginCommunityResponse
import com.hfut.schedule.logic.model.community.courseDetailDTOList
import com.hfut.schedule.logic.model.jxglstu.datumResponse
import com.hfut.schedule.logic.util.network.HfutCAS
import com.hfut.schedule.logic.util.network.parse.ParseJsons.isNextOpen
import com.hfut.schedule.logic.util.parse.SemseterParser.getSemseter
import com.hfut.schedule.logic.util.parse.SemseterParser.parseSemseter
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveInt
import com.hfut.schedule.logic.util.sys.DateTimeUtils
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.LargeCard
import com.hfut.schedule.ui.component.LoadingUI
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.appHorizontalDp
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.screen.home.calendar.communtiy.CourseDetailApi
import com.hfut.schedule.ui.screen.home.calendar.communtiy.DetailInfos
import com.hfut.schedule.ui.screen.home.calendar.examToCalendar
import com.hfut.schedule.ui.screen.home.calendar.getScheduleDate
import com.hfut.schedule.ui.screen.home.calendar.next.parseCourseName
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getTotalCourse
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.viewmodel.UIViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

//val distinctUnit<T> = { list : List<SnapshotStateList<String>> ->
//    for(t in list) {
//        val uniqueItems = t.distinct()
//        t.clear()
//        t.addAll(uniqueItems)
//    }
//}
//val clearUnit<T> = { list : List<SnapshotStateList<String>> ->
//    for(t in list) {
//        t.clear()
//    }
//}
// 去重
fun <T>distinctUnit(list : List<SnapshotStateList<T>>) {
    for(t in list) {
        val uniqueItems = t.distinct()
        t.clear()
        t.addAll(uniqueItems)
    }
}
// 清空
fun <T>clearUnit(list : List<SnapshotStateList<T>>) {
    for(t in list) {
        t.clear()
    }
}
@Composable
fun JxglstuCourseTableUI(
    showAll: Boolean,
    vm: NetWorkViewModel,
    innerPadding: PaddingValues,
    vmUI: UIViewModel,
    webVpn: Boolean,
    load: Boolean,
    onDateChange: (LocalDate) ->Unit,
    today: LocalDate,
    hazeState: HazeState
) {
    var showBottomSheetTotalCourse by remember { mutableStateOf(false) }
    var showBottomSheetMultiCourse by remember { mutableStateOf(false) }
    var courseName by remember { mutableStateOf("") }

    var courses by remember { mutableStateOf(listOf<String>()) }
    var multiWeekday by remember { mutableIntStateOf(0) }
    var multiWeek by remember { mutableIntStateOf(0) }

    if (showBottomSheetTotalCourse) {
        HazeBottomSheet (
            onDismissRequest = {
                showBottomSheetTotalCourse = false
            },
            showBottomSheet = showBottomSheetTotalCourse,
            hazeState = hazeState
        ) {
            CourseDetailApi(courseName = courseName, vm = vm, hazeState = hazeState)
        }
    }

    if (showBottomSheetMultiCourse) {
        HazeBottomSheet (
            showBottomSheet = showBottomSheetMultiCourse,
            onDismissRequest = {
                showBottomSheetMultiCourse = false
            },
            autoShape = false,
            hazeState = hazeState
        ) {
            MultiCourseSheetUI(courses = courses ,weekday = multiWeekday,week = multiWeek,vm = vm, hazeState = hazeState)
        }
    }

    var loading by remember { mutableStateOf(load) }

    val table = remember { List(30) { mutableStateListOf<String>() } }
    val tableAll = remember { List(42) { mutableStateListOf<String>() } }

    val dateList  =  getScheduleDate(showAll, today)
    val examList  = examToCalendar()

    var currentWeek by rememberSaveable {
        mutableLongStateOf(
            if(DateTimeUtils.weeksBetween > 20) {
                getNewWeek()
            } else DateTimeUtils.weeksBetween
        )
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

                if (item.weekIndex == currentWeek.toInt()) {
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

    LaunchedEffect(showAll,loading,currentWeek) {
        refreshUI(showAll)
    }

//////////////////////////////////////////////////////////////////////////////////
   if(load) {
        val cookie = if (!webVpn) prefs.getString(
            "redirect",
            ""
        ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")
        var num2 = 1
        val ONE = HfutCAS.casCookies
        val TGC = prefs.getString("TGC", "")
        val cardvalue = prefs.getString("borrow", "")
        val cookies = "$ONE;$TGC"
        val ticket = prefs.getString("TICKET", "")
        val CommuityTOKEN = prefs.getString("TOKEN", "")
        var a by rememberSaveable { mutableIntStateOf(0) }
        val job = Job()
        val job2 = Job()
        val scope = CoroutineScope(job)
        val nextBoolean = isNextOpen()


       //检测若登陆成功（200）则解析出CommunityTOKEN
       val loginCommunityObserver = Observer<String?> { result ->
           if (result != null) {
               if (result.contains("200") && result.contains("token")) {
                   try {
                       val tokens = Gson().fromJson(result, LoginCommunityResponse::class.java).result.token
                       SharedPrefs.saveString("TOKEN", tokens)
                       if (num2 == 1) {
                           showToast("Community登陆成功")
                           num2++
                       }
                   }catch (_:Exception) {}
               }
           }
       }

       //检测CommunityTOKEN的可用性
       val examObserver = Observer<Int> { result ->
           if (result == 500) {
               CoroutineScope(Job()).launch {
                   async { vm.gotoCommunity(cookies) }.await()
                   async {
                       delay(1000)
                       ticket?.let { vm.loginCommunity(it) }
                   }.await()
                   launch {
                       Handler(Looper.getMainLooper()).post {
                           vm.loginCommunityData.observeForever(loginCommunityObserver)
                       }
                   }
               }
           }
       }

        if(!webVpn) {
            val token = prefs.getString("bearer", "")
            //检测慧新易校可用性
            if (prefs.getString("auth", "") == "") vm.goToHuiXin("$ONE;$TGC")
           CoroutineScope(job2).launch {


               async { vm.goToHuiXin("$ONE;$TGC") }
               async { CommuityTOKEN?.let { vm.getExamFromCommunity(it) } }

               Handler(Looper.getMainLooper()).post { vm.examCodeFromCommunityResponse.observeForever(examObserver) }


               //登录信息门户的接口,还没做重构（懒）
               if (token != null) {
                   if (token.contains("AT") && cardvalue != "未获取") {
//                       async { vm.getSubBooks("Bearer $token") }
//                       async { vm.getBorrowBooks("Bearer $token") }
                   } else {
                       async {
                           async { vm.goToOne(cookies) }.await()
                           async {
                               delay(500)
                               vm.getToken()
                           }.await()
                       }
                   }
               }
           }
       }

        if (nextBoolean) saveInt("FIRST", 1)


        scope.launch {
//加载其他教务信息////////////////////////////////////////////////////////////////////////////////////////////////////
            async {
                val studentIdObserver = Observer<Int> { result ->
                    if (result != 0) {
                        SharedPrefs.saveString("studentId", result.toString())
                        CoroutineScope(Job()).launch {
                            async { vm.getBizTypeId(cookie!!) }.await()
                            async { vm.getInfo(cookie!!) }
                            if(prefs.getString("photo","") == null || prefs.getString("photo","") == "")
                            async { cookie?.let { vm.getPhoto(it) } }
                        }
                    }
                }
                val getBizTypeIdObserver = Observer<String?> { result ->
                    if(result != null) {
                        // 开始解析
                        val bizTypeId = HfutCAS.bizTypeId ?: HfutCAS.getBizTypeId(result)
                        if(bizTypeId != null) {
                            vm.getLessonIds(cookie!!,bizTypeId,vm.studentId.value.toString())
                            if(nextBoolean) {
                                vm.getLessonIdsNext(cookie,bizTypeId,vm.studentId.value.toString())
                            }
                        }
                    }
                }
                val lessonIdObserver = Observer<List<Int>> { result ->
                    if (result.toString() != "") {
                        val lessonIdsArray = JsonArray()
                        result.forEach { lessonIdsArray.add(JsonPrimitive(it)) }
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
                        result.forEach { lessonIdsArray.add(JsonPrimitive(it)) }
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
                        } else showToast("数据为空,尝试刷新")
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
        Column(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(
                visible = loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column() { LoadingUI(if(webVpn) "若加载时间过长，请重新刷新登陆状态" else null) }
                }
            }

            AnimatedVisibility(
                visible = !loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                //在这里插入课程表布局
                Column {
                    Box(modifier = Modifier.fillMaxHeight()) {
                        val scrollState = rememberLazyGridState()
                        val shouldShowAddButton by remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset == 0 } }

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(if(showAll)7 else 5),
                            modifier = Modifier.padding(10.dp),
                            state = scrollState
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
                                            if (texts.size == 1) {
                                                // 如果是考试
                                                if (texts[0].contains("考试")) {
                                                    return@clickable
                                                }
                                                val name =
                                                    parseCourseName(if (showAll) tableAll[cell][0] else table[cell][0])
                                                if (name != null) {
                                                    courseName = name
                                                    showBottomSheetTotalCourse = true
                                                }
                                            } else if (texts.size > 1) {
                                                multiWeekday =
                                                    if (showAll) (cell + 1) % 7 else (cell + 1) % 5
                                                multiWeek = currentWeek.toInt()
                                                courses = texts
                                                showBottomSheetMultiCourse = true
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
                                                    texts.add(it.startTime + "\n" + it.course  + "(考试)"+ "\n" + it.place?.replace("学堂",""))
                                                } else if(hour in 10..12 && j == 1) {
                                                    texts.add(it.startTime + "\n" + it.course + "(考试)" + "\n" + it.place?.replace("学堂",""))
                                                } else if(hour in 14..15  && j == 2) {
                                                    texts.add(it.startTime + "\n" + it.course  + "(考试)"+ "\n" + it.place?.replace("学堂",""))
                                                } else if(hour in 16..17  && j == 3) {
                                                    texts.add(it.startTime + "\n" + it.course  + "(考试)"+ "\n" + it.place?.replace("学堂",""))
                                                } else if(hour >= 18  && j == 4) {
                                                    texts.add(it.startTime + "\n" + it.course  + "(考试)"+ "\n" + it.place?.replace("学堂",""))
                                                }
                                            }
                                        }
                                    }
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(rememberScrollState())
                                    ) {
                                        Text(
                                            text =
                                                if(texts.size == 1) texts[0]
                                                else if(texts.size > 1) "${texts[0].substringBefore("\n")}\n" + "${texts.size}节课冲突\n点击查看"
                                                else "",
                                            fontSize = if(showAll)12.sp else 14.sp,
                                            textAlign = TextAlign.Center,
                                            fontWeight = if(texts.toString().contains("考试")) FontWeight.SemiBold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                            item {  Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding())) }
                        }
                        // 上一周
                        androidx.compose.animation.AnimatedVisibility(
                            visible = shouldShowAddButton,
                            enter = scaleIn(),
                            exit = scaleOut(),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(innerPadding)
                                .padding(
                                    horizontal = appHorizontalDp(),
                                    vertical = appHorizontalDp()
                                )
                        ) {
                            if (shouldShowAddButton) {
                                FloatingActionButton(
                                    onClick = {
                                        if (currentWeek > 1) {
                                            currentWeek-- - 1
//                                            refreshUI(showAll)
                                            onDateChange(today.minusDays(7))
                                        }
                                    },
                                ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Add Button") }
                            }
                        }
                        // 中间
                        androidx.compose.animation.AnimatedVisibility(
                            visible = shouldShowAddButton,
                            enter = scaleIn(),
                            exit = scaleOut(),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(innerPadding)
                                .padding(
                                    horizontal = appHorizontalDp(),
                                    vertical = appHorizontalDp()
                                )
                        ) {
                            if (shouldShowAddButton) {
                                ExtendedFloatingActionButton(
                                    onClick = {
                                        currentWeek = DateTimeUtils.weeksBetween
//                                        refreshUI(showAll)
                                        onDateChange(LocalDate.now())
                                    },
                                ) {
                                    AnimatedContent(
                                        targetState = currentWeek,
                                        transitionSpec = {
                                            scaleIn(animationSpec = tween(500)
                                            ) togetherWith(scaleOut(animationSpec = tween(500)))
                                        }, label = ""
                                    ){ n ->
                                        Text(text = "第 $n 周")
                                    }
                                }
                            }
                        }
                        // 学期显示
                        androidx.compose.animation.AnimatedVisibility(
                            visible = !shouldShowAddButton,
                            enter = scaleIn(),
                            exit = scaleOut(),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(innerPadding)
                                .padding(
                                    horizontal = appHorizontalDp(),
                                    vertical = appHorizontalDp()
                                )
                        ) {
                            TextButton(onClick = {  }) {
                                Text(
                                    text = parseSemseter(getSemseter()) + " 第${currentWeek}周",
                                    style = TextStyle(shadow = Shadow(
                                        color = Color.Gray,
                                        offset = Offset(5.0f,5.0f),
                                        blurRadius = 10.0f
                                    )
                                    )
                                )
                            }
                        }
                        // 下一周
                        androidx.compose.animation.AnimatedVisibility(
                            visible = shouldShowAddButton,
                            enter = scaleIn(),
                            exit = scaleOut(),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(innerPadding)
                                .padding(
                                    horizontal = appHorizontalDp(),
                                    vertical = appHorizontalDp()
                                )
                        ) {
                            if (shouldShowAddButton) {
                                FloatingActionButton(
                                    onClick = {
                                        if (currentWeek < 20) {
                                            currentWeek++ + 1
//                                            refreshUI(showAll)
                                            onDateChange(today.plusDays(7))
                                        }
                                    },
                                ) { Icon(Icons.AutoMirrored.Filled.ArrowForward, "Add Button") }
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
        val weeksBetweenJxglstu = ChronoUnit.WEEKS.between(firstWeekStartJxglstu, DateTimeUtils.getToday()) + 1
        weeksBetweenJxglstu  //固定本周
    } catch (_ : Exception) {
        DateTimeUtils.weeksBetween
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiCourseSheetUI(week : Int, weekday : Int, courses : List<courseDetailDTOList>, vm: NetWorkViewModel, hazeState: HazeState,friendUserName : String?) {
    var sheet by remember { mutableStateOf(courseDetailDTOList(0,0,"","","", listOf(0),0,"","")) }

    var showBottomSheetTotalCourse by remember { mutableStateOf(false) }
    if (showBottomSheetTotalCourse) {
        HazeBottomSheet(
            onDismissRequest = { showBottomSheetTotalCourse = false },
            showBottomSheet = showBottomSheetTotalCourse,
            hazeState = hazeState,
            autoShape = false
        ) {
            HazeBottomSheetTopBar(sheet.name, isPaddingStatusBar = false)
            DetailInfos(sheet,friendUserName != null, vm = vm, hazeState )
        }
    }
    Column {
        HazeBottomSheetTopBar("第${week}周 周${numToChinese(weekday)}", isPaddingStatusBar = false)
        LargeCard(
            title = "${courses.size}节课冲突"
        ) {
            for(index in courses.indices) {
                val course = courses[index]
                val startTime = course.classTime.substringBefore("-")
                val name = course.name
                val place = course.place
                TransplantListItem(
                    headlineContent = {
                        Text(name)
                    },
                    supportingContent = {
                        Text("${place?.replace("学堂","")} $startTime")
                    },
                    leadingContent = {
                        Text((index+1).toString())
                    },
                    colors =  if(name.contains("考试")) MaterialTheme.colorScheme.errorContainer else null,
                    modifier = Modifier.clickable {
                        // 如果是考试
                        if(name.contains("考试")) {
                            return@clickable
                        }
                        sheet = course
                        showBottomSheetTotalCourse = true
                    }
                )
            }
        }
        Spacer(Modifier.height(40.dp))
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiCourseSheetUI(week : Int, weekday : Int, courses : List<String>, vm: NetWorkViewModel, hazeState: HazeState) {
    var courseName by remember { mutableStateOf("") }
    var showBottomSheetTotalCourse by remember { mutableStateOf(false) }
    if (showBottomSheetTotalCourse) {
        HazeBottomSheet (
            onDismissRequest = {
                showBottomSheetTotalCourse = false
            },
            hazeState = hazeState,
            showBottomSheet = showBottomSheetTotalCourse
        ) {
            CourseDetailApi(courseName = courseName, vm = vm, hazeState = hazeState)
        }
    }
    Column {
        HazeBottomSheetTopBar("第${week}周 周${numToChinese(weekday)}", isPaddingStatusBar = false)
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
                        Text("${place?.replace("学堂","")} $startTime")
                    },
                    leadingContent = {
                        Text((index+1).toString())
                    },
                    colors =  if(name.contains("考试")) MaterialTheme.colorScheme.errorContainer else null,
                    modifier = Modifier.clickable {
                        // 如果是考试
                        if(name.contains("考试")) {
                            return@clickable
                        }
                        courseName = name
                        showBottomSheetTotalCourse = true
                    }
                )
            }
        }
        Spacer(Modifier.height(40.dp))
    }

}

private fun numToChinese(num : Int) : String {
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