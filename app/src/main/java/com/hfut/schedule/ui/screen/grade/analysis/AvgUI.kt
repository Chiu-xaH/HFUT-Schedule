package com.hfut.schedule.ui.screen.grade.analysis

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
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
import androidx.compose.runtime.LaunchedEffect
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
import com.hfut.schedule.logic.model.community.GradeAllResponse
import com.hfut.schedule.logic.model.community.GradeAllResult
import com.hfut.schedule.logic.model.community.GradeAvgResponse
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.component.appHorizontalDp
import com.hfut.schedule.ui.component.cardNormalColor
import com.hfut.schedule.ui.component.DividerText
import com.hfut.schedule.ui.component.DividerTextExpandedWith
import com.hfut.schedule.ui.component.LoadingUI
import com.hfut.schedule.ui.component.MyCustomCard
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation")
@Composable
fun GradeCountUI(vm : NetWorkViewModel, innerPadding : PaddingValues) {

    Column (modifier =   Modifier.verticalScroll(rememberScrollState())){
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
        Spacer(modifier = Modifier.height(5.dp))


        DividerTextExpandedWith(text = "总成绩 (整个大学阶段)") {
            AvgGrade(vm)
        }

        DividerTextExpandedWith(text = "学期统计 (各学期)") {
            AllGrade(vm)
        }

        DividerTextExpandedWith(text = "统计图") {
            StyleCardListItem(
                    headlineContent = { Text("正在开发") },
                    leadingContent = { Icon(painterResource(R.drawable.info), contentDescription = "Localized description",) },
                    modifier = Modifier.fillMaxWidth(),
                )
        }

        Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
    }
}


@Composable
fun AvgGrade(vm: NetWorkViewModel) {
    val CommuityTOKEN = prefs.getString("TOKEN","")
    var loading by remember { mutableStateOf(true) }


    LaunchedEffect(Unit) {
        if(loading) {
            CommuityTOKEN?.let {
                launch { vm.getAvgGrade(it) }
                launch {
                    Handler(Looper.getMainLooper()).post{
                        vm.avgData.observeForever { result ->
                            if (result != null && result.contains("success")) {
                                loading = false
                            }
                        }
                    }
                }
            }
        }
    }
    if(loading) {
        LoadingUI()
    } else {
        val resultAvg = getAvg(vm)
        MyCustomCard(hasElevation = false, containerColor = cardNormalColor()) {
            Text(text = "我的水平", modifier = Modifier.padding(horizontal = appHorizontalDp(), vertical = 8.dp), color = MaterialTheme.colorScheme.primary)

            Row {
                TransplantListItem(
                    headlineContent = { Text("绩点 ${resultAvg.myAvgGpa}") },
                    supportingContent = { Text("排名 ${resultAvg.majorAvgGpaRanking}") },
                    leadingContent = { Icon(painterResource(R.drawable.award_star), contentDescription = "Localized description",) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
//                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )

                TransplantListItem(
                    headlineContent = { Text("分数 ${resultAvg.myAvgScore}") },
                    supportingContent = { Text("排名 ${resultAvg.majorAvgScoreRanking}") },
                    leadingContent = { Icon(painterResource(R.drawable.filter_vintage), contentDescription = "Localized description",) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
//                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
            Divider()
            Text(text = "专业水平", modifier = Modifier.padding(horizontal = appHorizontalDp(), vertical = 8.dp), color = MaterialTheme.colorScheme.primary)

            Row {
                TransplantListItem(
                    headlineContent = { Text("绩点") },
                    supportingContent = { Text("平均 ${resultAvg.majorAvgGpa}\n最高 (待开发)") },
                    leadingContent = { Icon(painterResource(R.drawable.award_star), contentDescription = "Localized description",) },
                    modifier = Modifier

                        .fillMaxWidth()
                        .weight(.5f),
//                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )

                TransplantListItem(
                    headlineContent = { Text("分数") },
                    supportingContent = { Text("平均 ${resultAvg.majorAvgScore}\n最高 (待开发)") },
                    leadingContent = { Icon(painterResource(R.drawable.filter_vintage), contentDescription = "Localized description",) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
//                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }
    }
}

@Composable
fun AllGrade(vm: NetWorkViewModel) {
    val CommuityTOKEN = prefs.getString("TOKEN","")
    var loading by remember { mutableStateOf(true) }

    val chineseList = listOf("大一上","大一下","大二上","大二下","大三上","大三下","大四上","大四下")


    LaunchedEffect(Unit) {
        if(loading) {
            CommuityTOKEN?.let {
                async { vm.getAllAvgGrade(it) }.await()
                launch {
                    Handler(Looper.getMainLooper()).post{
                        vm.allAvgData.observeForever { result ->
                            if (result != null && result.contains("success")) {
                                loading = false
                            }
                        }
                    }
                }
            }
        }
    }
    if(loading) {
        LoadingUI()
    } else {
        val list = getAllGrade(vm)
        for (index in list.indices) {
            val item = list[index]
            if(item.myAvgGpa != null || item.myAvgScore != null || item.majorAvgGpa != null || item.majorAvgScore != null)
                MyCustomCard(hasElevation = false, containerColor = cardNormalColor()) {
                    Text(text = chineseList[index] + "学期", modifier = Modifier.padding(horizontal = appHorizontalDp(), vertical = 8.dp), color = MaterialTheme.colorScheme.primary)
                    Text(text = "我的水平", modifier = Modifier.padding(horizontal = appHorizontalDp(), vertical = 8.dp), color = MaterialTheme.colorScheme.primary)
                    Row {
                        TransplantListItem(
                            headlineContent = { Text("绩点 ${item.myAvgGpa}") },
                            leadingContent = { Icon(painterResource(R.drawable.award_star), contentDescription = "Localized description",) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(.5f),
//                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )

                        TransplantListItem(
                            headlineContent = { Text("分数 ${item.myAvgScore}") },
                            leadingContent = { Icon(painterResource(R.drawable.filter_vintage), contentDescription = "Localized description",) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(.5f),
//                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }
                    Divider()
                    Text(text = "专业水平", modifier = Modifier.padding(horizontal = appHorizontalDp(), vertical = 8.dp), color = MaterialTheme.colorScheme.primary)
                    Row {
                        TransplantListItem(
                            headlineContent = { Text("绩点(GPA)") },
                            supportingContent = { Text("平均 ${item.majorAvgGpa}\n最高 ${item.maxAvgGpa}") },
                            leadingContent = { Icon(painterResource(R.drawable.award_star), contentDescription = "Localized description",) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(.5f),
//                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )

                        TransplantListItem(
                            headlineContent = { Text("分数") },
                            supportingContent = { Text("平均 ${item.majorAvgScore}\n最高 ${item.maxAvgScore}") },
                            leadingContent = { Icon(painterResource(R.drawable.filter_vintage), contentDescription = "Localized description",) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(.5f),
//                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }
                }
        }
    }
}


fun getAllGrade(vm: NetWorkViewModel) : List<GradeAllResult> {
    return try {
        Gson().fromJson(vm.allAvgData.value,GradeAllResponse::class.java).result
    } catch (e : Exception) {
        emptyList()
    }
}