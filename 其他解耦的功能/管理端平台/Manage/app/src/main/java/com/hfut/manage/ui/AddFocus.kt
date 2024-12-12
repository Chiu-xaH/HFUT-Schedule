package com.hfut.manage.ui

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.window.DialogProperties
import com.hfut.manage.R
import com.hfut.manage.datamodel.enums.DataType
import com.hfut.manage.logic.utils.GetDate
import com.hfut.manage.ui.utils.MyToast
import com.hfut.manage.viewmodel.NetViewModel
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("Range")
@Composable
fun BoxScope.AddButton(isVisible: Boolean, innerPaddings : PaddingValues,vm : NetViewModel) {


  //  val sheetState = rememberModalBottomSheetState()


    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn() ,
        exit = scaleOut(),
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(innerPaddings)
            .padding(horizontal = 15.dp, vertical = 15.dp)
    ) {
        if (isVisible) {
            FloatingActionButton(
                onClick = {  },
            ) { Icon(Icons.Filled.Add, "Add Button") }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun addFocus(vm : NetViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 5.dp),
        shape = MaterialTheme.shapes.medium,
    ){
        ListItem(
            headlineContent = {  Text(text = "添加聚焦卡片") },
            // overlineContent = { Text(text = "${item["日期时间"]}") },
            leadingContent = { Icon(
                painterResource(id = R.drawable.lightbulb),
                contentDescription = "Localized description"
            ) },
            trailingContent = {Icon(Icons.Filled.Add, contentDescription = "Localized description",)},
            modifier = Modifier.clickable { showDialog = true },
        )
    }



    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale = animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f, // 按下时为0.9，松开时为1
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "" // 使用弹簧动画
    )

    var title by remember { mutableStateOf( "") }
    var info by remember { mutableStateOf( "") }


    var startYear by remember { mutableStateOf( GetDate.Date_yyyy) }
    var startMonth by remember { mutableStateOf( GetDate.Date_MM) }
    var startDate by remember { mutableStateOf( GetDate.Date_dd) }
    var startHour by remember { mutableStateOf( GetDate.formattedTime_Hour) }
    var startMinute by remember { mutableStateOf( GetDate.formattedTime_Minute) }
    var endYear by remember { mutableStateOf( GetDate.Date_yyyy) }
    var endMonth by remember { mutableStateOf( GetDate.Date_MM) }
    var endDate by remember { mutableStateOf( GetDate.Date_dd) }
    var endHour by remember { mutableStateOf( GetDate.formattedTime_Hour) }
    var endMinute by remember { mutableStateOf( GetDate.formattedTime_Minute) }

    var remark by remember { mutableStateOf("") }

    var start by remember { mutableStateOf( "$startYear-$startMonth-$startDate $startHour:$startMinute") }
    var end by remember { mutableStateOf( "$endYear-$endMonth-$endDate $endHour:$endMinute") }

    //真为Schedule，假为Wangke
    var typeBoolean by remember { mutableStateOf(true) }
    var pickerBoolean by remember { mutableStateOf(true) }


    if (showDialog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        actions = {

                            FilledTonalIconButton(
                                modifier = Modifier
                                    .scale(scale.value)
                                    .padding(25.dp),
                                interactionSource = interactionSource,
                                onClick = {
                                    val type = if(typeBoolean) DataType.Schedule.name else DataType.Wangke.name
                                    vm.addData(type,title,info,remark ,"$start:00", "$end:00")
                                    showDialog = false
                                    MyToast("添加成功")
                                }
                            ) { Icon(Icons.Filled.Check, contentDescription = "") }
                        },
                        title = { Text("添加聚焦卡片") }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {

                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 3.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp, vertical = 5.dp),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        androidx.compose.material3.ListItem(
                            headlineContent = {  Text(text = title) },
                            overlineContent = { Text(text = remark) },
                            supportingContent = { Text(text = info) },
                            //  leadingContent = {  },
                            leadingContent = {
                                FilledTonalIconButton(
                                    onClick = { typeBoolean = !typeBoolean }
                                ) {
                                    Icon(painterResource(if(typeBoolean)R.drawable.calendar else R.drawable.net ), contentDescription = "Localized description",)
                                }}
                        )
                        androidx.compose.material3.ListItem(headlineContent = {
                            Text(text = "起始$start\n结束$end")
                        })
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                    ) {
                        TextField(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 15.dp),
                            value = title,
                            onValueChange = { title = it },
                            leadingIcon = { Icon( painterResource(R.drawable.title), contentDescription = "Localized description") },
                            label = { Text("标题" ) },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            colors = TextFieldDefaults.textFieldColors(focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
                                unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
                            ),
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                    ) {
                        TextField(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 15.dp),
                            value = info,
                            onValueChange = { info = it },
                            leadingIcon = { Icon( painterResource(R.drawable.info_i), contentDescription = "Localized description") },
                            label = { Text("内容" ) },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            colors = TextFieldDefaults.textFieldColors(focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
                                unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
                            ),
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                    ) {
                        TextField(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 15.dp),
                            value = remark,
                            onValueChange = { remark = it },
                            leadingIcon = { Icon( painterResource(R.drawable.format_italic), contentDescription = "Localized description") },
                            label = { Text("备注" ) },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            colors = TextFieldDefaults.textFieldColors(focusedIndicatorColor = Color.Transparent,
                                // 有焦点时的颜色，透明
                                unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
                            ),
                        )
                    }
                    //Spacer(modifier = Modifier.height(10.dp))
                    FilledTonalButton(onClick = { pickerBoolean = !pickerBoolean }) {
                        Text(text = if (pickerBoolean) "开始时间" else "结束时间")
                    }


                    start = "$startYear-${transTime(startMonth.toInt())}-${transTime(startDate.toInt())} ${transTime(startHour.toInt())}:${transTime(startMinute.toInt())}"
                    end = "$endYear-${transTime(endMonth.toInt())}-${transTime(endDate.toInt())} ${transTime(endHour.toInt())}:${transTime(endMinute.toInt())}"

                    Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start){
                        Row(modifier = Modifier.weight(.33f)){
                            WheelPicker(
                                listOf(GetDate.Date_yyyy.toInt()-1,GetDate.Date_yyyy.toInt(),GetDate.Date_yyyy.toInt()+1),
                                1,
                                3,
                                modifier = Modifier.fillMaxWidth(),
                                { index, item -> if(pickerBoolean) startYear = item.toString() else endYear = item.toString()}
                            ) { Text(text = "${it}年") }
                        }
                        Row(modifier = Modifier.weight(.33f)){
                            WheelPicker(
                                listOf(1,2,3,4,5,6,7,8,9,10,11,12),
                                GetDate.Date_MM.toInt() - 1,
                                3,
                                modifier = Modifier.fillMaxWidth(),
                                { index, item -> if(pickerBoolean) startMonth = item.toString() else endMonth = item.toString() }
                            ) { Text(text = "${it}月") }
                        }
                        Row(modifier = Modifier.weight(.33f)){
                            WheelPicker(
                                listOf(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31),
                                GetDate.Date_dd.toInt() - 1,
                                3,
                                modifier = Modifier.fillMaxWidth(),
                                { index, item -> if(pickerBoolean) startDate = item.toString() else endDate = item.toString() }
                            ) { Text(text = "${it}日") }
                        }
                        Row(modifier = Modifier.weight(.33f)){
                            WheelPicker(
                                listOf(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23),
                                GetDate.formattedTime_Hour.toInt() - 1,
                                3,
                                modifier = Modifier.fillMaxWidth(),
                                { index, item -> if(pickerBoolean) startHour = item.toString() else endHour = item.toString() }
                            ) { Text(text = "${it}时") }
                        }
                        Row(modifier = Modifier.weight(.33f)){
                            WheelPicker(
                                listOf(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59),
                                GetDate.formattedTime_Minute.toInt() - 1,
                                3,
                                modifier = Modifier.fillMaxWidth(),
                                { index,item -> if(pickerBoolean) startMinute = item.toString() else endMinute = item.toString() }
                            ) { Text(text = "${it}分") }
                        }
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> WheelPicker(
    data: List<T>,
    selectIndex: Int,
    visibleCount: Int,
    modifier: Modifier = Modifier,
    onSelect: (index: Int, item: T) -> Unit,
    content: @Composable (item: T) -> Unit,
) {
    BoxWithConstraints(modifier = modifier, propagateMinConstraints = true) {
        val density = LocalDensity.current
        val size = data.size
        val count = size * 10000
        val pickerHeight = maxHeight
        val pickerHeightPx = density.run { pickerHeight.toPx() }
        val pickerCenterLinePx = pickerHeightPx / 2
        val itemHeight = pickerHeight / visibleCount
        val itemHeightPx = pickerHeightPx / visibleCount
        val startIndex = count / 2
        val listState = rememberLazyListState(
            initialFirstVisibleItemIndex = startIndex - startIndex.floorMod(size) + selectIndex,
            initialFirstVisibleItemScrollOffset = ((itemHeightPx - pickerHeightPx) / 2).roundToInt(),
        )
        val layoutInfo by remember { derivedStateOf { listState.layoutInfo } }
        LazyColumn(
            modifier = Modifier,
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(listState),
        ) {
            items(count) { index ->
                val currIndex = (index - startIndex).floorMod(size)
                val item = layoutInfo.visibleItemsInfo.find { it.index == index }
                var currentsAdjust = 1f
                if (item != null) {
                    val itemCenterY = item.offset + item.size / 2
                    currentsAdjust = 0.75f + 0.25f * if (itemCenterY < pickerCenterLinePx) {
                        itemCenterY / pickerCenterLinePx
                    } else {
                        1 - (itemCenterY - pickerCenterLinePx) / pickerCenterLinePx
                    }
                    if (!listState.isScrollInProgress
                        && item.offset < pickerCenterLinePx
                        && item.offset + item.size > pickerCenterLinePx
                    ) {
                        onSelect(currIndex, data[currIndex])
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .graphicsLayer {
                            alpha = currentsAdjust
                            scaleX = currentsAdjust
                            scaleY = currentsAdjust
                            rotationX = (1 + currentsAdjust) * 180
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    content(data[currIndex])
                }
            }
        }
    }
}

private fun Int.floorMod(other: Int): Int = when (other) {
    0 -> this
    else -> this - floorDiv(other) * other
}

fun transTime(num : Int) : String {
    return if(num < 10) {
        "0$num"
    } else "$num"
}