package com.byl.mvvm.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import com.byl.mvvm.R
import com.wang.avi.AVLoadingIndicatorView

/**
 * LoadingDialog
 *
 * @author lishide
 * @date 2020/7/25
 */
class LoadingDialog(context: Context) : Dialog(context) {

    private var mAVLoadingIndicatorView: AVLoadingIndicatorView

    init {
        this.setContentView(R.layout.layout_loading)
        // 设置 Dialog 参数
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val params = window?.attributes
        params?.gravity = Gravity.CENTER
        params?.dimAmount = 0f
        window?.attributes = params

        mAVLoadingIndicatorView = findViewById(R.id.pbLoading)
    }

    fun showLoading() {
        if (isShowing) {
            return
        }
        show()
    }

}