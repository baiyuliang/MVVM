package com.byl.mvvm.ui.main.vm

import android.app.Activity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.byl.mvvm.databinding.ActivityMainBinding
import com.byl.mvvm.ui.base.BaseViewModel
import com.byl.mvvm.ui.main.MainActivity
import com.byl.mvvm.ui.main.model.ArticleListBean

class MainActivityViewModel : BaseViewModel() {

    var articlesData = MutableLiveData<ArticleListBean>()

    fun getArticleList(page: Int, isShowLoading: Boolean = false) {
        launch({ httpUtil.getArticleList(page) }, articlesData, isShowLoading)
    }

    override fun observe(activity: Activity, owner: LifecycleOwner, viewBinding: ViewBinding) {
        val mContext = activity as MainActivity
        val vb = viewBinding as ActivityMainBinding
        articlesData.observe(owner, Observer {
            vb.refreshLayout.finishRefresh()
            vb.refreshLayout.finishLoadMore()
            if (mContext.page == 0) mContext.list!!.clear()
            it.datas?.let { it1 -> mContext.list!!.addAll(it1) }
            mContext.adapter!!.notifyDataSetChanged()
        })
        errorData.observe(owner, Observer {
            vb.refreshLayout.finishRefresh()
            vb.refreshLayout.finishLoadMore()
        })
    }
}