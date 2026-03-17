package com.fantonio.atra.ui.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fantonio.atra.AppInfo
import com.fantonio.atra.ui.components.StandarizedAppIcon
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    apps: List<AppInfo>,
    context: Context,
    onOpenDrawer: () -> Unit,
    onOpenSettings: () -> Unit
) {
    var currentTime by remember { mutableStateOf("")}
    var currentDate by remember {  mutableStateOf("")}
    var batteryLevel by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while(true) {
            val now = LocalDateTime.now()
            currentTime = now.format(DateTimeFormatter.ofPattern(
                "h:mm a", Locale.ENGLISH)).uppercase()
            currentDate = now.format(DateTimeFormatter.ofPattern(
                "EEEE, MMMM d", Locale.ENGLISH))

            val bm = context.getSystemService(Context.BATTERY_SERVICE)
                    as android.os.BatteryManager
            batteryLevel = bm.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY)

            delay(10000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 48.dp)
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount > -20f) {
                        onOpenDrawer()
                    }
                }
            },
    ) {
        Spacer(modifier = Modifier.height(120.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 48.dp)
        ) {
            Text(text = currentTime,
                fontFamily = FontFamily.Monospace,
                fontSize = 48.sp,
                color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = currentDate,
                fontFamily = FontFamily.Monospace,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BAT. $batteryLevel", fontFamily = FontFamily.Monospace,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    imageVector = Icons.Default.BatteryFull,
                    contentDescription = "Baterry",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            apps.forEach { app ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
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
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)

        ) {
            Icon(
                imageVector = Icons.Default.Apps,
                contentDescription = "Apps",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .align(Alignment.Center)
                    .clickable { onOpenDrawer() }
                    .padding(16.dp)
                    .size(42.dp)
            )
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable { onOpenSettings() }
                    .padding(16.dp)
                    .size(42.dp)
            )
        }
    }
}