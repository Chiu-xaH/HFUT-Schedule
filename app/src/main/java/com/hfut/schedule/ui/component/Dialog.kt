package com.hfut.schedule.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.style.dialogBlur
import dev.chrisbanes.haze.HazeDialog
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze

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
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = AppVersion.CAN_BLUR)
    val modifier = if(blur && hazeState != null) {
        Modifier.dialogBlur(hazeState)
    } else {
        Modifier
    }
    HazeDialog(
        hazeState = hazeState ?: HazeState(),
        onDismissRequest = onDismissRequest,
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface.copy(if(blur) 1f else 0.95f),
            modifier = Modifier.padding(appHorizontalDp())
        ) {
            Column(modifier = modifier) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(appHorizontalDp())
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