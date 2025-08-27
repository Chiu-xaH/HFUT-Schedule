package com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.jxglstu.lessons
import com.hfut.schedule.logic.util.network.ParseJsons.isNextOpen
import com.hfut.schedule.ui.component.container.AnimationCardListItem
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.xah.uicommon.component.text.ScrollText
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState

@Composable
fun TermFirstlyInfo(list: List<lessons>, isSearch : Boolean) {
    if(list.isEmpty()) return

    val info = list[0].semester
    AnimationCardListItem(
        overlineContent = { Text(text = info.startDate + " ~ " + info.endDate)},
        headlineContent = {  ScrollText(info.nameZh) },
        leadingContent = { Icon(
            painterResource(R.drawable.category),
            contentDescription = "Localized description",
        ) },
        color = MaterialTheme.colorScheme.secondaryContainer,
        trailingContent = {
            if(!isSearch)
                Text(text = "学分 ${periodsSum(list)}")
        },
        index = 0
    )
}



private fun periodsSum(list: List<lessons>) : Double {
    var num = 0.0
    for(i in list) {
        val credit = i.course.credits
        if (credit != null) {
            num += credit
        }
    }
    return num
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseTotalForApi(
    modifier: Modifier = Modifier,
    vm: NetWorkViewModel,
    isIconOrText : Boolean = false,
    next : Boolean = false,
    hazeState: HazeState,
    ifSaved : Boolean,
    onNextChange : (() -> Unit)? = null
) {
    var showBottomSheet_Total by remember { mutableStateOf(false) }

    var next2 by remember { mutableStateOf(false) }

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
        HazeBottomSheet (
            onDismissRequest = {
                showBottomSheet_Total = false
            },
            showBottomSheet = showBottomSheet_Total,
            hazeState = hazeState
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("课程汇总") {
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
                            if(next2) TotalCourseDataSource.MINE_NEXT
                            else TotalCourseDataSource.MINE
                        } else {
                            if(next) TotalCourseDataSource.MINE_NEXT
                            else TotalCourseDataSource.MINE
                        }
                        ,
                        sortType,
                        vm,
                        hazeState,
                        ifSaved,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
