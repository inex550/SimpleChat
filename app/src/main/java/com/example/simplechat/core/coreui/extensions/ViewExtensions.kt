package com.example.simplechat.core.coreui.extensions

import android.view.View

fun View.makeVisible(visible: Boolean) {
    visibility = if (visible)
        View.VISIBLE
    else
        View.GONE
}