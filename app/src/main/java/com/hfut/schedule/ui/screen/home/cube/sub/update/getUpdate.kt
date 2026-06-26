package com.hfut.schedule.ui.screen.home.cube.sub.update

import com.hfut.schedule.logic.model.GiteeReleaseResponse
import com.hfut.schedule.logic.util.dev.ExceptionHelper.getKeyStackTraceDesc
import com.xah.common.logic.state.NetworkUiState
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.bsdiffs.model.Patch
import com.xah.bsdiffs.util.parsePatchFile
import com.xah.common.logic.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

suspend fun getUpdates(vm : NetWorkViewModel) : GiteeReleaseResponse? = withContext(Dispatchers.IO) {
    val update = vm.giteeUpdatesResp.state.first { it !is NetworkUiState.Loading }
    return@withContext when(update) {
        is NetworkUiState.Error -> {
            GiteeReleaseResponse(name = "检查更新错误 ${update.code}", "无法检查更新 请留意软件内提醒\n" + update.exception?.let { getKeyStackTraceDesc(it) } ,emptyList())
        }
        is NetworkUiState.Success -> {
            val data = update.data
            if(data.name == AppVersion.getVersionName()) {
                null
            } else {
                data
            }
        }
        else -> {
            GiteeReleaseResponse(name = "检查更新错误 prepare", "无法检查更新 请留意软件内提醒" ,emptyList())
        }
    }
}

suspend fun getPatchVersions(vm : NetWorkViewModel) : List<Patch> = withContext(Dispatchers.IO) {
    val update = vm.giteeUpdatesResp.state.first()
    return@withContext try {
         if(update is NetworkUiState.Success) {
            val data = update.data.assets.filter { it.name.endsWith(".patch") }
            data.mapNotNull { e ->
                parsePatchFile(e.name)
            }
        } else {
            emptyList()
        }
    } catch (e : Exception) {
        LogUtil.error(e)
        emptyList()
    }
}
