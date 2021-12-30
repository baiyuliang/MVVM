package com.byl.mvvm.ui.main.adapter

import android.app.Activity
import com.bumptech.glide.Glide
import com.byl.mvvm.databinding.ItemArticleBinding
import com.byl.mvvm.ui.base.BaseAdapter
import com.byl.mvvm.ui.main.model.ArticleBean


class ArticleListAdapter(context: Activity, listDatas: ArrayList<ArticleBean>?) :
    BaseAdapter<ItemArticleBinding, ArticleBean>(context, listDatas) {

    override fun convert(vb: ItemArticleBinding, t: ArticleBean?, position: Int) {
        Glide.with(mContext).load(t?.envelopePic).into(vb.ivCover)
        vb.tvTitle.text = t?.title
        vb.tvDes.text = t?.desc
    }

}