package com.hfut.schedule.ui.screen.home.cube.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.saveBoolean
import com.hfut.schedule.logic.util.sys.showDevelopingToast
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.media.SimpleVideo
import com.hfut.schedule.ui.component.media.checkOrDownloadVideo
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.screen.home.cube.Screen
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.screen.home.cube.sub.ArrangeItem
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.xah.transition.util.TransitionBackHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/* 本kt文件已完成多语言文案适配 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkSettingsScreen(
    navController: NavHostController,
    innerPaddings : PaddingValues,
    ifSaved : Boolean,
) {
    val enablePredictive by DataStoreManager.enablePredictive.collectAsState(initial = AppVersion.CAN_PREDICTIVE)
    var scale by remember { mutableFloatStateOf(1f) }
    TransitionBackHandler(navController,enablePredictive) {
        scale = it
    }
    val context = LocalContext.current

    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .fillMaxSize()
        .padding(innerPaddings)
        .scale(scale))
    {
        Spacer(modifier = Modifier.height(CARD_NORMAL_DP*2))
        val switch_upload = SharedPrefs.prefs.getBoolean("SWITCHUPLOAD",true )
        var upload by remember { mutableStateOf(switch_upload) }
        saveBoolean("SWITCHUPLOAD",true,upload)


        val switch_server = SharedPrefs.prefs.getBoolean("SWITCHSERVER",false )
        var server by remember { mutableStateOf(switch_server) }
        saveBoolean("SWITCHSERVER",false,server)
        val scope = rememberCoroutineScope()
        val video by produceState<String?>(initialValue = null) {
            scope.launch {
                delay(AppAnimationManager.ANIMATION_SPEED*1L)
                value = checkOrDownloadVideo(context,"example_network.mp4","https://chiu-xah.github.io/videos/example_network.mp4")
            }
        }
        CustomCard (
            modifier = Modifier
                .aspectRatio(16 / 9f)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
        ) {
            video?.let {
                SimpleVideo(
                    filePath = it,
                    aspectRatio = 16/9f,
                )
            }
        }


        DividerTextExpandedWith(stringResource(R.string.network_settings_config_half_title)) {
            CustomCard(color = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { Text(text = stringResource(R.string.network_settings_init_data_title)) },
                    supportingContent = { Text(text = stringResource(R.string.network_settings_init_data_description)) },
                    leadingContent = { Icon(painterResource(R.drawable.reset_iso), contentDescription = "Localized description",) },
                    modifier = Modifier.clickable { navController.navigate(Screen.FocusCardScreen.route) }
                )
                PaddingHorizontalDivider()
                ArrangeItem()
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = stringResource(R.string.network_settings_school_card_password_title)) },
                    supportingContent = { Text(text = stringResource(R.string.network_settings_school_card_password_description)) },
                    leadingContent = { Icon(painterResource(R.drawable.credit_card), contentDescription = "Localized description",) },
                    modifier = Modifier.clickable { navController.navigate(Screen.HuiXinPasswordScreen.route) }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = stringResource(R.string.network_settings_jxglstu_password_title)) },
                    supportingContent = { Text(text = stringResource(R.string.network_settings_jxglstu_password_description)) },
                    leadingContent = { Icon(painterResource(R.drawable.lock), contentDescription = "Localized description",) },
                    modifier = Modifier.clickable { navController.navigate(Screen.JxglstuPasswordScreen.route) }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = stringResource(R.string.network_settings_auto_refresh_login_title)) },
                    supportingContent = { Text(text = stringResource(R.string.network_settings_auto_refresh_login_description)) },
                    leadingContent = { Icon(painterResource(R.drawable.rotate_auto), contentDescription = "Localized description",) },
                    trailingContent = { Switch(checked = false, enabled = false, onCheckedChange = { showDevelopingToast() })},
                    modifier = Modifier.clickable { showDevelopingToast() }
                )
            }
        }

        DividerTextExpandedWith(stringResource(R.string.network_settings_others_half_title)) {
            CustomCard(color = MaterialTheme.colorScheme.surface){
                TransplantListItem(
                    headlineContent = { Text(text = stringResource(R.string.network_settings_update_data_title)) },
                    supportingContent = { Text(text = stringResource(R.string.network_settings_update_data_description)) },
                    leadingContent = { Icon(painterResource(R.drawable.cloud_upload), contentDescription = "Localized description",) },
                    trailingContent = { Switch(checked = upload, onCheckedChange = { unUpload -> upload = unUpload }, enabled = true) }
                )
                if(ifSaved) {
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        headlineContent = { Text(text = stringResource(R.string.network_settings_refresh_login_title)) },
                        supportingContent = { Text(text = stringResource(R.string.network_settings_refresh_login_description)) },
                        leadingContent = { Icon(painterResource(R.drawable.rotate_right), contentDescription = "Localized description",) },
                        modifier = Modifier.clickable {
                            refreshLogin(context)
                        }
                    )
                }
            }
        }
        InnerPaddingHeight(innerPaddings,false)
    }
}
