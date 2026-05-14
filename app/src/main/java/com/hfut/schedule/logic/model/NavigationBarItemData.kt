package com.hfut.schedule.logic.model

import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.key

data class NavigationBarItemData(
    val route: String,
    val label: String,
    val icon: Int,
    val filledIcon: Int
) {
    fun toDynamic() : NavigationBarItemDataDynamic {
        return NavigationBarItemDataDynamic(
            route = route,
            label = label,
            icon = { selected -> NavigationBarItemDynamicIcon(selected,icon,filledIcon) },
            badge = null
        )
    }
}

data class NavigationBarItemDataDynamic(
    val route: String,
    val label: String,
    val icon: @Composable (Boolean) -> Unit, // 动态图标，传入 selected
    val badge: (@Composable BoxScope.() -> Unit)? = null // 可选 badge
)

@Composable
fun NavigationBarItemDynamicIcon(selected : Boolean,icon : Int,filledIcon: Int) {
    Icon(
        painterResource(
            if (selected) filledIcon
            else icon
        ),
        contentDescription = null
    )
}

@Composable
fun NavigationBarItemDynamicIconModern(
    selected: Boolean,
    avdResource: Int,
) {
    key(avdResource) {
        // 资源变了就重组，以免外部手动更新后依旧使用缓存
        val image = AnimatedImageVector.animatedVectorResource(avdResource)
        val painter = rememberAnimatedVectorPainter(
            animatedImageVector = image,
            atEnd = selected
        )
        Icon(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}
