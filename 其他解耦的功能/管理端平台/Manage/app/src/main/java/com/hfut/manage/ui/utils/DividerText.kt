package com.hfut.manage.ui.utils

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DividerText(text : String) {
    Text(text = text, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp))
}