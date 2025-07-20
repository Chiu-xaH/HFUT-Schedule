package com.hfut.schedule.ui.component.status

//import android.view.ContextThemeWrapper
//import android.view.LayoutInflater
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
//import androidx.compose.material3.CircularProgressIndicator
//import com.hfut.schedule.ui.utils.components.LoadingUI
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.LoadingIndicatorDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
//import androidx.compose.ui.viewinterop.AndroidView
//import com.google.android.material.loadingindicator.LoadingIndicator
//import com.hfut.schedule.R
import com.hfut.schedule.ui.style.RowHorizontal
import kotlinx.coroutines.delay
//
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoadingUI(
    text : String? = null,
    sizeX : Int = 2
) {

    val originalList = LoadingIndicatorDefaults.IndeterminateIndicatorPolygons
    val newList = listOf(
        originalList[0],
        originalList[1],
        originalList[4],
        originalList[2],//五边形
        LoadingIndicatorDefaults.DeterminateIndicatorPolygons[0] //圆形
    )

    val time = 650
    var isLarge by remember { mutableStateOf(true) }
    val scale by animateFloatAsState(
        targetValue = if (isLarge) 1f else 0.7f,
        animationSpec = tween(durationMillis = time), label = ""
    )

    LaunchedEffect(Unit) {
        while (true) {
            isLarge = !isLarge
            delay(time.toLong()) // 动画交替间隔时间
        }
    }


    Column  {
        RowHorizontal {
            LoadingIndicator(
                modifier = Modifier
                    .size(LoadingIndicatorDefaults.IndicatorSize * sizeX)
                    .scale(scale),
                polygons = newList,
            )
        }
        if(text != null) {
            Spacer(modifier = Modifier.height(2.dp))
            RowHorizontal {
                Text(text, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun LoadingScreen(
    text : String? = null,
    sizeX : Int = 2
) {
    CenterScreen {
        LoadingUI(text, sizeX)
    }
}
