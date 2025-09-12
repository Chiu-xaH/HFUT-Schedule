package com.hfut.schedule.logic.model

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R

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