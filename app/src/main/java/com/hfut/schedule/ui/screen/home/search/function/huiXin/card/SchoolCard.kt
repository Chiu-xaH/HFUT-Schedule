package com.hfut.schedule.ui.screen.home.search.function.huiXin.card

import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.community.TodayResult
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.status.EmptyIcon
import com.xah.uicommon.component.text.ScrollText
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.viewmodel.ui.UIViewModel


@SuppressLint("SuspiciousIndentation")
@Composable
fun SchoolCardItem(vmUI : UIViewModel,cardBool : Boolean) {

    val interactionSource2 = remember { MutableInteractionSource() }
    val isPressed2 by interactionSource2.collectIsPressedAsState()
    val scale2 = animateFloatAsState(
        targetValue = if (isPressed2) 0.9f else 1f, // 按下时为0.9，松开时为1
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "" // 使用弹簧动画
    )
    val cardValue by remember { derivedStateOf { vmUI.cardValue } }
    var text by remember { mutableStateOf(cardValue?.balance ?: prefs.getString("card","00")) }

    LaunchedEffect(cardValue) {
        text = cardValue?.balance ?: prefs.getString("card","00")
        if(text == "00") { text = "未登录" }
    }
    val context = LocalContext.current

    val showAdd = prefs.getBoolean("SWITCHCARDADD",true)
    TransplantListItem(
        headlineContent = {
            ScrollText(
                text = if(cardBool)"￥$text" else "一卡通 ￥$text",
                color = if (text != null && text != "未登录") {
                    if(text!!.length <= 4){
                        MaterialTheme.colorScheme.error
                    } else LocalContentColor.current
                } else LocalContentColor. current
            )
        },
        overlineContent = { if(cardBool) {
            ScrollText(
                if (text != null && text != "未登录") {
                    if(text!!.length <= 4){
                        "余额不足"
                    } else "一卡通"
                } else "一卡通",
                color = if (text != null && text != "未登录") {
                    if(text!!.length <= 4){
                        MaterialTheme.colorScheme.error
                    } else LocalContentColor.current
                } else LocalContentColor. current
            )
        }

                          },
        leadingContent = { Icon(
            painterResource(R.drawable.credit_card),
            contentDescription = "Localized description",
            tint = if (text != null && text != "未登录") {
                if(text!!.length <= 4){
                    MaterialTheme.colorScheme.error
                } else LocalContentColor.current
            } else LocalContentColor. current
            ) },
        trailingContent={
            if (text != null && text != "未登录") {
                if(showAdd || !cardBool || text!!.length <= 4)
                FilledTonalIconButton(
                    modifier = Modifier
                        .scale(scale2.value)
                        .size(30.dp),
                    interactionSource = interactionSource2,
                    onClick = { Starter.startAppUrl(context,MyApplication.ALIPAY_CARD_URL) },
//                    colors =  if(text!!.length <= 4) {
//                        IconButtonDefaults.filledTonalIconButtonColors(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
//                    } else IconButtonDefaults.filledTonalIconButtonColors()
                ) { Icon( painterResource(R.drawable.add), contentDescription = "Localized description",) }
            }
        },
//        colors = (
//            if (text != null && text != "未登录") {
//                if(text!!.length <= 4){
//                    MaterialTheme.colorScheme.errorContainer
//                } else null
//            } else null
//        ),
        modifier = Modifier.clickable {
            Starter.startCard(context)
        }
    )
}


@Composable
fun TodayInfo(bean : TodayResult) {
    with(bean) {
        if(todayExam.courseName == null && todayCourse.courseName == null && bookLending.bookName == null && todayActivity.activityName == null) {
            EmptyIcon()
        }
        with(todayExam) {
            courseName?.let {
                CardListItem(
                    headlineContent = { Text(text = it.toString()) },
                    overlineContent = { Text(text = "$startTime~$endTime") },
                    supportingContent = { Text(text = place.toString())},
                    leadingContent = { Icon(painter = painterResource(R.drawable.draw), contentDescription = "")},
                )
            }
        }
        with(todayCourse) {
            courseName?.let {
                CardListItem(
                    headlineContent = { Text(text = it.toString()) },
                    overlineContent = { Text(text = "$startTime~$endTime  $place")},
                    supportingContent = { className?.let { Text(text = it) } },
                    leadingContent = { Icon(painter = painterResource(R.drawable.calendar), contentDescription = "")},
                )
            }
        }
        with(bookLending) {
            bookName?.let {
                CardListItem(
                    headlineContent = { Text(text = it.toString()) },
                    supportingContent = { Text(text = "归还时间 $returnTime") },
                    overlineContent = { Text(text = "借阅于 $outTime\n应还于 $dueTime")},
                    leadingContent = { Icon(painter = painterResource(R.drawable.book), contentDescription = "")},
                )
            }
        }
        with(todayActivity) {
            activityName?.let {
                CardListItem(
                    headlineContent = { Text(text = it.toString()) },
                    overlineContent = { Text(text = startTime.toString()) },
                    leadingContent = { Icon(painter = painterResource(R.drawable.schedule), contentDescription = "")},
                )
            }
        }
    }
}


