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
   // DialogSample()
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
            .blur(30.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
    ) {
        Button(onClick = { /*TODO*/ }) {
            Text(text = "lll")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogSample() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Haze Dialog sample") },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        @Suppress("DEPRECATION")
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
        },
    ) { innerPadding ->
        val hazeState = remember { HazeState() }
        var showDialog by remember { mutableStateOf(false) }

        if (showDialog) {
            Dialog(onDismissRequest = { showDialog = false }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(fraction = .5f)
                        .hazeChild(
                            state = hazeState,
                            shape = MaterialTheme.shapes.extraLarge,
                            blurRadius = 35.dp, tint = Color.Transparent, noiseFactor = 0f
                        ),
                    shape = MaterialTheme.shapes.extraLarge,
                    // We can't use Haze tint with dialogs, as the tint will display a scrim over the
                    // background content. Instead we need to set a translucent background on the
                    // dialog content.
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ) {
                    // empty
                }
            }
        }

        LazyVerticalGrid(
            modifier = Modifier.haze(hazeState),
            columns = GridCells.Fixed(4),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(40) {
                Card(
                    modifier = Modifier.height(100.dp),
                    onClick = { showDialog = true },
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Text(text = "Card $it")
                    }
                }
            }
        }
    }
}