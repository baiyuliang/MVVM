package com.byl.mvvm

import android.app.Application
import android.content.Context
import com.byl.mvvm.event.Event
import com.byl.mvvm.event.EventMessage
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader


class App : Application() {

    companion object {
        var DEBUG: Boolean = false
        lateinit var instance: App

        fun post(eventMessage: EventMessage) {
            Event.getInstance().post(eventMessage)
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        DEBUG = true
    }

    init {
        //设置全局默认配置（优先级最低，会被其他设置覆盖）
        SmartRefreshLayout.setDefaultRefreshInitializer { _: Context?, layout: RefreshLayout ->
            //开始设置全局的基本参数
            layout.setFooterHeight(40f)
            layout.setDisableContentWhenLoading(false)
            layout.setDisableContentWhenRefresh(true) //是否在刷新的时候禁止列表的操作
            layout.setDisableContentWhenLoading(true) //是否在加载的时候禁止列表的操作
            layout.setEnableOverScrollBounce(false)
        }
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context: Context?, layout: RefreshLayout? ->
            ClassicsHeader(context)
                .setSpinnerStyle(SpinnerStyle.Translate)
                .setTextSizeTitle(13f)
                .setDrawableArrowSize(15f)
                .setDrawableProgressSize(15f)
                .setDrawableMarginRight(10f)
                .setFinishDuration(0)
        }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context: Context?, layout: RefreshLayout? ->
            ClassicsFooter(context)
                .setSpinnerStyle(SpinnerStyle.Translate)
                .setTextSizeTitle(13f)
                .setDrawableArrowSize(15f)
                .setDrawableProgressSize(15f)
                .setDrawableMarginRight(10f)
                .setFinishDuration(0)
        }
    }


}