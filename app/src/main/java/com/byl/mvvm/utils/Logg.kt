package com.byl.mvvm.utils

import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

/**
 * Log 日志工具，封装 logger
 *
 * @author lishide
 * @date 2020/7/19
 */
object Logg {
    /**
     * 初始化log工具，在app入口处调用
     *
     * @param isLogEnable 是否打印log
     */
    fun init(isLogEnable: Boolean) {
        Logger.addLogAdapter(object : AndroidLogAdapter() {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return isLogEnable
            }
        })
    }

    fun d(message: String, vararg args: Any?) {
        Logger.d(message, *args)
    }

    fun d(any: Any?) {
        Logger.d(any)
    }

    fun e(message: String, vararg args: Any?) {
        Logger.e(null, message, *args)
    }

    fun e(throwable: Throwable?, message: String, vararg args: Any?) {
        Logger.e(throwable, message, *args)
    }

    fun i(message: String, vararg args: Any?) {
        Logger.i(message, *args)
    }

    fun v(message: String, vararg args: Any?) {
        Logger.v(message, *args)
    }

    fun w(message: String, vararg args: Any?) {
        Logger.w(message, *args)
    }

    fun wtf(message: String, vararg args: Any?) {
        Logger.wtf(message, *args)
    }

    fun json(json: String?) {
        Logger.json(json)
    }

    fun xml(xml: String?) {
        Logger.xml(xml)
    }

}