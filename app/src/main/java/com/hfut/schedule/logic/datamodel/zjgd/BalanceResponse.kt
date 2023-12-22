package com.hfut.schedule.logic.datamodel.zjgd

data class BalanceResponse(val data : Balancedatas)

data class Balancedatas(val card : List<card>)

data class  card (val db_balance : Int,val unsettle_amount : Int)
