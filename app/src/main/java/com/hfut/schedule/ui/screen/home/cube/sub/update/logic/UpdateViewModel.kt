package com.hfut.schedule.ui.screen.home.cube.sub.update.logic

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UpdateViewModel : ViewModel() {
    private val _downloadState = MutableStateFlow<DownloadResult>(
        DownloadResult.Prepare
    )
    val downloadState: StateFlow<DownloadResult> = _downloadState

    private var downloadJob: Job? = null

    fun startDownload(url : String,filename : String,context: Context) {
        // 避免重复下载
        if (downloadJob != null) return

        downloadJob = viewModelScope.launch {
            downloadFile(
                context = context,
                url = url,
                fileName = filename
            ).collect { result ->
                _downloadState.value = result
            }
        }
    }

    fun reset() {
        downloadJob?.cancel()
        downloadJob = null
        _downloadState.value = DownloadResult.Prepare
    }
}