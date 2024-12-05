package com.hfut.schedule.ui.Activity.success.search.Search.Teachers

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.NetWorkViewModel
import com.hfut.schedule.logic.utils.StartApp
import com.hfut.schedule.logic.utils.loadImage
import com.hfut.schedule.ui.UIUtils.MyCard
import com.hfut.schedule.ui.UIUtils.URLImage
import com.hfut.schedule.ui.UIUtils.WebViewScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherListUI(vm: NetWorkViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var links by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("教师主页") }
    val dataList = getTeacherList(vm)

    if (showDialog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        actions = {
                            Row{
                                IconButton(onClick = { StartApp.startUri(links) }) { Icon(
                                    painterResource(id = R.drawable.net), contentDescription = "") }
                                IconButton(onClick = { showDialog = false }) { Icon(painterResource(id = R.drawable.close), contentDescription = "") }
                            }
                        },
                        title = { Text(title) }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    WebViewScreen(url = links)
                }
            }
        }
    }


    LazyVerticalGrid(columns = GridCells.Fixed(2),modifier = Modifier.padding(horizontal = 11.dp))  {
        items(dataList.size) { index->
            val item = dataList[index]
            item?.let {
                MyCard(modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)) {
                    ListItem(
                        headlineContent = { Text(text = it.name) },
                        supportingContent = {
                            Column {
                                Spacer(modifier = Modifier.height(2.dp))
                                URLImage(url = MyApplication.TeacherURL + it.picUrl, size = 120.dp)
                            }
                        },
//                        trailingContent = {
//                            Icon(Icons.Filled.ArrowForward, contentDescription = "")
//                        },
                        modifier = Modifier.clickable {
                            links = it.url
                            title = it.name
                            showDialog = true
                        }
                    )
                }
            }
        }
    }
}




