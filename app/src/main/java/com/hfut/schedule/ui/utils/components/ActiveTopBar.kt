package com.hfut.schedule.ui.utils.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.hfut.schedule.ui.utils.components.ScrollText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveTopBar(title : String) {
    TopAppBar(
        title = { ScrollText(text = title) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.primary)
    )
}