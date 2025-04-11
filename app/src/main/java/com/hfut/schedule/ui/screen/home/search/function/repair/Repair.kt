package com.hfut.schedule.ui.screen.home.search.function.repair

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
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
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.WebDialog
import com.hfut.schedule.ui.style.HazeBottomSheet
import dev.chrisbanes.haze.HazeState

@Composable
fun Repair(hazeState : HazeState) {
    var showBottomSheet by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    var url by remember { mutableStateOf("") }
    WebDialog(showDialog,{ showDialog = false }, url,"报修")

    TransplantListItem(
        headlineContent = { Text(text = "后勤报修") },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.build), contentDescription = "") },
        modifier = Modifier.clickable {
            showBottomSheet = true
        }
    )
    if (showBottomSheet ) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            hazeState = hazeState,
            isFullExpand = true,
            autoShape = false,
            showBottomSheet = showBottomSheet
        ) {
            Column(){
                HazeBottomSheetTopBar("选择校区", isPaddingStatusBar = false)

                StyleCardListItem(
                    headlineContent = {
                        Text("宣城校区")
                    },
                    modifier = Modifier.clickable {
                        url = MyApplication.REPAIR_XC_URL
                        showDialog = true
                    }
                )
                StyleCardListItem(
                    headlineContent = {
                        Text("合肥校区")
                    },
                    modifier = Modifier.clickable {
                        url = MyApplication.REPAIR_URL
                        showDialog = true
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}