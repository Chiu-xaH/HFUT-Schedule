package com.hfut.schedule.ui.nav.window

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.TimeTableItem
import com.hfut.schedule.ui.screen.home.calendar.timetable.ui.TimeTablePreview
import com.hfut.schedule.ui.nav.window.base.FloatingWindow
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.align.CenterScreen
import com.xah.common.ui.util.text
import com.xah.container.component.base.SharedContent
import com.xah.container.model.ContentStrategy

data class TimeTablePreviewWindow(
    val items: List<List<TimeTableItem>>,
    val currentWeek: Int,
    val onItemClick : (Int) -> Unit,
) : FloatingWindow() {
    override val key = KEY

    override val title = text("第${currentWeek}周")

    companion object {
        const val KEY = "time_table_preview"
    }

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    override fun BoxScope.Content() {
        val semester by produceState<Int?>(initialValue = null) {
            value = SemesterParser.getSemester()
        }
        CenterScreen {
            SharedContent(
                key = KEY,
                contentStrategy = ContentStrategy.Layer(isFloating = true),
                shape = MaterialTheme.shapes.largeIncreased,
                modifier = Modifier
                    .padding(APP_HORIZONTAL_DP)
                    .navigationBarsPadding()
                    .statusBarsPadding()
            ) {
                TimeTablePreview(
                    items = items, // 一周课程,
                    currentWeek = currentWeek,
                    title = semester?.let { SemesterParser.parseSemesterSimply(it) } ?: title.asString(),
                    onItemClick = onItemClick
                )
            }
        }
    }
}