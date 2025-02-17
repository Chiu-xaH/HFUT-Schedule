package com.hfut.schedule.ui.activity.home.search.functions.library

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import com.hfut.schedule.ui.utils.components.LoadingUI
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.enums.LibraryItems
import com.hfut.schedule.logic.beans.community.LibRecord
import com.hfut.schedule.logic.beans.community.LibraryResponse
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.ui.utils.components.MyCustomCard
import com.hfut.schedule.ui.utils.style.Round
import com.hfut.schedule.ui.utils.style.textFiledTransplant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryItem(vm : NetWorkViewModel) {
    val sheetState_Library = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_Library by remember { mutableStateOf(false) }
    val borrow = prefs.getString("borrow","获取")
    val sub = prefs.getString("sub","0")
    val CommuityTOKEN = prefs.getString("TOKEN","")
    CommuityTOKEN?.let { vm.GetBorrowed(it,"1") }
    CommuityTOKEN?.let { vm.GetHistory(it,"1") }

    ListItem(
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

    var input by remember { mutableStateOf( "") }

    fun LibItem() : List<LibRecord> {
        val library = vm.libraryData.value
        try {
            if(library != null && library.contains("操作成功")){
                val data = Gson().fromJson(library, LibraryResponse::class.java)
                return data.result.records
            } else {
                return emptyList()
            }
        } catch (e:Exception) {
            return emptyList()
        }
    }

    if (showBottomSheet_Library) {
        var loading by remember { mutableStateOf(true) }
        var onclick by remember { mutableStateOf(false) }

        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_Library = false },
            sheetState = sheetState_Library,
            shape = Round(sheetState_Library)
        ) {
         //  LibItem()
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("图书") },
                        actions = {
                            FilledTonalButton(
                                onClick = {
                                Starter.startWebUrl("http://210.45.242.5:8080/")
                            },
                                modifier = Modifier.padding(horizontal = 15.dp)
                            ) {
                                Text(text = "更多(官网)")
                            }
                        }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    Column {
                        LibraryChips()

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            TextField(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 15.dp),
                                value = input,
                                onValueChange = {
                                    input = it
                                    onclick = false
                                },
                                label = { Text("搜索图书" ) },
                                singleLine = true,
                                trailingIcon = {
                                    IconButton(
                                        // shape = RoundedCornerShape(5.dp),
                                        onClick = {
                                            SharePrefs.saveString("Query", input)
                                            onclick = true
                                            loading = true
                                            vm.libraryData.value = MyApplication.NullLib
                                            CoroutineScope(Job()).launch {
                                                    async { CommuityTOKEN?.let { vm.SearchBooks(it,input) } }.await()
                                                    async {
                                                        Handler(Looper.getMainLooper()).post{
                                                            vm.libraryData.observeForever { result ->
                                                               // Log.d("ee",result)
                                                                if(result.contains("操作成功")) {
                                                                    CoroutineScope(Job()).launch {
                                                                        async { loading = false }
                                                                        async { LibItem() }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                        }) {
                                        Icon(painter = painterResource(R.drawable.search), contentDescription = "description")
                                    }
                                },
                                shape = MaterialTheme.shapes.medium,
                                colors = textFiledTransplant(),
                                // leadingIcon = { Icon( painterResource(R.drawable.search), contentDescription = "Localized description") },
                            )
                            if (!onclick)
                                Spacer(modifier = Modifier.fillMaxHeight())

                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (onclick) {

                            AnimatedVisibility(
                                visible = loading,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center)  {
                                    //  Column() {
                                    Spacer(modifier = Modifier.height(5.dp))
                                    LoadingUI()
                                    //  }

                                }
                            }////加载动画居中，3s后消失

                            AnimatedVisibility(
                                visible = !loading,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                LazyColumn {
                                    items (LibItem().size){ item ->
                                        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center)
                                        {
                                            MyCustomCard{
                                                ListItem(
                                                    headlineContent = { LibItem()[item].name?.let { Text(text = it,fontWeight = FontWeight.Bold) } },
                                                    supportingContent = { LibItem()[item].callNumber?.let { Text(text = it) } },
                                                    overlineContent = { LibItem()[item].place?.let { Text(text = it) } },
                                                    trailingContent = { LibItem()[item].status_dictText?.let { Text(text = it) } },
                                                    leadingContent = {
                                                        Icon(
                                                            painterResource(R.drawable.book),
                                                            contentDescription = "Localized description",
                                                        )
                                                    }
                                                )
                                                Divider()
                                                ListItem(
                                                    headlineContent = { LibItem()[item].author?.let { Text(text = it) } },
                                                    supportingContent = {Text(text = LibItem()[item].year +  "  " + LibItem()[item].publisher) },
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
                            }
                        } else {
                            Spacer(modifier = Modifier.fillMaxHeight())
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}