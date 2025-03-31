package com.hfut.schedule.ui.activity.home.search.functions.practice

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.hfut.schedule.ui.utils.components.BottomSheetTopBar
import com.hfut.schedule.ui.utils.components.showToast
import com.hfut.schedule.ui.utils.components.TransplantListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Practice(ifSaved : Boolean){
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    TransplantListItem(
        headlineContent = { Text(text = "实习") },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.work), contentDescription = "") },
        modifier = Modifier.clickable {
            //if(ifSaved) Login() else
               // showBottomSheet = true
            showToast("暂未开发")
        }
    )
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    BottomSheetTopBar("实习")
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {

                }
            }
        }
    }
}