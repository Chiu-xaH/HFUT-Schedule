package com.hfut.schedule.ui.screen.home.search.function.jxglstu.courseSearch

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.ui.component.text.ScrollText
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.style.HazeBottomSheet
import dev.chrisbanes.haze.HazeState


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesSearch(ifSaved :  Boolean, vm : NetWorkViewModel, hazeState : HazeState) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    TransplantListItem(
        headlineContent = { ScrollText(text = "开课查询") },
       // overlineContent = { Text(text = "查询下学期")},
        leadingContent = {
            Icon(
                painterResource(R.drawable.search),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
            if(ifSaved) refreshLogin() else
            showBottomSheet = true
        }
    )

    if (showBottomSheet) {
        HazeBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet,
        ) {
            CourseSearchUI(vm, hazeState )
        }
    }
}

