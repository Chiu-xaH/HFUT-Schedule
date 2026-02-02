package com.hfut.schedule.ui.screen.home.search.function.school.sport.lepao

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter

import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.screen.AppNavRoute
import com.xah.transition.component.iconElementShare
import com.xah.uicommon.component.text.ScrollText
import kotlinx.coroutines.launch


@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LePaoYun(
    navController : NavHostController,
) {
    val context = LocalContext.current
    val icon = remember { R.drawable.sports_volleyball }
    val title = stringResource(R.string.navigation_label_physical_fitness_test)
    val route = AppNavRoute.WebView.shareRoute(MyApplication.PE_HOME_URL)
    val scope = rememberCoroutineScope()
    TransplantListItem(
        headlineContent = { ScrollText(text = title) },
        leadingContent = {
            Icon(painterResource(icon), contentDescription = null,modifier = Modifier.iconElementShare( route = route))
        },
        trailingContent = {
            FilledTonalIconButton(
                modifier = Modifier.size(30.dp),
                onClick = {
                    Starter.startAppLaunch(Starter.AppPackages.LE_PAO, context)
                },
            ) { Icon( painterResource(R.drawable.directions_run), contentDescription = null, modifier = Modifier.size(20.dp)) }
        },
        modifier = Modifier.clickable {
            scope.launch {
                Starter.startWebView(
                    navController,
                    url = MyApplication.PE_HOME_URL,
                    title = title,
                    icon = icon,
                    cookie = prefs.getString("PE","")
                )
            }
        }
    )
}
