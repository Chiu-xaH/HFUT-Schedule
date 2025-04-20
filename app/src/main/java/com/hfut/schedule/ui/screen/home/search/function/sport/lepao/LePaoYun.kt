package com.hfut.schedule.ui.screen.home.search.function.sport.lepao

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.model.lepaoyun.LePaoYunHomeResponse
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.appHorizontalDp
import com.hfut.schedule.ui.component.cardNormalColor
import com.hfut.schedule.ui.component.BottomSheetTopBar
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.LittleDialog
import com.hfut.schedule.ui.component.MyCustomCard
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.style.HazeBottomSheet
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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
                StyleCardListItem(
                    headlineContent = { Text("校园跑") },
                    modifier = Modifier.clickable { Starter.startLaunchAPK("com.yunzhi.tiyu","云运动") },
                    trailingContent = {
                        Icon(Icons.Default.ArrowForward,null)
                    },
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.mode_of_travel), contentDescription = "") }
                )
                StyleCardListItem(
                    headlineContent = { Text("体测平台") },
                    overlineContent = { Text("学校网站内测 接入校园网") },
                    modifier = Modifier.clickable { Starter.startWebUrl(MyApplication.PE_URL) },
                    trailingContent = {
                        Icon(Icons.Default.ArrowForward,null)
                    },
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.cardio_load), contentDescription = "") }
                )
                Spacer(Modifier.height(appHorizontalDp()))
            }
        }
    }
}
