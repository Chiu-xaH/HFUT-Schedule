package com.hfut.schedule.network.api.model.response.json.jxglstu.select

import com.hfut.schedule.network.api.model.response.json.jxglstu.JxglstuPostErrorMessage

data class JxglstuSelectCoursePostResponse(
    val errorMessage : JxglstuPostErrorMessage?,
    val success : Boolean
)