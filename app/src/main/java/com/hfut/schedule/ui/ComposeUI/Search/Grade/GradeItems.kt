package com.hfut.schedule.ui.ComposeUI.Search.Grade

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R

@Composable
fun GradeItemUI() {
    LazyColumn{
        items(getGrade().size) { item ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Column() {
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp, vertical = 5.dp),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        ListItem(
                            headlineContent = {  Text(getGrade()[item].title) },
                            supportingContent = { Text("学分: " + getGrade()[item].score + " | " + "绩点: " + getGrade()[item].GPA) },
                            leadingContent = { Icon(painterResource(R.drawable.article), contentDescription = "Localized description",) },
                            trailingContent = { Text(getGrade()[item].grade) },
                            modifier = Modifier.clickable {},
                        )
                    }
                }
            }
        }
    }
}