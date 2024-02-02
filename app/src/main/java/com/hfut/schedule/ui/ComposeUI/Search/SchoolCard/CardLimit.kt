package com.hfut.schedule.ui.ComposeUI.Search.SchoolCard

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.datamodel.zjgd.ChangeLimitResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun Click(vm : LoginSuccessViewModel,input : String) {
    val auth = SharePrefs.prefs.getString("auth","")
    val cardAccount = SharePrefs.prefs.getString("cardAccount","")
    val result = SharePrefs.prefs.getString("changeResult","{\"msg\":\"\"}")
    val json = JsonObject()
    val db = "${input}00".toInt()

    json.apply {
        addProperty("account",cardAccount)
        addProperty("autotransFlag","1")
        addProperty("autotransAmt",db)
        addProperty("synAccessSource","h5")
    }

    CoroutineScope(Job()).launch {
        async { auth?.let { vm.changeLimit(it,json) } }.await()
        async {
            delay(1000)
            Handler(Looper.getMainLooper()).post{
                val msg = Gson().fromJson(result,ChangeLimitResponse::class.java).msg
                Toast.makeText(MyApplication.context,msg, Toast.LENGTH_SHORT).show()
                if (msg.contains("成功")) SharePrefs.Save("input",input)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardLimit(vm : LoginSuccessViewModel) {
    var input by remember { mutableStateOf(SharePrefs.prefs.getString("input","") ?: "") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("限额修改") }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

                TextField(
                    modifier = Modifier.weight(1f).padding(horizontal = 15.dp, vertical = 5.dp),
                    value = input,
                    onValueChange = { input = it },
                    label = { Text("务必输入整数!") },
                    singleLine = true,
                    trailingIcon = { IconButton(onClick = {Click(vm,input)}) { Icon(Icons.Filled.Check, contentDescription = "description") } },
                    shape = MaterialTheme.shapes.medium,
                    colors = TextFieldDefaults.textFieldColors(focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                )
                Spacer(modifier = Modifier.height(500.dp))
            }
        }
    }
}