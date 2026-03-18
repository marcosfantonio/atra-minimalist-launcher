package com.fantonio.atra.ui.screens


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fantonio.atra.AppInfo
import com.fantonio.atra.ui.components.StandarizedAppIcon
import com.fantonio.atra.ui.theme.Language
import com.fantonio.atra.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppListScreen(
    apps: List<AppInfo>,
    context: Context,
    hiddenApps: Set<String>,
    language: Language,
    viewModel: MainViewModel,
    prefs: SharedPreferences,
    notifications: Set<String>,
    onBackToHome: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedAppPackage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val filteredApps = remember(searchQuery, apps, hiddenApps) {
        apps.filter {
            it.name.contains(searchQuery, ignoreCase = true) &&
                    !hiddenApps.contains(it.packageName)
        }
    }
    BackHandler {
        if (selectedAppPackage != null) {
            selectedAppPackage = null
        } else {
            viewModel.pendingSlotIndex = -1
            onBackToHome()
        }
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .clickable(enabled = selectedAppPackage != null) { selectedAppPackage = null }
    ) {
        stickyHeader {
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(48.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 22.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            decorationBox = { innerTextField ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Box {
                                        if (searchQuery.isEmpty()) {
                                            Text(
                                                text = if (language == Language.PORTUGUESE) "Procurar..." else "Search...",
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 22.sp,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            })
                    }
                    Spacer(
                        modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.outline
                            )
                    )
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(filteredApps) { app ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = {
                            if (viewModel.pendingSlotIndex != -1) {
                                viewModel.saveAppToSlot(
                                    viewModel.pendingSlotIndex,
                                    app.packageName,
                                    prefs
                                )
                                onBackToHome()
                            } else {
                                onBackToHome()
                                val launchIntent =
                                    context.packageManager.getLaunchIntentForPackage(app.packageName)
                                if (launchIntent != null) {
                                    context.startActivity(launchIntent)
                                }
                            }
                        },
                        onLongClick = {
                            selectedAppPackage = app.packageName
                        }
                    )
                    .padding(vertical = 2.dp)
            ) {
                StandarizedAppIcon(app = app)

                Spacer(modifier = Modifier.width(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = app.name,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    if (notifications.contains(app.packageName)) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            modifier = Modifier.size(8.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.onBackground
                        ) {}
                    }
                }

                if (selectedAppPackage == app.packageName) {
                    Spacer(modifier = Modifier.weight(1f))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // App Info button
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.onBackground,
                                    CircleShape
                                )
                                .clickable {
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = Uri.parse("package:${app.packageName}")
                                    }
                                    context.startActivity(intent)
                                    selectedAppPackage = null
                                    scope.launch {
                                        delay(1500)
                                        onBackToHome()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "App Info",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(48.dp)) }
    }
}
