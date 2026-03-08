package com.hfut.schedule.ui.component.button

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.sys.Starter
import com.xah.mirror.util.ShaderState


@Composable
fun StartAppIconButton(
    backdrop: ShaderState,
    app : Starter.AppPackages,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    LiquidButton  (
        isCircle = true,
        backdrop = backdrop,
        onClick = { Starter.startAppLaunch(app,context) },
        modifier = modifier,
//        innerPadding = 6.5.dp
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