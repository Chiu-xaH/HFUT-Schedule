package com.hfut.schedule.ui.MonetColor

import android.graphics.BitmapFactory
import androidx.datastore.preferences.core.edit
import androidx.palette.graphics.Palette
import com.hfut.schedule.App.MyApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() :
    BaseViewModel<IUiState, IUiEvent, MainIntent>() {
    override fun initUiState(): IUiState = object : IUiState {}

    override fun IUIChange.checkStateOrEvent() = this as? IUiState? to this as? IUiEvent

    override fun Flow<MainIntent>.handleIntent(): Flow<IUIChange> = merge(
        doIsInstance<MainIntent.UpdateThemeColor> { intent ->
            simpleFlow {
                if (intent.stickerUuid.isNotBlank() &&
                    MyApplication.context.dataStore.getOrDefault(StickerColorThemePreference)
                ) {
                    setPrimaryColor(uuid = intent.stickerUuid)
                }
            }
                .mapToUIChange { this }
                .defaultFinally()
        },
    )

    private suspend fun setPrimaryColor(uuid: String) {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        val stickerFilePath = stickerUuidToFile(uuid).path
        val l = Integer.max(options.outHeight, options.outWidth)
        options.apply {
            inSampleSize = l / 20
            inJustDecodeBounds = false
        }
        val bitmap = BitmapFactory.decodeFile(stickerFilePath, options)
        val swatch = suspendCancellableCoroutine { cont ->
            Palette.from(bitmap).generate {
                cont.resume(it?.dominantSwatch, onCancellation = null)
            }
        }
        bitmap.recycle()
        if (swatch == null) {
            return
        }
        delay(500)
        MyApplication.context.dataStore.edit { pref ->
            pref[ThemeNamePreference.key] = ThemeNamePreference.CUSTOM_THEME_NAME
            pref[CustomPrimaryColorPreference.key] = Integer.toHexString(swatch.rgb)
        }
    }
}