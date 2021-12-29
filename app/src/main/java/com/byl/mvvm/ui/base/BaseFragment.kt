package com.byl.mvvm.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.byl.mvvm.api.error.ErrorResult
import com.byl.mvvm.event.Event
import com.byl.mvvm.event.EventMessage
import com.byl.mvvm.ext.toast
import com.byl.mvvm.ui.dialog.LoadingDialog
import com.byl.mvvm.util.Logg
import com.byl.mvvm.utils.GenericParadigmUtil
import org.greenrobot.eventbus.Subscribe

abstract class BaseFragment<VM : BaseViewModel<VB>, VB : ViewBinding> : Fragment(), IView {

    lateinit var mContext: FragmentActivity
    var contentView: View? = null
    lateinit var vm: VM
    lateinit var vb: VB

    private val mLoading: LoadingDialog by lazy { LoadingDialog(mContext) }

    //Fragment的View加载完毕的标记
    private var isViewCreated = false

    //Fragment对用户可见的标记
    private var isUIVisible = false
    var isVisibleToUser = false

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = context as FragmentActivity

        var pathfinders = ArrayList<GenericParadigmUtil.Pathfinder>()
        pathfinders.add(GenericParadigmUtil.Pathfinder(0, 0))
        val clazzVM = GenericParadigmUtil.parseGenericParadigm(javaClass, pathfinders) as Class<VM>
        vm = ViewModelProvider(this).get(clazzVM)

        pathfinders = ArrayList()
        pathfinders.add(GenericParadigmUtil.Pathfinder(0, 1))
        val clazzVB = GenericParadigmUtil.parseGenericParadigm(javaClass, pathfinders)
        val method = clazzVB.getMethod("inflate", LayoutInflater::class.java)
        vb = method.invoke(null, layoutInflater) as VB

        vm.binding(vb)
        vm.observe(this, this)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (null == contentView) {
            contentView = vb.root
            Logg.i(getClassName())
            init()
            initView()
            initClick()
            initData()
        }

        return contentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewCreated = true
        lazyLoad()
    }

    private fun init() {
        Event.register(this)
        // loading
        vm.isShowLoading.observe(this, {
            if (it) showLoading() else dismissLoading()
        })
        // 错误信息
        vm.errorData.observe(this, {
            if (it.show) showMessage(it.errMsg)
            errorResult(it)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        Event.unregister(this)
    }

    /**
     * 事件传递
     */
    @Subscribe
    fun onEventMainThread(msg: EventMessage) {
        handleEvent(msg)
    }

    open fun getClassName(): String {
        val className = "BaseFragment"
        try {
            return javaClass.name
        } catch (e: Exception) {
        }
        return className
    }

    abstract fun initView()

    abstract fun initClick()

    abstract fun initData()

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        this.isVisibleToUser = isVisibleToUser
        //isVisibleToUser这个boolean值表示:该Fragment的UI 用户是否可见
        if (isVisibleToUser) {
            isUIVisible = true
            lazyLoad()
        } else {
            isUIVisible = false
        }
    }

    fun lazyLoad() {
        //这里进行双重标记判断,是因为setUserVisibleHint会多次回调,并且会在onCreateView执行前回调,必须确保onCreateView加载完毕且页面可见,才加载数据
        if (isViewCreated && isUIVisible) {
            lazyLoadData()
            //数据加载完毕,恢复标记,防止重复加载
            isViewCreated = false
            isUIVisible = false
        }
    }

    //需要懒加载的数据，重写此方法
    abstract fun lazyLoadData()

    override fun showLoading() {
        mLoading.showLoading()
    }

    override fun dismissLoading() {
        mLoading.dismiss()
    }

    override fun showMessage(message: String?) {
        mContext.toast(message)
    }

    /**
     * 消息、事件接收回调
     */
    open fun handleEvent(msg: EventMessage) {}

    /**
     * 接口请求错误回调
     */
    open fun errorResult(errorResult: ErrorResult) {}

    override fun onDestroyView() {
        super.onDestroyView()
        contentView = null
    }
}
