package com.example.simplechat.core.ui.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.example.simplechat.core.ui.navigation.subnavigation.RouterProvider
import com.github.terrakok.cicerone.Router

abstract class BaseFragment(@LayoutRes layoutId: Int): Fragment(layoutId) {

    open fun prepareUi() = Unit
    open fun setupViewModel() = Unit

    fun getRouter(): Router =
            (parentFragment as? RouterProvider)?.provideRouter() ?: (activity as? RouterProvider)?.provideRouter()!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareUi()
        setupViewModel()
    }
}