package com.hfut.schedule

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    companion object {
        lateinit var context: Context
        const val version = "2.5.2"
        const val EleURL = "http://172.31.248.26:8988/"
        const val ZJGDBillURL = "http://121.251.19.62/"
        const val LePaoYunURL = "http://210.45.246.53:8080/"
        const val LibURL = "http://210.45.242.5:8080/"
        const val AlipayURL = "alipays://platformapi/startapp?appId=20000067&url=https://ur.alipay.com/_4kQhV32216tp7bzlDc3E1k"
        const val OneURL = "https://one.hfut.edu.cn/"
        const val LoginURL = "https://cas.hfut.edu.cn/"
        const val JxglstuURL = "http://jxglstu.hfut.edu.cn/eams5-student/"
        const val MyURL = "https://chiu-xah.github.io/"
        const val XuanquURL = "http://39.106.82.121/"
        const val RedirectURL = "https://cas.hfut.edu.cn/cas/login?service=http%3A%2F%2Fjxglstu.hfut.edu.cn%2Feams5-student%2Fneusoft-sso%2Flogin&exception.message=A+problem+occurred+restoring+the+flow+execution+with+key+%27e1s1%27"
        const val NullCardblance = "{\"data\":{\"card\":[{ \"db_balance\":0000}]}}"
        const val NullMonthYue = "{\"data\":{ \"income\":0.0, \"expenses\":0.0 },}"
        const val NullLePao = "{\"msg\" : \"获取失败\",\"data\" : {\"distance\" : \"0.0\"}}"
        const val NullSearch = "{\"data\":{\"未查找到数据,输入格式是否正确?\":0.0} }"
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
                "     \"semesterId\" : \"234\"\n" +
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