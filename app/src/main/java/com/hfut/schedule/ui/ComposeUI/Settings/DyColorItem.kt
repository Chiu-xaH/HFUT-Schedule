package com.hfut.schedule.ui.ComposeUI.Settings

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.ui.ComposeUI.Settings.PaletteDialogScreen
import com.hfut.schedule.ui.DynamicColor.DynamicColorViewModel

@Composable
fun DyColorItem(dynamicColorViewModel : DynamicColorViewModel,dynamicColorEnabled : Boolean,onChangeDynamicColorEnabled : (Boolean) -> Unit) {
    var openAlertDialog by remember { mutableStateOf(false) }

    if (openAlertDialog) {
        PaletteDialogScreen(
            dynamicColorViewModel = dynamicColorViewModel,
            dynamicColorEnabled = dynamicColorEnabled,
            onChangeDynamicColorEnabled = onChangeDynamicColorEnabled,
            onDismissed = { openAlertDialog = false }
        )
    }

    ListItem(
        headlineContent = { Text(text = "取色设置") },
        // supportingText =
        leadingContent = {
            Icon(
                painterResource(R.drawable.color),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable{
            openAlertDialog = true
        },
    )
}