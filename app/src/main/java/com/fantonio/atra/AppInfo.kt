package com.fantonio.atra

import android.graphics.drawable.Drawable

data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: Drawable,
    val isNativeMonochrome: Boolean,
    val isAdaptive: Boolean
)
