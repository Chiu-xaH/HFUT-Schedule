package com.hfut.schedule.ui.screen.home.search.function.my.supabase

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.JumpTransitionEffectWallpaper
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.nav.destination.SupabaseDestination
import com.hfut.schedule.ui.nav.destination.SupabaseLoginDestination
import com.xah.common.ui.component.text.ScrollText
import com.xah.navigation.util.LocalNavController

@Composable
fun Supabase() {
//    val jwt by DataStoreManager.supabaseJwt.collectAsState(initial = "")
//    val refreshToken by DataStoreManager.supabaseRefreshToken.collectAsState(initial = "")
//    val scope = rememberCoroutineScope()
//    var loading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val navController = LocalNavController.current

    TransplantListItem(
        headlineContent = { ScrollText(text = stringResource(R.string.navigation_label_supabase)) },
        leadingContent = {
//            if(loading) {
//                LoadingIcon()
//            } else {
                Icon(painterResource(id = R.drawable.cloud), contentDescription = "")
//            }
        },
        trailingContent = {
            FilledTonalIconButton(
                modifier = Modifier.size(30.dp),
                onClick = {
                    navController.push(SupabaseLoginDestination, effect = JumpTransitionEffectWallpaper())
                }
            ) {
                Icon(painterResource(R.drawable.refresh),null)
            }
        },
        modifier = Modifier.clickable {
            navController.push(SupabaseDestination)
//           scope.launch {
//               loading = true
//               loginSupabaseWithCheck(jwt,refreshToken,vm,context)
//               loading = false
//           }
        }
    )
}
