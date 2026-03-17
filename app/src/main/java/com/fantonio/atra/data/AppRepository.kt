package com.fantonio.atra.data

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import com.fantonio.atra.AppInfo

class AppRepository {
    fun getInstalledApps(context: Context): List<AppInfo> {
        val packageManager = context.packageManager
        val appList = mutableListOf<AppInfo>()

        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val allApps: List<ResolveInfo> = packageManager.queryIntentActivities(intent, 0)

        for (resolveInfo in allApps) {
            val icon = resolveInfo.loadIcon(packageManager)
            var isNativeMonochrome = false
            var isAdaptive = false

            val finalIcon = if (icon is AdaptiveIconDrawable) {
                isAdaptive = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    icon.monochrome != null
                ) {
                    isNativeMonochrome = true
                    icon.monochrome!!
                } else {
                    icon.foreground
                }
            } else {
                icon
            }

            val name = resolveInfo.loadLabel(packageManager).toString()
            val packageName = resolveInfo.activityInfo.packageName
            appList.add(AppInfo(name, packageName, finalIcon, isNativeMonochrome, isAdaptive))

        }

        return appList.sortedBy { it.name.lowercase() }
    }
}