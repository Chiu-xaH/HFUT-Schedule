package com.hfut.schedule.ui.ComposeUI.Search

import android.webkit.WebView
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import com.google.gson.Gson
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.SharePrefs.Save
import com.hfut.schedule.logic.SharePrefs.prefs
import com.hfut.schedule.logic.datamodel.SearchEleResponse
import com.hfut.schedule.ui.ComposeUI.MyToast
import com.hfut.schedule.ui.theme.FWDTColr
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



@Composable
fun WebViewScreen(url: String) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                loadUrl(url)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchEle(vm : LoginSuccessViewModel) {

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()


    val scale = animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f, // 按下时为0.9，松开时为1
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "" // 使用弹簧动画
    )

    if (showBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("宣区电费查询") }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    EleUI(vm)
                    Spacer(modifier = Modifier.height(30.dp))
                }
                }
            }
        }

    ListItem(
        headlineContent = { Text(text = "寝室电费") },
        supportingContent = { Text(text = "仅宣城校区,需接入校园网使用")},
        leadingContent = {
            Icon(painterResource(R.drawable.flash_on), contentDescription = "Localized description",)
        },
        modifier = Modifier.clickable { showBottomSheet  = true }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EleUI(vm : LoginSuccessViewModel) {
    val SavedBuildNumber = prefs.getString("BuildNumber", "0")
    var BuildingsNumber by remember { mutableStateOf(SavedBuildNumber ?: "0") }
    val SavedRoomNumber = prefs.getString("RoomNumber", "")
    var RoomNumber by remember { mutableStateOf(SavedRoomNumber ?: "") }
    val SavedEndNumber = prefs.getString("EndNumber", "")
    var EndNumber by remember { mutableStateOf(SavedEndNumber ?: "") }

    var input = "300"+BuildingsNumber+RoomNumber+EndNumber
    var jsons = "{ \"query_elec_roominfo\": { \"aid\":\"0030000000007301\", \"account\": \"24027\",\"room\": { \"roomid\": \"${input}\", \"room\": \"${input}\" },  \"floor\": { \"floorid\": \"\", \"floor\": \"\" }, \"area\": { \"area\": \"\", \"areaname\": \"\" }, \"building\": { \"buildingid\": \"\", \"building\": \"\" },\"extdata\":\"info1=\" } }"


    var showitem by remember { mutableStateOf(false) }
    var showitem2 by remember { mutableStateOf(false) }
    var showitem3 by remember { mutableStateOf(false) }
    var showitem4 by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }

    DropdownMenu(expanded = showitem, onDismissRequest = { showitem = false }, offset = DpOffset(103.dp,0.dp)) {
        DropdownMenuItem(text = { Text(text = "北一号楼") }, onClick = { BuildingsNumber =  "1"
            showitem = false})
        DropdownMenuItem(text = { Text(text = "北二号楼") }, onClick = {  BuildingsNumber =  "2"
            showitem = false})
        DropdownMenuItem(text = { Text(text = "北三号楼") }, onClick = {  BuildingsNumber =  "3"
            showitem = false})
        DropdownMenuItem(text = { Text(text = "北四号楼") }, onClick = {  BuildingsNumber =  "4"
            showitem = false})
        DropdownMenuItem(text = { Text(text = "北五号楼") }, onClick = {  BuildingsNumber =  "5"
            showitem = false})
        DropdownMenuItem(text = { Text(text = "南六号楼") }, onClick = {  BuildingsNumber =  "6"
            showitem = false})
        DropdownMenuItem(text = { Text(text = "南七号楼") }, onClick = {  BuildingsNumber =  "7"
            showitem = false})
        DropdownMenuItem(text = { Text(text = "南八号楼") }, onClick = {  BuildingsNumber =  "8"
            showitem = false})
        DropdownMenuItem(text = { Text(text = "南九号楼") }, onClick = {  BuildingsNumber =  "9"
            showitem = false})
        DropdownMenuItem(text = { Text(text = "南十号楼") }, onClick = {  BuildingsNumber = "10"
            showitem = false})
    }

    DropdownMenu(expanded = showitem2, onDismissRequest = { showitem2 = false }, offset = DpOffset(210.dp,0.dp)) {
        DropdownMenuItem(text = { Text(text = "南边照明") }, onClick = { EndNumber = "11"
            showitem2 = false})
        DropdownMenuItem(text = { Text(text = "南边空调") }, onClick = { EndNumber = "12"
            showitem2 = false})
        DropdownMenuItem(text = { Text(text = "北边照明") }, onClick = { EndNumber = "21"
            showitem2 = false})
        DropdownMenuItem(text = { Text(text = "北边空调") }, onClick = { EndNumber = "22"
            showitem2 = false})
    }
    DropdownMenu(expanded = showitem3, onDismissRequest = { showitem3 = false }) {
        DropdownMenuItem(text = { Text(text = "南边") }, onClick = { EndNumber = "11"
            showitem3 = false })
        DropdownMenuItem(text = { Text(text = "北边") }, onClick = { EndNumber = "21"
            showitem3 = false })
    }

    if (BuildingsNumber == "0") BuildingsNumber = ""

    if (showDialog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = FWDTColr,
                            titleContentColor = Color.White,
                        ),
                        actions = { IconButton(onClick = { showDialog = false }) {
                          Icon(painterResource(id = R.drawable.close), contentDescription = "", tint = Color.White)
                        }},
                        title = { Text("服务大厅") }
                    )
                },
            ) { innerPadding ->
               Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                     WebViewScreen(url = "http://172.31.248.26:8088")
                }
            }
        }
    }

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 15.dp, vertical = 0.dp), horizontalArrangement = Arrangement.Start){

        AssistChip(
            onClick = {showDialog = true},
            label = { Text(text = "充值") },
            leadingIcon = { Icon(painter = painterResource(R.drawable.add), contentDescription = "description") }
        )

        Spacer(modifier = Modifier.width(10.dp))

        AssistChip(
            onClick = { showitem = true },
            label = { Text(text = "选择楼栋 ${BuildingsNumber}") },
            //leadingIcon = { Icon(painter = painterResource(R.drawable.add), contentDescription = "description") }
        )

        Spacer(modifier = Modifier.width(10.dp))


        AssistChip(
            onClick = {
                when {
                BuildingsNumber.toInt() > 5 -> showitem2 = true
                BuildingsNumber.toInt() in 1..4 -> showitem3 = true
                else -> Toast.makeText(MyApplication.context,"请选择楼栋",Toast.LENGTH_SHORT).show()
            }
                      },
            label = { Text(text = "选择南北") },
        //    leadingIcon = { Icon(painter = painterResource(R.drawable.calendar), contentDescription = "description") }
        )

        Spacer(modifier = Modifier.width(10.dp))

    }

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 15.dp, vertical = 0.dp), horizontalArrangement = Arrangement.Start){

        AssistChip(
            onClick = { RoomNumber = RoomNumber.replaceFirst(".$".toRegex(), "") },
            label = { Text(text = "删除") },
            leadingIcon = { Icon(painter = painterResource(R.drawable.close), contentDescription = "description") }
        )
        Spacer(modifier = Modifier.width(10.dp))
        AssistChip(
            onClick = {
                CoroutineScope(Job()).launch {
                    async {
                        showitem4 = false
                        Save("BuildNumber", BuildingsNumber)
                        Save("EndNumber", EndNumber)
                        Save("RoomNumber", RoomNumber)
                    }
                    async { vm.searchEle(jsons) }.await()
                    async {
                        delay(1000)
                        val result = prefs.getString("SearchEle", "")
                        if (result?.contains("query_elec_roominfo") == true) {
                            val msg = Gson().fromJson(
                                result,
                                SearchEleResponse::class.java
                            ).query_elec_roominfo.errmsg
                            MyToast(msg)
                        } else MyToast("无法获取,是否连接hfut-wlan")
                    }
                }
            },
            label = { Text(text = "搜索") },
            leadingIcon = { Icon(painter = painterResource(R.drawable.search), contentDescription = "description") }
        )
        Spacer(modifier = Modifier.width(10.dp))

        AssistChip(
            onClick = { showitem4 = !showitem4 },
            label = { Text(text = "房间号 ${RoomNumber}") },
            //leadingIcon = { Icon(painter = painterResource(R.drawable.add), contentDescription = "description") }
        )

    }

    Spacer(modifier = Modifier.height(7.dp))


    AnimatedVisibility(
        visible = showitem4,
        enter = slideInVertically(
            initialOffsetY = { -40 }
        ) + expandVertically(
            expandFrom = Alignment.Top
        ) + scaleIn(
            // Animate scale from 0f to 1f using the top center as the pivot point.
            transformOrigin = TransformOrigin(0.5f, 0f)
        ) + fadeIn(initialAlpha = 0.3f),
        exit = slideOutVertically() + shrinkVertically() + fadeOut() + scaleOut(targetScale = 1.2f)
    ){
        Row (modifier = Modifier.padding(horizontal = 15.dp)){
        OutlinedCard{
            LazyColumn(modifier = Modifier.padding(horizontal = 10.dp)) {
                item {
                    Text(text = " 选取房间号", modifier = Modifier.padding(10.dp))
                }
                item {
                    LazyRow {
                        items(5) { items ->
                            IconButton(onClick = {
                                if (RoomNumber.length < 3)
                                    RoomNumber = RoomNumber + items.toString()
                                else Toast.makeText(
                                    MyApplication.context,
                                    "三位数",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }) { Text(text = items.toString()) }
                        }
                    }
                }
                item {
                    LazyRow {
                        items(5) { items ->
                            val num = items + 5
                            IconButton(onClick = {
                                if (RoomNumber.length < 3)
                                    RoomNumber = RoomNumber + num
                                else Toast.makeText(
                                    MyApplication.context,
                                    "三位数",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }) { Text(text = num.toString()) }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }}
   // if(showitem4) {

//    }
}