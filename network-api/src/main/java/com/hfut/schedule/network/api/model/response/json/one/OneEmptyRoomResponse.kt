package com.hfut.schedule.network.api.model.response.json.one

data class OneBuildingResponse(
    val data : List<OneBuilding>
)

data class OneBuilding(
    val nameZh : String,
    val code : String,
    val id : Int
)

data class OneClassroomResponse(
    val data : OneClassroom
)

data class OneClassroom(
    val records : List<OneClassroomRecord>
)

data class OneClassroomRecord(
    val nameZh : String,
    val floor : Int,
    val roomTypeId : String,
    val seatsForLesson : Int,
    val enabled : Int,
)