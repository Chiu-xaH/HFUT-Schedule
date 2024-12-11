package com.hfut.schedule.ui.utils.monet

sealed class MainIntent : IUiIntent {
    override val showLoading: Boolean = false

    data class UpdateThemeColor(val stickerUuid: String) : MainIntent()
}