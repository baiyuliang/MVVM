package com.byl.mvvm

import android.app.Application
import com.byl.mvvm.util.Logg
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout


class App : Application() {

    companion object {
        var DEBUG: Boolean = false
        lateinit var instance: App

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        DEBUG = true

        Logg.init(BuildConfig.DEBUG)
    }

    init {
        //设置全局默认配置（优先级最低，会被其他设置覆盖）
        SmartRefreshLayout.setDefaultRefreshInitializer { _, layout ->
            layout.setEnableLoadMore(true)
            layout.setEnableLoadMoreWhenContentNotFull(false)
            layout.setDisableContentWhenRefresh(true)
            layout.setDisableContentWhenLoading(true)
        }
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ ->
            ClassicsHeader(context)
        }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
            ClassicsFooter(context)
        }
    }


}