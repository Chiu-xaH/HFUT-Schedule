package com.hfut.schedule.logic.datamodel.Community

data class ProgramResponse(val result : ProgramResult)

data class ProgramResult(
    val majorName : String,
    val totalCreditDone : Int,
    val totalCreditRequirement : Int,
  //  val trainingProgramCategoryDTOList : List<trainingProgramCategoryDTOList>
)

//data class trainingProgramCategoryDTOList()
