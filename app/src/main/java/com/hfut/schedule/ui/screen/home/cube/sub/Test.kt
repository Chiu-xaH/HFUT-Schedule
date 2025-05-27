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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
//import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
//import androidx.compose.material3.adaptive.layout.AnimatedPane
//import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
//import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
//import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.AppNotificationManager
import com.hfut.schedule.ui.component.BottomSheetTopBar
import com.hfut.schedule.ui.component.SharedTopBar
import com.hfut.schedule.ui.component.SmallCard
import com.hfut.schedule.ui.component.TransplantListItem
import kotlinx.coroutines.launch
import java.io.Serializable


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
@RequiresApi(36)
@Composable
fun TEST(innerPaddings : PaddingValues) {
    Column (modifier = Modifier.padding(innerPaddings)) {
        Spacer(Modifier.height(innerPaddings.calculateTopPadding()))
//        val  feedList = listOf(
//                    Feed("A",  "Feed A Content"),
//                    Feed("B",  "Feed B Content"),
//                    Feed("C",  "Feed C Content"),
//                    Feed("D",  "Feed D Content"),
//                    Feed("E",  "Feed E Content"),
//                    Feed("F",  "Feed F Content"),
//                    Feed("G",  "Feed G Content"),
//              )
//               val  navigator = rememberListDetailPaneScaffoldNavigator<Feed>()
//               val  scope = rememberCoroutineScope()
//              NavigableListDetailPaneScaffold(
//                    navigator = navigator,
//                    listPane = {
//                          AnimatedPane {
//                                ListPane(feedList) { feed ->
//                                      scope.launch {
//                                            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, feed)
//                                      }
//                                }
//                          }
//                    },
//                    detailPane = {
//                          AnimatedPane {
//                                navigator.currentDestination?.contentKey?.let {
//                                      DetailPane(it)
//                                } ?: DetailPane(feedList[0])
//                          }
//                    },
//              )
//        ShareUI()
    }
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