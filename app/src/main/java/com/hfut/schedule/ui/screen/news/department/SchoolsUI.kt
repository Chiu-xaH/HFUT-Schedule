package com.hfut.schedule.ui.screen.news.department


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.xah.common.logic.state.NetworkUiState
import com.hfut.schedule.logic.util.sys.Starter

import com.hfut.schedule.ui.component.container.CardListItem
import com.xah.common.ui.component.text.ScrollText
import com.hfut.schedule.ui.component.icon.DepartmentIcons
import com.hfut.schedule.ui.component.icon.departmentIcon
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.network.UrlImage
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.style.padding.InnerPaddingHeight
import kotlinx.coroutines.launch

@Composable
fun SchoolsUI(vm : NetWorkViewModel,innerPadding : PaddingValues? = null) {
    val uiState by vm.departmentsResp.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val refreshNetwork = suspend {
        if(uiState !is NetworkUiState.Success) {
            vm.getDepartments()
        }
    }

    LaunchedEffect(Unit) {
        refreshNetwork()
    }

    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        val result = (uiState as NetworkUiState.Success).data
        LazyColumn {
            item { if(innerPadding != null) InnerPaddingHeight(innerPadding,true) }
            items(result.size, key = {  result[it].name }) { index ->
                val item = result[index]
                var title = item.name
                val icon = departmentIcon(title)
                val subTitle = if (title.contains("（")) {
                    title.substringAfter("（").let {
                        if(it.isEmpty() || it.isBlank()) {
                            null
                        } else {
                            title = title.substringBefore("（")
                            it.replace("）", "")
                        }
                    }
                } else {
                    null
                }
                CardListItem(
                    headlineContent = { Text(text = title) },
                    supportingContent = {
                        subTitle?.let { Text(it) }
                    },
                    leadingContent = { DepartmentIcons(title) },
                    trailingContent = {
                        UrlImage(
                            item.iconUrl,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(50.dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                        )
                    },
                    modifier = Modifier.clickable {
                        scope.launch {
                            Starter.startWebUrlInner(context,item.url, icon = icon)
                        }
                    }
                )
            }
            item { if(innerPadding != null) InnerPaddingHeight(innerPadding,false) }
        }
    }


}