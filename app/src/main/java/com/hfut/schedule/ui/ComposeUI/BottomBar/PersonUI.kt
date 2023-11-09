package com.hfut.schedule.ui.ComposeUI.BottomBar

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.activity.LoginActivity



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonScreen() {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("嗨  工大人") }
            )
        },) {innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
            //.background()插入背景
        ) {
          Column(modifier = Modifier
              .fillMaxWidth()) {
              Spacer(modifier = Modifier.height(15.dp))
              Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                  Card(
                      elevation = CardDefaults.cardElevation(
                          defaultElevation = 8.dp
                      ),
                      modifier = Modifier
                          .size(width = 350.dp, height = 150.dp),
                      shape = MaterialTheme.shapes.medium,
                      onClick = {
                          //
                      }
                  ) {
                      //Text(text = "姓名，班级，专业，学院，学号")
                  }
              }

              Spacer(modifier = Modifier.height(15.dp))

              Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                  Card(
                      elevation = CardDefaults.cardElevation(
                          defaultElevation = 10.dp
                      ),
                      modifier = Modifier
                          .size(width = 350.dp, height = 150.dp),
                      shape = MaterialTheme.shapes.medium,
                      onClick = {
                          //
                      }
                  ) {
                     // Text(text = "临近课程")
                  }
              }
          }
        }
    }
    //待开发
}

