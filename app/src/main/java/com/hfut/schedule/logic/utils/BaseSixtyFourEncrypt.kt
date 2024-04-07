package com.hfut.schedule.logic.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.util.Base64

object BaseSixtyFourEncrypt {
    @RequiresApi(Build.VERSION_CODES.O)
    fun encodeToBase64(input: String): String {
        return Base64.getEncoder().encodeToString(input.toByteArray(Charsets.UTF_8))
    }

}