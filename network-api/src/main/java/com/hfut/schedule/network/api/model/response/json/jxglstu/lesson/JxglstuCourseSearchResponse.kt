package com.hfut.schedule.network.api.model.response.json.jxglstu.lesson

data class JxglstuCourseSearchResponse(
    val data : List<JxglstuCourseSearchData>
)

data class JxglstuCourseSearchData(
    val lesson : JxglstuLesson
)
