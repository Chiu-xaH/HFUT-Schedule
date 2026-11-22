package com.hfut.schedule.ui.screen.home.search.function.school.alumni

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.xah.common.logic.util.EMPTY_STRING
import com.xah.common.ui.util.res
import kotlinx.coroutines.launch


@Composable
fun Alumni() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val title = res(R.string.navigation_label_alumni).asString()
    TransplantListItem(
        headlineContent = { Text(text = title) },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.person_book), contentDescription = EMPTY_STRING) },
        modifier = Modifier.clickable {
            scope.launch {
                Starter.startWebUrlInner(context,Constant.ALUMNI_URL,title, icon = R.drawable.person_book)
            }
        }
    )
}