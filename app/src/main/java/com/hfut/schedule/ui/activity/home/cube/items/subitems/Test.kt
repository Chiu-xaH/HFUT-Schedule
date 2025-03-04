package com.hfut.schedule.ui.activity.home.cube.items.subitems

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.MyDownloadManager.installApk
import com.hfut.schedule.logic.utils.MyNotificationManager
import com.hfut.schedule.receiver.BroadcastAction
import com.hfut.schedule.receiver.UpdateReceiver
import com.hfut.schedule.ui.utils.components.CustomTopBar
import com.hfut.schedule.ui.utils.components.SharedTopBar
import com.hfut.schedule.ui.utils.components.SmallCard
import com.hfut.schedule.ui.utils.components.TransplantListItem

@Composable
fun TEST(innerPaddings : PaddingValues) {
    Column (modifier = Modifier.padding(innerPaddings)) {
        Spacer(Modifier.height(innerPaddings.calculateTopPadding()))
        ShareUI()
    }
}
enum class ShareBarRoutes {
    FIRST,SECOND
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalSharedTransitionApi::class)
//@Preview
@Composable
fun ShareUI() {
    SharedTransitionLayout {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = ShareBarRoutes.FIRST.name
        ) {
            composable(ShareBarRoutes.FIRST.name) {
                FirstUI(navController,this@SharedTransitionLayout,this@composable)
            }
            composable(ShareBarRoutes.SECOND.name) {
                SecondUI(navController,this@SharedTransitionLayout,this@composable)
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.FirstUI(
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            CustomTopBar("一级界面")
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){
            LazyVerticalGrid(columns = GridCells.Fixed(2),modifier = Modifier.padding(horizontal = 11.dp)) {
                item {
                    SmallCard(modifier = Modifier.sharedBounds(
                        sharedContentState = rememberSharedContentState(key = "container"),
                        animatedVisibilityScope = animatedContentScope,
                        resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                    )) {
                        TransplantListItem(
                            headlineContent = {
                                Text(
                                    "二级界面",
                                    modifier = Modifier.sharedBounds(
                                        sharedContentState = rememberSharedContentState(key = "title"),
                                        animatedVisibilityScope = animatedContentScope
                                    )
                                )
                            },
                            leadingContent = {
                                Icon(painterResource(R.drawable.home), null)
                            },
                            modifier = Modifier.clickable { navController.navigate(ShareBarRoutes.SECOND.name) },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.SecondUI(
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope) {
    Scaffold(
        modifier = Modifier.fillMaxSize().sharedBounds(
            sharedContentState = rememberSharedContentState(key = "container"),
            animatedVisibilityScope = animatedContentScope,
            resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
        ),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        topBar = {
            SharedTopBar(
                "二级界面",
                key = "title",
                sharedTransitionScope,
                animatedContentScope,
            )
        },
    ) { innerPadding ->
        val context = LocalContext.current
        val activity = context as Activity
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){
            Button(
                onClick = {


                },
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text("发送通知")
            }
        }
    }
}