package com.byl.mvvm.util

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import com.byl.mvvm.App
import com.byl.mvvm.R
import java.io.File


object SysUtils {

    fun dp2Px(context: Context, dp: Float): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    fun px2Dp(context: Context, px: Float): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (px / scale + 0.5f).toInt()
    }


    // 获取当前APP名称
    fun getAppName(context: Context): String? {
        val packageManager = context.packageManager
        val applicationInfo: ApplicationInfo
        applicationInfo = try {
            packageManager.getApplicationInfo(context.packageName, 0)
        } catch (e: java.lang.Exception) {
            return context.resources.getString(R.string.app_name)
        }
        return packageManager.getApplicationLabel(applicationInfo).toString()
    }

    fun getAppVersion(): String? {
        val context: Context = App.instance
        val manager: PackageManager = context.packageManager
        return try {
            val info: PackageInfo = manager.getPackageInfo(context.packageName, 0)
            info.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "1.0.0"
        }
    }

    fun getAppVersionCode(): Int {
        val context: Context = App.instance
        val manager: PackageManager = context.packageManager
        return try {
            val info: PackageInfo = manager.getPackageInfo(context.packageName, 0)
            info.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            1
        }
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    fun getSystemModel(): String? {
        return try {
            Build.MODEL
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    fun getDeviceBrand(): String? {
        return try {
            Build.BRAND
        } catch (e: Exception) {
            ""
        }
    }

    fun initFiles() {
        var file = File(Environment.getExternalStorageDirectory(), "MVVM/data")
        if (!file.exists()) file.mkdirs()
        file = File(Environment.getExternalStorageDirectory(), "MVVM/images")
        if (!file.exists()) file.mkdirs()
        file = File(Environment.getExternalStorageDirectory(), "MVVM/download")
        if (!file.exists()) file.mkdirs()
    }

    fun getScreenWidth(activity: Activity): Int {
        var width = 0
        val windowManager = activity.windowManager
        val display = windowManager.defaultDisplay
        width = display.width
        return width
    }

    fun getScreenHeight(activity: Activity): Int {
        var height = 0
        val windowManager = activity.windowManager
        val display = windowManager.defaultDisplay
        height = display.height
        return height
    }
}