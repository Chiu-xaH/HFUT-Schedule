package com.hfut.schedule.ui.screen.home.search.function.jxglstu.person

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.destination.PersonInfoDestination
import com.hfut.schedule.ui.screen.AppNavRoute
import com.xah.navigation.utils.LocalNavController
import com.xah.uicommon.component.text.ScrollText

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun PersonUI() {
    val navController = LocalNavController.current

    TransplantListItem(
        headlineContent = { ScrollText(text =stringResource(AppNavRoute.PersonInfo.label)) },
        leadingContent = {
            Icon(painterResource(AppNavRoute.PersonInfo.icon), contentDescription = null)

        },
        modifier = Modifier.clickable {
            navController.push(PersonInfoDestination)
        }
    )
}

