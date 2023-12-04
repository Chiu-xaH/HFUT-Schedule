package com.hfut.schedule.ui.ComposeUI.Settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.ui.MonetColor.MonetUI

@Composable
fun MonetColorItem() {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 5.dp),
        shape = MaterialTheme.shapes.medium

    ){

    }
    ListItem(
        headlineContent = { Text(text = "莫奈取色") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.color),
                contentDescription = "Localized description",
            )
        }
    )
   // Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
     //   Spacer(modifier = Modifier.width(30.dp))
        MonetUI()
   // }
    Spacer(modifier = Modifier.height(5.dp))


}