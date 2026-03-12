package com.hfut.schedule.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.status.StatusIcon
import com.xah.navigation.model.dest.Destination
import com.xah.uicommon.style.align.ColumnVertical
import com.xah.uicommon.util.language.UiText

abstract class NavDestination : Destination() {
    abstract val title : UiText
    open val description : String? = null
    open val icon : Int = R.drawable.stacks
    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    override val PlaceHolder = @Composable {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
            Box(
                modifier = Modifier.align(Alignment.Center)
            ) {
                StatusIcon(icon,title, textColor = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}