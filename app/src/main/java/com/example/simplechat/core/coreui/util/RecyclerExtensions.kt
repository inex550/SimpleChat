package com.example.simplechat.core.coreui.util

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.isScrolledInEnd(): Boolean {

    val layoutManager = layoutManager as? LinearLayoutManager ?: let {
        return false
    }

    val visibleChildCount = layoutManager.childCount
    val totalChildCount = layoutManager.itemCount
    val passedChildCount = layoutManager.findFirstCompletelyVisibleItemPosition()

    return (visibleChildCount + passedChildCount) >= totalChildCount
}