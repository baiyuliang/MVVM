package com.byl.mvvm.api.error

import android.net.ParseException
import com.google.gson.JsonParseException
import org.json.JSONException
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorUtil {

    fun getError(e: Throwable): ErrorResult {
        val errorResult = ErrorResult()
        if (e is HttpException) {
            errorResult.code = e.code()
        }
        errorResult.errMsg = handleResponseError(e)
        if (errorResult.errMsg.isNullOrEmpty()) {
            errorResult.errMsg = "网络请求失败，请重试"
        }
        return errorResult
    }

    fun getError(apiIndex: Int, e: Throwable): ErrorResult {
        val errorResult = ErrorResult()
        errorResult.index = apiIndex
        if (e is HttpException) {
            errorResult.code = e.code()
        }
        errorResult.errMsg = handleResponseError(e)
        if (errorResult.errMsg.isNullOrEmpty()) {
            errorResult.errMsg = "网络请求失败，请重试"
        }
        return errorResult
    }

    private fun handleResponseError(t: Throwable?): String? {
        // 这里不光是只能打印错误，还可以根据不同的错误作出不同的逻辑处理
        // 这里只是对几个常用错误进行简单的处理, 在实际开发中请您自行对更多错误进行更严谨的处理
        var msg: String? = "未知错误"
        when (t) {
            is UnknownHostException -> {
                msg = "网络不可用"
            }
            is SocketTimeoutException -> {
                msg = "请求网络超时"
            }
            is HttpException -> {
                msg = convertStatusCode(t)
            }
            is JsonParseException, is ParseException, is JSONException -> {
                msg = "数据解析错误"
            }
        }
        return msg
    }

    private fun convertStatusCode(httpException: HttpException): String? {
        return when (httpException.code()) {
            500 -> "服务器发生错误"
            503 -> "服务不可用"
            404 -> "请求地址不存在"
            403 -> "请求被服务器拒绝"
            307 -> "请求被重定向到其他页面"
            400 -> "请求无效"
            else -> {
                httpException.message()
            }
        }
    }

}