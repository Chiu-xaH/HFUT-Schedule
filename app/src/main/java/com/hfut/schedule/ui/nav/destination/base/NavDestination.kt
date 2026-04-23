package com.hfut.schedule.ui.nav.destination.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hfut.schedule.R
import com.hfut.schedule.ui.component.status.StatusIcon
import com.xah.common.ui.model.text.UiText
import com.xah.navigation.model.dest.Destination

abstract class NavDestination : Destination() {
    abstract val title : UiText
    open val description : String? = null
    open val icon : Int = R.drawable.texture
    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    override val PlaceHolder = @Composable {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Box(
                modifier = Modifier.align(Alignment.Center)
            ) {
                StatusIcon(icon, title, textColor = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}