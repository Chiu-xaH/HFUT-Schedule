package com.hfut.schedule.logic.model.supabase

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.gson.annotations.SerializedName
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.xah.common.ui.style.align.CenterScreen

data class SupabaseUserTrackRequest(
    @SerializedName("user_name")
    val username : String = getPersonInfo().getNameFinally(),
    @SerializedName("system_version")
    val systemVersion : Int = AppVersion.sdkInt,
    @SerializedName("student_id")
    val studentID : String = getPersonInfo().getStudentIdFinally() ?: "空",
    @SerializedName("campus")
    val campus : String = getPersonInfo().campus ?: "空",
    @SerializedName("department")
    val department : String = getPersonInfo().department ?: "空",
    @SerializedName("app_version_name")
    val appVersionName : String = AppVersion.getVersionName(),
    @SerializedName("app_version_code")
    val appVersionCode : Int = AppVersion.getVersionCode(),
    @SerializedName("device_name")
    val deviceName : String = AppVersion.deviceName,
    @SerializedName("is_harmony_next")
    val isHarmonyNext : Boolean = AppVersion.isHarmonyNext,
    @SerializedName("abi_type")
    val appAbi : String = AppVersion.getSplitType().name
)