package com.hfut.schedule.ui.activity.home.cube.items.subitems.update

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.APPVersion
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.ui.utils.components.MyCard

@Composable
fun UpdateUI() {

    MyCard {
        UpdateItem()
    }
}

@Composable
fun UpdateItem() {
    var version by remember { mutableStateOf(getUpdates()) }

    var expandItems by remember { mutableStateOf(false) }
    ListItem(
        headlineContent = { Text(text = "发现新版本") },
        supportingContent = { Text(text = "${APPVersion.getVersionName()} → ${version.version}") },
        leadingContent = { Icon(painterResource(R.drawable.arrow_upward), contentDescription = "Localized description",) },
        trailingContent = {
            FilledTonalIconButton(
                onClick = { expandItems = !expandItems },
                colors = IconButtonDefaults.filledTonalIconButtonColors(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
            ) { Icon(painterResource(id = if(!expandItems) R.drawable.expand_content else R.drawable.collapse_content), contentDescription = "")
            }
        },
        modifier = Modifier.clickable{ Starter.startWebUrl(MyApplication.UpdateURL+ "/releases/tag/Android") },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.errorContainer)
    )

    AnimatedVisibility(
        visible = expandItems,
        enter = slideInVertically(
            initialOffsetY = { -40 }
        ) + expandVertically(
            expandFrom = Alignment.Top
        ) + scaleIn(
            transformOrigin = TransformOrigin(0.5f, 0f)
        ) + fadeIn(initialAlpha = 0.3f),
        exit = slideOutVertically() + shrinkVertically() + fadeOut() + scaleOut(targetScale = 1.2f)) {
        ListItem(
            headlineContent = { Text(text ="更新日志") },
            supportingContent = {
                getUpdates().text?.let { Text(text = " $it") }
            },
            leadingContent = { Icon(painter = painterResource(id = R.drawable.hotel_class), contentDescription = "") },
            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.errorContainer)
        )
    }
}