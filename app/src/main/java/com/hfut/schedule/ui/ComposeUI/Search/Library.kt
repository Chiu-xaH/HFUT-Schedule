package com.hfut.schedule.ui.ComposeUI.Search

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.JxglstuViewModel
import com.hfut.schedule.logic.OpenAlipay
import com.hfut.schedule.logic.datamodel.Library
import com.hfut.schedule.logic.datamodel.MyList
import com.hfut.schedule.logic.datamodel.content
import com.hfut.schedule.logic.datamodel.data4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Library(vm : JxglstuViewModel) {
    val sheetState_Library = rememberModalBottomSheetState()
    var showBottomSheet_Library by remember { mutableStateOf(false) }
    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
    val borrow =prefs.getString("borrow","正在获取")
    val sub =prefs.getString("sub","正在获取")


    ListItem(
        headlineContent = { Text(text = "图书  借阅 ${borrow} 本") },
        supportingContent = {Text(text = "搜索需接入校园网")},
        leadingContent = {
            Icon(
                painterResource(R.drawable.book),
                contentDescription = "Localized description",
            )
        },
        trailingContent={
            FilledTonalIconButton(onClick = {
                showBottomSheet_Library = true
            }) {
                Icon( painterResource(R.drawable.search),
                    contentDescription = "Localized description",)
            }
        },
        modifier = Modifier.clickable {
            showBottomSheet_Library = true
        }
    )

    val query = prefs.getString("Query", "")
    var input by remember { mutableStateOf(query ?: "") }

    fun LibItem() : MutableList<content> {

        val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
        val library = prefs.getString("library",MyApplication.NullLib)
        val data = Gson().fromJson(library,Library::class.java)
        val content = data.content
        var LibItems = mutableListOf<content>()
        content.forEach {  LibItems.add(it) }
        return LibItems
    }

    if (showBottomSheet_Library) {
        var loading by remember { mutableStateOf(true) }
        var onclick by remember { mutableStateOf(false) }
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet_Library = false
            },
            sheetState = sheetState_Library
        ) {
           LibItem()

            Column() {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextField(
                        value = input,
                        onValueChange = {
                            input = it
                            onclick = false
                                        },
                        label = { Text("点击右侧搜索" ) },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(
                               // shape = RoundedCornerShape(5.dp),
                                onClick = {
                                    val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                                    if(sp.getString("Query","") != input){ sp.edit().putString("Query", input).apply() }
                                onclick = true
                                    loading = true
                                CoroutineScope(Job()).apply {
                                    launch {
                                        async {
                                            val searchWords = JsonArray().apply {
                                                val fieldList = JsonArray().apply {
                                                    val field = JsonObject().apply {
                                                        addProperty("fieldCode", "")
                                                        addProperty("fieldValue", input)
                                                    }
                                                    add(field)
                                                }
                                                val searchWord = JsonObject().apply {
                                                    add("fieldList", fieldList)
                                                }
                                                add(searchWord)
                                            }
                                            val jsonObject = JsonObject().apply {
                                                add("searchWords",searchWords)
                                                add("filters", JsonArray())
                                                add("limiter", JsonArray())
                                                addProperty("sortField","relevance")
                                                addProperty("sortType","desc")
                                                addProperty("pageSize",20)
                                                addProperty("pageCount",1)
                                                addProperty("locale","zh_CN")
                                                addProperty("first",true)
                                            }
                                            vm.LibSearch(jsonObject)
                                        }.await()
                                        async {
                                            delay(1000)
                                            loading = false
                                            LibItem()
                                        }
                                    }
                                }

                            }) {
                                Icon(painter = painterResource(R.drawable.search), contentDescription = "description")
                            }
                        },
                        shape = MaterialTheme.shapes.medium,
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
                            unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
                        ),
                       // leadingIcon = { Icon( painterResource(R.drawable.search), contentDescription = "Localized description") },
                    )
                    if (onclick == false)
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
                CircularProgressIndicator()
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
                    Card(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 3.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp, vertical = 5.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        ListItem(
                            headlineContent = { Text(text = LibItem()[item].title,fontWeight = FontWeight.Bold) },
                            supportingContent = { Text(text = LibItem()[item].callNo) },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.book),
                                    contentDescription = "Localized description",
                                )
                            }
                        )
                        Divider()
                        ListItem(
                            headlineContent = { Text(text = LibItem()[item].author)  },
                            supportingContent = {Text(text = LibItem()[item].pubYear +  "  " + LibItem()[item].publisher) },
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