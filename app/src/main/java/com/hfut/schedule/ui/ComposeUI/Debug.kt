package com.hfut.schedule.ui.ComposeUI

import android.annotation.SuppressLint
import android.util.Half.toFloat
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.logic.datamodel.zjgd.BillMonth

data class Item(var item: String, var isNew: Boolean)
//在这里放测试布局
@SuppressLint("UnrememberedMutableState")
@Composable
fun Tests() {
    Column {
        var showRed by remember { mutableStateOf(true) }
        var showGreen by remember { mutableStateOf(true) }

        Button(onClick = { showGreen = !showGreen}) { Text(text = "颜色") }

        AnimatedVisibility(
            visible = showGreen,
            enter = scaleIn() + expandVertically(expandFrom = Alignment.CenterVertically),
            exit = scaleOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically)
        ) {
            Box(
                Modifier
                    .size(100.dp)
                    .background(color = Color.Green, shape = RoundedCornerShape(20.dp))
            )
        }

        AnimatedVisibility(
            visible = showRed,
            // Scale up from the TopLeft by setting TransformOrigin to (0f, 0f), while expanding the
            // layout size from Top start and fading. This will create a coherent look as if the
            // scale is impacting the size.
            enter = scaleIn(transformOrigin = TransformOrigin(0f, 0f)) +
                    fadeIn() + expandIn(expandFrom = Alignment.TopStart),
            // Scale down from the TopLeft by setting TransformOrigin to (0f, 0f), while shrinking
            // the layout towards Top start and fading. This will create a coherent look as if the
            // scale is impacting the layout size.
            exit = scaleOut(transformOrigin = TransformOrigin(0f, 0f)) +
                    fadeOut() + shrinkOut(shrinkTowards = Alignment.TopStart)
        ) {
            Box(
                Modifier
                    .size(100.dp)
                    .background(color = Color.Red, shape = RoundedCornerShape(20.dp))
            )
        }
    }
}


