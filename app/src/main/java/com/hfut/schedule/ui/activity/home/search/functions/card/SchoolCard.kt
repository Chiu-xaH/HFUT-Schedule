package com.hfut.schedule.ui.activity.home.search.functions.card

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.UIViewModel
import com.hfut.schedule.activity.funiction.CardActivity
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.ui.activity.home.focus.funictions.getToday
import com.hfut.schedule.ui.utils.components.EmptyUI
import com.hfut.schedule.ui.utils.components.MyCustomCard
import com.hfut.schedule.ui.utils.components.ScrollText


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun SchoolCardItem(vmUI : UIViewModel,cardBool : Boolean) {

    val interactionSource2 = remember { MutableInteractionSource() }
    val isPressed2 by interactionSource2.collectIsPressedAsState()
    val scale2 = animateFloatAsState(
        targetValue = if (isPressed2) 0.9f else 1f, // 按下时为0.9，松开时为1
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "" // 使用弹簧动画
    )

    var card = prefs.getString("card","00")
  //  val bd = BigDecimal(card)
  //  val str = bd.setScale(2, RoundingMode.HALF_UP).toString()
    val test = vmUI.CardValue.value?.balance ?: card
val showAdd = prefs.getBoolean("SWITCHCARDADD",true)
    ListItem(
        headlineContent = { if(cardBool) ScrollText(text = "￥$test") else Text(text = "一卡通 ￥$test") },
        overlineContent = { if(cardBool) {
            ScrollText(
                if (test != null) {
                    if(test.length <= 4){
                        "余额不足"
                    } else "一卡通"
                } else "一卡通")
        }

                          },
        leadingContent = { Icon(painterResource(R.drawable.credit_card), contentDescription = "Localized description",) },
        trailingContent={
            if (test != null) {
                if(showAdd || !cardBool)
                FilledTonalIconButton(
                    modifier = Modifier
                        .scale(scale2.value)
                        .size(30.dp),
                    interactionSource = interactionSource2,
                    onClick = { Starter.startAppUrl(MyApplication.AlipayCardURL) },
                    colors =  if(test.length <= 4) {
                        IconButtonDefaults.filledTonalIconButtonColors(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                    } else IconButtonDefaults.filledTonalIconButtonColors()
                ) { Icon( painterResource(R.drawable.add), contentDescription = "Localized description",) }
            }
        },
        colors = (
            if (test != null) {
                if(test.length <= 4){
                    ListItemDefaults.colors(MaterialTheme.colorScheme.errorContainer)
                } else ListItemDefaults.colors()
            } else ListItemDefaults.colors()
        ),
        modifier = Modifier.clickable {
            val it = Intent(MyApplication.context, CardActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            MyApplication.context.startActivity(it)
        }
    )
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodayInfo() {
    if(getToday()?.todayExam?.courseName == null && getToday()?.todayCourse?.courseName == null && getToday()?.bookLending?.bookName == null && getToday()?.todayActivity?.activityName == null) {
        EmptyUI()
    }
        if(getToday()?.todayExam?.courseName != null) {
            MyCustomCard {
                ListItem(
                    headlineContent = { Text(text = getToday()?.todayExam?.courseName.toString()) },
                    overlineContent = { Text(text = getToday()?.todayExam?.startTime + "~" + getToday()?.todayExam?.endTime) },
                    supportingContent = { Text(text = getToday()?.todayExam?.place.toString())},
                    leadingContent = { Icon(painter = painterResource(R.drawable.draw), contentDescription = "")},
                )
            }
        }
        if(getToday()?.todayCourse?.courseName != null) {
            MyCustomCard {
                ListItem(
                    headlineContent = { Text(text = getToday()?.todayCourse?.courseName.toString()) },
                    overlineContent = { Text(text = getToday()?.todayCourse?.startTime + "~" + getToday()?.todayCourse?.endTime +  "  " +  getToday()?.todayCourse?.place)},
                    supportingContent = { getToday()?.todayCourse?.className?.let { Text(text = it) } },
                    leadingContent = { Icon(painter = painterResource(R.drawable.calendar), contentDescription = "")},
                )
            }
        }
        if(getToday()?.bookLending?.bookName != null) {
            MyCustomCard {
                ListItem(
                    headlineContent = { Text(text = getToday()?.bookLending?.bookName.toString()) },
                    supportingContent = { Text(text = "归还时间 " + getToday()?.bookLending?.returnTime) },
                    overlineContent = { Text(text = "借阅于 " + getToday()?.bookLending?.outTime + "\n应还于 " + getToday()?.bookLending?.dueTime )},
                    leadingContent = { Icon(painter = painterResource(R.drawable.book), contentDescription = "")},
                )
            }
        }
        if(getToday()?.todayActivity?.activityName != null) {
            MyCustomCard {
                ListItem(
                    headlineContent = { Text(text = getToday()?.todayActivity?.activityName.toString()) },
                    overlineContent = { Text(text = getToday()?.todayActivity?.startTime.toString()) },
                    leadingContent = { Icon(painter = painterResource(R.drawable.schedule), contentDescription = "")},
                )
            }
        }
}


