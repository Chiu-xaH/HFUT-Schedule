package com.hfut.schedule.ui.ComposeUI.Search

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.OpenAlipay
import com.hfut.schedule.logic.SharePrefs
import com.hfut.schedule.logic.SharePrefs.Save
import com.hfut.schedule.logic.SharePrefs.prefs
import com.hfut.schedule.logic.datamodel.SearchEleResponse
import com.hfut.schedule.ui.MyToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchEle(vm : LoginSuccessViewModel) {

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }


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
                }
                }
            }
        }

    ListItem(
        headlineContent = { Text(text = "电费查询") },
        supportingContent = { Text(text = "仅宣城校区,需接入校园网使用")},
        leadingContent = {
            Icon(painterResource(R.drawable.flash_on), contentDescription = "Localized description",)
        },
        modifier = Modifier.clickable { showBottomSheet  = true}
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



    DropdownMenu(expanded = showitem, onDismissRequest = { showitem = false }) {
        DropdownMenuItem(text = { Text(text = "北1") }, onClick = { BuildingsNumber =  "1"
            showitem = false})
        DropdownMenuItem(text = { Text(text = "北2") }, onClick = {  BuildingsNumber =  "2"
            showitem = false})
        DropdownMenuItem(text = { Text(text = "北3") }, onClick = {  BuildingsNumber =  "3"
            showitem = false})
        DropdownMenuItem(text = { Text(text = "北4") }, onClick = {  BuildingsNumber =  "4"
            showitem = false})
        DropdownMenuItem(text = { Text(text = "北5") }, onClick = {  BuildingsNumber =  "5"
            showitem = false})
        DropdownMenuItem(text = { Text(text = "南6") }, onClick = {  BuildingsNumber =  "6"
            showitem = false})
        DropdownMenuItem(text = { Text(text = "南7") }, onClick = {  BuildingsNumber =  "7"
            showitem = false})
        DropdownMenuItem(text = { Text(text = "南8") }, onClick = {  BuildingsNumber =  "8"
            showitem = false})
        DropdownMenuItem(text = { Text(text = "南9") }, onClick = {  BuildingsNumber =  "9"
            showitem = false})
        DropdownMenuItem(text = { Text(text = "南10") }, onClick = {  BuildingsNumber = "10"
            showitem = false})
    }

    DropdownMenu(expanded = showitem2, onDismissRequest = { showitem2 = false }) {
        DropdownMenuItem(text = { Text(text = "南照明") }, onClick = { EndNumber = "11"
            showitem2 = false})
        DropdownMenuItem(text = { Text(text = "南空调") }, onClick = { EndNumber = "12"
            showitem2 = false})
        DropdownMenuItem(text = { Text(text = "北照明") }, onClick = { EndNumber = "21"
            showitem2 = false})
        DropdownMenuItem(text = { Text(text = "北空调") }, onClick = { EndNumber = "22"
            showitem2 = false})
    }
    DropdownMenu(expanded = showitem3, onDismissRequest = { showitem3 = false }) {
        DropdownMenuItem(text = { Text(text = "南") }, onClick = { EndNumber = "11"
            showitem3 = false })
        DropdownMenuItem(text = { Text(text = "北") }, onClick = { EndNumber = "21"
            showitem3 = false })
    }

    if (BuildingsNumber == "0") BuildingsNumber = ""


    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 15.dp, vertical = 0.dp), horizontalArrangement = Arrangement.Start){

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
            label = { Text(text = "选择南北 ${EndNumber}") },
        //    leadingIcon = { Icon(painter = painterResource(R.drawable.calendar), contentDescription = "description") }
        )

        Spacer(modifier = Modifier.width(10.dp))


    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
    ) {
        TextField(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 15.dp),
            value = RoomNumber,
            onValueChange = {
                RoomNumber = it
            },
            label = { Text("输入房间号") },
            singleLine = true,
            trailingIcon = {
                IconButton(
                    // shape = RoundedCornerShape(5.dp),
                    onClick = {
                        CoroutineScope(Job()).launch {
                            async {
                                Save("BuildNumber",BuildingsNumber)
                                Save("EndNumber",EndNumber)
                                Save("RoomNumber",RoomNumber)
                            }
                            async { vm.searchEle(jsons) }.await()
                            async {
                                delay(1000)
                                 val result = prefs.getString("SearchEle","")
                                if (result?.contains("query_elec_roominfo") == true) {
                                    val msg = Gson().fromJson(result,SearchEleResponse::class.java).query_elec_roominfo.errmsg
                                    MyToast(msg)
                                } else{
                                    MyToast("无法获取")
                                }

                            }
                        }



                    }) {
                    Icon(
                        painter = painterResource(R.drawable.search),
                        contentDescription = "description"
                    )
                }
            },
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
                unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
            ),
        )
    }
}