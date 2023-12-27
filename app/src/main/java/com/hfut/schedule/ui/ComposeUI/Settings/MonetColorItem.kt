package com.hfut.schedule.ui.ComposeUI.Settings

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.MonetColor.MonetUI

@SuppressLint("SuspiciousIndentation")
@Composable
fun MonetColorItem() {
    var expandItems by remember { mutableStateOf(prefs.getBoolean("expandMonet",false)) }
    var text by remember { mutableStateOf("") }
    text = when(expandItems){
        true -> "收回"
        false -> "展开"
    }
    ListItem(
        headlineContent = { Text(text = "莫奈取色") },
        supportingContent = { Text(text = "选择主色调,点击${text}") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.color),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
            expandItems = !expandItems
            SharePrefs.SaveBoolean("expandMonet",false,expandItems)
        }
    )

    AnimatedVisibility(
        visible = expandItems,
        enter = slideInVertically(
            initialOffsetY = { -40 }
        ) + expandVertically(
            expandFrom = Alignment.Top
        ) + scaleIn(
            // Animate scale from 0f to 1f using the top center as the pivot point.
            transformOrigin = TransformOrigin(0.5f, 0f)
        ) + fadeIn(initialAlpha = 0.3f),
        exit = slideOutVertically() + shrinkVertically() + fadeOut() + scaleOut(targetScale = 1.2f)
    ) {
        Column(Modifier.fillMaxWidth().requiredHeight(240.dp)) {
            MonetUI()
        }

    }

    Spacer(modifier = Modifier.height(5.dp))

}