package com.hfut.schedule.ui.Activity.success.search.Search.Program

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.datamodel.Jxglstu.ProgramShow
import com.hfut.schedule.logic.utils.ReservDecimal
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.Activity.success.search.Search.More.Login
import com.hfut.schedule.ui.Activity.success.search.Search.TotalCourse.courseIcons
import com.hfut.schedule.ui.UIUtils.BottomTip
import com.hfut.schedule.ui.UIUtils.CardForListColor
import com.hfut.schedule.ui.UIUtils.DividerText
import com.hfut.schedule.ui.UIUtils.MyToast
import com.hfut.schedule.ui.UIUtils.Round
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Program(vm : LoginSuccessViewModel,ifSaved : Boolean) {
    val sheetState_Program = rememberModalBottomSheetState()
    var showBottomSheet_Program by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text(text = "培养方案") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.conversion_path),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
            if (prefs.getString("program","")?.contains("children") == true || !ifSaved) {
                showBottomSheet_Program = true
            }
            else Login()
        }
    )


    if (showBottomSheet_Program ) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_Program = false },
            sheetState = sheetState_Program,
            shape = Round(sheetState_Program)
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("培养方案") },
                        actions = {
                            Row (modifier = Modifier.padding(horizontal = 15.dp)){
                                FilledTonalButton(
                                    onClick = { MyToast("正在开发") },
                                ) {
                                    Text(text = "筛选")
                                }
                            }
                        }
                    )
                },
            ) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    ProgramUI2(vm,ifSaved)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun ProgramUI() {
  //  val list =
        val json = prefs.getString("programJSON","")
       // try {
            val listType = object : TypeToken<List<ProgramShow>>() {}.type
            val list: List<ProgramShow> = Gson().fromJson(json,listType)
      //  }

    var sum = 0.0
    for(i in list.indices) {
        val credits = list[i].credit
        if (credits != null) { sum += credits }
    }

    LazyColumn {
        item {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium,
            ){
                ListItem(
                    headlineContent = { Text(text = "合计 ${list.size} 门 ${sum} 学分") },
                    supportingContent = { Text(text = "不含选修课!")},
                 //   overlineContent = { },
                 //   trailingContent = { Text(text = "学分 " +  list[item].credit)},
                    colors = ListItemDefaults.colors(MaterialTheme.colorScheme.primaryContainer),
                    leadingContent = { Icon(painterResource(id = R.drawable.conversion_path), contentDescription = "Localized description") },
                    modifier = Modifier.clickable {},
                )
            }
        }
        items(list.size) {item ->
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium,
            ){
                ListItem(
                    headlineContent = { Text(text = list[item].name) },
                    supportingContent = { Text(text = list[item].school + "  第" + list[item].term[0] + "学期")},
                    overlineContent = { list[item].type?.let { Text(text = it) } },
                    trailingContent = { Text(text = "学分 " +  list[item].credit)},
                    leadingContent = { Icon(painterResource(id = R.drawable.calendar), contentDescription = "Localized description") },
                    modifier = Modifier.clickable {},
                )
            }
        }
    }
}
@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramUI2(vm: LoginSuccessViewModel,ifSaved: Boolean) {
    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
    val cookie = SharePrefs.prefs.getString("redirect", "")

    val sheetState_Program = rememberModalBottomSheetState()
    var showBottomSheet_Program by remember { mutableStateOf(false) }
    val listOne = getProgramListOne(vm,ifSaved)
    var title by remember { mutableStateOf("培养方案") }
    var num by remember { mutableStateOf(0) }
    var loadingCard by remember { mutableStateOf(true) }
    val scale = animateFloatAsState(
        targetValue = if (loadingCard) 0.9f else 1f, // 按下时为0.9，松开时为1
        //animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        animationSpec = tween(MyApplication.Animation / 2, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )
    val scale2 = animateFloatAsState(
        targetValue = if (loadingCard) 0.97f else 1f, // 按下时为0.9，松开时为1
        //animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        animationSpec = tween(MyApplication.Animation / 2, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )

    val blurSize by animateDpAsState(
        targetValue = if (loadingCard) 10.dp else 0.dp, label = ""
        ,animationSpec = tween(MyApplication.Animation / 2, easing = LinearOutSlowInEasing),
    )
    val completion = getProgramCompletion(vm)
    if(ifSaved) {
        loading = false
        refresh = false
        loadingCard = true
    } else {
        if(refresh) {
            loading = true
            loadingCard = true
            CoroutineScope(Job()).launch{
                async{ cookie?.let { vm.getProgramCompletion(it)} }.await()
                async {
                    Handler(Looper.getMainLooper()).post{
                        vm.ProgramCompletionData.observeForever { result ->
                            if (result != null) {
                                if(result.contains("总学分")) {
                                    loadingCard = false
                                }
                            }
                        }
                    }
                }
            }
            CoroutineScope(Job()).launch{
                async{ cookie?.let { vm.getProgram(it)} }.await()
                async {
                    Handler(Looper.getMainLooper()).post{
                        vm.ProgramData.observeForever { result ->
                            if (result != null) {
                                if(result.contains("children")) {
                                    loading = false
                                    refresh = false
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    DividerText(text = if(ifSaved)"完成情况(登录后可查看)" else "完成情况")
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale2.value)
            .padding(horizontal = 15.dp, vertical = 5.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardForListColor()
    ) {
        Column (modifier = Modifier.blur(blurSize).scale(scale.value)){
            val res = completion.total.actual/completion.total.full * 100.0
            ListItem(
                headlineContent = { Text(text = "已修 ${completion.total.actual}/${completion.total.full}",fontSize = 28.sp) },
                trailingContent = {
                    Text(text = "${ReservDecimal.reservDecimal(res,1)} %")
                }
            )
            for(i in 0 until completion.other.size step 2)
            Row {
                ListItem(
                    headlineContent = { Text(text = completion.other[i].name) },
                    overlineContent = {Text(text = "${completion.other[i].actual}/${completion.other[i].full}", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.weight(.5f)
                )
                if(i+1 < completion.other.size)
                ListItem(
                    headlineContent = { Text(text = completion.other[i+1].name) },
                    overlineContent = {Text(text = "${completion.other[i+1].actual}/${completion.other[i+1].full}",fontWeight = FontWeight.Bold) },
                    modifier = Modifier.weight(.5f)
                )
            }
        }
    }

    DividerText(text = "课程安排")
    Box {
        AnimatedVisibility(
            visible = loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(5.dp))
                CircularProgressIndicator()
            }
        }
    }

    if (showBottomSheet_Program ) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_Program = false },
            sheetState = sheetState_Program,
            shape = Round(sheetState_Program)
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text(title) }
                    )
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){

                    ProgramUIInfo(num,vm, ifSaved)

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

        AnimatedVisibility(
            visible = !loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            var total = 0.0
            LazyColumn {
                items(listOne.size) {item ->
                    total += listOne[item].requiedCredits ?: 0.0
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp, vertical = 5.dp),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        ListItem(
                            headlineContent = { Text(text = listOne[item].type + " | 学分要求 " + listOne[item].requiedCredits) },
                            trailingContent = { Icon(Icons.Filled.ArrowForward, contentDescription = "")},
                            //   leadingContent = { Icon(painterResource(id = R.drawable.calendar), contentDescription = "Localized description") },
                            modifier = Modifier.clickable {
                                showBottomSheet_Program = true
                                num = item
                                title = listOne[item].type.toString()
                            },
                        )
                    }
                }
                item {
                    BottomTip(str = "总修 $total 学分")
                }
            }
        }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramUIInfo(num : Int,vm : LoginSuccessViewModel,ifSaved : Boolean) {
    val listTwo = getProgramListTwo(num,vm,ifSaved)
    var show by remember { mutableStateOf(true) }
    val sheetState_Program = rememberModalBottomSheetState()
    var showBottomSheet_Program by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("培养方案") }
    var num2 by remember { mutableStateOf(0) }
    if (showBottomSheet_Program ) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_Program = false },
            sheetState = sheetState_Program,
            shape = Round(sheetState_Program)
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text(title) }
                    )
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    ProgramUIInfo2(num,num2,vm, ifSaved)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
    listTwo.forEach{ item->
        show = item.type != "直接跳转"
    }
    if(show) {
        LazyColumn {
            items(listTwo.size) {item ->
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 5.dp),
                    shape = MaterialTheme.shapes.medium,
                ){
                    ListItem(
                        headlineContent = { Text(text = listTwo[item].type + " | 学分要求 " + listTwo[item].requiedCredits) },
                        trailingContent = { Icon(Icons.Filled.ArrowForward, contentDescription = "")},
                        //   leadingContent = { Icon(painterResource(id = R.drawable.calendar), contentDescription = "Localized description") },
                        modifier = Modifier.clickable {
                            showBottomSheet_Program = true
                            num2 = item
                            title = listTwo[item].type.toString()
                        },
                    )
                }
            }
        }
    } else {
       ProgramUIInfo2(num,num2,vm, ifSaved)
    }
}

@Composable
fun ProgramUIInfo2(num1 : Int,num2 : Int,vm : LoginSuccessViewModel,ifSaved : Boolean) {
    val listThree = getProgramListThree(num1,num2,vm, ifSaved)
    listThree.sortBy { it.term }
    LazyColumn {
        items(listThree.size) {item ->
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium,
            ){
                ListItem(
                    headlineContent = { Text(text = listThree[item].name) },
                    supportingContent = { Text(text = listThree[item].depart) },
                    overlineContent = { Text(text = "第" + listThree[item].term + "学期 | 学分 ${listThree[item].credit}")},
                    leadingContent = { courseIcons(name = listThree[item].depart) },
                    modifier = Modifier.clickable {
                    },
                )
            }
        }
    }
}