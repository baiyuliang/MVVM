package com.byl.mvvm.ui.main

import android.content.Intent
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.byl.mvvm.databinding.ActivityMainBinding
import com.byl.mvvm.event.EventCode
import com.byl.mvvm.event.EventMessage
import com.byl.mvvm.ext.toast
import com.byl.mvvm.ui.base.BaseActivity
import com.byl.mvvm.ui.main.adapter.ArticleListAdapter
import com.byl.mvvm.ui.main.model.ArticleBean

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    var adapter: ArticleListAdapter? = null
    var list: ArrayList<ArticleBean>? = null
    var page: Int = 0


    override fun initView() {
        list = ArrayList()
        adapter = ArticleListAdapter(mContext, list!!)
        adapter?.itemClick {
            startActivity(Intent(mContext, TestEventActivity::class.java))
        }
        v.mRecyclerView.layoutManager = LinearLayoutManager(mContext)
        v.mRecyclerView.adapter = adapter

        v.refreshLayout.autoRefresh()
        v.refreshLayout.setOnRefreshListener {//下拉刷新
            page = 0
            vm.getArticleList(page, true)
        }
        v.refreshLayout.setOnLoadMoreListener {//上拉加载
            vm.getArticleList(++page, true)
        }
    }

    override fun initClick() {

    }

    override fun initData() {

    }

    override fun initVM() {
        vm.articlesData.observe(this, Observer {
            if (page == 0) list?.clear()
            it.datas?.let { it1 -> list?.addAll(it1) }
            adapter?.notifyDataSetChanged()
        })
    }

    /**
     * 接收消息
     */
    override fun handleEvent(msg: EventMessage) {
        super.handleEvent(msg)
        if (msg.code == EventCode.REFRESH) {
            showMessage("主页：刷新")
            page = 0
            vm.getArticleList(page, true)
        }
    }

    override fun showLoading() {
    }

    override fun dismissLoading() {
        if (page == 0) {
            v.refreshLayout.finishRefresh()
        } else {
            v.refreshLayout.finishLoadMore()
        }
    }

}