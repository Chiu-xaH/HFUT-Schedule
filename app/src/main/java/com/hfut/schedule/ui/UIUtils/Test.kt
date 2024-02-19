package com.hfut.schedule.ui.UIUtils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.DismissDirection
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.navigation.Navigator
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.datamodel.NavigationBarItemData
import com.hfut.schedule.ui.ComposeUI.BottomBar.Refresh
import com.hfut.schedule.ui.ComposeUI.BottomBar.getName
import com.hfut.schedule.ui.ComposeUI.Focus.AddItem
import com.hfut.schedule.ui.ComposeUI.Focus.AddedItems
import com.hfut.schedule.ui.ComposeUI.Focus.RemoveItems
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.HazeMaterials

import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnrememberedMutableState")
@Composable
fun Tests2() {
      var Lists by remember { mutableStateOf( AddedItems() ) }
      LazyColumn {
          items(Lists.size, key = { Lists[it].id }){ item ->

              val dismissState = rememberDismissState()
              if(dismissState.isDismissed(DismissDirection.EndToStart)){
                  Lists = Lists.toMutableList().also { it.removeAt(item) }
                  RemoveItems(AddedItems()[item].id)
              }

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                  Spacer(modifier = Modifier.height(100.dp))
                  SwipeToDismiss(
                      state = dismissState,
                      // animateItemPlacement() 此修饰符便添加了动画
                      modifier = Modifier
                          .fillMaxWidth()
                          .animateItemPlacement(),
                      // 下面这个参数为触发滑动删除的移动阈值
                      dismissThresholds = { direction ->
                          FractionalThreshold(if (direction == DismissDirection.EndToStart) 0.25f else 0.5f)
                      },
                      // 允许滑动删除的方向
                      directions = setOf(DismissDirection.EndToStart),
                      // "背景 "，即原来显示的内容被划走一部分时显示什么
                      background = {
                          /*保证观看体验，省略此处内容*/
                      }
                  ){
                      Card(
                          elevation = CardDefaults.cardElevation(
                              defaultElevation = 3.dp
                          ),
                          modifier = Modifier
                              .fillMaxWidth()
                              .padding(horizontal = 15.dp, vertical = 5.dp),
                          shape = MaterialTheme.shapes.medium,
                      ) {
                          ListItem(
                              headlineContent = { Text(text = Lists[item].title) },
                              overlineContent = { Text(text = Lists[item].remark) },
                              supportingContent = { Text(text = Lists[item].info) },
                              leadingContent = { Icon(painterResource(R.drawable.lightbulb), contentDescription = "Localized description",) },
                              modifier = Modifier.combinedClickable(
                                  onClick = { MyToast("滑删除") },
                                  onDoubleClick = {
                                      //双击操作
                                  },
                                  onLongClick = {
                                      //长按操作
                                  })
                          )
                      }
                  }
              }
          }
      }
}

@Composable
fun Tests() {
//AnimatedVectorDrawable()
    val wifiManager = MyApplication.context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val wifiInfo = wifiManager.connectionInfo.ssid
    Log.d("SSID",wifiInfo)
    DialogSample()
}

@OptIn(ExperimentalAnimationGraphicsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AnimatedVectorDrawable() {
    val image = AnimatedImageVector.animatedVectorResource(R.drawable.ic_hourglass_animated)
    var atEnd by remember { mutableStateOf(false) }
    val blurEffect = BlurEffect(0.5f,0.5f)

    Icon(
        painter = rememberAnimatedVectorPainter(image, atEnd),
        contentDescription = "Timer",
        modifier = Modifier.clickable { atEnd = !atEnd },
       // contentScale = ContentScale.Crop
    )


}


@Composable
fun ts() {
    Box(
        Modifier
            .size(300.dp)
            // Blur content allowing the result to extend beyond the bounds of the original content
            .blur(30.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
    ) {
        Button(onClick = { /*TODO*/ }) {
            Text(text = "lll")
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogSample() {
    val hazeState = remember { HazeState() }
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            TopAppBar(
            modifier = Modifier.hazeChild(state = hazeState),
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = .45f),
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
            title = {  Text("同学") },
            actions = {
                IconButton(onClick = {

                }) {
                    Icon(painter = painterResource(id = R.drawable.rotate_right),
                        contentDescription = "",
                        //modifier = Modifier.graphicsLayer(rotationZ = if (rotating.value) angle else 0f)
                    )
                }
            },
        )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(.35f),
                modifier = Modifier
                .hazeChild(state = hazeState)) {
                //    val image = AnimatedImageVector.animatedVectorResource(R.drawable.ic_hourglass_animated)
                //  var atEnd by remember { mutableStateOf(false) }

                val items = listOf(
                    NavigationBarItemData("1", "课程表", painterResource(R.drawable.calendar ), painterResource(R.drawable.calendar_month_filled)),
                    NavigationBarItemData("2","聚焦", painterResource(R.drawable.lightbulb), painterResource(R.drawable.lightbulb_filled)),
                    NavigationBarItemData("search","查询中心", painterResource(R.drawable.search),painterResource(R.drawable.search_filledx)),
                    NavigationBarItemData("3","选项", painterResource(R.drawable.cube), painterResource(R.drawable.deployed_code_filled))
                )
                items.forEach { item ->
                    val route = item.route
                    val selected = navController.currentBackStackEntryAsState().value?.destination?.route == route
                    NavigationBarItem(
                        selected = selected,

                                // alwaysShowLabel = sholable,
                     //   enabled = isEnabled,
                        onClick = {
                            //   if(item == items[2])
                            //     atEnd = !atEnd
                            if (!selected) {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        label = { Text(text = item.label) },
                        icon = {
                            BadgedBox(badge = {
                                if (item == items[3]){
                                 //   if (showBadge)
                                      //  Badge{ Text(text = "1")}
                                }
                            }) { Icon(if(selected)item.filledIcon else item.icon, contentDescription = item.label) }
                        }
                    )
                }
            }
        },
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .haze(
                    state = hazeState,
                    backgroundColor = MaterialTheme.colorScheme.surface,
                ),
        ) {
            items(50){item ->
                Card(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 3.dp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 5.dp)
                    //.size(width = 350.dp, height = 90.dp)
                    ,shape = MaterialTheme.shapes.medium

                ) {
                    ListItem(
                        headlineContent = {  Text(text = "高斯模糊测试列表") },
                        supportingContent = { Text(text = "项目 ${item + 1}")},
                        leadingContent = {
                                Icon(painterResource(R.drawable.deblur), contentDescription = "Localized description",)
                        },
                        colors = if(item % 2 == 0)
                            ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        else ListItemDefaults.colors(),
                        modifier = Modifier.clickable {}
                    )
                }
            }
        }
    }
}

val LorumIspum by lazy {
    """
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras sit amet congue mauris, iaculis accumsan eros. Aliquam pulvinar est ac elit vulputate egestas. Vestibulum consequat libero at sem varius, vitae semper urna rhoncus. Aliquam mollis, ipsum a convallis scelerisque, sem dui consequat leo, in tempor risus est ac mi. Nam vel tellus dolor. Nunc lobortis bibendum fermentum. Mauris sed mollis justo, eu tristique elit. Cras semper augue a tortor tempor, vitae vestibulum eros convallis. Curabitur id justo eget tortor iaculis lobortis. Integer pharetra augue ac elit porta iaculis non vitae libero. Nam eros turpis, suscipit at iaculis vitae, malesuada vel arcu. Donec tincidunt porttitor iaculis. Pellentesque non augue magna. Mauris mattis purus vitae mi maximus, id molestie ipsum facilisis. Donec bibendum gravida dolor nec suscipit. Pellentesque tempus felis iaculis, porta diam sed, tristique tortor.

Sed vel tellus vel augue pulvinar semper sit amet eu est. In porta arcu eu sapien luctus scelerisque. In hac habitasse platea dictumst. Aenean varius lobortis malesuada. Sed vitae ornare arcu. Nunc maximus lectus purus, vel aliquet velit facilisis a. Nulla maximus bibendum magna id vulputate. Mauris volutpat lorem et risus porta dignissim. In at elit a est vulputate tincidunt.

Nulla facilisi. Curabitur gravida quam nec massa tempus, sed placerat nunc hendrerit. Duis sit amet cursus ipsum. Phasellus eget congue lacus. Duis vehicula venenatis posuere. Morbi non tempor risus. Aenean bibendum efficitur tortor, eu interdum velit gravida rutrum. Sed tempus elementum libero. Suspendisse dapibus lorem vitae justo congue pellentesque. Phasellus et tellus sagittis, blandit nibh a, porta felis. Proin ornare eget odio eget laoreet. Cras id augue fringilla, molestie ligula sit amet, sollicitudin neque.

Suspendisse vitae bibendum justo, nec egestas mauris. Mauris id metus mi. Morbi ut maximus ex, eu consequat elit. Sed malesuada pellentesque mauris vel molestie. Nulla facilisi. Cras pellentesque metus id nibh sodales gravida. Vivamus a feugiat felis. Vivamus et justo libero. Maecenas ac augue viverra, blandit diam sed, porttitor sapien. Proin eu eros mollis, commodo lectus nec, imperdiet nisi. Proin nulla nulla, vehicula a faucibus sit amet, auctor sed lorem. Mauris ut ipsum sit amet massa posuere maximus eget porttitor nisl. Quisque nunc dolor, pharetra id nunc sit amet, maximus convallis nunc.

Ut magna diam, ullamcorper vel imperdiet at, dignissim sit amet turpis. Duis ut enim eu sapien fringilla placerat. Integer at dui eget leo tincidunt iaculis. Fusce nec elementum turpis. Aenean gravida, ipsum sit amet varius hendrerit, elit nisi hendrerit ex, et porta enim lorem eget mi. Duis convallis dolor a lacinia aliquam. Aliquam erat volutpat.
""".trim()
}


