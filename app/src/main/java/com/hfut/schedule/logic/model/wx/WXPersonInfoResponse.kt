package com.hfut.schedule.logic.model.wx

data class WXPersonInfoResponse(
    override val msg: String,
    override val data: WXPersonInfoBean
) : WXBaseResponse()

data class WXPersonInfoBean(
    val qq : String?,
    val mail : String?,
    val orgId : String,
    val officePhone : String?,
    val phone : String?,
    val avatorUrl : String?,
)
