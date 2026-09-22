package com.hfut.schedule.network.api.model.response.json.studentportrait

import com.google.gson.annotations.SerializedName

data class StudentPortraitEatTop3Response(
    val success: Boolean?,
    val message: String?,
    val code: Int?,
    val value: StudentPortraitEatTop3Value?
)

data class StudentPortraitEatTop3Value(
    val myData: List<StudentPortraitEatAmount>?,
    val allData: List<StudentPortraitEatAmount>?
)

data class StudentPortraitEatAmount(
    @SerializedName("jydd") val place: String?,
    @SerializedName("jyze") val amount: Double?
)

data class StudentPortraitEatAnnualResponse(
    val success: Boolean?,
    val message: String?,
    val code: Int?,
    val value: List<StudentPortraitEatAnnualRecord>?
)

data class StudentPortraitEatAnnualRecord(
    @SerializedName("xn") val academicYear: String?,
    @SerializedName("myze") val myAmount: Double?,
    @SerializedName("avgze") val averageAmount: Double?
)

data class StudentPortraitEatFrequencyResponse(
    val success: Boolean?,
    val message: String?,
    val code: Int?,
    val value: StudentPortraitEatFrequencyValue?
)

data class StudentPortraitEatFrequencyValue(
    val my: List<StudentPortraitEatFrequencyRecord>?,
    val all: List<StudentPortraitEatFrequencyRecord>?
)

data class StudentPortraitEatFrequencyRecord(
    @SerializedName("jydd") val place: String?,
    @SerializedName("count") val count: Int?
)

data class StudentPortraitEatOneDayResponse(
    val success: Boolean?,
    val message: String?,
    val code: Int?,
    val value: StudentPortraitEatOneDay?
)

data class StudentPortraitEatOneDay(
    @SerializedName("myFirstJysj") val myFirstTime: String?,
    @SerializedName("myFirstJydd") val myFirstPlace: String?,
    @SerializedName("myJycs") val myCount: Int?,
    @SerializedName("allFirstJysj") val allFirstTime: String?,
    @SerializedName("allFirstJydd") val allFirstPlace: String?,
    @SerializedName("allJycs") val allCount: Int?
)

data class StudentPortraitEatAverageResponse(
    val success: Boolean?,
    val message: String?,
    val code: Int?,
    val value: StudentPortraitEatAverage?
)

data class StudentPortraitEatAverage(
    @SerializedName("yearAvgMy") val myYearAverage: Double?,
    @SerializedName("yearAvgAll") val allYearAverage: Double?,
    @SerializedName("monthAvgMy") val myMonthAverage: Double?,
    @SerializedName("monthAvgAll") val allMonthAverage: Double?,
    @SerializedName("dayAvgMy") val myDayAverage: Double?,
    @SerializedName("dayAvgAll") val allDayAverage: Double?
)

data class StudentPortraitEatYearAverage(
    val academicYear: String,
    val average: StudentPortraitEatAverage
)

data class StudentPortraitEatData(
    val top3: StudentPortraitEatTop3Value,
    val annual: List<StudentPortraitEatAnnualRecord>,
    val frequency: StudentPortraitEatFrequencyValue,
    val oneDay: StudentPortraitEatOneDay,
    val average: StudentPortraitEatAverage,
    val yearAverages: List<StudentPortraitEatYearAverage>,
    val enrollmentYear: Int?
)
