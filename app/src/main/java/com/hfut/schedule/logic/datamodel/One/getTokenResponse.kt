package com.hfut.schedule.logic.datamodel.One

data class getTokenResponse(val msg : String,
                            val data : getTokendatas
)
data class  getTokendatas(val access_token : String)

//{
//  "code": 1,
//  "msg": "success",
//  "data": {
//    "access_token": "AT-1306418-4TWIGAXcW6uCk9B7ZDZHlJI1QEOct9Ci",
//    "expires_in": "28800"
//  }
//}