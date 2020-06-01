package com.byl.mvvm.ui.main

import androidx.lifecycle.MutableLiveData
import com.byl.mvvm.ui.base.BaseViewModel
import com.byl.mvvm.ui.main.model.ArticleListBean

class MainViewModel : BaseViewModel() {

    var articlesData = MutableLiveData<ArticleListBean>()

    fun getArticleList(page: Int, isShowLoading: Boolean) {
        launch({ httpUtil.getArticleList(page) }, articlesData, isShowLoading)
    }

}