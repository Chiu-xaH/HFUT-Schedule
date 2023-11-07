package com.hfut.schedule.ui.ComposeUI

import android.content.Intent
import android.net.Uri
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.hfut.schedule.MyApplication

@Composable
fun AboutAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("好")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                    val it = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:zsh0908@outlook.com"))
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    MyApplication.context.startActivity(it)
                }
            ) {
                Text("我要反馈")
            }
        }
    )
}
