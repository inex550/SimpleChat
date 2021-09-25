package com.example.simplechat.core.coreui.util

import android.os.Bundle
import androidx.fragment.app.Fragment

fun <T: Fragment> T.withArgs(applyArgs: Bundle.() -> Unit): T = apply {
    arguments = Bundle().apply { applyArgs() }
}