package com.fantonio.atra.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fantonio.atra.AppInfo
import com.fantonio.atra.data.AppRepository
import com.fantonio.atra.ui.theme.Theme
import com.fantonio.atra.ui.theme.Language
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {
    private val repository = AppRepository()

    private val _homeSlots = MutableStateFlow<Map<Int, String>>(emptyMap())
    val homeSlots: StateFlow<Map<Int, String>> = _homeSlots
    var pendingSlotIndex by mutableIntStateOf(-1)

    private val _currentScreen = MutableStateFlow("HOME")
    val currentScreen: StateFlow<String> = _currentScreen

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    fun loadHomeSlots(prefs: SharedPreferences) {
        val map = mutableMapOf<Int, String>()
        for (i in 0..4) {
            val pkg = prefs.getString("home_slot_$i", null)
            if (pkg != null) map[i] = pkg
        }
        _homeSlots.value = map
    }

    fun saveAppToSlot(slotIndex: Int, packageName: String, prefs: SharedPreferences) {
        val current = _homeSlots.value.toMutableMap()
        current[slotIndex] = packageName
        _homeSlots.value = current
        prefs.edit().putString("home_slot_$slotIndex", packageName).apply()
        pendingSlotIndex = -1
    }

    fun removeAppFromSlot(slotIndex: Int, prefs: SharedPreferences) {
        val current = _homeSlots.value.toMutableMap()
        current.remove(slotIndex)
        _homeSlots.value = current
        prefs.edit().remove("home_slot_$slotIndex").apply()
    }

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps

    private val _hiddenApps = MutableStateFlow<Set<String>>(emptySet())
    val hiddenApps: StateFlow<Set<String>> = _hiddenApps

    private val _theme = MutableStateFlow(Theme.LIGHT)
    val theme: StateFlow<Theme> = _theme

    private val _language = MutableStateFlow(Language.ENGLISH)
    val language: StateFlow<Language> = _language

    fun loadHiddenApps(prefs: SharedPreferences) {
        _hiddenApps.value = prefs.getStringSet("hiddenApps", emptySet()) ?: emptySet()
    }

    fun toggleAppVisibility(packageName: String, prefs: SharedPreferences) {
        val currentSet = _hiddenApps.value.toMutableSet()

        if (currentSet.contains(packageName)) {
            currentSet.remove(packageName)
        } else {
            currentSet.add(packageName)
        }

        _hiddenApps.value = currentSet
        prefs.edit().putStringSet("hiddenApps", currentSet).apply()
    }

    fun loadApps(context: Context) {
        viewModelScope.launch {
            val installedApps = withContext(Dispatchers.IO) {
                repository.getInstalledApps(context)
            }
            _apps.value = installedApps
        }
    }

    fun refreshApps(context: Context) {
        loadApps(context)
    }

    fun setTheme(theme: Theme, prefs: SharedPreferences) {
        _theme.value = theme
        prefs.edit().putString("theme", theme.name).apply()
    }

    fun setLanguage(language: Language, prefs: SharedPreferences) {
        _language.value = language
        prefs.edit().putString("language", language.name).apply()
    }
    
    fun loadSettings(prefs: SharedPreferences) {
        loadHiddenApps(prefs)
        loadHomeSlots(prefs)
        
        val themeName = prefs.getString("theme", Theme.LIGHT.name) ?: Theme.LIGHT.name
        _theme.value = try { Theme.valueOf(themeName) } catch (e: Exception) { Theme.LIGHT }
        
        val langName = prefs.getString("language", Language.ENGLISH.name) ?: Language.ENGLISH.name
        _language.value = try { Language.valueOf(langName) } catch (e: Exception) { Language.ENGLISH }
    }
}
