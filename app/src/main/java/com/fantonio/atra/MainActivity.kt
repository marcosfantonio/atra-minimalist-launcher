package com.fantonio.atra

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import com.fantonio.atra.ui.components.AppListManager
import com.fantonio.atra.ui.screens.AppListScreen
import com.fantonio.atra.ui.screens.HomeScreen
import com.fantonio.atra.ui.screens.SettingsScreen
import com.fantonio.atra.ui.theme.AtraTheme
import com.fantonio.atra.ui.theme.Theme
import com.fantonio.atra.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()
    private val appInstallReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModel.refreshApps(this@MainActivity)
        }
    }

    private fun goBackToHome() {
        viewModel.navigateTo("HOME")
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasCategory(Intent.CATEGORY_HOME) || intent.action == Intent.ACTION_MAIN) {
            goBackToHome()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        val prefs = getSharedPreferences("atra", MODE_PRIVATE)

        setContent {
            val currentTheme by viewModel.theme.collectAsState()
            val currentLanguage by viewModel.language.collectAsState()
            val hiddenApps by viewModel.hiddenApps.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.loadApps(this@MainActivity)
                viewModel.loadSettings(prefs)
            }

            // Update status/navigation bar appearance based on theme
            LaunchedEffect(currentTheme) {
                val isDarkTheme = currentTheme == Theme.DARK
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { isDarkTheme },
                    navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { isDarkTheme }
                )
                
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !isDarkTheme
                    isAppearanceLightNavigationBars = !isDarkTheme
                }
            }

            AppListManager(viewModel) { myApps ->
                AtraTheme(theme = currentTheme) {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        when (currentScreen) {
                            "HOME" -> HomeScreen(
                                apps = myApps,
                                context = this@MainActivity,
                                language = currentLanguage,
                                viewModel = viewModel,
                                onOpenDrawer = { viewModel.navigateTo("DRAWER")},
                                onOpenSettings = { viewModel.navigateTo("SETTINGS")}
                            )
                            "DRAWER" -> AppListScreen(
                                apps = myApps,
                                context = this@MainActivity,
                                hiddenApps = hiddenApps,
                                language = currentLanguage,
                                viewModel = viewModel,
                                prefs = prefs,
                                notifications = emptySet(),
                                onBackToHome = { goBackToHome() }
                            )
                            "SETTINGS" -> SettingsScreen(
                                onBack = { goBackToHome() },
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
