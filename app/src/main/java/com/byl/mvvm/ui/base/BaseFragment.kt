package com.byl.mvvm.ui.base

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.byl.mvvm.api.error.ErrorResult
import com.byl.mvvm.event.EventMessage
import com.byl.mvvm.utils.LogUtil
import com.byl.mvvm.utils.ToastUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.lang.reflect.ParameterizedType


abstract class BaseFragment<VM : BaseViewModel, VB : ViewBinding> : Fragment() {

    lateinit var mContext: FragmentActivity
    var contentView: View? = null
    lateinit var vm: VM
    lateinit var v: VB
    private var loadingDialog: ProgressDialog? = null

    //Fragment的View加载完毕的标记
    private var isViewCreated = false

    //Fragment对用户可见的标记
    private var isUIVisible = false

    var isVisibleToUser = false

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //注意 type.actualTypeArguments[0]=BaseViewModel，type.actualTypeArguments[1]=ViewBinding
        val type = javaClass.genericSuperclass as ParameterizedType
        val clazz1 = type.actualTypeArguments[0] as Class<VM>
        vm = ViewModelProvider(this).get(clazz1)

        val clazz2 = type.actualTypeArguments[1] as Class<VB>
        val method = clazz2.getMethod("inflate", LayoutInflater::class.java)
        v = method.invoke(null, layoutInflater) as VB

        mContext = context as AppCompatActivity

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (null == contentView) {
            contentView = v.root
            init()
            initView()
            initClick()
            initData()
            initVM()
            LogUtil.e(getClassName())
        }

        return contentView
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewCreated = true
        lazyLoad()
    }

    private fun init() {
        EventBus.getDefault().register(this)
        //loading
        vm.isShowLoading.observe(this, Observer {
            if (it) showLoading() else dismissLoding()
        })
        //错误信息
        vm.errorData.observe(this, Observer {
            if (it.show) ToastUtil.showToast(mContext, it.errMsg)
            errorResult(it)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    //事件传递
    @Subscribe
    fun onEventMainThread(msg: EventMessage) {
        handleEvent(msg)
    }

    open fun getClassName(): String? {
        val className = "BaseFragment"
        try {
            return javaClass.name
        } catch (e: Exception) {
        }
        return className
    }

    abstract fun initVM()

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

    private fun showLoading() {
        if (loadingDialog == null) {
            loadingDialog = ProgressDialog(mContext)
        }
        loadingDialog!!.show()
    }

    private fun dismissLoding() {
        loadingDialog?.dismiss()
        loadingDialog = null
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
