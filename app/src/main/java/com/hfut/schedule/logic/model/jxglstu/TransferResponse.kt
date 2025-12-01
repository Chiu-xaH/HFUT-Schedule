package com.hfut.schedule.logic.model.jxglstu

data class TransferResponse(val data : List<TransferData>)
data class TransferData(val registrationConditions : String?,
                        val id : Int,
                        val changeMajorBatch : changeMajorBatch?,
                        val department : NameZh,
                        val major : NameZh,
                        val preparedStdCount : Int,
                        val applyStdCount : Int)
data class changeMajorBatch(val nameZh : String,
                            val applyStartTime : String,
                            val applyEndTime : String,
                            val submitStartTime : String,
                            val submitEndTime : String,
                            val enrollStartTime : String,
                            val enrollEndTime : String,
                            val inGrade : String,
                            val bulletin : String?,
                            val applyLimitCount : Int)

data class MyApplyResponse(val models : List<MyApplyModels>)
data class MyApplyModels(val changeMajorSubmit : TransferData,val applyStatus : String?,val id : Int)