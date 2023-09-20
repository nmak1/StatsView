package ru.netology.statsview.utils

import android.content.Context

object AndroidUtils {

    fun dp(context: Context, dp: Int): Int =
        (context.resources.displayMetrics.density * dp).toInt()
}