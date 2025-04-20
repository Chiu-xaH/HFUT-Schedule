package com.hfut.schedule.ui.screen.home.search.function.webLab

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.appHorizontalDp
import com.hfut.schedule.ui.component.cardNormalDp
import com.hfut.schedule.ui.component.ScrollText
import com.hfut.schedule.ui.component.SmallCard
import com.hfut.schedule.ui.component.TransplantListItem

@Composable
fun WebItem() {
    data class WebItemData(val url : String,val icon : Int,val title : String,val remark : String? = null)
    val webs = listOf(
        WebItemData(title = "信息门户", icon = R.drawable.person, url = MyApplication.ONE_URL),
        WebItemData(title = "教务系统", icon = R.drawable.school, url = MyApplication.JXGLSTU_URL),
        WebItemData(title = "WEBVPN", icon = R.drawable.vpn_key, url = MyApplication.WEBVPN_URL),
        WebItemData(title = "服务大厅", icon = R.drawable.credit_card, url = MyApplication.CARD_URL, remark = "需接入校园网\n校园卡官方网址"),
    )

    Column(modifier = Modifier.padding(horizontal = appHorizontalDp())) {
        for(index in webs.indices step 2) {
            Row {
                val item = webs[index]
                SmallCard(modifier = Modifier.padding(horizontal = cardNormalDp(), vertical = cardNormalDp()).weight(.5f)) {
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
                        modifier = Modifier.clickable { Starter.startWebUrl(item.url) }
                    )
                }
                if(index+1 != webs.size) {
                    val item2 = webs[index+1]
                    SmallCard(modifier = Modifier.padding(horizontal = cardNormalDp(), vertical = cardNormalDp()).weight(.5f)) {
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
                            modifier = Modifier.clickable { Starter.startWebUrl(item2.url) }
                        )
                    }
                }
            }
        }
    }
}