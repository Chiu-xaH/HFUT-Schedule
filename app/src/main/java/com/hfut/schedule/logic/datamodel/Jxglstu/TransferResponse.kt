package com.hfut.schedule.logic.datamodel.Jxglstu

data class TransferResponse(val data : List<TransferData>)
data class TransferData(val registrationConditions : String?,
                    //    val changeMajorBatch : changeMajorBatch,
                        val department : courseType,
                        val major : courseType,
                        val preparedStdCount : Int,val applyStdCount : Int)
data class changeMajorBatch(val nameZh : String,
                            val applyStartTime : String,
                            val applyEndTime : String,
                            val enrollStartTime : String,
                            val enrollEndTime : String,
                            val inGrade : String,
                            val applyLimitCount : Int)