package com.hfut.schedule.ui.screen.home.search.function.one.emptyRoom

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import dev.chrisbanes.haze.HazeState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmptyRoom(vm : NetWorkViewModel, ifSaved : Boolean, hazeState: HazeState){
    var showBottomSheet_EmptyRoom by remember { mutableStateOf(false) }

    TransplantListItem(
        headlineContent = { Text(text = "空教室") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.meeting_room),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
            showToast("暂时关闭，后续版本重构")
//            if(ifSaved) refreshLogin()
//            else {
//                val token = prefs.getString("bearer","")
//                showBottomSheet_EmptyRoom = true
//                token?.let { vm.searchEmptyRoom("XC001", it) }
//                token?.let { vm.searchEmptyRoom("XC002", it) }
//                // view = "待开发"
//            }
        }
    )

    if (showBottomSheet_EmptyRoom) {
        HazeBottomSheet (
            onDismissRequest = {
                showBottomSheet_EmptyRoom = false
            },
            showBottomSheet = showBottomSheet_EmptyRoom,
            hazeState = hazeState
        ) {
            Column() {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    emptyRoomUI(vm)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
