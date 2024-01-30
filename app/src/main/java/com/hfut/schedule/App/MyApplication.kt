package com.hfut.schedule.App

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    companion object {
        lateinit var context: Context
        const val version = "3.1"
        const val EleURL = "http://172.31.248.26:8988/"
        const val CommunityURL = "https://community.hfut.edu.cn/"
        const val ZJGDBillURL = "http://121.251.19.62/"
        const val LePaoYunURL = "http://210.45.246.53:8080/"
        const val AlipayCardURL = "alipays://platformapi/startapp?appId=20000067&url=https://ur.alipay.com/_4kQhV32216tp7bzlDc3E1k"
        const val AlipayHotWaterURL = "alipays://platformapi/startapp?appId=20000067&url=https://puwise.com/p?i=x4nNVOVYX6kp"
        const val OneURL = "https://one.hfut.edu.cn/"
        const val LoginURL = "https://cas.hfut.edu.cn/"
        const val JxglstuURL = "http://jxglstu.hfut.edu.cn/eams5-student/"
        const val MyURL = "https://chiu-xah.github.io/"
        const val XuanquURL = "http://39.106.82.121/"
        const val RedirectURL = "https://cas.hfut.edu.cn/cas/login?service=http%3A%2F%2Fjxglstu.hfut.edu.cn%2Feams5-student%2Fneusoft-sso%2Flogin&exception.message=A+problem+occurred+restoring+the+flow+execution+with+key+%27e1s1%27"
        const val NullLoginCommunity = "{\"result\": {\"token\": \"\"}}"
        const val NullExams = "{\"result\": {\"examArrangementList\": []}}"
        const val NullTotal = "{\n" +
                "  \"result\": {\n" +
                "    \"startTime\": [\n" +
                "      \"08:00\",\n" +
                "      \"09:00\",\n" +
                "      \"10:10\",\n" +
                "      \"11:10\",\n" +
                "      \"14:00\",\n" +
                "      \"15:00\",\n" +
                "      \"16:00\",\n" +
                "      \"17:00\",\n" +
                "      \"19:00\",\n" +
                "      \"20:00\",\n" +
                "      \"21:00\"\n" +
                "    ],\n" +
                "    \"endTime\": [\n" +
                "      \"08:50\",\n" +
                "      \"09:50\",\n" +
                "      \"11:00\",\n" +
                "      \"12:00\",\n" +
                "      \"14:50\",\n" +
                "      \"15:50\",\n" +
                "      \"16:50\",\n" +
                "      \"17:50\",\n" +
                "      \"19:50\",\n" +
                "      \"20:50\",\n" +
                "      \"21:50\"\n" +
                "    ],\n" +
                "    \"xn\": \"2023-2024\",\n" +
                "    \"xq\": \"2\",\n" +
                "    \"start\": \"2024-02-26 00:00:00\",\n" +
                "    \"currentWeek\": 17,\n" +
                "    \"end\": \"2024-07-13 00:00:00\",\n" +
                "    \"totalWeekCount\": 20,\n" +
                "    \"courseBasicInfoDTOList\": []\n" +
                "  }\n" +
                "}"
        const val NullBorrow = "{\"result\": {\"records\": []}}"
        const val NullProgram = "{\"result\":{\"majorName\":\"培养方案\",\"totalCreditDone\":0,\"totalCreditRequirement\":0,\"trainingProgramCategoryDTOList\":[]}}"
        const val NullCardblance = "{\"data\":{\"card\":[{ \"db_balance\":0000}]}}"
        const val NullMonthYue = "{\"data\":{ \"income\":0.0, \"expenses\":0.0 },}"
        const val NullLePao = "{\"msg\" : \"获取失败\",\"data\" : {\"distance\" : \"0.0\"}}"
        const val NullSearch = "{\"data\":{\"未查找到数据,输入格式是否正确?\":0.0} }"
        const val NullGrades = "{\n" +
                "    \"result\":{\n" +
                "        \"gpa\": 0.00,\n" +
                "        \"classRanking\":\"登录失效\",\n" +
                "        \"majorRanking\":\"登录失效\",\n" +
                "        \"scoreInfoDTOList\":[]\n" +
                "        }\n" +
                "    }"
        const val NullBill = "{\n" +
                "    \"data\":{\n" +
                "        \"records\":[],\n" +
                "        \"pages\":0\n" +
                "        }\n" +
                "    }"
        const val NullExam =  "        <tbody>\n" +
                "          <tr>\n" +
                "            <td>空</td>\n" +
                "            <td class=\"time\">空</td>\n" +
                "            <td>空</td>\n" +
                "          </tr>\n" +
                "        </tbody>"

        const val NullDatum = "{\n" +
                "    \"result\" : {\n" +
                "        \"lessonList\" : [\n" +
                "            {\n" +
                "                \"courseName\" : \"\",\n" +
                "                \"suggestScheduleWeekInfo\" : \"\",\n" +
                "                \"courseTypeName\" : \"\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"scheduleList\" : [\n" +
                "            {\n" +
                "                \"lessonId\" : 0,\n" +
                "                \"room\" : {\n" +
                "                    \"nameZh\" : \"2\"\n" +
                "                },\n" +
                "                \"weekday\" : 0,\n" +
                "                \"personName\" : \"\",\n" +
                "                \"weekIndex\" : 0,\n" +
                "                \"startTime\" : 0000,\n" +
                "                \"endTime\" : 0000,\n" +
                "                \"date\" : \"\",\n" +
                "                \"lessonType\" : \"\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"scheduleGroupList\" : [\n" +
                "            {\n" +
                "                \"lessonId\" : 0,\n" +
                "                \"stdCount\" : 0\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}\n" +
                "\n"

        const val NullMy = "{\n" +
                "    \"Lessons\" : {\n" +
                "        \"MyList\" : [\n" +
                "        {\n" +
                "            \"title\" : \"\",\n" +
                "            \"time\"  : \"01-01\",\n" +
                "            \"info\"  : \"\"\n" +
                "        }\n" +
                "    ],\n" +
                "        \"Schedule\" : [\n" +
                "            {\n" +
                "            \"title\" : \"\",\n" +
                "            \"time\" : \"01-01\",\n" +
                "            \"info\"  : \"\"\n" +
                "        }\n" +
                "]\n" +
                "},\n" +
                "    \"SettingsInfo\" : {\n" +
                "        \"version\" : \"正在获取\",\n" +
                "        \"title\" : \"开发者接口\",\n" +
                "        \"info\"  : \"本接口在不更新APP前提下可实时更新信息\"\n" +
                "    },\n" +
                "     \"semesterId\" : \"234\",\n" +
                "    \"Notifications\" : [\n" +
                "        {\n" +
                "            \"title\" : \"\",\n" +
                "            \"info\" : \"\",\n" +
                "            \"remark\" : \"\"\n" +
                "        }\n" +
                "    ]\n" +
                "}"

        const val NullLib = "{\n" +
                "    \"total\":9,\n" +
                "    \"content\":[\n" +
                "        {\n" +
                "            \"author\":\"\",\n" +
                "            \"callNo\":\"\",\n" +
                "            \"pubYear\":\"\",\n" +
                "            \"publisher\":\"\",\n" +
                "            \"title\":\"\"\n" +
                "            }\n" +
                "           ]\n" +
                "}"
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}