package com.hfut.schedule.ui.component.button

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.style.ColumnVertical



@Composable
fun LargeButton(
    modifier : Modifier = Modifier,
    iconModifier : Modifier = Modifier,
    onClick : () -> Unit,
    enabled: Boolean = true,
    containerColor: Color =  Color.Unspecified,
    contentColor : Color =  Color.Unspecified,
    icon : Int,
    text : String) {
    Button(
        enabled = enabled,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor)
    ) {
        ColumnVertical {
            Icon(painterResource(icon),null,modifier = iconModifier)
            Spacer(Modifier.Companion.height(CARD_NORMAL_DP))
            Text(text)
        }
    }
}