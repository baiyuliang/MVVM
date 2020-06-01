package com.byl.mvvm.ui.main.model

class ArticleListBean {
    var curPage: Int = 0
    var offset: Int = 0
    var over: Boolean = true
    var pageCount: Int = 0
    var size: Int = 0
    var total: Int = 0
    var datas: List<ArticleBean>? = null
}