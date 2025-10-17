package com.hfut.schedule.ui.screen.home.search.function.community.library

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.hfut.schedule.logic.enumeration.LibraryItems
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.AnimationCardListItem
 
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryChips(vm : NetWorkViewModel, hazeState: HazeState) {
    var showBottomSheet_History by remember { mutableStateOf(false) }
    var showBottomSheet_Borrow by remember { mutableStateOf(false) }
    var showBottomSheet_Overdue by remember { mutableStateOf(false) }

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = APP_HORIZONTAL_DP, vertical = 0.dp), horizontalArrangement = Arrangement.Start){

        AssistChip(
            onClick = { showBottomSheet_Borrow = true },
            label = { Text(text = "当前借阅") },
        )

        Spacer(modifier = Modifier.width(10.dp))

        AssistChip(
            onClick = { showBottomSheet_History = true },
            label = { Text(text = "借阅历史") },
        )

        Spacer(modifier = Modifier.width(10.dp))

        AssistChip(
            onClick = { showBottomSheet_Overdue = true },
            label = { Text(text = "逾期书本") },
        )
    }
    if(showBottomSheet_History) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_History = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_History
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
                ) { BorrowItems(vm,LibraryItems.HISTORY) }
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
                ) { BorrowItems(vm,LibraryItems.BORROWED) }
            }
        }
    }

    if(showBottomSheet_Overdue) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_Overdue = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_Overdue
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("逾期书本")
                },) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        // .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ) { BorrowItems(vm,LibraryItems.OVERDUE) }
            }
        }
    }
}

@Composable
fun BorrowItems(vm : NetWorkViewModel, type : LibraryItems) {
    val uiState by vm.booksChipData.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = {
        prefs.getString("TOKEN","")?.let {
            vm.booksChipData.clear()
            vm.communityBooks(it,type)
        }
    }
    LaunchedEffect(Unit) {
        refreshNetwork()
    }
    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        val list = (uiState as UiState.Success).data
        LazyColumn {
            items(list.size){ index ->
                val item = list[index]
                val Outtime = item.outTime
                val Returntime = item.returnTime
                AnimationCardListItem(
                    headlineContent = { Text(text = item.bookName,fontWeight = FontWeight.Bold) },
                    supportingContent = {  Text(text = item.author) },
                    overlineContent = {
                        if(Returntime == null)
                            Text(text = "借于 $Outtime")
                        else
                            Text(text = "借于 $Outtime\n还于 $Returntime")
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.book), contentDescription = "Localized description",)
                    },
                    index = index
                )
            }
        }
    }
}
