package com.hfut.schedule.logic.model.jxglstu

data class TransferPostResponse(val result : Boolean,val errors : List<ErrorText>)
data class ErrorText(val textZh : String)