package com.hfut.schedule.network.api.model.response.json.shared

abstract class ProgramBaseResponse {
    abstract val children : List<ProgramBaseResponse>
    abstract val type : MultiLanguageBaseData?
    abstract val requireInfo : Any?
    abstract val remark : String?
    abstract val reference : Boolean
    abstract val planCourses : List<Any>
}