package com.byl.mvvm.ui.base

interface IView {
    /**
     * 显示加载
     */
    fun showLoading() {}

    /**
     * 隐藏加载
     */
    fun dismissLoading() {}

    /**
     * 显示信息
     *
     * @param message 消息内容, 不能为 `null`
     */
    fun showMessage(message: String) {}

    /**
     * 关闭页面
     */
    fun close() {}

    /**
     * 重新登录
     */
    fun reLogin() {}
}