package com.hfut.schedule.logic.model.wx

data class WXOrganizationTreeResponse(
    override val msg: String,
    override val data: List<WXOrganizationTreeBean>
) : WXBaseResponse()

data class WXOrganizationTreeBean(
    val id : String,
    val name : String,
    val isLeaf : Boolean,// 当为true，不用再向下找了
)

