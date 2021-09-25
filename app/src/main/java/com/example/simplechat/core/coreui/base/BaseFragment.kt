package com.example.simplechat.core.coreui.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

abstract class BaseFragment(@LayoutRes layoutId: Int): Fragment(layoutId) {

    open fun prepareUi() = Unit
    open fun setupViewModel() = Unit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareUi()
        setupViewModel()
    }
}