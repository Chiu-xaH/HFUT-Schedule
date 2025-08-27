package com.hfut.schedule.logic.model.one

data class BuildingResponse(val data : List<BuildingBean>)


data class BuildingBean(val nameZh : String,val code : String)


data class ClassroomResponse(val data : ClassroomData)

data class ClassroomData(val records : List<ClassroomBean>)

data class ClassroomBean(
    val nameZh : String,
    val floor : Int,
    val roomTypeId : String,
    val seatsForLesson : Int,
    val enabled : Int,
//    val experiment : Int
)