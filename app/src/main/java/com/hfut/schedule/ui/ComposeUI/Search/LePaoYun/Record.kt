package com.hfut.schedule.ui.ComposeUI.Search.LePaoYun

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.gson.Gson
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.datamodel.LePaoYun.LePaoYunRecordResponse


fun getRecord(){
    val json = prefs.getString("LePaoYunRecord","")
    val result = Gson().fromJson(json,LePaoYunRecordResponse::class.java).data.rank
    for (i in 0 until result.size) {
        val list = result[i].rank.rankList
        for (j in 0 until list.size) {
            val timt = list[j].recordEndTime
            Log.d("s",timt)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordUI() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("跑步记录") }
            )
        },) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                // .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            //getRecord()
        }
    }
}