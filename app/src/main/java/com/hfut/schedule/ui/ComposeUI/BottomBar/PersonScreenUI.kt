package com.hfut.schedule.ui.ComposeUI

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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