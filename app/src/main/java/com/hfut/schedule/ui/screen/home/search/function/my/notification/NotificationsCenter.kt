package com.hfut.schedule.ui.screen.home.search.function.my.notification

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.saveString
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.util.navigateForTransition
import com.xah.transition.component.iconElementShare
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalSharedTransitionScope
import com.xah.transition.util.navigateAndSaveForTransition
import com.xah.uicommon.component.text.ScrollText
import dev.chrisbanes.haze.HazeState


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun NotificationsCenter(
    navController : NavHostController,
) {
    val route = remember { AppNavRoute.Notifications.route }

    TransplantListItem(
        headlineContent = { ScrollText(text = AppNavRoute.Notifications.label) },
        modifier = Modifier.clickable {
            navController.navigateForTransition(AppNavRoute.Notifications,route)
            saveString("Notifications", getNotifications().size.toString())
        },
        leadingContent = {
            BadgedBox(badge = {
                if (prefs.getString("Notifications","0") != getNotifications().size.toString())
                Badge { Text(text = getNotifications().size.toString())}
            }) {
                Icon(painterResource(AppNavRoute.Notifications.icon), contentDescription = null,modifier = Modifier.iconElementShare( route = route))

            }
        }
    )
}
