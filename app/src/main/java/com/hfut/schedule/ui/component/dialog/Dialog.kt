package com.hfut.schedule.ui.component.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.ui.style.special.dialogBlur
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
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val modifier = if(blur >= HazeBlurLevel.MID.code && hazeState != null) {
        Modifier.dialogBlur(hazeState)
    } else {
        Modifier
    }
    HazeDialog(
        hazeState = hazeState ?: rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code),
        onDismissRequest = onDismissRequest,
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface.copy(if(blur >= HazeBlurLevel.MID.code) 1f else 0.95f),
            modifier = Modifier.Companion.padding(APP_HORIZONTAL_DP)
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
                    TextButton (
                        onClick = onDismissRequest,
                        shape = RoundedCornerShape(0.dp),
                        modifier = Modifier.fillMaxWidth().weight(.5f)
                    ) {
                        Text(text = dismissText)
                    }
                    VerticalDivider(thickness = 0.7.dp, modifier = Modifier.height(48.dp))
                    TextButton (
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        onClick = onConfirmation,
                        shape = RoundedCornerShape(0.dp),
                        modifier = Modifier.fillMaxWidth().weight(.5f)
                    ) {
                        Text(text = conformText)
                    }
                }
            }
        }
    }

}
//            modifier = Modifier
//                .fillMaxWidth()
//                .fillMaxHeight(fraction = .5f),
//        AlertDialog(
//            title = { Text(text = dialogTitle) },
//            text = { Text(text = dialogText) },
//            onDismissRequest = { onDismissRequest() },
//            confirmButton = {
//                Button(onClick = { onConfirmation() }
//                ) { Text(conformtext) }
//            },
//            dismissButton = {
//                FilledTonalButton(
//                    onClick = { onDismissRequest() }
//                ) { Text(dismisstext) }
//            },
//        )