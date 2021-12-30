package com.byl.mvvm.ui.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

open class BaseViewHolder(var vb: ViewBinding, itemView: View) : RecyclerView.ViewHolder(itemView)