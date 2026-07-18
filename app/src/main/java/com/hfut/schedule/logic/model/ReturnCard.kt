package com.hfut.schedule.logic.model

data class ReturnCard(
    val balance : String,
    val settle : String,
    val now : String,
    val autoTransLimit : String,
    val autoTransAmt : String,
    val name : String
)