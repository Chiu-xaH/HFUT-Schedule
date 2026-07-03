package com.hfut.schedule.viewmodel.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfut.schedule.logic.model.huixin.ReturnCard
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.WebInfo
@Deprecated("使用GlobalUiStateHolder替代")
class UIViewModel : ViewModel()  {
    var cardValue by mutableStateOf<ReturnCard?>(null)
    @Deprecated("LiveData已不再作为本项目主力，请使用StateFlow或封装好的UiStateHolder")
    var electricValue = MutableLiveData<String?>()
    @Deprecated("LiveData已不再作为本项目主力，请使用StateFlow或封装好的UiStateHolder")
    var webValue = MutableLiveData<WebInfo>()

    // 挪到Event中
    var specialWorkDayChange by mutableIntStateOf(0)
}
// 呱呱、supabase的返回图标