package com.hfut.schedule.network.api.model.response.json.uniapp

data class UniAppEmptyClassroomResponse(
    val data : UniAppEmptyClassroomData
)

data class UniAppEmptyClassroomData(
    val data : List<UniAppEmptyClassroom>
)

data class UniAppEmptyClassroom(
    val id : Int,
    val nameZh : String,
    val campusNameZh : String,
    val roomOccupationInfoVms : List<UniAppEmptyClassroomLesson>?
)

data class UniAppEmptyClassroomLesson(
    val date : String,
    // HH-MM
    val startTimeString : String,
    val endTimeString : String,
    val activityType : String,
    val activityName : String,
    val teacherName : String?
)

enum class ClassroomOccupiedCause(
    val activityType : String,
    val description: String
) {
    BORROWED("RoomBorrow","借用"),
    IN_LESSON("Lesson","上课"),
    EXAM("Exam","考试")
}
