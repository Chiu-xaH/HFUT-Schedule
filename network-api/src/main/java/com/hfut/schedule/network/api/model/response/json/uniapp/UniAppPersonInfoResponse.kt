package com.hfut.schedule.network.api.model.response.json.uniapp

import com.google.gson.annotations.SerializedName
import com.hfut.schedule.network.api.model.response.json.shared.MultiLanguageBaseData

data class UniAppPersonInfoResponse(
    val data : List<UniAppPersonInfo>
)

data class UniAppPersonInfo(
    val studentId : Int,
    val code : String,
    val cultivateType : MultiLanguageBaseData,
    val grade : String,
    val stdType : MultiLanguageBaseData,
    val department : MultiLanguageBaseData,
    val major : MultiLanguageBaseData,
    val majorDirection : String?,
    @SerializedName("adminclass")
    val classes : MultiLanguageBaseData,
    val campus : MultiLanguageBaseData,
    val program : MultiLanguageBaseData,
    val stdStatus : MultiLanguageBaseData,
    val personInfo : UniAppDetailPersonInfo
)

data class UniAppDetailPersonInfo(
    val nameZh : String,
    val birthday : String,
    val idCardNumber : String,
    val gender : MultiLanguageBaseData,
    val politicalVisage : MultiLanguageBaseData,// 党派
    val nation : MultiLanguageBaseData,// 民族
    val contactInfo : UniAppContactInfo,
    val enterSchoolDate : String,
    val studyYears : Float,
    val needRegister : Boolean
)

data class UniAppContactInfo(
    val email : String?,
    val telephone : String?,
    val mobile : String?,
    val address : String?,
    val postcode : String?
)