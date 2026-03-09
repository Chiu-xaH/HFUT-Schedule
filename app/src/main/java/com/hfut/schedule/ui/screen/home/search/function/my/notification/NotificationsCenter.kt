package com.hfut.schedule.ui.screen.home.search.function.my.notification

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.destination.NotificationsDestination
import com.hfut.schedule.ui.screen.AppNavRoute
import com.xah.navigation.utils.LocalNavController
import com.xah.uicommon.component.text.ScrollText


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun NotificationsCenter() {
    val navController = LocalNavController.current

    val count by produceState(initialValue = 0) {
        value = calculatedReadNotificationCount()
    }

    TransplantListItem(
        headlineContent = { ScrollText(text = stringResource(AppNavRoute.Notifications.label)) },
        modifier = Modifier.clickable {
            navController.push(NotificationsDestination)
        },
        leadingContent = {
            BadgedBox(badge = {
                if (count != 0) {
                    Badge {
                        Text(text = count.toString())
                    }
                }
            }) {
                Icon(painterResource(AppNavRoute.Notifications.icon), contentDescription = null)

            }
        }
    )
}
