package com.hfut.schedule.ui.screen.home.search.function.jxglstu.nextCourse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.ParseJsons.isNextOpen
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.webview.WebDialog
import com.hfut.schedule.ui.component.text.BottomSheetTopBar
import com.hfut.schedule.ui.component.text.ScrollText
import com.hfut.schedule.ui.screen.home.calendar.next.JxglstuCourseTableUINext
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.CourseTotalForApi
import com.hfut.schedule.ui.style.CustomBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import dev.chrisbanes.haze.HazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NextCourse(ifSaved : Boolean, vmUI : UIViewModel, vm : NetWorkViewModel, hazeState: HazeState) {

    val sheetState_next = rememberModalBottomSheetState(true)
    var showBottomSheet_next by remember { mutableStateOf(false) }

    var next by remember { mutableStateOf(isNextOpen()) }

    var showAll by remember { mutableStateOf(false) }

    var showDialogN by remember { mutableStateOf(false) }
    val webVpnCookie by DataStoreManager.webVpnCookie.collectAsState(initial = "")

    val cookie = if (!vm.webVpn) prefs.getString(
        "redirect",
        ""
    ) else MyApplication.WEBVPN_COOKIE_HEADER + webVpnCookie
    WebDialog(
        showDialogN,
        { showDialogN = false },
        url = if(vm.webVpn) MyApplication.JXGLSTU_WEBVPN_URL else MyApplication.JXGLSTU_URL + "for-std/course-table",
        title = "教务系统",
        cookie = cookie
    )

    if (showBottomSheet_next) {
        CustomBottomSheet (
            onDismissRequest = { showBottomSheet_next = false },
            showBottomSheet = showBottomSheet_next,
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    BottomSheetTopBar("下学期课程表") {
                        Row {
                            CourseTotalForApi(vm=vm, next=next, onNextChange = { next = !next}, hazeState = hazeState, ifSaved = ifSaved)
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
                    JxglstuCourseTableUINext(showAll,vm,vmUI,hazeState)
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
                if(!ifSaved) {
                    showDialogN = true
                } else {
                    showToast("入口暂未开放")
                }
            }
        }
    )
}