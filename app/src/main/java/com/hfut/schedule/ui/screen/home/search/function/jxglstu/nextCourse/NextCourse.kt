package com.hfut.schedule.ui.screen.home.search.function.jxglstu.nextCourse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
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
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.network.parse.ParseJsons.isNextOpen
import com.hfut.schedule.ui.screen.home.calendar.next.DatumUI
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.CourseTotalForApi
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.ScrollText
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.UIViewModel
import dev.chrisbanes.haze.HazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NextCourse(ifSaved : Boolean, vmUI : UIViewModel, vm : NetWorkViewModel, hazeState: HazeState) {

    val sheetState_next = rememberModalBottomSheetState(true)
    var showBottomSheet_next by remember { mutableStateOf(false) }

    var next by remember { mutableStateOf(isNextOpen()) }

    var showAll by remember { mutableStateOf(false) }


    if (showBottomSheet_next) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_next = false },
//            sheetState = sheetState_next,
//            shape = bottomSheetRound(sheetState_next),
            showBottomSheet = showBottomSheet_next,
            hazeState = hazeState
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("下学期课程表") {
                        Row {
                            CourseTotalForApi(vm=vm, next=next, onNextChange = { next = !next}, hazeState = hazeState)
                            TextButton(onClick = { showAll = !showAll }) {
                                Icon(painter = painterResource(id = if (showAll) R.drawable.collapse_content else R.drawable.expand_content), contentDescription = "")
                            }
                        }
                    }
                },) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    DatumUI(showAll, innerPadding, vmUI,vm, hazeState)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    TransplantListItem(
        headlineContent = { ScrollText(text = "下学期课表") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.calendar),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
            if (isNextOpen()) {
                if(ifSaved) {
                    if(prefs.getInt("FIRST",0) != 0)
                        showBottomSheet_next = true
                    else refreshLogin()
                } else showBottomSheet_next = true
            } else {
                showToast("入口暂未开放")
            }
        }
    )
}