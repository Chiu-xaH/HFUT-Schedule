package com.hfut.schedule

import android.app.Application
import android.content.Context
import com.hfut.schedule.ui.DynamicColor.CatalogTheme
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    companion object {
        val DEFAULT_THEME = CatalogTheme.BLUE_THEME.name
        lateinit var context: Context
        const val AlipayURL = "alipays://platformapi/startapp?appId=20000067&url=https://ur.alipay.com/_4kQhV32216tp7bzlDc3E1k"
        const val OneURL = "https://one.hfut.edu.cn/"
        const val LoginURL = "https://cas.hfut.edu.cn/"
        const val JxglstuURL = "http://jxglstu.hfut.edu.cn/eams5-student/"
        const val MyURL = "https://chiu-xah.github.io/"
        const val RedirectURL = "https://cas.hfut.edu.cn/cas/login?service=http%3A%2F%2Fjxglstu.hfut.edu.cn%2Feams5-student%2Fneusoft-sso%2Flogin&exception.message=A+problem+occurred+restoring+the+flow+execution+with+key+%27e1s1%27"
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
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}