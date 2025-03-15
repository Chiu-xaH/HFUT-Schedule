package com.hfut.schedule.ui.activity.home.search.functions.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AssistChip
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.enums.LibraryItems
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.ui.utils.components.AnimationCardListItem
import com.hfut.schedule.ui.utils.components.AppHorizontalDp
import com.hfut.schedule.ui.utils.components.BottomSheetTopBar
import com.hfut.schedule.ui.utils.components.HazeBottomSheetTopBar
import com.hfut.schedule.ui.utils.components.MyCustomCard
import com.hfut.schedule.ui.utils.components.StyleCardListItem
import com.hfut.schedule.ui.utils.style.HazeBottomSheet
import com.hfut.schedule.ui.utils.style.bottomSheetRound
import com.hfut.schedule.viewmodel.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.joinAll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryChips(vm : NetWorkViewModel,hazeState: HazeState) {
    val sheetState_History = rememberModalBottomSheetState(true)
    var showBottomSheet_History by remember { mutableStateOf(false) }
    val sheetState_Borrow = rememberModalBottomSheetState(true)
    var showBottomSheet_Borrow by remember { mutableStateOf(false) }

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = AppHorizontalDp(), vertical = 0.dp), horizontalArrangement = Arrangement.Start){

        AssistChip(
            onClick = { showBottomSheet_Borrow = true },
            label = { Text(text = "当前借阅  ${getBorrow(LibraryItems.BORROWED.name).size} 本") },
            // leadingIcon = { Icon(painter = painterResource(R.drawable.add), contentDescription = "description") }
        )

        Spacer(modifier = Modifier.width(10.dp))

        AssistChip(
            onClick = { showBottomSheet_History = true },
            label = { Text(text = "借阅历史  ${getBorrow(LibraryItems.HISTORY.name).size} 本") },
            // leadingIcon = { Icon(painter = painterResource(R.drawable.calendar), contentDescription = "description") }
        )
    }
    if(showBottomSheet_History) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_History = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_History
//            sheetState = sheetState_History,
//            shape = bottomSheetRound(sheetState_History)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("借阅历史")
                },) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) { BorrowItems(LibraryItems.HISTORY.name) }
            }
        }
    }

    if(showBottomSheet_Borrow) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_Borrow = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_Borrow
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("当前借阅")
                },) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        // .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ) { BorrowItems(LibraryItems.BORROWED.name) }
            }
        }
    }
}

@Composable
fun BorrowItems(PerfsJson : String) {
    LazyColumn {
        items(getBorrow(PerfsJson).size){ item ->
            val Outtime = getBorrow(PerfsJson)[item].outTime
            val Returntime = getBorrow(PerfsJson)[item].returnTime
            AnimationCardListItem(
                headlineContent = { Text(text = getBorrow(PerfsJson)[item].bookName,fontWeight = FontWeight.Bold) },
                supportingContent = {  Text(text = getBorrow(PerfsJson)[item].author) },
                overlineContent = {
                    if(Returntime == null)
                        Text(text = "借于 $Outtime")
                    else
                        Text(text = "借于 $Outtime\n还于 $Returntime")
                },
                leadingContent = {
                    Icon(painterResource(R.drawable.book), contentDescription = "Localized description",)
                },
                index = item
            )
        }
    }
}
