package com.hfut.schedule.ui.screen.home.search.function.one.emptyRoom

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.logic.model.one.EmptyRoomResponse
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private fun search() : MutableList<String> {
    val rooms = mutableListOf<String>()

    val Roomjson = prefs.getString("emptyjson", "{\"data\": {\"records\": [{ \"nameZh\": \"请选择楼栋\"}] }}")
    try {
        val data = Gson().fromJson(Roomjson, EmptyRoomResponse::class.java)
        val record = data.data.records
        for (element in record) {
            var room = element.nameZh
            room = room.replace("学堂"," ")
            rooms.add(room)
        }
    } catch (_ : Exception) { }
    return rooms
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun emptyRoomUI(vm : NetWorkViewModel) {
    var selected1 by remember { mutableStateOf(true) }
    var selected2 by remember { mutableStateOf(false) }
    var selected3 by remember { mutableStateOf(false) }
    var selected4 by remember { mutableStateOf(false) }
    var selected5 by remember { mutableStateOf(false) }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            HazeBottomSheetTopBar(" 空教室")
        },) {innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Row() {
                Spacer(modifier = Modifier.width(20.dp))

                FilterChip(
                    onClick = {selected1 = selected1},
                    label = { Text(text = "宣城") },
                    selected = selected1,
                    leadingIcon = if (selected1) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "OK",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else null

                )
                Spacer(modifier = Modifier.width(20.dp))

                FilterChip(
                    onClick = {
                        selected2 = selected2
                        Toast.makeText(MyApplication.context,"未开发", Toast.LENGTH_SHORT).show()
                    },
                    label = { Text(text = "屯溪路") },
                    selected = selected2,
                    leadingIcon = if (selected2) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "OK",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else null

                )

                Spacer(modifier = Modifier.width(20.dp))

                FilterChip(
                    onClick = {
                        selected3 = selected3
                        Toast.makeText(MyApplication.context,"未开发", Toast.LENGTH_SHORT).show()
                    },
                    label = { Text(text = "翡翠湖") },
                    selected = selected3,
                    leadingIcon = if (selected3) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "OK",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else null

                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Row() {
                Spacer(modifier = Modifier.width(20.dp))

                FilterChip(
                    onClick = {
                        selected5 = !selected5
                        if (selected5) {
                            val token = prefs.getString("bearer","")
                            token?.let { vm.searchEmptyRoom("XC001", it) }
                            CoroutineScope(Job()).launch {
                                delay(500)
                                search()
                            }
                        }

                    },
                    label = { Text(text = "新安学堂") },
                    selected = selected5,
                    leadingIcon = if (selected5) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "OK",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else null

                )
                Spacer(modifier = Modifier.width(20.dp))

                FilterChip(
                    onClick = {
                        selected4 = !selected4
                        if (selected4) {
                            val token = prefs.getString("bearer","")
                            token?.let { vm.searchEmptyRoom("XC001", it) }
                            CoroutineScope(Job()).launch {
                                delay(500)
                                search() }
                        }
                    },
                    label = { Text(text = "敬亭学堂") },
                    selected = selected4,
                    leadingIcon = if (selected4) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "OK",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else null

                )



            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(10.dp))

            val chunkedRooms = search().chunked(3)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                LazyColumn() {
                    items(chunkedRooms) { rowrooms ->
                        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                            for (room in rowrooms) {
                                SuggestionChip(
                                    onClick = { /*TODO*/ },
                                    label = { Text(text = room) },
                                    modifier = Modifier.padding(horizontal = 14.dp)
                                )
                            }
                        }

                    }
                }
            }

        }
    }
}