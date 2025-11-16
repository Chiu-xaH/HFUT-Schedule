package com.hfut.schedule.receiver.widget.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.hfut.schedule.receiver.widget.focus.widgetPadding


/**
 * 类似 Material3 ListItem 的 Glance 版本
 *
 * 支持插槽：
 * - leadingContent：左侧图标或头像
 * - overlineContent：标题上方的辅助信息
 * - headlineContent：主标题（必传）
 * - supportingContent：副标题
 * - trailingContent：右侧文字或图标
 */
@Composable
fun WidgetCardListItem(
    textSize : Float = 1f,
    modifier: GlanceModifier = GlanceModifier,
    leadingContent: (@Composable () -> Unit)? = null,
    overlineText: String? = null,
    headlineText: String,
    textDecoration: TextDecoration? = null,
    supportingText: String? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    colors: ColorProvider = GlanceTheme.colors.primaryContainer,
) {
    Box (
        modifier = modifier
    ){
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .cornerRadius(12.dp)
                .background(colors)
                .padding(horizontal = 10.dp, vertical = widgetPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 左侧图标或图片
            if (leadingContent != null) {
                Box(
                    modifier = GlanceModifier.size(19.dp*textSize),
                    contentAlignment = Alignment.Center
                ) {
                    leadingContent()
                }
                Spacer(modifier = GlanceModifier.width(widgetPadding))
            }
            // 中间文本区域
            Column(
                modifier = GlanceModifier
                    .defaultWeight()
                    .padding(end = widgetPadding)
            ) {
                if (overlineText != null) {
                    Text(
                        text = overlineText,
                        maxLines = 1,
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurface,
                            fontSize = 9.sp*textSize,
                            fontWeight = FontWeight.Medium,
                            textDecoration = textDecoration
                        ),
                    )
                }

                Text(
                    maxLines = 1,
                    text = headlineText,
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 14.5.sp*textSize,
                        fontWeight = FontWeight.Bold,
                        textDecoration = textDecoration
                    ),
                )

                if (supportingText != null) {
                    Text(
                        maxLines = 1,
                        text = supportingText,
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurface,
                            fontSize = 12.sp*textSize,
                            fontWeight = FontWeight.Normal,
                            textDecoration = textDecoration
                        ),
                    )
                }
            }


            // 右侧内容
            if (trailingContent != null) {
                Spacer(modifier = GlanceModifier.width(8.dp))
                trailingContent()
            }
        }
    }
}