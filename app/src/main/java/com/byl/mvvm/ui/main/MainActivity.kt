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
import com.byl.mvvm.utils.ToastUtil

class MainActivity : BaseActivity<MainActivityViewModel, ActivityMainBinding>() {

    var adapter: ArticleListAdapter? = null
    var list: ArrayList<ArticleBean>? = null
    var page: Int = 0


    override fun initView() {
        list = ArrayList()
        adapter = ArticleListAdapter(mContext, list!!)
        adapter!!.itemClick {
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
        vm.getArticleList(page, true)
    }

    /**
     * 接收消息
     */
    override fun handleEvent(msg: EventMessage) {
        super.handleEvent(msg)
        if (msg.code == EventCode.REFRESH) {
            ToastUtil.showToast(mContext, "主页：刷新")
            page = 0
            vm.getArticleList(page)
        }
    }
}