package com.hfut.schedule.network.api.model.response.html

data class JxglstuStudentId(
    val studentId: Int,
    val type: String? = null,
    val department: String? = null,
    val major: String? = null,
    val direction: String? = null,
    val level: String? = null,
    val status: String? = null
)