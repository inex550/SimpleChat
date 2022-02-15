package com.example.simplechat.core.ui.extensions

import android.view.View

fun View.makeVisible(visible: Boolean) {
    visibility = if (visible)
        View.VISIBLE
    else
        View.GONE
}