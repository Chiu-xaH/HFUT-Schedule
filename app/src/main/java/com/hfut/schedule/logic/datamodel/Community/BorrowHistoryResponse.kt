package com.hfut.schedule.logic.datamodel.Community

data class BorrowHistoryResponse(val result : BorrowHistoryResult)

data class BorrowHistoryResult(val records : List<BorrowHistoryRecords>)

data class BorrowHistoryRecords(val bookName : String,
                                val author : String,
                                val outTime : String,
                                val returnTime : String,
                                val callNumber : String)
