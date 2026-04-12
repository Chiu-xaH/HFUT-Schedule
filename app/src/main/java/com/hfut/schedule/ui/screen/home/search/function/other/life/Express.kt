package com.hfut.schedule.ui.screen.home.search.function.other.life

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.xah.common.ui.component.text.ScrollText
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import com.hfut.schedule.ui.nav.window.ExpressWindow
import com.xah.floating.util.LocalFloatingController

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Express() {
    val floatController = LocalFloatingController.current
    TransplantListItem(
        headlineContent = { ScrollText(text = stringResource(R.string.navigation_label_express)) },
        leadingContent = {
            Icon(painterResource(R.drawable.package_2), contentDescription = null)

        },
        modifier = Modifier.clickable {
            floatController.push(ExpressWindow)
        }
    )
}