package com.byl.mvvm.ui.main.adapter

import android.app.Activity
import com.bumptech.glide.Glide
import com.byl.mvvm.databinding.ItemArticleBinding
import com.byl.mvvm.ui.base.BaseAdapter
import com.byl.mvvm.ui.base.BaseViewHolder
import com.byl.mvvm.ui.main.model.ArticleBean


class ArticleListAdapter(context: Activity, listDatas: ArrayList<ArticleBean>) :
    BaseAdapter<ItemArticleBinding, ArticleBean>(context, listDatas) {

    override fun convert(holder: BaseViewHolder, t: ArticleBean, position: Int) {
        val v = holder.v as ItemArticleBinding
        Glide.with(mContext).load(t.envelopePic).into(v.ivCover)
        v.tvTitle.text = t.title
        v.tvDes.text = t.desc
    }

}