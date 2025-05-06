package com.hfut.schedule.ui.screen.home.search.function.library

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
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.SimpleUiState
import com.hfut.schedule.logic.util.network.reEmptyLiveDta
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.component.AnimationCustomCard
import com.hfut.schedule.ui.component.CommonNetworkScreen
import com.hfut.schedule.ui.component.DividerTextExpandedWith
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.LoadingUI
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.WebDialog
import com.hfut.schedule.ui.component.appHorizontalDp
import com.hfut.schedule.ui.component.cardNormalColor
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation")
@Composable
fun LibraryItem(vm : NetWorkViewModel, hazeState: HazeState) {
    var showBottomSheet_Library by remember { mutableStateOf(false) }

    TransplantListItem(
        headlineContent = { Text(text = "图书馆") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.book),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable { showBottomSheet_Library = true }
    )

    var showDialog by remember { mutableStateOf(false) }
    WebDialog(showDialog,{ showDialog = false },MyApplication.NEW_LIBRARY_URL,"新图书馆")
    var showDialog2 by remember { mutableStateOf(false) }
    WebDialog(showDialog2,{ showDialog2 = false },MyApplication.LIBRARY_SEAT + "home/web/f_second","座位预约(合肥校区)(校园网)")


    if (showBottomSheet_Library) {
        HazeBottomSheet(
            onDismissRequest = { showBottomSheet_Library = false },
            showBottomSheet = showBottomSheet_Library,
            hazeState = hazeState
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("图书") {
                        Row {
                            FilledTonalButton(
                                onClick = {
                                    showDialog = true
                                }
                            ) {
                                Text(text = "官网")
                            }
                            Spacer(Modifier.width(appHorizontalDp()/3))
                            FilledTonalButton(
                                onClick = {
                                    showDialog2 = true
                                }
                            ) {
                                Text(text = "座位预约")
                            }
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
fun BooksUI(vm: NetWorkViewModel, hazeState: HazeState) {
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
                    .padding(horizontal = appHorizontalDp()),
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
                                containerColor = cardNormalColor(),
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
                    modifier = Modifier.padding(appHorizontalDp()).align(Alignment.BottomCenter)
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
                    modifier = Modifier.padding(appHorizontalDp()).align(Alignment.BottomStart)
                ) {
                    FloatingActionButton(
                        onClick = {
                            if(page > 1) {
                                page--
                                refresh = true
                            } else {
                                showToast("第一页")
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
                    modifier = Modifier.padding(appHorizontalDp()).align(Alignment.BottomEnd)
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
fun DetailBookUI(vm: NetWorkViewModel, callNo : String) {
    val uiState by vm.bookPositionData.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = {
        prefs.getString("TOKEN","")?.let {
            vm.bookPositionData.clear()
            vm.getBookPosition(it,callNo)
        }
    }
    LaunchedEffect(Unit) {
        refreshNetwork()
    }
    DividerTextExpandedWith("索书号 $callNo") {
        CommonNetworkScreen(uiState, onReload = refreshNetwork,isFullScreen = false) {
            val list = (uiState as SimpleUiState.Success).data
            LazyColumn {
                items(list.size) { index ->
                    val item = list[index]
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
        Spacer(Modifier.height(20.dp))
    }
}