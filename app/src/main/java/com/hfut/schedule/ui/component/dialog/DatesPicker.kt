package com.hfut.schedule.ui.component.dialog

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.ScrollAxisRange
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.verticalScrollAxisRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.ui.screen.home.calendar.common.numToChinese
import com.xah.common.logic.util.LogUtil
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import java.time.LocalDate
import java.time.YearMonth

/**
 * 多选日期
 * rememberDatesPickerState暂时对外隐藏，如果需要更多功能时再引入，
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatesPickerDialog(
    modifier: Modifier = Modifier,
    initialDates: List<String> = emptyList(),
    confirmEnabled: Boolean = true,
    onConfirm: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberDatesPickerState(initialDates)
    DatesPickerDialog(
        modifier = modifier,
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        state = state,
        confirmEnabled = confirmEnabled
    )
}

// 浮窗
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatesPickerDialog(
    modifier: Modifier = Modifier,
    state: DatesPickerState = rememberDatesPickerState(),
    confirmEnabled: Boolean = true,
    onConfirm: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    DatePickerDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        confirmButton = {
            TextButton(
                enabled = state.selectedDates.isNotEmpty() && confirmEnabled,
                onClick = {
                    onConfirm(
                        state.selectedDates.map {
                            it.format(DateTimeManager.formatter_YYYY_MM_DD)
                        }
                    )
                    onDismiss()
                },
            ) {
                Text("完成")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
            ) {
                Text("取消")
            }
        },
    ) {
        Box {
            Box(modifier = Modifier.padding(APP_HORIZONTAL_DP)) {
                DatesPicker(state = state)
            }
        }
    }
}

// UI
@Composable
fun DatesPicker(
    state: DatesPickerState,
    modifier: Modifier = Modifier,
) {
    val colors = DatePickerDefaults.colors()
    val monthsListState = rememberLazyListState(state.displayedMonthIndex)

    val displayedMonthIndex = state.displayedMonthIndex
    LaunchedEffect(displayedMonthIndex) {
        if (monthsListState.firstVisibleItemIndex != displayedMonthIndex) {
            monthsListState.scrollToItem(displayedMonthIndex)
        }
    }

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter,
    ) {
        val DayCellTouchSize = remember { 48.dp }
        val DayCellContentRatio = remember { 40f / DayCellTouchSize.value }
        val MinDayCellTouchSize = remember { 32.dp }
        val MaxCalendarWidth = remember { 336.dp }
        val DayTextNarrowThreshold = remember { 36.dp }

        val availableWidth = if (maxWidth == Dp.Infinity) MaxCalendarWidth else maxWidth
        val touchSize = (availableWidth / 7f).coerceIn(MinDayCellTouchSize, DayCellTouchSize)
        val cellSize = touchSize * DayCellContentRatio
        val contentWidth = minOf(availableWidth, MaxCalendarWidth)
        val dayTextStyle = if (touchSize < DayTextNarrowThreshold) {
            MaterialTheme.typography.bodyMedium
        } else {
            MaterialTheme.typography.bodyLarge
        }
        val ContainerMaxHeight = remember { 568.dp }

        Column(
            modifier = Modifier.width(contentWidth)
        ) {
            // 星期行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val weekdayLabels = remember {
                    (1..7).map { i ->
                        numToChinese(i)
                    }
                }

                weekdayLabels.forEach { label ->
                    Box(
                        modifier = Modifier
                            .size(touchSize)
                            .clearAndSetSemantics { },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.weekdayContentColor,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
            MonthsList(
                state = state,
                lazyListState = monthsListState,
                touchSize = touchSize,
                cellSize = cellSize,
                dayTextStyle = dayTextStyle,
                modifier = Modifier.heightIn(max = ContainerMaxHeight),
            )
        }
    }
}

// 月份列表
@Composable
private fun MonthsList(
    state: DatesPickerState,
    lazyListState: LazyListState,
    touchSize: Dp,
    cellSize: Dp,
    dayTextStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    val today = remember { DateTimeManager.getToday() }
    val colors = DatePickerDefaults.colors()

    ProvideTextStyle(MaterialTheme.typography.bodyLarge) {
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .semantics {
                    verticalScrollAxisRange = ScrollAxisRange(value = { 0f }, maxValue = { 0f })
                },
            state = lazyListState,
        ) {
            items(state.totalMonths) { index ->
                Column(modifier = Modifier.fillParentMaxWidth()) {
                    // 月份小标题
                    val month = monthAt(index, state.yearRange)
                    Text(
                        text = with(month) { "${year}年${monthValue}月" },
                        modifier = Modifier.padding(PaddingValues(start = 24.dp, top = 20.dp, bottom = 8.dp)),
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.subheadContentColor,
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectableGroup()
                    ) {
                        val RowsPerMonth = remember { 6 }
                        for (row in 0 until RowsPerMonth) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                val offset = (month.atDay(1).dayOfWeek.value + 7 - 1) % 7
                                for (col in 0 until 7) {
                                    val dayOfMonth = row * 7 + col - offset + 1
                                    if (dayOfMonth in 1..month.lengthOfMonth()) {
                                        val date = month.atDay(dayOfMonth)
                                        key(date) {
                                            SelectedDayCell(
                                                date = date,
                                                state = state,
                                                today = today,
                                                touchSize = touchSize,
                                                cellSize = cellSize,
                                                dayTextStyle = dayTextStyle,
                                            )
                                        }
                                    } else {
                                        Spacer(Modifier.size(touchSize))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    // 滚动位置同步
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .collect { state.onMonthIndexChanged(it) }
    }
}

// 单个日期格
@Composable
private fun SelectedDayCell(
    date: LocalDate,
    state: DatesPickerState,
    today: LocalDate,
    touchSize: Dp,
    cellSize: Dp,
    dayTextStyle: TextStyle,
) {
    val selected = state.selectedDates.contains(date)
    val isToday = date == today
    val colors = DatePickerDefaults.colors()

    val containerColor by animateColorAsState(
        targetValue = when {
            selected -> colors.selectedDayContainerColor
            else -> Color.Transparent
        }
    )
    val contentColor by animateColorAsState(
        targetValue = when {
            selected -> colors.selectedDayContentColor
            isToday -> colors.todayContentColor
            else -> colors.dayContentColor
        }
    )

    Box(
        modifier = Modifier
            .size(touchSize)
            .clip(CircleShape)
            .selectable(
                selected = selected,
                role = Role.Checkbox,
                onClick = {
                    state.toggle(date)
                }
            )
        ,
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(cellSize)
                .clip(CircleShape)
                .background(containerColor)
                .then(
                    if (isToday && !selected) {
                        Modifier.border(
                            border = BorderStroke(1.dp, colors.todayDateBorderColor),
                            shape = CircleShape,
                        )
                    } else {
                        Modifier
                    }
                )
                .clearAndSetSemantics { },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = dayTextStyle,
                color = contentColor,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun rememberDatesPickerState(
    initialDates: List<String> = emptyList(),
) = remember {
    DatesPickerState(initialDates)
}

@Stable
class DatesPickerState (initialDates: List<String>) {
    val yearRange: IntRange = 1900..2100
    private val firstMonth = YearMonth.of(yearRange.first, 1)
    val totalMonths = (yearRange.last - yearRange.first + 1) * 12
    private val _selectedDates = mutableStateListOf<LocalDate>().apply {
        val finalDates = initialDates.mapNotNull {
            try {
                LocalDate.parse(it, DateTimeManager.formatter_YYYY_MM_DD)
            } catch (e: Exception) {
                LogUtil.error(e)
                null
            }
        }
        addAll(finalDates.distinct().sorted())
    }
    val selectedDates: List<LocalDate> = _selectedDates
    var displayedMonth by mutableStateOf(
        (
                initialDates.firstOrNull()?.let {
                    YearMonth.from(LocalDate.parse(it))
                } ?: YearMonth.now()
        ).coerceIn(yearRange)
    )
        private set
    val displayedMonthIndex = displayedMonth.indexIn(yearRange)

    // 选择
    fun toggle(date: LocalDate) {
        if (!_selectedDates.remove(date)) {
            _selectedDates.add(date)
            _selectedDates.sort()
        }
    }

    // 清空
    fun clear() = _selectedDates.clear()

    private fun showMonth(month: YearMonth) {
        displayedMonth = month.coerceIn(yearRange)
    }

    fun onMonthIndexChanged(index: Int) {
        showMonth(firstMonth.plusMonths(index.toLong()))
    }
}

private fun YearMonth.indexIn(yearRange: IntRange): Int = (year - yearRange.first) * 12 + (monthValue - 1)

private fun monthAt(index: Int, yearRange: IntRange): YearMonth = YearMonth.of(yearRange.first + index / 12, index % 12 + 1)

private fun YearMonth.coerceIn(yearRange: IntRange): YearMonth = when {
    year < yearRange.first -> YearMonth.of(yearRange.first, 1)
    year > yearRange.last -> YearMonth.of(yearRange.last, 12)
    else -> this
}