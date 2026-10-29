package com.hfut.schedule.ui.screen.home.search.function.huiXin.card

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.enumeration.FocusSchoolCardQuickButton
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.network.api.model.response.json.community.CommunityToday
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.status.EmptyIcon
import com.hfut.schedule.ui.nav.destination.SchoolCardDestination
import com.hfut.schedule.ui.screen.home.search.QuickButtonForSearchScreen
import com.hfut.schedule.ui.util.state.GlobalUiStateHolder
import com.sharednav.common.helper.NoneRoundShape
import com.xah.common.logic.util.EMPTY_STRING
import com.xah.common.logic.util.LogUtil
import com.xah.common.ui.component.text.ScrollText
import com.xah.container.component.base.sharedContainer
import com.xah.navigation.util.LocalNavController
import kotlinx.coroutines.launch


@SuppressLint("SuspiciousIndentation")
@Composable
fun SchoolCardItem(isFromFocus : Boolean) {
    val navController = LocalNavController.current
    val cardValue by remember { derivedStateOf { GlobalUiStateHolder.cardValue } }
    var text by remember { mutableStateOf(cardValue?.balance ?: prefs.getString("card","00")) }
    val nullStr = remember { "--" }

    LaunchedEffect(cardValue) {
        text = cardValue?.balance ?: prefs.getString("card","00")
        if(text == "00") { text = nullStr }
    }

    val focusSchoolCardQuickButton by DataStoreManager.focusSchoolCardQuickButton.collectAsState(FocusSchoolCardQuickButton.NONE.code)
    val color = if (text != null && text != nullStr) {
        if(text!!.length <= 4) {
            MaterialTheme.colorScheme.error
        } else {
            LocalContentColor.current
        }
    } else {
        LocalContentColor.current
    }


    TransplantListItem(
        color = cardNormalColor(),
        headlineContent = {
            if(!isFromFocus || focusSchoolCardQuickButton != FocusSchoolCardQuickButton.NONE.code) {
                ScrollText(
                    text = "￥$text",
                    color = color
                )
            } else {
                Row {
                    Text(
                        text = "￥",
                        color = color
                    )
                    ScrollText(
                        text = "$text",
                        color = color
                    )
                }
            }
        },
        overlineContent = {
            ScrollText(
                if (text != null && text != nullStr) {
                    if(text!!.length <= 4){
                        stringResource(R.string.navigation_label_school_card_warn)
                    } else stringResource(R.string.navigation_label_school_card)
                } else stringResource(R.string.navigation_label_school_card),
                color = color
            )
        },
        leadingContent = {
            Icon(
                painterResource(R.drawable.credit_card),
                contentDescription = "Localized description",
                tint = color
            )
        },
        trailingContent= {
            if(isFromFocus) {
                when(focusSchoolCardQuickButton) {
                    FocusSchoolCardQuickButton.ADD.code -> SchoolCardAddQuickButton()
                    FocusSchoolCardQuickButton.PAY.code -> HuiXinPayQuickButton()
                }
            } else {
                SchoolCardAddQuickButton()
            }
        },
        modifier = Modifier
            .let {
                if(isFromFocus) {
                    it.sharedContainer(
                        SchoolCardDestination.key,
                        shape = NoneRoundShape.copy(
                            topStart = MaterialTheme.shapes.medium.topStart,
                        ),
                        containerColor = cardNormalColor()
                    )
                } else {
                    it
                }
            }
            .clickable {
                navController.push(SchoolCardDestination)
            }
    )
}


@Composable
fun TodayInfo(bean : CommunityToday) {
    with(bean) {
        if(todayExam.courseName == null && todayCourse.courseName == null && bookLending.bookName == null && todayActivity.activityName == null) {
            EmptyIcon()
        }
        with(todayExam) {
            courseName?.let {
                CardListItem(
                    headlineContent = { Text(text = it) },
                    overlineContent = { Text(text = "$startTime~$endTime") },
                    supportingContent = { Text(text = place.toString())},
                    leadingContent = { Icon(painter = painterResource(R.drawable.draw), contentDescription = EMPTY_STRING)},
                )
            }
        }
        with(todayCourse) {
            courseName?.let {
                CardListItem(
                    headlineContent = { Text(text = it) },
                    overlineContent = { Text(text = "$startTime~$endTime  $place")},
                    supportingContent = { className?.let { Text(text = it) } },
                    leadingContent = { Icon(painter = painterResource(R.drawable.calendar), contentDescription = EMPTY_STRING)},
                )
            }
        }
        with(bookLending) {
            bookName?.let {
                CardListItem(
                    headlineContent = { Text(text = it) },
                    supportingContent = { Text(text = "归还时间 $returnTime") },
                    overlineContent = { Text(text = "借阅于 $outTime\n应还于 $dueTime")},
                    leadingContent = { Icon(painter = painterResource(R.drawable.book_5), contentDescription = EMPTY_STRING)},
                )
            }
        }
        with(todayActivity) {
            activityName?.let {
                CardListItem(
                    headlineContent = { Text(text = it) },
                    overlineContent = { Text(text = startTime.toString()) },
                    leadingContent = { Icon(painter = painterResource(R.drawable.schedule), contentDescription = EMPTY_STRING)},
                )
            }
        }
    }
}


@Composable
private fun SchoolCardAddQuickButton() {
    val context = LocalContext.current
    QuickButtonForSearchScreen(
        onClick = {
            Starter.startAppUrl(context, Constant.ALIPAY_CARD_URL)
        }
    ) {
        Icon(
            painterResource(R.drawable.add),
            null
        )
    }
}

@Composable
fun HuiXinPayQuickButton() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    QuickButtonForSearchScreen(
        onClick = {
            scope.launch {
                val url = Constant.HUI_XIN_URL + "plat/pay" + "?synjones-auth=" + prefs.getString("auth","")
                Starter.startWebUrlInner(context, url,"付款码",icon = R.drawable.barcode)
            }
        }
    ) {
        // 特殊缩小size
        Icon(painterResource(R.drawable.barcode),null, modifier = Modifier.size(20.dp-1.dp))
    }
}