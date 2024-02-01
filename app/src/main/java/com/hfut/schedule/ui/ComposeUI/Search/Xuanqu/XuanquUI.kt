package com.hfut.schedule.ui.ComposeUI.Search.Xuanqu

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.Save
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.datamodel.XuanquResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation")
@Composable
fun space(space : Boolean) {
    if (space)
    Spacer(modifier = Modifier.height(700.dp))
}



@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun XuanquUI(vm : LoginSuccessViewModel) {
    val Savedcode = prefs.getString("Room","")
    var code by remember { mutableStateOf(Savedcode ?: "") }
    var clicked by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    var space by remember { mutableStateOf(true) }

    fun getXuanqu() : List<XuanquResponse>? {
        val html = vm.XuanquData.value

        // 定义一个正则表达式来匹配HTML标签
        val regex = """<td rowspan="(\d+)">(\d+)</td>\s*<td>(\d+)</td>\s*<td>(\d+)</td>\s*<td rowspan="\d+">(\d{4}-\d{2}-\d{2})</td>""".toRegex()

        val data = html?.let {
            regex.findAll(it).map {
                XuanquResponse(score = it.groupValues[2].toInt(), date = it.groupValues[5])
            }.toList()
        }
        return data
    }

    val SavedBuildNumber = prefs.getString("BuildNumber", "0")
    var BuildingsNumber by remember { mutableStateOf(SavedBuildNumber ?: "0") }
    var NS by remember { mutableStateOf( "S") }
    var NSBoolean by remember { mutableStateOf(prefs.getBoolean("NS",true)) }
    val SavedRoomNumber = prefs.getString("RoomNumber", "")
    var RoomNumber by remember { mutableStateOf(SavedRoomNumber ?: "") }
    var showitem by remember { mutableStateOf(false) }
    var showitem4 by remember { mutableStateOf(false) }

    if (NSBoolean) NS = "S"
    else NS = "N"


    DropdownMenu(expanded = showitem, onDismissRequest = { showitem = false }, offset = DpOffset(15.dp,0.dp)) {
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



    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("寝室卫生评分查询") }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column {

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
                        onClick = {NSBoolean = !NSBoolean},
                        label = { Text(text = "南北 $NS") },
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    AssistChip(
                        onClick = { showitem4 = !showitem4 },
                        label = { Text(text = "房间号 ${RoomNumber}") },
                        //leadingIcon = { Icon(painter = painterResource(R.drawable.add), contentDescription = "description") }
                    )
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
                                    Save("BuildNumber", BuildingsNumber)
                                    Save("RoomNumber", RoomNumber)
                                    SharePrefs.SaveBoolean("NS",true,NSBoolean)
                                    showitem4 = false
                                }
                                async {
                                    Handler(Looper.getMainLooper()).post{
                                        vm.XuanquData.value = "{}"
                                    }
                                            clicked = true
                                            loading = true
                                            Save("Room",code)

                                            vm.SearchXuanqu(BuildingsNumber + NS + RoomNumber) }.await()
                                async {
                                        Handler(Looper.getMainLooper()).post{
                                            vm.XuanquData.observeForever { result ->
                                               // Log.d("r",result)
                                                if(result.contains("div")) {
                                                    CoroutineScope(Job()).launch {
                                                        async { loading = false }
                                                        async { getXuanqu() }
                                                    }
                                                }
                                            }
                                        }
                                }
                            }
                        },
                        label = { Text(text = "搜索") },
                        leadingIcon = { Icon(painter = painterResource(R.drawable.search), contentDescription = "description") }
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
                    }
                }
             //   if(showitem4) {

               // }
                Row{ space(space) }

                if (clicked) {
                    space  = false
                    AnimatedVisibility(
                        visible = loading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center)  {
                            Spacer(modifier = Modifier.height(5.dp))
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(50.dp))
                        }
                    }


                    AnimatedVisibility(
                        visible = !loading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ){
                        LazyColumn {

                            getXuanqu()?.let {
                                items(it.size) { item ->
                                    Card(
                                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 15.dp, vertical = 5.dp),
                                        shape = MaterialTheme.shapes.medium
                                    ) {
                                        ListItem(
                                            headlineContent = { getXuanqu()?.get(item)?.let { it1 -> Text(text = it1.date) } },
                                            supportingContent = { Text(text =  "${getXuanqu()?.get(item)?.score} 分")}
                                        )
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }


}