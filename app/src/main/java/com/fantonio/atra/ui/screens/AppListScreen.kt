package com.fantonio.atra.ui.screens


import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppListScreen(
    apps: List<AppInfo>,
    context: Context,
    hiddenApps: Set<String>,
    language: Language,
    onBackToHome: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredApps = remember(searchQuery, apps, hiddenApps) {
        apps.filter {
            it.name.contains(searchQuery, ignoreCase = true) &&
                    !hiddenApps.contains(it.packageName)
        }
    }
    BackHandler {
        onBackToHome()
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
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
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = if (language == Language.PORTUGUESE) "Procurar..." else "Search...",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 22.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                                innerTextField()
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
                    .clickable {
                        onBackToHome()
                        val launchIntent =
                            context.packageManager.getLaunchIntentForPackage(app.packageName)
                        if (launchIntent != null) {
                            context.startActivity(launchIntent)
                        }
                    }
                    .padding(vertical = 2.dp)
            ) {
                StandarizedAppIcon(app = app)

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = app.name,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }

        item { Spacer(modifier = Modifier.height(48.dp)) }
    }
}
