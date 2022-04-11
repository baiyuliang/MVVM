package com.byl.mvvm.ui.base

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.viewbinding.ViewBinding
import com.byl.mvvm.api.HttpUtil
import com.byl.mvvm.api.error.ErrorResult
import com.byl.mvvm.api.error.ErrorUtil
import com.byl.mvvm.api.response.BaseResult
import com.byl.mvvm.util.Logg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class BaseViewModel<VB : ViewBinding> : ViewModel() {

    val httpUtil by lazy { HttpUtil.getInstance().getService() }
    var isShowLoading = MutableLiveData<Boolean>()//是否显示loading
    var errorData = MutableLiveData<ErrorResult>()//错误信息

    lateinit var vb: VB

    fun binding(vb: VB) {
        this.vb = vb
    }

    open fun observe(activity: Activity, owner: LifecycleOwner) {

    }

    open fun observe(fragment: Fragment, owner: LifecycleOwner) {

    }

    private fun showLoading() {
        isShowLoading.value = true
    }

    private fun dismissLoading() {
        isShowLoading.value = false
    }

    private fun showError(error: ErrorResult) {
        errorData.value = error
    }

    fun showError(msg: String) {
        val errorResult = ErrorResult()
        errorResult.show = true
        errorResult.errMsg = msg
        showError(errorResult)
    }

    /**
     * 请求接口，可定制是否显示 loading 和错误提示
     *
     * @param block  请求接口方法，T 表示 data 实体泛型，调用时可将 data 对应的 bean 传入即可
     * block：闭包（功能代码块，定义了其，为返回值为BaseResult的协程），
     * 相当于 val block={ suspend { httpUtil.getArticleList(page) } }
     *       val result=block()
     */
    fun <T> launch(
        block: suspend CoroutineScope.() -> BaseResult<T>,
        liveData: MutableLiveData<T>,
        isShowLoading: Boolean = true,
        isShowError: Boolean = true,
        apiIndex: Int = 0
    ) {
        if (isShowLoading) showLoading()
        viewModelScope.launch {
            try {
                val result = block()
                if (result.isSuccess()) {
                    // 请求成功
                    liveData.value = result.data
                } else {
                    Logg.e("请求错误>>" + result.errorMsg)
                    showError(ErrorResult(result.errorCode, result.errorMsg, isShowError, apiIndex))
                }
            } catch (e: Throwable) {
                // 接口请求失败
                Logg.e("请求异常>>" + e.message)
                val errorResult = ErrorUtil.getError(apiIndex, e)
                errorResult.show = isShowError
                showError(errorResult)
            } finally {
                // 请求结束
                dismissLoading()
            }
        }
    }

}
