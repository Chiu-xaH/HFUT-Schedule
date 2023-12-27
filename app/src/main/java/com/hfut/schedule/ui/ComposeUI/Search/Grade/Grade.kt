package com.hfut.schedule.ui.ComposeUI.Search.Grade

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.datamodel.GradeResponse
import org.jsoup.Jsoup


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Grade(vm : LoginSuccessViewModel)  {
    val cookie = prefs.getString("redirect", "")
    vm.getGrade(cookie!!)
    val sheetState_Grade = rememberModalBottomSheetState()
    var showBottomSheet_Grade by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text(text = "我的成绩") },
        leadingContent = { Icon(painterResource(R.drawable.article), contentDescription = "Localized description",) },
        modifier = Modifier.clickable {
            getGrade()
            showBottomSheet_Grade = true
        }
    )


    if (showBottomSheet_Grade ) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_Grade = false },
            sheetState = sheetState_Grade
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("我的成绩") }
                    )
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    GradeItemUI()
                }
            }
        }
    }
}


