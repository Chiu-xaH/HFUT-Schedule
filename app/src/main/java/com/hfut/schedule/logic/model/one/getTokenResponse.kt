package com.hfut.schedule.logic.model.one

data class getTokenResponse(val msg : String,
                            val data : getTokendatas
)
data class  getTokendatas(val access_token : String)
