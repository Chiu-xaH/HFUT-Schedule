package com.hfut.schedule.ui.Activity.success.cube.Settings


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.Tracing.enabled
import com.hfut.schedule.R
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun TEST(innerPaddings : PaddingValues) {
    Column (modifier = Modifier.padding(innerPaddings)){
        Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding()))
        Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding()))
        Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding()))
        Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding()))
        Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding()))

        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
            AnimatedVectorDrawable()
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

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun AnimatedVectorDrawable() {

    var big by remember { mutableStateOf(false) }

    val size by animateDpAsState(
        targetValue = if (big) 300.dp else 50.dp,
        animationSpec = spring(Spring.DampingRatioHighBouncy, Spring.StiffnessLow, 0.1.dp,) ,
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