package com.xah.shared

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class JxglstuCourseGroup(
    val date: String,
    val courses: List<Course>
) : Parcelable

@Parcelize
data class Course(
    val dateTime : Pair<String, String>,
    val place : String?,
    val courseName : String
) : Parcelable