package com.hfut.schedule.ui.screen.home.search.function.school.news

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.destination.NewsDestination
import com.hfut.schedule.ui.screen.AppNavRoute
import com.xah.navigation.utils.LocalNavController
import com.xah.common.component.text.ScrollText

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun News() {
    val navController = LocalNavController.current

    TransplantListItem(
        headlineContent = { ScrollText(text = stringResource(AppNavRoute.News.label)) },
        leadingContent = {
            Icon(painterResource(AppNavRoute.News.icon), contentDescription = null)
        },
        modifier = Modifier.clickable {
            navController.push(NewsDestination)
        }
    )
}




