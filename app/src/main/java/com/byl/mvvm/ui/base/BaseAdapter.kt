package com.byl.mvvm.ui.base

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.byl.mvvm.utils.GenericParadigmUtil
import com.byl.mvvm.widget.clicks

/**
 * 通过传入ViewBinding，不再需要写具体xml资源，省略onBindViewHolder中findviewById
 * 注意点：item的最外层布局高度要设为wrap_content，
 * 如果item有需求要设置为固定宽高，可以在子类的convert方法里，通过代码设置
 */
abstract class BaseAdapter<VB : ViewBinding, T>(
    var mContext: Activity,
    var listDatas: ArrayList<T>?
) : RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val pathfinders = ArrayList<GenericParadigmUtil.Pathfinder>()
        pathfinders.add(GenericParadigmUtil.Pathfinder(0, 0))
        val clazzVB = GenericParadigmUtil.parseGenericParadigm(javaClass, pathfinders)
        val method = clazzVB.getMethod("inflate", LayoutInflater::class.java)
        val vb = method.invoke(null, LayoutInflater.from(mContext)) as VB
        vb.root.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
        return BaseViewHolder(vb, vb.root)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.itemView.clicks {
            itemClick?.let { it(position) }
        }
        holder.itemView.setOnLongClickListener {
            itemLongClick?.let { it1 -> it1(position) }
            true
        }

        convert(holder.vb as VB, listDatas?.get(position), position)
    }

    abstract fun convert(vb: VB, t: T?, position: Int)

    override fun getItemCount(): Int {
        return listDatas?.size ?: 0
    }


    private var itemClick: ((Int) -> Unit)? = null
    private var itemLongClick: ((Int) -> Unit)? = null


    fun itemClick(itemClick: (Int) -> Unit) {
        this.itemClick = itemClick
    }

    fun itemLongClick(itemLongClick: (Int) -> Unit) {
        this.itemLongClick = itemLongClick
    }
}
