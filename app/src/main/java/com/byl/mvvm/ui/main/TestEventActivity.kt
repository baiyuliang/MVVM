package com.byl.mvvm.ui.main


import com.byl.mvvm.App
import com.byl.mvvm.databinding.ActivityTestEventBinding
import com.byl.mvvm.event.EventCode
import com.byl.mvvm.event.EventMessage
import com.byl.mvvm.ui.base.BaseActivity
import com.byl.mvvm.ui.base.BaseViewModel
import com.byl.mvvm.widget.clicks


class TestEventActivity : BaseActivity<BaseViewModel, ActivityTestEventBinding>() {


    override fun initView() {

    }

    override fun initClick() {
        v.btn.clicks {
            App.post(EventMessage(EventCode.REFRESH))
        }
    }

    override fun initData() {

    }

    override fun initVM() {

    }

}