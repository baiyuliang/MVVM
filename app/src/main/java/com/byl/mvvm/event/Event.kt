package com.byl.mvvm.event

import org.greenrobot.eventbus.EventBus

object Event {
    fun getInstance(): EventBus {
        return EventBus.getDefault()
    }

    fun post(eventMessage: EventMessage) {
        getInstance().post(eventMessage)
    }

}