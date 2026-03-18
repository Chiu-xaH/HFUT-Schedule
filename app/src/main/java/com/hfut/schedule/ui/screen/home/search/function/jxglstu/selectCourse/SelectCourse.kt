package com.hfut.schedule.ui.screen.home.search.function.jxglstu.selectCourse

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.destination.SelectCoursesDestination
import com.hfut.schedule.ui.screen.AppNavRoute
import com.xah.navigation.util.LocalNavController

import com.xah.common.ui.component.text.ScrollText

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SelectCourse(
    ifSaved : Boolean,
) {
    val navController = LocalNavController.current
    val context = LocalContext.current

    TransplantListItem(
        headlineContent = { ScrollText(text = stringResource(AppNavRoute.SelectCourses.label)) },
        leadingContent = {
            Icon(painterResource(AppNavRoute.SelectCourses.icon), contentDescription = null)
        },
        modifier = Modifier.clickable {
            if(!ifSaved) navController.push(SelectCoursesDestination)
            else refreshLogin(context)
        }
    )
}

