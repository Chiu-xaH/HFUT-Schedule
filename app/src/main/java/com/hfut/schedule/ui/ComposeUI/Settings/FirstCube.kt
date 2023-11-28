package com.hfut.schedule.ui.ComposeUI.Settings

import android.annotation.SuppressLint
import android.content.Context
import android.preference.PreferenceManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.datamodel.data4
import com.hfut.schedule.ui.DynamicColor.DynamicColorViewModel

@SuppressLint("SuspiciousIndentation")
@Composable
fun FirstCube(dynamicColorViewModel: DynamicColorViewModel, dynamicColorEnabled: Boolean, onChangeDynamicColorEnabled: (Boolean) -> Unit, ) {
    CardItem()
    Spacer(modifier = Modifier.height(5.dp))
    DyColorItem(dynamicColorViewModel,dynamicColorEnabled, onChangeDynamicColorEnabled )
    SettingsItems()
}