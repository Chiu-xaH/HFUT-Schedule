package com.hfut.schedule.ui.screen.home.search.function.school.repair

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
   
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.xah.uicommon.component.text.ScrollText
import dev.chrisbanes.haze.HazeState

@Composable
fun Repair(hazeState : HazeState) {
    var showBottomSheet by remember { mutableStateOf(false) }

    TransplantListItem(
        headlineContent = { ScrollText(text = "后勤报修") },
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

                CardListItem(
                    headlineContent = {
                        Text("宣城校区")
                    },
                    modifier = Modifier.clickable {
                        Starter.startWebUrl(MyApplication.REPAIR_XC_URL)
                    }
                )
                CardListItem(
                    headlineContent = {
                        Text("合肥校区")
                    },
                    modifier = Modifier.clickable {
                        Starter.startWebUrl(MyApplication.REPAIR_URL)
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}