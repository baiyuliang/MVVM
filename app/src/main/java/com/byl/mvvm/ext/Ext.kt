package com.byl.mvvm.ext

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes

/**
 * show toast
 * @param text text
 */
@SuppressLint("ShowToast")
fun Context.toast(text: CharSequence?, duration: Int = Toast.LENGTH_SHORT, view: View? = null) {
    if (text.isNullOrEmpty()) return

    Toast.makeText(this.applicationContext, text, duration).apply {
        view?.let { setView(it) }
    }.show()
}

/**
 * show toast
 * @param id strings.xml
 */
fun Context.toast(@StringRes id: Int) {
    toast(getString(id))
}

