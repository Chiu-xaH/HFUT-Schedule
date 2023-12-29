package com.hfut.schedule.logic.datamodel.Community

data class CourseTotalResponse(val result : CourseResult)

data class CourseResult(val courseBasicInfoDTOList : List<courseBasicInfoDTOList>)

data class courseBasicInfoDTOList(val courseName : String,val credit : Double)