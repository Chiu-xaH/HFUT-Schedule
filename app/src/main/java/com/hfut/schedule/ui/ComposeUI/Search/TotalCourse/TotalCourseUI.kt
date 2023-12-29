package com.hfut.schedule.ui.ComposeUI.Search.TotalCourse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R

@Composable
fun CourseTotalUI() {

    var num = 0.0
    for(i in 0 until getCourse().size) {
        val credit = getCourse()[i].credit
        num += credit
    }

    LazyColumn {
        item { Another(num) }
        items(getCourse().size) { item ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Column() {
                    Card(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 3.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 15.dp,
                                vertical = 5.dp
                            ),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        ListItem(
                            headlineContent = {  Text(getCourse()[item].courseName) },
                            supportingContent = { Text("学分 " + getCourse()[item].credit.toString()) },
                            leadingContent = { Icon(painterResource(R.drawable.calendar_view_month), contentDescription = "Localized description",) },
                            modifier = Modifier.clickable {},
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Another(num : Double) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Column() {
            Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 15.dp,
                        vertical = 5.dp
                    ),
                shape = MaterialTheme.shapes.medium,
            ){
                ListItem(
                    headlineContent = {  Text("学分合计 ${num}") },
                    leadingContent = { Icon(painterResource(R.drawable.category), contentDescription = "Localized description",) },
                    modifier = Modifier.clickable {},
                    colors = ListItemDefaults.colors(MaterialTheme.colorScheme.errorContainer)
                )
            }
        }
    }
}