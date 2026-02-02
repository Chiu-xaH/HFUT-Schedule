package com.hfut.schedule.ui.screen.home.search.function.huiXin.hotWater

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.xah.uicommon.component.text.ScrollText

@Composable
fun HotWater() {
    val context = LocalContext.current
    TransplantListItem(
        headlineContent = { ScrollText(text = stringResource(R.string.navigation_label_hot_water)) },
        leadingContent = { Icon(painterResource(R.drawable.water_voc), contentDescription = "")},
        modifier = Modifier.clickable { Starter.startAppUrl(context,MyApplication.ALIPAY_HOT_WATER_URL) }
    )
}