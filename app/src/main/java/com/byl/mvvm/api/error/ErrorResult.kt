package com.byl.mvvm.api.error

class ErrorResult @JvmOverloads constructor(
    var code: Int = 0,
    var errMsg: String? = "",
    var show: Boolean = false,
    var index: Int = 0//表示api类型（确定是哪个api）
)