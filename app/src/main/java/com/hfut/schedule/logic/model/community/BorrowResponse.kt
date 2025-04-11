package com.hfut.schedule.logic.model.community

data class BorrowResponse(val result : BorrowResult)

data class BorrowResult(val records : List<BorrowRecords>)

data class BorrowRecords(val bookName : String,
                         val author : String,
                         val outTime : String,
                         val returnTime : String?,
                         val callNumber : String)
