package com.fantonio.atra.ui.screens

import android.content.SharedPreferences
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fantonio.atra.AppInfo
import com.fantonio.atra.ui.components.SettingsOptions
import com.fantonio.atra.ui.components.SettingsComponents
import com.fantonio.atra.ui.theme.Theme

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    currentTheme: Theme,
    onThemeChange: (Theme) -> Unit,
    prefs: SharedPreferences,
    allApps: List<AppInfo>,
    hiddenApps: Set<String>,
    onToggleHide: (String) -> Unit
) {
    var isHideAppsExpanded by remember { mutableStateOf(false)}

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
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(48.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                SettingsComponents(label = "THEME") {
                    Column {
                        Theme.entries.forEach { theme ->
                            SettingsOptions(
                                text = theme.name,
                                isSelected = currentTheme == theme,
                                onClick = { onThemeChange(theme) }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                SettingsComponents(label = "APPS") {
                    SettingsOptions(
                        text = "HIDE APPS",
                        isSelected = isHideAppsExpanded,
                        onClick = { isHideAppsExpanded = !isHideAppsExpanded }
                    )

                    AnimatedVisibility(visible = isHideAppsExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, top = 8.dp)
                        ) {
                            allApps.sortedBy { it.name }.forEach { app ->
                                val isHidden = hiddenApps.contains(app.packageName)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = app.name.uppercase(),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 18.sp,
                                        color = if (isHidden) MaterialTheme.colorScheme.outline else
                                            MaterialTheme.colorScheme.onBackground
                                    )
                                    Icon(
                                        imageVector = if (isHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clickable { onToggleHide(app.packageName) },
                                        tint = if (isHidden) MaterialTheme.colorScheme.outline else
                                            MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
