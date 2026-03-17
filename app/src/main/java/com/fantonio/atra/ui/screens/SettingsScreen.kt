package com.fantonio.atra.ui.screens

import android.content.SharedPreferences
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fantonio.atra.ui.components.SettingsOptions
import com.fantonio.atra.ui.components.SettingsComponents
import com.fantonio.atra.ui.theme.Theme

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    currentTheme: Theme,
    onThemeChange: (Theme) -> Unit,
    prefs: SharedPreferences
) {
    var selectedLocale by remember { mutableStateOf("ENGLISH") }

    BackHandler { onBack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 48.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "SETTINGS",
            fontFamily = FontFamily.Monospace,
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        SettingsComponents(label = "THEME") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SettingsOptions("LIGHT", currentTheme == Theme.LIGHT) {
                    onThemeChange(Theme.LIGHT)
                }
                SettingsOptions("DARK", currentTheme == Theme.DARK) {
                    onThemeChange(Theme.DARK)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        SettingsComponents(label = "LOCALE") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingsOptions("ENGLISH", selectedLocale == "ENGLISH") { 
                    selectedLocale = "ENGLISH" 
                }
                SettingsOptions("BRAZILIAN PORTUGUESE", selectedLocale == "BRAZILIAN PORTUGUESE") { 
                    selectedLocale = "BRAZILIAN PORTUGUESE" 
                }
            }
        }
    }
}
