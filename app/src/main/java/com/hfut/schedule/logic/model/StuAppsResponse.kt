package com.hfut.schedule.logic.model

import com.google.gson.annotations.SerializedName

data class StuAppsResponse(
    val data: List<StuAppsBean>
)//  "msg": "成功",
data class StuAppsBean(
    @SerializedName("FWDZ")
    val url : String,
    @SerializedName("YYMC")
    val name : String,
    @SerializedName("YYID")
    val iconId : String
)
/*
https://stu.hfut.edu.cn/xsfw/sys/xggzptapp/modules/pubWork/appIcon.do?id=4991583903533629
{
      "FWDZ": "/sys/xshdapp/*default/index.do",
      "YYID": "4805589443814173",
      "YYMC": "学生活动"
    }
 */