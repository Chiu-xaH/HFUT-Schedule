package com.hfut.schedule.logic.enumeration

enum class WorkSearchType(val code: Int, val description: String,val url : String) {
    ALL(0,"全部",""),
    JOB_FAIR(1, "双选会","jobfair?id="),
    JOB_FAIR_COMPANY(2, "双选会参会单位","company?company_id="),
    PRESENTATION(3, "宣讲会","career?id="),
    ONLINE_RECRUITMENT(4, "在线招聘","online?id="),
    POSITION(5, "职位","job?id="),
    ANNOUNCEMENT(6, "通知公告","news?id=")
}

