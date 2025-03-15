package com.hfut.schedule.ui.activity.home.cube.items.subitems

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.data.SharePrefs
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.ui.utils.components.BottomSheetTopBar
import com.hfut.schedule.ui.utils.components.TransplantListItem
import java.math.BigDecimal
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestArrange() {
    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BottomSheetTopBar("请求范围")
        },
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
        ){
            LazyColumn {
                item { ArrangeItem(Title = "图书检索", Icon = R.drawable.book, SaveTitle = "BookRequest") }
                item { ArrangeItem(Title = "一卡通", Icon = R.drawable.credit_card, SaveTitle = "CardRequest") }
                item { ArrangeItem(Title = "挂科率", Icon = R.drawable.monitoring, SaveTitle = "FailRateRequest") }
            }
        }
    }
}


@Composable
fun ArrangeItem(Title : String,Icon : Int,SaveTitle : String) {
        var prefss = prefs.getString(SaveTitle,"15")
        var sliderPosition by remember { mutableStateOf(prefss!!.toFloat()) }
        val bd = BigDecimal(sliderPosition.toString())
        val str = bd.setScale(0, RoundingMode.HALF_UP).toString()
      //  var Num by remember { mutableStateOf(sliderPosition.toString()) }
    TransplantListItem(
            headlineContent = { Text(text = "$Title   $str 条/页")},
            leadingContent = { Icon(painterResource(id = Icon), contentDescription = "") },
            supportingContent = {
                Slider(
                    value = sliderPosition,
                    onValueChange = {
                        sliderPosition = it
                        val bd = BigDecimal(sliderPosition.toString())
                        val str = bd.setScale(0, RoundingMode.HALF_UP).toString()
                        SharePrefs.saveString(SaveTitle,str)
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.secondary,
                        activeTrackColor = MaterialTheme.colorScheme.secondary,
                        inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    steps = 24,
                    valueRange = 5f..30f,
                    modifier = Modifier.padding(horizontal = 25.dp)
                )
            }
        )
}