package com.hfut.schedule.logic.network.api

import com.hfut.schedule.logic.util.network.Encrypt.encryptTimestamp
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.Url

const val ROOT_NODE_ID = "-1"

interface WXService {
    @GET("api/auth/bsauth/getTgtWx")
    fun login(
        @Header("x-stamp") stamp : String = encryptTimestamp(),
        @Query("username") username : String = prefs.getString("Username","") ?: "",
        @Query("password") password : String = prefs.getString("Password","") ?: ""
    ) : Call<ResponseBody>

    // 也可以用于check-login
    @GET("api/center/user/selectUserInfoForApp")
    fun getMyInfo(
        @Header("Authorization") auth : String
    ) : Call<ResponseBody>


    // 直到查询为空时，才用获取到的最后一个nodeId取查同班同学 这个没啥用 因为叶子的nodeId在getMyInfo可以直接得到
    @GET("api/center/userGroup/selectOrgTreeForAPP")
    fun getOrganizationTree(
        @Query("nodeId") nodeId : String = ROOT_NODE_ID,
        @Header("Authorization") auth : String
    ) : Call<ResponseBody>

    @GET("api/center/userGroup/selectCrewDataByOrgIdForAPP")
    fun getClassmates(
        @Query("nodeId") nodeId : String,
        @Header("Authorization") auth : String,
        @Query("isShowPhoto") isShowPhoto : Int = 1,
        @Query("current") page : Int = 1,
        @Query("size") pageSize : Int = 200,
    ) : Call<ResponseBody>

    // 扫码登陆
    /*
    将二维码的域名HOST换成这个WX_URL，带auth，得到msg=success即可
     */
    // 确定登录 uuid从二维码中获取
    @GET("cas/app/confirmLogin")
    fun confirmLogin(
        @Query("uuid") uuid : String,
        @Header("Authorization") auth : String
    ) : Call<ResponseBody>


    @GET
    fun loginCas(
        @Url url: String,
        @Header("Authorization") auth: String
    ): Call<ResponseBody>
}