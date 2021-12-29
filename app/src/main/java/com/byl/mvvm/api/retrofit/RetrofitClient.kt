package com.byl.mvvm.api.retrofit

import com.byl.mvvm.App
import com.byl.mvvm.BuildConfig
import com.byl.mvvm.api.URLConstant
import com.byl.mvvm.util.JsonUtils
import com.byl.mvvm.util.Logg
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class RetrofitClient {

    companion object {
        fun getInstance() = SingletonHolder.INSTANCE

        private lateinit var retrofit: Retrofit
    }

    private object SingletonHolder {
        val INSTANCE = RetrofitClient()
    }

    private var cookieJar: PersistentCookieJar = PersistentCookieJar(
        SetCookieCache(),
        SharedPrefsCookiePersistor(App.instance)
    )

    init {
        retrofit = Retrofit.Builder()
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(URLConstant.BASE_URL)
            .build()
    }

    private fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .cookieJar(cookieJar)
            .addNetworkInterceptor(getLoggingInterceptor())
//                .sslSocketFactory(SSLContextSecurity.createIgnoreVerifySSL("TLS"))
            .build()
    }

    private fun getLoggingInterceptor(): Interceptor {
        return if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor(HttpLogger()).apply {
                // 打印完整的 log
                level = HttpLoggingInterceptor.Level.BODY
            }
        } else {
            HttpLoggingInterceptor().apply {
                // 不打印 log
                level = HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    private class HttpLogger : HttpLoggingInterceptor.Logger {
        private val hashMap = ConcurrentHashMap<Long, StringBuilder>()

        override fun log(message: String) {
            var msg = message
            val threadId = Thread.currentThread().id
            hashMap.putIfAbsent(threadId, java.lang.StringBuilder())?.apply {
                // 以{}或者[]形式的说明是响应结果的json数据，需要进行格式化
                if ((msg.startsWith("{") && msg.endsWith("}"))
                    || (msg.startsWith("[") && msg.endsWith("]"))
                ) {
                    msg = JsonUtils.formatJson(JsonUtils.decodeUnicode(msg))
                }
                append(msg + "\n")
                // 响应结束，打印整条日志
                if (msg.startsWith("<-- END HTTP")) {
                    Logg.d(toString())
                    hashMap.remove(threadId)
                }
            }
        }
    }

    /**
     * 根据传入的 Class 获取对应的 Retrofit Service
     */
    fun <T> obtainService(service: Class<T>): T = retrofit.create(service)

}