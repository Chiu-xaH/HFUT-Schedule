package com.hfut.schedule.network.api.model.request.admission

enum class AdmissionType(val type : String,val description: String) {
    PLAN("zsjh","招生计划"),
    HISTORY("lnfs","历年分数"),
}