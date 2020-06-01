package com.byl.mvvm.event

class EventMessage @JvmOverloads constructor(
    var code: EventCode,
    var msg: String = "",
    var arg1: Int = 0,
    var arg2: Int = 0,
    var obj: Any? = null
)