package com.hfut.schedule.ui.component.button

import android.graphics.drawable.shapes.Shape
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.screen.AppNavRoute
import com.kyant.backdrop.Backdrop
import com.xah.transition.component.containerShare
import com.xah.transition.state.LocalSharedTransitionScope
import com.xah.uicommon.style.APP_HORIZONTAL_DP


@Composable
fun StartAppIconButton(
    backdrop: Backdrop,
    app : Starter.AppPackages,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    LiquidButton  (
        isCircle = true,
        backdrop = backdrop,
        onClick = { Starter.startAppLaunch(app,context) },
        modifier = modifier,
        innerPadding = 6.5.dp
    ) {
        Image(
            painterResource(app.icon),
            null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
fun StartAppIcon(
    app : Starter.AppPackages,
    size : Dp = 33.dp,
    shadow : Dp = 10.dp,
    shape :  RoundedCornerShape = RoundedCornerShape(7.5.dp)
) {
    Image(
        painterResource(app.icon),
        null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .shadow(shadow, shape = shape)
            .size(size)
            .clip(shape)
    )
}



@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun StartAppIconShare(
    app : Starter.AppPackages,
    size : Dp = 33.dp,
    shadow : Dp = 10.dp,
    shape :  RoundedCornerShape = RoundedCornerShape(7.5.dp)
) {
    val animating = LocalSharedTransitionScope.current.isTransitionActive
    val e by animateDpAsState(
        if(animating) 0.dp else shadow
    )
    Box(
        modifier = Modifier
            .containerShare(AppNavRoute.OpenOuterApplication.withArgs(app),shape)

    ) {
        Image(
            painterResource(app.icon),
            null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .shadow(e, shape = shape)
                .size(size)
                .clip(shape)
        )
    }

}
