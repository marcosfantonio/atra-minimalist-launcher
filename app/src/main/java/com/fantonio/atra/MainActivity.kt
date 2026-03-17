package com.fantonio.atra

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import com.fantonio.atra.ui.components.AppListManager
import com.fantonio.atra.ui.screens.AppListScreen
import com.fantonio.atra.ui.screens.HomeScreen
import com.fantonio.atra.ui.screens.SettingsScreen
import com.fantonio.atra.ui.theme.AtraTheme
import com.fantonio.atra.ui.theme.Theme
import com.fantonio.atra.ui.theme.Language
import com.fantonio.atra.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()
    private val appInstallReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModel.refreshApps(this@MainActivity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val prefs = getSharedPreferences("atra", MODE_PRIVATE)

        setContent {
            val currentTheme by viewModel.theme.collectAsState()
            val currentLanguage by viewModel.language.collectAsState()
            val hiddenApps by viewModel.hiddenApps.collectAsState()
            
            LaunchedEffect(Unit) {
                viewModel.loadApps(this@MainActivity)
                viewModel.loadSettings(prefs)
            }

            AppListManager(viewModel) { myApps ->
                AtraTheme(theme = currentTheme) {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        val currentScreen = remember { mutableStateOf("HOME") }

                        when (currentScreen.value) {
                            "HOME" -> HomeScreen(
                                apps = myApps,
                                context = this@MainActivity,
                                hiddenApps = hiddenApps,
                                language = currentLanguage,
                                onOpenDrawer = { currentScreen.value = "DRAWER"},
                                onOpenSettings = { currentScreen.value = "SETTINGS"}
                            )
                            "DRAWER" -> AppListScreen(
                                apps = myApps,
                                context = this@MainActivity,
                                hiddenApps = hiddenApps,
                                language = currentLanguage,
                                onBackToHome = { currentScreen.value = "HOME"}
                            )
                            "SETTINGS" -> SettingsScreen(
                                onBack = { currentScreen.value = "HOME"},
                                currentTheme = currentTheme,
                                onThemeChange = { viewModel.setTheme(it, prefs) },
                                currentLanguage = currentLanguage,
                                onLanguageChange = { viewModel.setLanguage(it, prefs) },
                                prefs = prefs,
                                allApps = myApps,
                                hiddenApps = hiddenApps,
                                onToggleHide = { viewModel.toggleAppVisibility(it, prefs) }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(appInstallReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        })
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(appInstallReceiver)
    }
}
