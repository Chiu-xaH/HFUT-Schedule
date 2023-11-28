package com.hfut.schedule.ui.ComposeUI.Settings

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.hfut.schedule.MyApplication
import com.hfut.schedule.ui.DynamicColor.CatalogTheme
import com.hfut.schedule.ui.DynamicColor.DynamicColorViewModel

@Composable
fun PaletteDialogScreen(
    dynamicColorViewModel: DynamicColorViewModel,
    dynamicColorEnabled: Boolean,
    onChangeDynamicColorEnabled: (Boolean) -> Unit,
    onDismissed: () -> Unit
) {

    var selectedName by remember { mutableStateOf(dynamicColorViewModel.currentTheme) }

    AlertDialog(
        onDismissRequest = onDismissed,
        title = {
            Text("取色设置")
        },
        text = {
            Column(
                modifier = Modifier.height(240.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("动态取色(Android 12+)")
                    Spacer(modifier = Modifier.padding(horizontal = 6.dp))
                    Switch(
                        checked = dynamicColorEnabled,
                        onCheckedChange =  onChangeDynamicColorEnabled
                    )
                }
                if (!dynamicColorEnabled) {

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                    ) {
                        items(CatalogTheme.values()) {
                            Column(
                                modifier = Modifier.padding(4.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    shape = RectangleShape,
                                    border = if (selectedName.value != it.paletteName) null else BorderStroke(
                                        4.dp,
                                        Color.White
                                    ),
                                    modifier = Modifier
                                        .size(48.dp)
                                        .padding(horizontal = 4.dp)
                                        .selectable(
                                            selected = selectedName.value == it.paletteName,
                                            onClick = {
                                                if (selectedName.value != it.paletteName) {
                                                    selectedName.value = it.paletteName
                                                    dynamicColorViewModel.setCurrentTheme(it.name)
                                                }
                                            }
                                        ),
                                    color = if (isSystemInDarkTheme()) it.darkColorScheme.primary else it.lightColorScheme.primary
                                ) {}
                                Spacer(modifier = Modifier.padding(vertical = 4.dp))
                                Text(
                                    text = it.paletteName,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }

                        }
                    }
                }

            }


        },
        confirmButton = {
            TextButton(
                onClick = onDismissed,
            ) {
                Text("关闭")
            }
        }
    )
}