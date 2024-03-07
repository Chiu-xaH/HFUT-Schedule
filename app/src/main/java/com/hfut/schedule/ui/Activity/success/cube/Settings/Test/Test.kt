package com.hfut.schedule.ui.Activity.success.cube.Settings.Test

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.DismissDirection
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.pullrefresh.PullRefreshDefaults
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.platform.inspectable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ui.Activity.success.focus.Focus.AddedItems
import com.hfut.schedule.ui.Activity.success.focus.Focus.RemoveItems
import com.hfut.schedule.ui.UIUtils.MyToast
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.lang.Math.abs
import kotlin.math.pow

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
    var isExpanded by remember { mutableStateOf(false) }
    val transition = updateTransition(targetState = isExpanded, label = "Stack Expand")
    val blurRadius by transition.animateDp(label = "Title blur") { expanded ->
        if (expanded) 8.dp else 0.dp
    }

    Button(onClick = { /*TODO*/ }, modifier = Modifier.blur(blurRadius, BlurredEdgeTreatment.Unbounded)) {
        androidx.compose.material.Text(
            text = "Certificates",
            style = androidx.compose.material.MaterialTheme.typography.h2,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 24.dp)
            // .align(Alignment.TopStart)

        )
    }
    
    
    Button(onClick = { isExpanded = !isExpanded }) {
        Text(text = "模糊")
    }
}
