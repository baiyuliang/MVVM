package com.byl.mvvm.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byl.mvvm.api.HttpUtil
import com.byl.mvvm.api.error.ErrorResult
import com.byl.mvvm.api.error.ErrorUtil
import com.byl.mvvm.api.response.BaseResult
import com.byl.mvvm.utils.Logg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class BaseViewModel : ViewModel() {

    val httpUtil by lazy { HttpUtil.getInstance().getService() }

    var isShowLoading = MutableLiveData<Boolean>() // 是否显示 loading
    var errorData = MutableLiveData<ErrorResult>() // 错误信息

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
                val result = withContext(Dispatchers.IO) { block() }
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
