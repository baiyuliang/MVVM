package com.byl.mvvm.ui.main.vm

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.byl.mvvm.databinding.FragmentMainBinding
import com.byl.mvvm.ui.base.BaseViewModel
import com.byl.mvvm.ui.main.MainFragment
import com.byl.mvvm.ui.main.model.ArticleListBean

class MainFragmentViewModel : BaseViewModel<FragmentMainBinding>() {

    var articlesData = MutableLiveData<ArticleListBean>()

    fun getArticleList(page: Int, isShowLoading: Boolean = false) {
        launch({ httpUtil.getArticleList(page) }, articlesData, isShowLoading)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun observe(fragment: Fragment, owner: LifecycleOwner) {
        val mContext = fragment as MainFragment
        articlesData.observe(owner, {
            if (mContext.page == 0) mContext.list?.clear()
            it.datas?.let { it1 -> mContext.list?.addAll(it1) }
            mContext.adapter?.notifyDataSetChanged()
        })
    }
}