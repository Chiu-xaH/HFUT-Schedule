package com.hfut.schedule.ui.UIUtils

import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeChild

@Composable
fun Modifier.bottomBarBlur(hazeState : HazeState, blur : Boolean) : Modifier {
    return this.hazeChild(state = hazeState,
        style = HazeStyle(
            tint = HazeTint(color =
            if(blur) MaterialTheme.colorScheme.surface else ListItemDefaults.containerColor,),
            backgroundColor =  Color.Transparent
            ,blurRadius = MyApplication.Blur,
            noiseFactor = 0f)
    ) {
        progressive = HazeProgressive.verticalGradient(startIntensity = 0f, endIntensity = .7f, startY = 85f, endY = Float.POSITIVE_INFINITY)
    }
}


@Composable
fun Modifier.topBarBlur(hazeState : HazeState, blur : Boolean) : Modifier {
    return this.hazeChild(state = hazeState,
        style = HazeStyle(
            tint = HazeTint(color =
            if(blur) MaterialTheme.colorScheme.surface else ListItemDefaults.containerColor,),
            backgroundColor =  Color.Transparent
            ,blurRadius = MyApplication.Blur,
            noiseFactor = 0f)
    ) {
        progressive = HazeProgressive.verticalGradient(startIntensity = .7f, endIntensity = 0f)
    }
}
