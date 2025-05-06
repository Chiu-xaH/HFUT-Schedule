package com.hfut.schedule.logic.util.network

import androidx.lifecycle.MutableLiveData
import okhttp3.Headers
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response


object NetWork {
    //引入接口
    // 通用的网络请求方法，支持自定义的操作
    @JvmStatic
    fun <T> makeRequest(
        call: Call<ResponseBody>,
        liveData: (MutableLiveData<T>)? = null,
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
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    private fun <T> parseResponse(responseBody: String?): T? {
        return responseBody as? T
    }

    @JvmStatic
    suspend fun <T,E> launchRequest(
        holder: StateHolder<T,E>,
        request: suspend () -> Response<ResponseBody>,
        transform: (Headers, String) -> T?,
        transformError: ((String) -> E?)? = null,
        transformRedirect: ((Headers) -> T?)? = null
    ) = try {
        holder.setLoading()
        val response = request()
        val headers = response.headers()
        val bodyString = response.body()?.string().orEmpty()
        if (response.isSuccessful) {
            // 成功
            val result = transform(headers, bodyString)
            holder.emitData(result)
        } else if(response.code() in 300..399){
            // 重定向 特殊处理
            val result = transformRedirect?.let { it(headers) }
            holder.emitData(result)
        } else {
            // 承接错误解析 可选
            val result = transformError?.let { it(bodyString) }
            holder.emitError(HttpException(response), response.code(),result)
        }
    } catch (e: Exception) {
        holder.emitError(e,null,null)
    }

    @JvmStatic
    suspend fun <T> launchRequestSimple(
        holder: SimpleStateHolder<T>,
        request: suspend () -> Response<ResponseBody>,
        transformSuccess: (Headers, String) -> T,
        transformRedirect: ((Headers) -> T)? = null
    ) = try {
        holder.setLoading()
        val response = request()
        val headers = response.headers()
        val bodyString = response.body()?.string().orEmpty()

        if (response.isSuccessful) {
            // 成功
            val result = try {
                transformSuccess(headers, bodyString)
            } catch (e: Exception) {
                holder.emitError(e, PARSE_ERROR_CODE)
                return
            }
            holder.emitData(result)
        }
        else if(response.code() == 302){
//             重定向 特殊处理
            val result = try {
                transformRedirect!!(headers)
            } catch (e: Exception) {
                holder.emitError(e, PARSE_ERROR_CODE)
                return
            }
            holder.emitData(result)
        }
        else {
            // 承接错误解析 可选
            holder.emitError(HttpException(response), response.code())
        }
    } catch (e: Exception) {
        holder.emitError(e,null)
    }

    fun <T> parseResponseBody(body : String,function : (String) -> T) : T = try {
        function(body)
    } catch (e : Exception) { throw e }
}


