package com.hfut.schedule.ui.MonetColor

import com.hfut.schedule.MyApplication
import java.io.File

val STICKER_DIR: String = File(MyApplication.context.filesDir.path, "Sticker").path

fun stickerUuidToFile(uuid: String): File = File(STICKER_DIR, uuid)


