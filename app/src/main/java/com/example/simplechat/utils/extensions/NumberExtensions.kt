package com.example.simplechat.utils.extensions

import android.content.Context
import android.util.DisplayMetrics

fun Int.dp(context: Context): Int {
    return this * (context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
}