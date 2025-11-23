package com.hfut.schedule.ui.component.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.style.special.dialogBlur
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import dev.chrisbanes.haze.HazeDialog
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun LittleDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String = "提示",
    dialogText: String,
    conformText : String = "确定",
    dismissText : String = "取消",
    hazeState: HazeState? = null
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val modifier = if(blur && hazeState != null) {
        Modifier.dialogBlur(hazeState)
    } else {
        Modifier
    }
    HazeDialog(
        hazeState = hazeState ?: rememberHazeState(blurEnabled = blur),
        onDismissRequest = onDismissRequest,
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(APP_HORIZONTAL_DP)
        ) {
            Column(modifier = modifier) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(APP_HORIZONTAL_DP)
                ) {
                    Text(
                        text = dialogTitle,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = dialogText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                HorizontalDivider(thickness = 0.7.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    FakeButton(
                        text = dismissText,
                        onClick = onDismissRequest,
                        modifier = Modifier.weight(1f),
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                    VerticalDivider(thickness = 0.7.dp, modifier = Modifier.height(48.dp))
                    FakeButton(
                        text = conformText,
                        onClick = onConfirmation,
                        modifier = Modifier.weight(1f),
                        textColor = MaterialTheme.colorScheme.error, // 因为下面 FakeButton() 里面新增了可传入文本颜色，如果直接用 contentColor 控制可能会混乱而且会丢失单独控制水波纹颜色的方法，所以分开
                        contentColor = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun FakeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Transparent,
    textColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color,
    height: Dp = 48.dp
) {
    Card(
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        modifier = modifier.height(height),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = textColor,
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}
