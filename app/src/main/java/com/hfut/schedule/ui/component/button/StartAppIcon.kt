package com.hfut.schedule.ui.component.button

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.sys.Starter


@Composable
fun StartAppIcon(
    app : Starter.AppPackages,
    icon : Int,
    modifier: Modifier = Modifier
) {
    FilledTonalIconButton (
        onClick = { Starter.startAppLaunch(app) },
        modifier = modifier
    ) {
        Image(
            painterResource(icon),
            null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
        )
    }
}
