package com.hfut.schedule.ui.Activity.success.cube.Settings


import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ui.Activity.card.counts.RadarChart
import com.hfut.schedule.ui.Activity.card.counts.RadarData
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TEST(innerPaddings : PaddingValues) {
    Column (modifier = Modifier.padding(innerPaddings)){
        Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding()))
        Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding()))
        Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding()))
        Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding()))
        Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding()))


        var showBottomSheet by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = false,
        )

        LaunchedEffect(sheetState.currentValue) {
            when (sheetState.currentValue) {
                SheetValue.Expanded -> {
                    // ModalBottomSheet 全屏展开时的处理逻辑
                    println("Expanded")
                }
                SheetValue.PartiallyExpanded -> {
                    // ModalBottomSheet 半屏展开时的处理逻辑
                    println("Partially Expanded")
                }
                SheetValue.Hidden -> {
                    // ModalBottomSheet 隐藏时的处理逻辑
                    println("Hidden")
                }
            }
        }
        val dpAnimation by animateDpAsState(
            targetValue = if (sheetState.currentValue != SheetValue.Expanded) 28.dp else 0.dp, label = ""
            ,animationSpec = tween(MyApplication.Animation / 2, easing = LinearOutSlowInEasing),
        )
        //Log.d("ss",BottomSheetDefaults.ExpandedShape.toString())
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                onClick = { showBottomSheet = true }
            ) {
                Text("Display partial bottom sheet")
            }

            if (showBottomSheet) {
                ModalBottomSheet(
                    modifier = Modifier.fillMaxHeight(),
                    sheetState = sheetState,
                    onDismissRequest = { showBottomSheet = false },
                    shape =  RoundedCornerShape(dpAnimation)
                ) {
                    Text(
                        "Swipe up to open sheet. Swipe down to dismiss.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
            //AnimatedVectorDrawable()
            //TransformableSample()
           Button(onClick = { showBottomSheet = true }) {
               Text(text = "展开")
           }
        }

     // //  AnimatedVectorDrawable()
      //  AnimatedVectorDrawable()
       // AnimatedVectorDrawable()

      //  Gesture()
    }
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun Gesture() {
    val offset = remember { Animatable(Offset(0f, 0f), Offset.VectorConverter) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                coroutineScope {
                    while (true) {
                        // Detect a tap event and obtain its position.
                        awaitPointerEventScope {
                            val position = awaitFirstDown().position

                            launch {
                                // Animate to the tap position.
                                offset.animateTo(position)
                            }
                        }
                    }
                }
            }
    ) {
        val image = AnimatedImageVector.animatedVectorResource(R.drawable.ic_hourglass_animated)
        var atEnd by remember { mutableStateOf(false) }
        Image(
            painter = rememberAnimatedVectorPainter(image, atEnd),
            contentDescription = "Timer",
            modifier = Modifier
                .clickable {
                    atEnd = !atEnd
                }
                .offset { offset.value.toIntOffset() },
            contentScale = ContentScale.Crop
        )
       // Icon(modifier = Modifier },painter = painterResource(id = R.drawable.deployed_code), contentDescription = "")
    }
}

@Composable
private fun AnimationSpecTween(enabled: Boolean) {
    // [START android_compose_animations_spec_tween]
    val alpha: Float by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.5f,
        // Configure the animation duration and easing.
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing), label = ""
    )
    // [END android_compose_animations_spec_tween]
}

@Composable
fun AnimatedVectorDrawable() {

    var big by remember { mutableStateOf(false) }

    val size by animateDpAsState(
        targetValue = if (big) 300.dp else 50.dp,
        animationSpec = spring(Spring.DampingRatioHighBouncy, Spring.StiffnessLow, 0.1.dp) ,
        label = ""
    )
    val size2 by animateDpAsState(
        targetValue = if (big) 225.dp else 37.dp,
        animationSpec = spring(Spring.DampingRatioHighBouncy, Spring.StiffnessLow, 0.1.dp),
        label = ""
    )
    FilledTonalIconButton(
        modifier = Modifier
            .size(size)
           // .background(Color.Blue)
            , onClick = {big = !big}
    ) {
        Icon(painter = painterResource(id = R.drawable.animation), contentDescription = "", modifier = Modifier.size(size2))
    }

}
private fun Offset.toIntOffset() = IntOffset(x.roundToInt(), y.roundToInt())
@Composable
fun TransformableSample() {
    // set up all transformation states
    var scale by remember { mutableStateOf(1f) }
    val rotation by remember { mutableStateOf(0f) }
    val offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, _, _ ->
        scale *= zoomChange
    }
    Box(
        Modifier
            // apply other transformations like rotation and zoom
            // on the pizza slice emoji
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                rotationZ = rotation,
                translationX = offset.x,
                translationY = offset.y
            )
            // add transformable to listen to multitouch transformation events
            // after offset
            .transformable(state = state)
            .background(Color.Blue)
            .fillMaxSize()
            .size(50.dp)
    )
}



@Composable
@Preview
fun RadarChartPreview() {
    val data = listOf(
        RadarData("Speed", 0.8f),
        RadarData("Accuracy", 0.6f),
        RadarData("Power", 0.7f),
        RadarData("Endurance", 0.9f),
        RadarData("Agility", 0.5f)
    )
    RadarChart(data = data, modifier = Modifier.size(300.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartialBottomSheet() {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = { showBottomSheet = true }
        ) {
            Text("Display partial bottom sheet")
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                modifier = Modifier.fillMaxHeight(),
                sheetState = sheetState,
                onDismissRequest = { showBottomSheet = false }
            ) {
                Text(
                    "Swipe up to open sheet. Swipe down to dismiss.",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
