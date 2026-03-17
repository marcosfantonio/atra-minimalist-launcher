package com.fantonio.atra.ui.screens

import android.content.Context
import android.os.BatteryManager
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.fantonio.atra.ui.theme.Language
import com.fantonio.atra.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    apps: List<AppInfo>,
    context: Context,
    language: Language,
    viewModel: MainViewModel,
    onOpenDrawer: () -> Unit,
    onOpenSettings: () -> Unit
) {
    var currentTime by remember { mutableStateOf("")}
    var currentDate by remember {  mutableStateOf("")}
    var batteryLevel by remember { mutableStateOf(0) }
    var slotToRemove by remember { mutableStateOf<Int?>(null) }

    val homeSlots by viewModel.homeSlots.collectAsState()
    val locale = if (language == Language.PORTUGUESE) Locale.forLanguageTag("pt-BR") else Locale.ENGLISH

    val timePattern = if (language == Language.PORTUGUESE) "hh:mm a" else "h:mm a"
    val datePattern = if (language == Language.PORTUGUESE) "EEEE, d 'de' MMMM" else "EEEE, MMMM d"

    val batteryText = "BAT."

    LaunchedEffect(language) {
        while(true) {
            val now = LocalDateTime.now()
            currentTime = now.format(DateTimeFormatter.ofPattern(timePattern, locale)).uppercase()
            
            val rawDate = now.format(DateTimeFormatter.ofPattern(datePattern, locale))
            currentDate = if (language == Language.PORTUGUESE) {
                rawDate.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
            } else {
                rawDate
            }

            val bm = context.getSystemService(Context.BATTERY_SERVICE)
                    as BatteryManager
            batteryLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

            delay(10000)
        }
    }

    val batteryIcon = remember(batteryLevel) {
        when {
            batteryLevel >= 95 -> Icons.Default.BatteryFull
            batteryLevel >= 85 -> Icons.Default.Battery6Bar
            batteryLevel >= 70 -> Icons.Default.Battery5Bar
            batteryLevel >= 55 -> Icons.Default.Battery4Bar
            batteryLevel >= 40 -> Icons.Default.Battery3Bar
            batteryLevel >= 25 -> Icons.Default.Battery2Bar
            batteryLevel >= 10 -> Icons.Default.Battery1Bar
            else -> Icons.Default.Battery0Bar
        }
    }

    val prefs = remember { context.getSharedPreferences("atra", Context.MODE_PRIVATE) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 48.dp)
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -20f) {
                        onOpenDrawer()
                    }
                }
            }
            .clickable(enabled = slotToRemove != null) { slotToRemove = null },
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
                    text = "$batteryText $batteryLevel", fontFamily = FontFamily.Monospace,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onBackground)

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    imageVector = batteryIcon,
                    contentDescription = if (language == Language.PORTUGUESE) "Bateria" else "Battery",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            for (i in 0..4) {
                val packageName = homeSlots[i]
                val app = apps.find { it.packageName == packageName }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = {
                                if (app != null) {
                                    val launchIntent =
                                        context.packageManager.getLaunchIntentForPackage(app.packageName)
                                    if (launchIntent != null) {
                                        context.startActivity(launchIntent)
                                    }
                                } else {
                                    viewModel.pendingSlotIndex = i
                                    onOpenDrawer()
                                }
                            },
                            onLongClick = {
                                if (app != null) {
                                    slotToRemove = i
                                }
                            }
                        )
                ) {
                    Box(
                        modifier = Modifier.size(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (app != null) {
                            StandarizedAppIcon(app = app)
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .border(1.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add app",
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    if (app != null) {
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = app.name,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )

                        if (slotToRemove == i) {
                            Spacer(modifier = Modifier.width(16.dp))
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .border(1.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(4.dp))
                                    .clickable {
                                        viewModel.removeAppFromSlot(i, prefs)
                                        slotToRemove = null
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Remove app",
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
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
                contentDescription = if (language == Language.PORTUGUESE) "Apps" else "Apps",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .align(Alignment.Center)
                    .clickable { onOpenDrawer() }
                    .padding(16.dp)
                    .size(42.dp)
            )
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = if (language == Language.PORTUGUESE) "Configurações" else "Settings",
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
