package com.hfut.schedule.ui.screen.home.search.function.my.webLab

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.Starter
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP


import com.hfut.schedule.ui.component.container.SmallCard
import com.hfut.schedule.ui.component.container.TransplantListItem

@Composable
fun WebItem() {
    data class WebItemData(val url : String,val icon : Int,val title : String,val remark : String? = null)
    val webs = listOf(
        WebItemData(title = "信息门户", icon = R.drawable.person, url = MyApplication.ONE_URL),
        WebItemData(title = "WebVpn", icon = R.drawable.vpn_key, url = MyApplication.WEBVPN_URL),
    )
    val context = LocalContext.current
    Column(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
        for(index in webs.indices step 2) {
            Row {
                val item = webs[index]
                SmallCard(modifier = Modifier.padding(horizontal = CARD_NORMAL_DP, vertical = CARD_NORMAL_DP).weight(.5f)) {
                    TransplantListItem(
                        headlineContent = { Text(item.title) },
                        overlineContent = if(item.remark != null) {
                            { Text(item.remark) }
                        } else{
                            null
                        },
                        leadingContent = {
                            Icon(
                                painterResource(item.icon),
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.clickable { Starter.startWebUrl(context,item.url) }
                    )
                }
                if(index+1 != webs.size) {
                    val item2 = webs[index+1]
                    SmallCard(modifier = Modifier.padding(horizontal = CARD_NORMAL_DP, vertical = CARD_NORMAL_DP).weight(.5f)) {
                        TransplantListItem(
                            headlineContent = { Text(item2.title) },
                            overlineContent = if(item2.remark != null) {
                                { Text(item2.remark) }
                            } else{
                                null
                            },
                            leadingContent = {
                                Icon(
                                    painterResource(item2.icon),
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.clickable { Starter.startWebUrl(context,item2.url) }
                        )
                    }
                }
            }
        }
    }
}