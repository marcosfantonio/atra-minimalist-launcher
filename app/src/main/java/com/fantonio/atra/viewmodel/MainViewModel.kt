package com.fantonio.atra.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.fantonio.atra.AppInfo
import com.fantonio.atra.data.AppRepository
import com.fantonio.atra.ui.theme.Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {
    private val repository = AppRepository()

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps

    private val _hiddenApps = MutableStateFlow<Set<String>>(emptySet())
    val hiddenApps: StateFlow<Set<String>> = _hiddenApps

    fun loadHiddenApps(prefs: android.content.SharedPreferences) {
        _hiddenApps.value = prefs.getStringSet("hiddenApps", emptySet()) ?: emptySet()
    }

    fun toggleAppVisibility(packageName: String, prefs: android.content.SharedPreferences) {
        val currentSet = _hiddenApps.value.toMutableSet()

        if (currentSet.contains(packageName)) {
            currentSet.remove(packageName)
        } else {
            currentSet.add(packageName)
        }

        _hiddenApps.value = currentSet
        prefs.edit().putStringSet("hiddenApps", currentSet).apply()
    }

    private val _theme = MutableStateFlow(Theme.LIGHT)
    val theme: StateFlow<Theme> = _theme

    fun loadApps(context: Context) {
        _apps.value = repository.getInstalledApps(context)
    }

    fun refreshApps(context: Context) {
        loadApps(context)
    }

    fun setTheme(theme: Theme, prefs: android.content.SharedPreferences) {
        _theme.value = theme
        prefs.edit().putString("theme", theme.name).apply()
    }
}
