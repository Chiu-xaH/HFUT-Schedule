package com.hfut.schedule.ui.activity.home.search.functions.library

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.enums.LibraryItems
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.logic.utils.data.reEmptyLiveDta
import com.hfut.schedule.ui.utils.components.AnimationCustomCard
import com.hfut.schedule.ui.utils.components.AppHorizontalDp
import com.hfut.schedule.ui.utils.components.CardNormalColor
import com.hfut.schedule.ui.utils.components.BottomSheetTopBar
import com.hfut.schedule.ui.utils.components.DividerTextExpandedWith
import com.hfut.schedule.ui.utils.components.HazeBottomSheetTopBar
import com.hfut.schedule.ui.utils.components.LoadingUI
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.components.StyleCardListItem
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.style.HazeBottomSheet
import com.hfut.schedule.ui.utils.style.bottomSheetRound
import com.hfut.schedule.ui.utils.style.textFiledTransplant
import com.hfut.schedule.viewmodel.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryItem(vm : NetWorkViewModel,hazeState: HazeState) {
    val sheetState_Library = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_Library by remember { mutableStateOf(false) }


    TransplantListItem(
        headlineContent = { Text(text = "图书") },
        overlineContent = { Text(text = "已借 ${getBorrow(LibraryItems.BORROWED.name).size} 本")},
        leadingContent = {
            Icon(
                painterResource(R.drawable.book),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable { showBottomSheet_Library = true }
    )



    if (showBottomSheet_Library) {
        HazeBottomSheet(
            onDismissRequest = { showBottomSheet_Library = false },
            showBottomSheet = showBottomSheet_Library,
            hazeState = hazeState
//            sheetState = sheetState_Library,
//            shape = bottomSheetRound(sheetState_Library)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("图书") {
                        FilledTonalButton(
                            onClick = {
                                Starter.startWebUrl("http://210.45.242.5:8080/")
                            }
                        ) {
                            Text(text = "更多(官网)")
                        }
                    }
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    BooksUI(vm,hazeState)
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksUI(vm: NetWorkViewModel,hazeState: HazeState) {
    var input by remember { mutableStateOf( "") }
    val CommuityTOKEN = prefs.getString("TOKEN","")
    var refresh by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("地点") }
    var callNum by remember { mutableStateOf<String>("") }
    var page by remember { mutableStateOf(1) }

    LaunchedEffect(refresh) {
        if(refresh) {
            async { reEmptyLiveDta(vm.libraryData) }.await()
            async { loading = true }.await()
            async { CommuityTOKEN?.let { vm.SearchBooks(it,input,page) } }.await()
            launch {
                Handler(Looper.getMainLooper()).post {
                    vm.libraryData.observeForever { result ->
                        if (result != null) {
                            if(result.contains("成功")) {
                                refresh = false
                                loading = false
                            }
                        }
                    }
                }
            }
        }
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet,
            autoShape = false
//            sheetState = sheetState,
        ) {
            Column {
                HazeBottomSheetTopBar(title, isPaddingStatusBar = false)
                DetailBookUI(vm,callNum)
            }
        }
    }

    Column {
        LibraryChips(vm,hazeState)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = AppHorizontalDp()),
                value = input,
                onValueChange = {
                    input = it
                },
                label = { Text("搜索图书" ) },
                singleLine = true,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            refresh = true
                        }) {
                        Icon(painter = painterResource(R.drawable.search), contentDescription = "description")
                    }
                },
                shape = MaterialTheme.shapes.medium,
                colors = textFiledTransplant(),
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        if(loading) {
            LoadingUI("加载较慢 请稍后")
        } else {
            val listState = rememberLazyListState()
            val shouldShowAddButton by remember { derivedStateOf { listState.firstVisibleItemScrollOffset == 0 } }

            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(state = listState) {
                    val books = getBooks(vm)
                    items (books.size){ index ->
                        val item = books[index]
                        val name = item.name
                        val callNo = item.callNumber
                        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                            AnimationCustomCard(
                                containerColor = CardNormalColor(),
                                index = index,
                                modifier = Modifier.clickable {
                                    title = name
                                    callNum = callNo
                                    showBottomSheet = true
                                }
                            ) {
                                TransplantListItem(
                                    headlineContent = { Text(text = name,fontWeight = FontWeight.Bold) },
                                    supportingContent = { Text(text = callNo) },
                                    leadingContent = {
                                        Icon(
                                            painterResource(R.drawable.book),
                                            contentDescription = "Localized description",
                                        )
                                    },
                                    trailingContent = {
                                        Icon(
                                            Icons.Default.ArrowForward,
                                            contentDescription = "Localized description",
                                        )
                                    }
                                )
                                Divider()
                                TransplantListItem(
                                    headlineContent = { item.author?.let { Text(text = it) } },
                                    supportingContent = {Text(text = item.year +  "  " + item.publisher) },
                                    leadingContent = {
                                        Icon(
                                            painterResource(R.drawable.info),
                                            contentDescription = "Localized description",
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = shouldShowAddButton,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.padding(AppHorizontalDp()).align(Alignment.BottomCenter)
                ) {
                    ExtendedFloatingActionButton(
                        onClick = {
                            page++
                            refresh = true
                        }
                    ) {
                        Text("第${page}页")
                    }
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = shouldShowAddButton,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.padding(AppHorizontalDp()).align(Alignment.BottomStart)
                ) {
                    FloatingActionButton(
                        onClick = {
                            if(page > 1) {
                                page--
                                refresh = true
                            } else {
                                MyToast("第一页")
                            }
                        },
                    ) {
                        Icon(Icons.Default.ArrowBack,null)
                    }
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = shouldShowAddButton,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.padding(AppHorizontalDp()).align(Alignment.BottomEnd)
                ) {
                    FloatingActionButton(
                        onClick = {
                            page++
                            refresh = true
                        }
                    ) {
                        Icon(Icons.Default.ArrowForward,null)
                    }
                }
            }
        }
    }
}


@Composable
fun DetailBookUI(vm: NetWorkViewModel,callNo : String) {
    val CommuityTOKEN = prefs.getString("TOKEN","")
    var refresh by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(refresh) {
        async { reEmptyLiveDta(vm.bookPositionData) }.await()
        async { loading = true }.await()
        async { CommuityTOKEN?.let { vm.getBookPosition(it,callNo) } }.await()
        launch {
            Handler(Looper.getMainLooper()).post {
                vm.bookPositionData.observeForever { result ->
                    if (result != null) {
                        if(result.contains("成功")) {
                            refresh = false
                            loading = false
                        }
                    }
                }
            }
        }
    }
    DividerTextExpandedWith("索书号 $callNo") {
        if(loading) {
            LoadingUI()
        } else {
            val infos = getBookDetail(vm)
            LazyColumn {
                items(infos.size) { index ->
                    val item = infos[index]
                    val status = item.status_dictText
                    StyleCardListItem(
                        headlineContent = {
                            Text(item.place)
                        },
                        leadingContent = {
                            Icon(painterResource(R.drawable.near_me),null)
                        },
                        trailingContent = {
                            Text(status)
                        },
                    )
                }
            }
        }
    }
    Spacer(Modifier.height(20.dp))
}