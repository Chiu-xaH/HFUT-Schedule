package com.hfut.schedule.logic.model.wx

data class WXClassmatesResponse(
    override val msg: String,
    override val data: WXClassmatesBean
) :  WXBaseResponse()

data class WXClassmatesBean(
    val records : List<WXClassmate>,
    val total : Int,
)
data class WXClassmate(
    val id : String,
    val name : String,
    val photoUrl : String?,
    val phone : String?,
    val email : String?
)
