package com.hfut.schedule.ui.utils.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.DialogProperties
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.logic.utils.Starter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebDialog(showDialog : Boolean,showChanged : () -> Unit,url : String,title : String = "网页",cookie :String? = null,showTop : Boolean = true) {

    val switch_startUri = prefs.getBoolean("SWITCHSTARTURI",true)

    if (showDialog) {
        if(switch_startUri) {
            androidx.compose.ui.window.Dialog(
                onDismissRequest = showChanged,
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                WebViewScreen(url,cookie, showChanged,title,showTop)
            }
        } else {
            Starter.startWebUrl(url)
        }
    }
}