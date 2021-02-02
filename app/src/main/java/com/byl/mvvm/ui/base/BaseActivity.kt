package com.byl.mvvm.ui.base

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.byl.mvvm.api.error.ErrorResult
import com.byl.mvvm.event.Event
import com.byl.mvvm.event.EventCode
import com.byl.mvvm.event.EventMessage
import com.byl.mvvm.ext.toast
import com.byl.mvvm.ui.dialog.LoadingDialog
import com.byl.mvvm.util.Logg
import org.greenrobot.eventbus.Subscribe
import java.lang.reflect.ParameterizedType


abstract class BaseActivity<VM : BaseViewModel, VB : ViewBinding> : AppCompatActivity(), IView {

    lateinit var mContext: FragmentActivity
    lateinit var vm: VM
    lateinit var v: VB

    private val mLoading: LoadingDialog by lazy { LoadingDialog(mContext) }

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initResources()

        //注意 type.actualTypeArguments[0]=BaseViewModel，type.actualTypeArguments[1]=ViewBinding
        val type = javaClass.genericSuperclass as ParameterizedType
        val clazz1 = type.actualTypeArguments[0] as Class<VM>
        vm = ViewModelProvider(this).get(clazz1)

        val clazz2 = type.actualTypeArguments[1] as Class<VB>
        val method = clazz2.getMethod("inflate", LayoutInflater::class.java)
        v = method.invoke(null, layoutInflater) as VB

        setContentView(v.root)

        mContext = this
        Logg.i(getClassName())
        init()
        initView()
        initClick()
        initData()
        initVM()
    }

    /**
     * 防止系统字体影响到app的字体
     *
     * @return
     */
    open fun initResources(): Resources? {
        val res: Resources = super.getResources()
        val config = Configuration()
        config.setToDefaults()
        res.updateConfiguration(config, res.displayMetrics)
        return res
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
        val className = "BaseActivity"
        try {
            return javaClass.name
        } catch (e: Exception) {
        }
        return className
    }

    abstract fun initView()

    abstract fun initClick()

    abstract fun initData()

    abstract fun initVM()

    private fun init() {
        Event.register(this)
        // loading
        vm.isShowLoading.observe(this, Observer {
            if (it) showLoading() else dismissLoading()
        })
        // 错误信息
        vm.errorData.observe(this, Observer {
            if (it.show) showMessage(it.errMsg)
            errorResult(it)
        })
    }

    override fun showLoading() {
        mLoading.showLoading()
    }

    override fun dismissLoading() {
        mLoading.dismiss()
    }

    override fun showMessage(message: String?) {
        mContext.toast(message)
    }

    override fun close() {
        finish()
    }

    override fun onStop() {
        super.onStop()
        dismissLoading()
    }

    /**
     * 消息、事件接收回调
     */
    open fun handleEvent(msg: EventMessage) {
        if (msg.code == EventCode.LOGIN_OUT) {
            finish()
        }
    }

    /**
     * 接口请求错误回调
     */
    open fun errorResult(errorResult: ErrorResult) {}

}