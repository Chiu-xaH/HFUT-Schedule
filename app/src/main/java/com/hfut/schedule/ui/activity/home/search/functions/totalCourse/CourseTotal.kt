package com.hfut.schedule.ui.activity.home.search.functions.totalCourse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.ui.activity.home.main.saved.isNextOpen
import com.hfut.schedule.ui.utils.components.AnimationCardListItem
import com.hfut.schedule.ui.utils.components.AppHorizontalDp
import com.hfut.schedule.ui.utils.components.CustomTopBar
import com.hfut.schedule.ui.utils.components.MyCustomCard
import com.hfut.schedule.ui.utils.style.Round
import com.hfut.schedule.ui.utils.components.ScrollText
import com.hfut.schedule.ui.utils.components.StyleCardListItem
import com.hfut.schedule.ui.utils.components.TransplantListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseTotal(vm :NetWorkViewModel) {
    val sheetState_Total = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_Total by remember { mutableStateOf(false) }
    val CommuityTOKEN = prefs.getString("TOKEN","")
    val json = prefs.getString("courses","")
    val jsonNext = prefs.getString("coursesNext","")

    TransplantListItem(
        headlineContent = { Text(text = "课程汇总") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.category),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
             showBottomSheet_Total = true
        }
    )
    var next by remember { mutableStateOf(false) }
    var sortType by remember { mutableStateOf(true) }
    if (showBottomSheet_Total) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet_Total = false
            },
            sheetState = sheetState_Total,
            shape = Round(sheetState_Total)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    CustomTopBar("课程汇总") {
                        Row {
                            if (isNextOpen()) {
                                FilledTonalButton(
                                    onClick = {
                                        next = !next
                                    }
                                    ,) {
                                    Text(text = if(next) "下学期" else "本学期")
                                }
                            }
                            FilledTonalButton(
                                onClick = { sortType = !sortType },) {
                                Text(text = if(sortType) "开课时间" else "学分高低")
                            }
                        }
                    }
                },

            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    CourseTotalUI(
                        if(next)
                            jsonNext
                        else
                            json
                        ,
                        false,
                        sortType,
                        vm)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

fun periodsSum(json: String) : Double {
    var num = 0.0
    //val json = prefs.getString("courses","")
    for(i in 0 until getTotalCourse(json).size) {
        val credit = getTotalCourse(json)[i].course.credits
        if (credit != null) {
            num += credit
        }
    }
    return num
}


@Composable
fun SemsterInfo(json : String?) {

    val semsterInfo = getTotalCourse(json)[0].semester
    AnimationCardListItem(
        overlineContent = { Text(text = semsterInfo.startDate + " ~ " + semsterInfo.endDate)},
        headlineContent = {  ScrollText(semsterInfo.nameZh) },
        leadingContent = { Icon(
            painterResource(R.drawable.category),
            contentDescription = "Localized description",
        ) },
        modifier = Modifier.clickable {},
        color = MaterialTheme.colorScheme.primaryContainer,
        trailingContent = { if (json != null) { if(json.contains("lessonIds"))Text(text = "学分 ${periodsSum(json)}") } },
        index = 0
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseTotalForApi(modifier: Modifier = Modifier, vm: NetWorkViewModel, isIconOrText : Boolean = false,next : Boolean = false,onNextChange : (() -> Unit)? = null) {
    val sheetState_Total = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_Total by remember { mutableStateOf(false) }

    //
    var next2 by remember { mutableStateOf(false) }
    val json = prefs.getString("courses","")
    val jsonNext = prefs.getString("coursesNext","")

    if(isIconOrText) {
        IconButton(onClick = {
            showBottomSheet_Total= true
        }) {
            Icon(painter = painterResource(id =  R.drawable.category), contentDescription = "", tint = MaterialTheme.colorScheme.primary)
        }
    } else {
        FilledTonalButton(onClick = { showBottomSheet_Total = true }, modifier = modifier) {
            Text(text = "课程汇总")
        }
    }

    var sortType by remember { mutableStateOf(true) }
    if (showBottomSheet_Total) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet_Total = false
            },
            sheetState = sheetState_Total,
            shape = Round(sheetState_Total)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    CustomTopBar("课程汇总") {
                        if(onNextChange == null) {
                            if(isNextOpen()) {
                                FilledTonalButton(
                                    onClick = {
                                        next2 = !next2
                                    }
                                    ,) {
                                    Text(text = if(next) "下学期" else "本学期")
                                }
                            }
                        } else {
                            if (isNextOpen()) {
                                FilledTonalButton(
                                    onClick = onNextChange
                                    ,) {
                                    Text(text = if(next) "下学期" else "本学期")
                                }
                            }
                        }

                        FilledTonalButton(
                            onClick = { sortType = !sortType },) {
                            Text(text = if(sortType) "开课时间" else "学分高低")
                        }
                    }
                },

                ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){


                    CourseTotalUI(
                        if(isNextOpen() && onNextChange == null) {
                            if(next2) jsonNext
                            else json
                        } else {
                            if(next) jsonNext
                            else json
                        }
                        ,
                        false,
                        sortType,
                        vm)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
