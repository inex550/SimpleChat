package com.example.simplechat.core.coreui.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.example.simplechat.core.coreui.navigation.subnavigation.RouterProvider
import com.github.terrakok.cicerone.Router

abstract class BaseFragment(@LayoutRes layoutId: Int): Fragment(layoutId) {

    open fun prepareUi() = Unit
    open fun setupViewModel() = Unit

    fun getRouter(local: Boolean = true): Router =
        if (local)
            (parentFragment as? RouterProvider)?.provideRouter() ?: (activity as? RouterProvider)?.provideRouter()!!
        else
            (activity as? RouterProvider)?.provideRouter()!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareUi()
        setupViewModel()
    }
}