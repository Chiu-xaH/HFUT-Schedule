package com.hfut.schedule.logic.model

data class NewsResponse(val title : String,
                        val date : String,
                        val link : String)

data class AcademicNewsResponse(val news : List<NewsResponse>,val totalPage : Int)


enum class AcademicType(val type : String,val title : String) {
    TEACHING("tzgg/jxbzl","教学布置"),
    IETP("tzgg/cxcyl","创新创业"),
    OTHER("tzgg/qt","其他"),
    DOCUMENT_DOWNLOAD("wdxz","文档下载"),
    NATIONAL_RULES("gzzd/gjgzzd","国家规章制度"),
    STUDY_RULES("gzzd/xxgzzd","学习规章制度"),
    PROVINCE_RULES("gzzd/sjgzzd","省级规章制度"),
}

enum class AcademicXCType(val type : Int,val title : String) {
    TEACHING(1177,"教学安排"),
    EXAM(1178,"考试信息"),
    STATUS(1179,"学籍管理"),
    IETP(1183,"创新创业"),
    COMPETITION(1184,"学科竞赛"),

    NEWS(573,"新闻公告"),
    DYNAMIC_NEWS(713,"新闻动态"),
    NOTIFICATION(714,"通知公告"),


    CALENDAR(1180,"校历"),

    OFFICE(2186,"办公人员"),
    OFFICE_TEACHING(2187,"教学秘书"),
    // 规章制度
    DOCUMENT_ON(1143,"上级文件"),
    DOCUMENT_SCHOOL(1144,"校内文件"),
    // 文档下载
    DOWNLOAD_DOCUMENT_ACADEMIC(1145,"教务管理"),
    DOWNLOAD_DOCUMENT_PRACTICE(1146,"实践教学"),
    DOWNLOAD_DOCUMENT_IETP(1147,"创新教学"),
    DOWNLOAD_DOCUMENT_QUALITY(1148,"质量管理"),
    DOWNLOAD_DOCUMENT_LAB(1149,"实验教学"),
    DOWNLOAD_DOCUMENT_WORK(1150,"办事流程"),
}