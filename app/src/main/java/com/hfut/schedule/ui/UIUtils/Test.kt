package com.hfut.schedule.ui.UIUtils

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun Tests() {
  //Ver(5,4)
    NoNet()
}

@Composable
fun Ver(widnum : Int,heinum : Int) {
    // 定义Card的高度和边距
    val cardHeight = 120.dp
    val cardMargin = 4.dp


    // 使用LazyVerticalGrid创建一个4列的网格
    LazyVerticalGrid(
        columns = GridCells.Fixed(widnum),
        modifier = Modifier
            // 填充父组件的宽度
            .fillMaxWidth()
            .padding(horizontal = 7.dp)
        // 设置水平居中对齐
    ) {
        // 使用items函数创建20个Card
        items(widnum*heinum) { index ->
            // 使用Card函数创建一个Card
            Card(
                modifier = Modifier
                    // 设置Card的高度
                    .height(cardHeight)
                    // 设置Card的边距
                    .padding(cardMargin),
                shape = MaterialTheme.shapes.extraSmall,
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Text(
                    text = "20:00\n内容测试A\n${index}",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}