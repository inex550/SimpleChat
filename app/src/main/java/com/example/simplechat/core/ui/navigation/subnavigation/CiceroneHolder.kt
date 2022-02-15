package com.example.simplechat.core.ui.navigation.subnavigation

import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router

class CiceroneHolder {

    private val containers = HashMap<String, Cicerone<Router>>()

    fun getCicerone(tag: String): Cicerone<Router> =
        containers.getOrPut(tag) {
            Cicerone.create()
        }
}