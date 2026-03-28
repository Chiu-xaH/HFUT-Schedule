package com.hfut.schedule.logic.model

import com.google.gson.annotations.SerializedName

data class GithubIssueBean(
    val number : Long,
    @SerializedName("html_url")
    val url : String,
    val title : String,
    @SerializedName("pull_request")
    val pr: Any?,
    val labels : List<GithubIssueLabelBean>,
    @SerializedName("created_at")
    val createTime : String,
    @SerializedName("updated_at")
    val updatedTime : String,
    @SerializedName("state")
    val stateStr : String,
    val user : GithubIssueUser
) {
    fun getStateOpen() : Boolean = stateStr == "open"
}

data class GithubIssueLabelBean(
    val id : Long,
)


/*
O 新建问题 空List[]
|
O 评估
|----------------------------
|                           |
√ 评估通过 join plan         × 评估未通过 discard
|
O 设计 in design
|
O 开发 in dev
|
O 测试 verity
|
O 发布 resolved

 */


enum class GithubIssueLabel(val id : Long,val status : String) {
    JOIN_PLAN(10335171318,"评估通过"),
    DISCARD(6088920400,"评估未通过"),
    IN_DESIGN(10544808661,"设计"),
    IN_DEV(10356443852,"开发"),
    VERITY(10356445150,"测试"),
    RESOLVED(10356457015,"发布")
}


data class GithubIssueUser(
    @SerializedName("login")
    val name : String,
    @SerializedName("avatar_url")
    val photoUrl : String,
    val id : Long
)
// 帮我设计一个compose流程图UI，利用github issues模拟一个事务系统（评估那里分叉就行），直接画好图，遍历列表然后点亮存在的点就行，不需要考虑那么复杂