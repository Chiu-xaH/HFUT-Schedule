package com.hfut.schedule.ui.screen.home.search.function.school.sport.lepao

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP

import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import dev.chrisbanes.haze.HazeState


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LePaoYun(hazeState: HazeState) {
    var showBottomSheet by remember { mutableStateOf(false) }

    TransplantListItem(
        headlineContent = { Text(text = "体育") },
        leadingContent = { Icon(painterResource(R.drawable.directions_run),null) },
        modifier = Modifier.clickable {
            showBottomSheet = true
        }
    )

    if (showBottomSheet) {
        HazeBottomSheet(
            showBottomSheet = showBottomSheet,
            onDismissRequest =  { showBottomSheet = false },
            isFullExpand = false,
            autoShape = false,
            hazeState = hazeState
        ) {
            Column {
                HazeBottomSheetTopBar("体育", isPaddingStatusBar = false)
                CardListItem(
                    headlineContent = { Text("校园跑") },
                    modifier = Modifier.clickable { Starter.startAppLaunch(Starter.AppPackages.LEPAO) },
                    trailingContent = {
                        Icon(Icons.Default.ArrowForward,null)
                    },
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.mode_of_travel), contentDescription = "") }
                )
                CardListItem(
                    headlineContent = { Text("体测平台") },
                    overlineContent = { Text("学校网站内测 接入校园网") },
                    modifier = Modifier.clickable { Starter.startWebUrl(MyApplication.PE_URL + "bdlp_h5_fitness_test/public/index.php/index/login/hfutLogin") },
                    trailingContent = {
                        Icon(Icons.Default.ArrowForward,null)
                    },
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.cardio_load), contentDescription = "") }
                )
                Spacer(Modifier.height(APP_HORIZONTAL_DP))
            }
        }
    }
}
