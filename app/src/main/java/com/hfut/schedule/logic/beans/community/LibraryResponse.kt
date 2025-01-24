package com.hfut.schedule.logic.beans.community

data class LibraryResponse(val result : LibResult)

data class LibResult(val records : List<LibRecord>)

data class LibRecord(val callNumber : String?,
                     val name : String?,
                     val author : String?,
                     val publisher : String?,
                     val year : String?,
                     val place : String?,
                     val status_dictText : String?)
