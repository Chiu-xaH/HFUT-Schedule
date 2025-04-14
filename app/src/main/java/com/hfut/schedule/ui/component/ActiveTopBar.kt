package com.hfut.schedule.ui.component

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.SharePrefs.prefs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveTopBar(title : String) {
    TopAppBar(
        title = { ScrollText(text = title) },
        colors = topAppBarColors(
        containerColor = Color.Transparent,
        titleContentColor = MaterialTheme.colorScheme.primary
        )
    )
}
// BottomSheet专用
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetTopBar(
    title: String,
    singleLine : Boolean = false,
    textModifier : Modifier = Modifier,
    rightContent : (@Composable () -> Unit)? = null
) {
    Row (
        modifier = Modifier.fillMaxWidth().padding(appHorizontalDp()+1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            color = MaterialTheme.colorScheme.primary,
            modifier = textModifier.weight(1f),
            fontSize = 22.sp,
            maxLines = if(singleLine) 1 else Int.MAX_VALUE
        )
        rightContent?.let {
            Box(modifier = Modifier.wrapContentSize()) {
                it()
            }
        }
    }
}

@Composable
fun HazeBottomSheetTopBar(
    title: String,
    singleLine : Boolean = false,
    textModifier : Modifier = Modifier,
    isPaddingStatusBar : Boolean = true,
    rightContent : (@Composable () -> Unit)? = null
) {
    Column {
        Spacer(Modifier.height(appHorizontalDp() * if(isPaddingStatusBar) 1.5f else 0f))
        BottomSheetTopBar(
            title,
            singleLine,
            textModifier,
            rightContent
        )
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTopBar(
    title: String,
    key: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    with(sharedTransitionScope) {
        Text(
            title,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.sharedBounds(
                rememberSharedContentState(key = key),
                animatedVisibilityScope = animatedContentScope
            ).padding(appHorizontalDp()+1.dp),
            fontSize = 22.sp,
        )
    }
}