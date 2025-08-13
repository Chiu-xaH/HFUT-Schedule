package com.hfut.schedule.logic.model.wx

abstract class WXBaseResponse {
    abstract val msg : String
    abstract val data : Any?
}