package com.hfut.schedule.ui.component.status

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.align.ColumnVertical
import com.xah.uicommon.util.language.UiText
import com.xah.uicommon.util.language.text

@Composable
fun EmptyIcon(text: String = "空") = StatusIcon(R.drawable.upcoming,text(text))
@Composable
fun DevelopingIcon() = StatusIcon(R.drawable.sdk,text("正在开发"))
@Composable
fun SuccessfulIcon() = StatusIcon(R.drawable.check,text("成功"))
@Composable
fun ErrorIcon(string: String) = StatusIcon(R.drawable.close,text(string))
@Composable
fun PrepareSearchIcon() = StatusIcon(R.drawable.search,text("开始搜索"))


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StatusIcon(
    icon : Int,
    title : UiText,
    onTextClick : (() -> Unit)? = null,
    iconColor : Color = MaterialTheme.colorScheme.secondary,
    iconContainerColor : Color = MaterialTheme.colorScheme.secondaryContainer,
    textColor : Color = if(onTextClick != null) MaterialTheme.colorScheme.primary else iconColor.copy(.75f),
) {
    ColumnVertical(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier.size(100.dp),
            shape = MaterialTheme.shapes.largeIncreased,
            color = iconContainerColor
        ) {
            Icon(
                painterResource(icon),
                null,
                tint = iconColor,
                modifier = Modifier.fillMaxSize().padding(CARD_NORMAL_DP*3)
            )
        }
        Text(
            text = title.asString(),
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = CARD_NORMAL_DP*3)
                .padding(horizontal = APP_HORIZONTAL_DP)
                .clickable(onTextClick != null) {
                    onTextClick?.let { it() }
                }
        )
    }
}
