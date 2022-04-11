package com.byl.mvvm.api

import com.byl.mvvm.api.retrofit.RetrofitClient

class HttpUtil {

    //suspend fun test(options: LinkedHashMap<String, String?>) = mService.test(options)

    //suspend fun getArticleList(page: Int) = mService.getArticleList(page)


    companion object {
        @Volatile
        private var httpUtil: HttpUtil? = null

        fun getInstance() = httpUtil ?: synchronized(this) {
            httpUtil ?: HttpUtil().also { httpUtil = it }
        }
    }

    private val mService by lazy {
        RetrofitClient.getInstance().obtainService(ApiService::class.java)
    }

    // 可以直接在 BaseViewModel 中获取取 ApiService 对象，简化接口调用
    fun getService(): ApiService {
        return mService
    }

}
