package com.hfut.schedule.ui.activity.home.calendar.next

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.hfut.schedule.viewmodel.UIViewModel
import com.hfut.schedule.logic.beans.MyAPIResponse
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.Starter.refreshLogin
import com.hfut.schedule.ui.utils.MyToast
import com.hfut.schedule.ui.utils.Round
import com.hfut.schedule.ui.utils.ScrollText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NextCourse(ifSaved : Boolean,vmUI : UIViewModel,) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    var showAll by remember { mutableStateOf(false) }



    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            shape = Round(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("下学期课程表") },
                        actions = {
                            TextButton(onClick = { showAll = !showAll }) {
                                Icon(painter = painterResource(id = if (showAll) R.drawable.collapse_content else R.drawable.expand_content), contentDescription = "")
                            }
                        }
                    )
                },) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    SharePrefs.prefs.getString("gradeNext","23")?.let { DatumUI(showAll, it, innerPadding, vmUI) }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    ListItem(
        headlineContent = { ScrollText(text = "下学期课表") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.calendar),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
            if(Gson().fromJson(
                    SharePrefs.prefs.getString("my", MyApplication.NullMy),
                    MyAPIResponse::class.java).Next) {
                if(ifSaved) {
                    if(SharePrefs.prefs.getInt("FIRST",0) != 0)
                        showBottomSheet = true
                    else refreshLogin()
                } else showBottomSheet = true
            } else {
                MyToast("入口暂未开放")
            }
        }
    )
}