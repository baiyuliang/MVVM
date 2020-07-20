package com.byl.mvvm.utils

/**
 * Platform
 *
 * @author lishide
 * @date 2020/7/20
 */
object Platform {

    var DEPENDENCY_ANDROID_EVENTBUS = false
    var DEPENDENCY_EVENTBUS = false

    init {
        DEPENDENCY_ANDROID_EVENTBUS = findClassByClassName("org.simple.eventbus.EventBus")
        DEPENDENCY_EVENTBUS = findClassByClassName("org.greenrobot.eventbus.EventBus")
    }

    private fun findClassByClassName(className: String): Boolean {
        return try {
            Class.forName(className)
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }
}