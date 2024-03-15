package com.hfut.schedule.ui.Activity.success.search.Search.Xuanqu

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.utils.SharePrefs.Save
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XuanquItem(vm : LoginSuccessViewModel) {
    vm.XuanquData.value = "{}"
    val sheetState_Xuanqu = rememberModalBottomSheetState()
    var showBottomSheet_Xuanqu by remember { mutableStateOf(false) }


    ListItem(
        headlineContent = { Text(text = "寝室卫生评分") },
        overlineContent = { Text(text = "宣城校区") },
        leadingContent = { Icon(painter = painterResource(R.drawable.psychiatry),"" ) },
        modifier = Modifier.clickable { showBottomSheet_Xuanqu = true }
    )

    if (showBottomSheet_Xuanqu) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet_Xuanqu = false
            },
            sheetState = sheetState_Xuanqu
        ) {
            Column() {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                ) { XuanquUI(vm) }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}