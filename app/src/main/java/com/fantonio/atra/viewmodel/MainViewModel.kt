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