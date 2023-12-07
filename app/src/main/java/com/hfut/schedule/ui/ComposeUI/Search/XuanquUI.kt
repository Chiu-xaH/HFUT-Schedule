package com.hfut.schedule.ui.ComposeUI.Search

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
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
    var code by remember { mutableStateOf("") }
    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
    var clicked by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }

    var space by remember { mutableStateOf(true) }



    fun getXuanqu() : List<XuanquResponse>? {
        val html = prefs.getString("xuanqu", "")

        // 定义一个正则表达式来匹配HTML标签
        val regex = """<td rowspan="(\d+)">(\d+)</td>\s*<td>(\d+)</td>\s*<td>(\d+)</td>\s*<td rowspan="\d+">(\d{4}-\d{2}-\d{2})</td>""".toRegex()

        val data = html?.let {
            regex.findAll(it).map {
                XuanquResponse(score = it.groupValues[2].toInt(), date = it.groupValues[5])
            }.toList()
        }
        return data
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
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

                    TextField(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 15.dp, vertical = 5.dp),
                        value = code,
                        onValueChange = {
                            code = it
                            clicked = false
                            space  = true
                        },
                        label = { Text("楼号+N/S+寝号") },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    CoroutineScope(Job()).launch {
                                        async {
                                            clicked = true
                                            loading = true
                                            vm.SearchXuanqu(code) }.await()
                                        async {
                                            delay(500)
                                            loading = false
                                            getXuanqu()
                                        }
                                    }
                                }) { Icon(painter = painterResource(R.drawable.search), contentDescription = "description") }
                        },
                        shape = MaterialTheme.shapes.medium,
                        colors = TextFieldDefaults.textFieldColors(focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                    )
                    space(space)
                }


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