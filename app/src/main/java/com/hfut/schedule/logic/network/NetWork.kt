package com.hfut.schedule.logic.network

import androidx.lifecycle.MutableLiveData
import com.hfut.schedule.logic.network.ServiceCreator.CommunitySreviceCreator
import com.hfut.schedule.logic.network.ServiceCreator.DormitoryScoreServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.GiteeServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.GuaGuaServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Jxglstu.JxglstuHTMLServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Jxglstu.JxglstuJSONServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Jxglstu.JxglstuSurveyServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.LePaoYunServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Login.GetAESKeyServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Login.GetCookieServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Login.LoginServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Login.LoginWeb2ServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Login.LoginWebServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Login.LoginWebVpnServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.MyServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.NewsServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.OneGotoServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.OneServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.SearchEleServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.ServerServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.XuanChengServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.ZJGDBillServiceCreator
import com.hfut.schedule.logic.network.api.CommunityService
import com.hfut.schedule.logic.network.api.DormitoryScore
import com.hfut.schedule.logic.network.api.FWDTService
import com.hfut.schedule.logic.network.api.GiteeService
import com.hfut.schedule.logic.network.api.GuaGuaService
import com.hfut.schedule.logic.network.api.JxglstuService
import com.hfut.schedule.logic.network.api.LePaoYunService
import com.hfut.schedule.logic.network.api.LoginService
import com.hfut.schedule.logic.network.api.LoginWebsService
import com.hfut.schedule.logic.network.api.MyService
import com.hfut.schedule.logic.network.api.NewsService
import com.hfut.schedule.logic.network.api.OneService
import com.hfut.schedule.logic.network.api.ServerService
import com.hfut.schedule.logic.network.api.WebVpnService
import com.hfut.schedule.logic.network.api.XuanChengService
import com.hfut.schedule.logic.network.api.ZJGDBillService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class NetWork(webVpn: Boolean) {
    //引入接口
    companion object {
        const val JxglstuURL = "http://jxglstu.hfut.edu.cn/eams5-student/"
        const val JxglstuWebVpnURL = "https://webvpn.hfut.edu.cn/http/77726476706e69737468656265737421faef469034247d1e760e9cb8d6502720ede479/eams5-student/"
        const val WebVpnURL = "https://webvpn.hfut.edu.cn/"
        const val NewsURL = "https://news.hfut.edu.cn/"
        //const val DownloadURL = "https://gitee.com/chiu-xah/HFUT-Schedule/releases/tag/Android"
        const val JwglappURL = "https://jwglapp.hfut.edu.cn/"
        const val UpdateURL = "https://gitee.com/chiu-xah/HFUT-Schedule/"
        const val EleURL = "http://172.31.248.26:8988/"
        const val CommunityURL = "https://community.hfut.edu.cn/"
        const val ZJGDBillURL = "http://121.251.19.62/"
        const val LePaoYunURL = "http://210.45.246.53:8080/"
        const val AlipayCardURL = "alipays://platformapi/startapp?appId=20000067&url=https://ur.alipay.com/_4kQhV32216tp7bzlDc3E1k"
        const val AlipayHotWaterOldURL = "alipays://platformapi/startapp?appId=20000067&url=https://puwise.com/s/cnM5jQKY02DD.wise"
        const val AlipayHotWaterURL = "alipays://platformapi/startapp?appId=20000067&url=https://ur.alipay.com/_3B2YzKjbV75xL9a2oapcNz"
        const val OneURL = "https://one.hfut.edu.cn/"
        const val LoginURL = "https://cas.hfut.edu.cn/"
        const val MyURL = "https://chiu-xah.github.io/"
        const val DormitoryScoreURL = "http://39.106.82.121/"
        const val loginWebURL = "http://172.18.3.3/"
        const val loginWebURL2 = "http://172.18.2.2/"
        const val RedirectURL = "https://cas.hfut.edu.cn/cas/login?service=http%3A%2F%2Fjxglstu.hfut.edu.cn%2Feams5-student%2Fneusoft-sso%2Flogin&exception.message=A+problem+occurred+restoring+the+flow+execution+with+key+%27e1s1%27"
        const val GuaGuaURL = "https://guagua.klcxkj-qzxy.cn/"
        const val XuanChengURL = "https://xc.hfut.edu.cn/"
        const val TeacherURL = "https://faculty.hfut.edu.cn/"
    }


    private val guaGua = GuaGuaServiceCreator.create(GuaGuaService::class.java)
    private val LoginWebVpn = LoginWebVpnServiceCreator.create(WebVpnService::class.java)
    private val Login = LoginServiceCreator.create(LoginService::class.java)
    private val GetCookie = GetCookieServiceCreator.create(LoginService::class.java)
    private val GetAESKey = GetAESKeyServiceCreator.create(LoginService::class.java)
    private val MyAPI = MyServiceCreator.create(MyService::class.java)

    private val JxglstuJSON = JxglstuJSONServiceCreator.create(JxglstuService::class.java, webVpn)
    private val JxglstuHTML = JxglstuHTMLServiceCreator.create(JxglstuService::class.java, webVpn)
    private val OneGoto = OneGotoServiceCreator.create(LoginService::class.java)
    private val One = OneServiceCreator.create(OneService::class.java)
    private val ZJGDBill = ZJGDBillServiceCreator.create(ZJGDBillService::class.java)
    private val Xuanqu = DormitoryScoreServiceCreator.create(DormitoryScore::class.java)
    private val LePaoYun = LePaoYunServiceCreator.create(LePaoYunService::class.java)
    private val searchEle = SearchEleServiceCreator.create(FWDTService::class.java)
    private val CommunityLogin = LoginServiceCreator.create(CommunityService::class.java)

    /// private val JwglappLogin = JwglappServiceCreator.create(JwglappService::class.java)
    private val Community = CommunitySreviceCreator.create(CommunityService::class.java)

    //  private val Jwglapp = JwglappServiceCreator.create(JwglappService::class.java)
    private val News = NewsServiceCreator.create(NewsService::class.java)
    private val xuanCheng = XuanChengServiceCreator.create(XuanChengService::class.java)
    private val JxglstuSurvey =
        JxglstuSurveyServiceCreator.create(JxglstuService::class.java, webVpn)
    private val server = ServerServiceCreator.create(ServerService::class.java)
    private val guagua = GuaGuaServiceCreator.create(GuaGuaService::class.java)

    private val Gitee = GiteeServiceCreator.create(GiteeService::class.java)
    private val LoginWeb = LoginWebServiceCreator.create(LoginWebsService::class.java)
    private val LoginWeb2 = LoginWeb2ServiceCreator.create(LoginWebsService::class.java)

    // 通用的网络请求方法，支持自定义的操作
    fun <T> makeRequest(
        call: Call<ResponseBody>,
        liveData: MutableLiveData<T>?,
        onSuccess: ((Response<ResponseBody>) -> Unit)? = null
    ) {
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful && liveData != null) {
                    val responseBody = response.body()?.string()
                    val result: T? = parseResponse(responseBody)
                    liveData.value = result
                }

                // 执行自定义操作
                onSuccess?.invoke(response)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    // 通用方法用于解析响应（根据需要进行调整）
    @Suppress("UNCHECKED_CAST")
    private fun <T> parseResponse(responseBody: String?): T? {
        return responseBody as? T
    }
}