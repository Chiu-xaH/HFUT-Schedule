package com.hfut.schedule.ui.MonetColor

sealed class MainIntent : IUiIntent {
    override val showLoading: Boolean = false

    data class UpdateThemeColor(val stickerUuid: String) : MainIntent()
}