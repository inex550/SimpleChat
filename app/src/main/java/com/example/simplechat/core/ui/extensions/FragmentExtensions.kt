package com.example.simplechat.core.ui.extensions

import android.os.Bundle
import androidx.fragment.app.Fragment

fun <T: Fragment> T.withArgs(block: Bundle.() -> Unit): T = apply {
    arguments = Bundle().apply(block)
}