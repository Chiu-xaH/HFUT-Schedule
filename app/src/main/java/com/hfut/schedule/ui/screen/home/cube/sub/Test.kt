package com.hfut.schedule.ui.screen.home.cube.sub

//import androidx.compose.ui.tooling.preview.Preview
import android.annotation.SuppressLint
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
//import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
//import androidx.compose.material3.adaptive.layout.AnimatedPane
//import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
//import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
//import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.other.AppVersion.CAN_MOTION_BLUR
import com.hfut.schedule.logic.util.sys.AppNotificationManager
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.BottomSheetTopBar
import com.hfut.schedule.ui.component.text.SharedTopBar
import com.hfut.schedule.ui.component.container.SmallCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.xah.transition.state.TransitionState
import com.xah.transition.util.TransitionPredictiveBackHandler


//class Feed(val title: String, val content: String) : Serializable
//
//
//
//@Composable
//fun   ListPane(feedList:List<Feed>, onItemClick: (Feed) ->   Unit)   {
//            LazyColumn(
//                    modifier = Modifier.fillMaxSize(),
//            ) {
//                        items(feedList.size) {
//                                Card(
//                                        onClick = { onItemClick(feedList[it]) },
//                                        modifier = Modifier
//                                                .fillMaxWidth()
//                                                .height(120.dp)
//                                                .padding(10.dp),
//                                        shape = RoundedCornerShape(10.dp)
//                                ) {
//                                        Box(modifier = Modifier.fillMaxSize()) {
//                                                Text(
//                                                        modifier = Modifier
//                                                                .padding(8.dp)
//                                                                .align(Alignment.Center),
//                                                        text = feedList[it].title,
//                                                )
//                                        }
//                                }
//                        }
//                }
//}
//
//@Composable
//fun   DetailPane(feed:   Feed)   {
//            Box(modifier = Modifier.fillMaxSize()) {
//                        Text(
//                                text = feed.content,
//                                fontSize =   30.sp,
//                                modifier = Modifier.align(Alignment.Center),
//                        )
//                }
//}
//@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@OptIn(ExperimentalSharedTransitionApi::class)
@RequiresApi(36)
@Composable
fun TEST(innerPaddings : PaddingValues,navController : NavHostController) {
//    var scale by remember { mutableFloatStateOf(1f) }
//    TransitionPredictiveBackHandler(navController) {
//        scale = it
//    }
//    var motionBlur by remember { mutableStateOf(TransitionState.transitionBackgroundStyle.motionBlur) }
//    LaunchedEffect(motionBlur) {
//        TransitionState.transitionBackgroundStyle.motionBlur = motionBlur
//    }
//
//    var forceAnimation by remember { mutableStateOf(TransitionState.transitionBackgroundStyle.level) }
//    LaunchedEffect(forceAnimation) {
//        TransitionState.transitionBackgroundStyle.level = forceAnimation
//    }
//
//
//    var animationSpeed by remember { mutableStateOf(TransitionState.curveStyle.speedMs.toFloat()) }
//    LaunchedEffect(animationSpeed) {
//        TransitionState.curveStyle.speedMs = animationSpeed.toInt()
//    }
//    Column (modifier = Modifier.padding(innerPaddings).scale(scale)) {
//        DividerTextExpandedWith("模糊") {
//            TransplantListItem(
//                headlineContent = { Text("运动模糊") },
//                leadingContent = {
//                    Icon(painterResource(R.drawable.deblur),null)
//                },
//                trailingContent = {
//                    Switch(enabled = CAN_MOTION_BLUR, checked = motionBlur, onCheckedChange = { motionBlur = !motionBlur })
//                },
//                supportingContent = { Text("一些组件在运动中会伴随模糊效果" + if(CAN_MOTION_BLUR) "(Android 12+)" else "")},
//                modifier = Modifier.clickable { motionBlur = !motionBlur }
//            )
//        }
//        DividerTextExpandedWith("动效") {
//            TransplantListItem(
//                headlineContent = { Text("增强转场动画") },
//                leadingContent = {
//                    Icon(painterResource(R.drawable.animation),null)
//                },
//                trailingContent = {
//                    Switch(checked = forceAnimation, onCheckedChange = { forceAnimation = !forceAnimation })
//                },
//                supportingContent = { Text("转场时启用背景模糊和缩放")},
//                modifier = Modifier.clickable { forceAnimation = !forceAnimation }
//            )
//        }
////        val  feedList = listOf(
////                    Feed("A",  "Feed A Content"),
////                    Feed("B",  "Feed B Content"),
////                    Feed("C",  "Feed C Content"),
////                    Feed("D",  "Feed D Content"),
////                    Feed("E",  "Feed E Content"),
////                    Feed("F",  "Feed F Content"),
////                    Feed("G",  "Feed G Content"),
////              )
////               val  navigator = rememberListDetailPaneScaffoldNavigator<Feed>()
////               val  scope = rememberCoroutineScope()
////              NavigableListDetailPaneScaffold(
////                    navigator = navigator,
////                    listPane = {
////                          AnimatedPane {
////                                ListPane(feedList) { feed ->
////                                      scope.launch {
////                                            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, feed)
////                                      }
////                                }
////                          }
////                    },
////                    detailPane = {
////                          AnimatedPane {
////                                navigator.currentDestination?.contentKey?.let {
////                                      DetailPane(it)
////                                } ?: DetailPane(feedList[0])
////                          }
////                    },
////              )
////        ShareUI()
//    }
}
enum class ShareBarRoutes {
    FIRST,SECOND
}

@RequiresApi(36)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalSharedTransitionApi::class)
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
            BottomSheetTopBar("一级界面")
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

@RequiresApi(36)
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
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){
            Button(
                onClick = {
                    AppNotificationManager.updateCourseProgress("机器学习","16:00","17:50")
                },
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text("打开Dialog")
            }
        }
    }
}