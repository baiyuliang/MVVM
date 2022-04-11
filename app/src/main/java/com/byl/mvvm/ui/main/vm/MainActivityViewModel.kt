package com.byl.mvvm.ui.main.vm

import android.annotation.SuppressLint
import android.app.Activity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.byl.mvvm.databinding.ActivityMainBinding
import com.byl.mvvm.ui.base.BaseViewModel
import com.byl.mvvm.ui.main.MainActivity
import com.byl.mvvm.ui.main.model.ArticleListBean

class MainActivityViewModel : BaseViewModel<ActivityMainBinding>() {

    var articlesData = MutableLiveData<ArticleListBean>()

    fun getArticleList(page: Int, isShowLoading: Boolean = false) {
        launch({ httpUtil.getArticleList(page) }, articlesData, isShowLoading)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun observe(activity: Activity, owner: LifecycleOwner) {
        val mContext = activity as MainActivity
        articlesData.observe(owner, {
            if (mContext.page == 0) mContext.list?.clear()
            it.datas?.let { it1 -> mContext.list?.addAll(it1) }
            mContext.adapter?.notifyDataSetChanged()
        })
    }
}