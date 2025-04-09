package com.hfut.schedule.ui.activity.home.search.functions.library

import android.os.Handler
import android.os.Looper
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
import androidx.compose.runtime.LaunchedEffect
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
import com.hfut.schedule.logic.utils.data.reEmptyLiveDta
import com.hfut.schedule.ui.utils.components.AnimationCardListItem
import com.hfut.schedule.ui.utils.components.appHorizontalDp
import com.hfut.schedule.ui.utils.components.BottomSheetTopBar
import com.hfut.schedule.ui.utils.components.HazeBottomSheetTopBar
import com.hfut.schedule.ui.utils.components.LoadingUI
import com.hfut.schedule.ui.utils.components.MyCustomCard
import com.hfut.schedule.ui.utils.components.StyleCardListItem
import com.hfut.schedule.ui.utils.style.HazeBottomSheet
import com.hfut.schedule.ui.utils.style.bottomSheetRound
import com.hfut.schedule.viewmodel.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.async
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryChips(vm : NetWorkViewModel,hazeState: HazeState) {
    var showBottomSheet_History by remember { mutableStateOf(false) }
    var showBottomSheet_Borrow by remember { mutableStateOf(false) }
    var showBottomSheet_Overdue by remember { mutableStateOf(false) }

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = appHorizontalDp(), vertical = 0.dp), horizontalArrangement = Arrangement.Start){

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
fun BorrowItems(vm : NetWorkViewModel,PerfsJson : LibraryItems) {
    val CommuityTOKEN = prefs.getString("TOKEN","")

    var loading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        if(loading) {
            CommuityTOKEN?.let {
                async { reEmptyLiveDta(vm.booksChipData) }.await()
                async { vm.communityBooks(it,PerfsJson) }.await()
                launch {
                    Handler(Looper.getMainLooper()).post{
                        vm.booksChipData.observeForever { result ->
                            if (result != null && result.contains("success")) {
                                loading = false
                            }
                        }
                    }
                }
            }
        }
    }

    if(loading) {
        LoadingUI()
    } else {
        val list = getBorrow(vm)
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
