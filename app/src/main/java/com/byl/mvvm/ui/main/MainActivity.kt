package com.byl.mvvm.ui.main

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.byl.mvvm.databinding.ActivityMainBinding
import com.byl.mvvm.event.EventCode
import com.byl.mvvm.event.EventMessage
import com.byl.mvvm.ui.base.BaseActivity
import com.byl.mvvm.ui.main.adapter.ArticleListAdapter
import com.byl.mvvm.ui.main.model.ArticleBean
import com.byl.mvvm.ui.main.vm.MainActivityViewModel

class MainActivity : BaseActivity<MainActivityViewModel, ActivityMainBinding>() {

    var adapter: ArticleListAdapter? = null
    var list: ArrayList<ArticleBean>? = null
    var page: Int = 0


    override fun initView() {
        list = ArrayList()
        adapter = ArticleListAdapter(mContext, list)
        adapter?.itemClick {
            startActivity(Intent(mContext, TestEventActivity::class.java))
        }
        vb.mRecyclerView.layoutManager = LinearLayoutManager(mContext)
        vb.mRecyclerView.adapter = adapter

        vb.refreshLayout.autoRefresh()
        vb.refreshLayout.setOnRefreshListener {//下拉刷新
            page = 0
            vm.getArticleList(page, true)
        }
        vb.refreshLayout.setOnLoadMoreListener {//上拉加载
            vm.getArticleList(++page, true)
        }
    }

    override fun initClick() {

    }

    override fun initData() {
        vm.getArticleList(page, true)
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
            vb.refreshLayout.finishRefresh()
        } else {
            vb.refreshLayout.finishLoadMore()
        }
    }

}