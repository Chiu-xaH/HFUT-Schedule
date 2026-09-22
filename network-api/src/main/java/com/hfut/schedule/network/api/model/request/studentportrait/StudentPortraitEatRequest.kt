package com.hfut.schedule.network.api.model.request.studentportrait

import com.google.gson.annotations.SerializedName

data class StudentPortraitEatRequest(
    val year: String? = "all",
    val env: String = "prod",
    @SerializedName("nj") val enrollmentYear: Int? = null,
    @SerializedName("xdnx") val studyDurationYears: String? = null
)
