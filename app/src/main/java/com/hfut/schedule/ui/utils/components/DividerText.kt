package com.hfut.schedule.ui.utils.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.utils.AndroidVersion
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.ui.utils.BlurManager

// 小标题
@Composable
fun DividerText(text: String, onClick: (() -> Unit?)? = null) {
    var isPressed by remember { mutableStateOf(false) }
    val scale = animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f, // 按下时为0.9，松开时为1
        animationSpec = tween(MyApplication.Animation / 2, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )
    val color by animateColorAsState(
        targetValue = if (isPressed) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.primary,
        label = ""
    )

    Text(
        text = text,
        color = color,
        modifier = Modifier
            .padding(horizontal = 17.dp, vertical = 10.dp)
            .clickable { onClick?.invoke() }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = {
                        onClick?.invoke()
                    }
                )
            }
            .indication(interactionSource = remember { MutableInteractionSource() }, indication = null)
            .scale(scale.value)
    )
}
// 按压小标题展开/收起下面内容
@Composable
fun DividerTextExpandedWith(
    text : String,
    openBlurAnimation : Boolean = true,
    defaultIsExpanded : Boolean = true,
    content: @Composable () -> Unit
) {


    val speed = MyApplication.Animation

    var expanded by remember { mutableStateOf(defaultIsExpanded) }
    fun set() {
        expanded = !expanded
    }

    val blurSize by animateDpAsState(
        targetValue = if (!expanded) 10.dp else 0.dp, label = ""
        ,animationSpec = tween(speed, easing = LinearOutSlowInEasing),
    )

    val canBlur = BlurManager.getValue()

    DividerText(text, onClick = {
        set()
    })

    AnimatedVisibility(
        enter =
        scaleIn(animationSpec = tween(durationMillis = speed)) +
                expandVertically(expandFrom = Alignment.Bottom,animationSpec = tween(durationMillis = speed))
        ,
        exit =
        scaleOut(animationSpec = tween(durationMillis = speed)) +
                shrinkVertically(shrinkTowards = Alignment.Bottom,animationSpec = tween(durationMillis = speed))
        ,
        visible = expanded,
        modifier = if (openBlurAnimation && canBlur) Modifier.blur(blurSize) else Modifier
    ) {
        Column {
            content()
        }
    }
}


