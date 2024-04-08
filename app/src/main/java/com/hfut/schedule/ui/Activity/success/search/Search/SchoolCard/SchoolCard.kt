package com.hfut.schedule.ui.Activity.success.search.Search.SchoolCard

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
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
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.UIViewModel
import com.hfut.schedule.activity.CardActivity
import com.hfut.schedule.logic.utils.StartApp
import com.hfut.schedule.logic.utils.SharePrefs.prefs

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun SchoolCardItem(vmUI : UIViewModel) {

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

    ListItem(
        headlineContent = { Text(text =
        "一卡通   $test 元"
                +
                if (test != null) {
                    if(test.length <= 4){
                        "  余额不足"
                    } else ""
                } else ""
        ) },
        leadingContent = { Icon(painterResource(R.drawable.credit_card), contentDescription = "Localized description",) },
        trailingContent={
                FilledTonalIconButton(
                    modifier = Modifier.scale(scale2.value),
                    interactionSource = interactionSource2,
                    onClick = { StartApp.openAlipay(MyApplication.AlipayCardURL) },
                ) { Icon( painterResource(R.drawable.add), contentDescription = "Localized description",) }
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