package com.hfut.schedule.logic.model.huixin

data class BalanceResponse(val data : Balancedatas)

data class Balancedatas(val card : List<card>)

data class  card (val db_balance : Int,
                  val unsettle_amount : Int,
                  val autotrans_limite : Int,
                  val autotrans_amt : Int,
                  val name : String,
                  val account : String)

data class ReturnCard(val balance : String,
                      val settle : String,
                      val now : String,
                      val autotrans_limite : String,
                      val autotrans_amt : String,
                      val name : String)