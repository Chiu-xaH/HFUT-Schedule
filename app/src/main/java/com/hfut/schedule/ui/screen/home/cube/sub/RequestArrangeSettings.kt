package com.hfut.schedule.ui.screen.home.cube.sub

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.xah.uicommon.component.slider.CustomSlider
import com.xah.uicommon.style.APP_HORIZONTAL_DP

private const val key = "BookRequest"
/* 本kt文件已完成多语言文案适配 */
@Composable
fun ArrangeItem() {
    val pageSize = prefs.getString(key,MyApplication.DEFAULT_PAGE_SIZE.toString()) ?: MyApplication.DEFAULT_PAGE_SIZE.toString()
    var sliderPosition by remember { mutableFloatStateOf(pageSize.toFloat()) }
    val str = formatDecimal(sliderPosition.toDouble(),0)
    TransplantListItem(
        headlineContent = { Text(text = stringResource(
            R.string.network_settings_request_page_size_title,
            str
        )) },
        supportingContent = { Text(text = stringResource(R.string.network_settings_request_page_size_description)) },
        leadingContent = { Icon(painterResource(R.drawable.settings_ethernet), contentDescription = "Localized description",) },
    )
    CustomSlider(
        value = sliderPosition,
        onValueChange = {
            sliderPosition = it
            val str = formatDecimal(sliderPosition.toDouble(),0)
            SharedPrefs.saveString(key,str)
        },
        steps = 39,
        valueRange = 10f..50f,
    )
    Spacer(Modifier.height(APP_HORIZONTAL_DP))
}