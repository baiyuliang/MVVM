package com.byl.mvvm.utils

import android.util.Log
import com.byl.mvvm.BuildConfig

object LogUtil {
    private const val TAG = "mvvm_log"
    private const val TAG_NET = "mvvm_net"

    fun i(message: String?) {
        if (BuildConfig.DEBUG) Log.i(TAG, message)
    }

    fun e(message: String?) {
        if (BuildConfig.DEBUG) Log.e(TAG, message)
    }

    fun showHttpHeaderLog(message: String?) {
        if (BuildConfig.DEBUG) Log.d(TAG_NET, message)
    }

    fun showHttpApiLog(message: String?) {
        if (BuildConfig.DEBUG) Log.w(TAG_NET, message)
    }

    fun showHttpLog(message: String?) {
        if (BuildConfig.DEBUG) Log.i(TAG_NET, message)
    }
}