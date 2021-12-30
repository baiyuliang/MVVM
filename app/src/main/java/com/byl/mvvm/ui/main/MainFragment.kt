package com.byl.mvvm.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.byl.mvvm.databinding.FragmentMainBinding
import com.byl.mvvm.ui.base.BaseFragment
import com.byl.mvvm.ui.main.adapter.ArticleListAdapter
import com.byl.mvvm.ui.main.model.ArticleBean
import com.byl.mvvm.ui.main.vm.MainFragmentViewModel

class MainFragment : BaseFragment<MainFragmentViewModel, FragmentMainBinding>() {

    var id: Int? = 0
    var adapter: ArticleListAdapter? = null
    var list: ArrayList<ArticleBean>? = null
    var page: Int = 0

    companion object {
        fun getInstance(id: Int): MainFragment {
            val fragment = MainFragment()
            val b = Bundle()
            b.putInt("id", id)
            fragment.arguments = b
            return fragment
        }
    }

    override fun initView() {
        id = arguments?.get("id") as Int?
        list = ArrayList()
        adapter = ArticleListAdapter(mContext, list)
        adapter?.itemClick {
            startActivity(Intent(mContext, TestEventActivity::class.java))
        }
        vb.mRecyclerView.layoutManager = LinearLayoutManager(mContext)
        vb.mRecyclerView.adapter = adapter

        vb.refreshLayout.setOnRefreshListener {//下拉刷新
            page = 0
            vm.getArticleList(page)
        }
        vb.refreshLayout.setOnLoadMoreListener {//上拉加载
            vm.getArticleList(++page)
        }
    }

    override fun initClick() {

    }

    override fun initData() {

    }

    override fun lazyLoadData() {
        vm.getArticleList(page)
    }

    override fun dismissLoading() {
        if (page == 0) {
            vb.refreshLayout.finishRefresh()
        } else {
            vb.refreshLayout.finishLoadMore()
        }
    }

}