package com.fantonio.atra

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fantonio.atra.ui.theme.AtraTheme
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: Drawable,
    val isNativeMonochrome: Boolean,
    val isAdaptive: Boolean
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val myApps = getInstalledApps(this)
        setContent {
            AtraTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    var currentScreen by remember { mutableStateOf("HOME") }
                    val homeApps = myApps.take(8)

                    when(currentScreen) {
                        "HOME" -> AtraHomeScreen(
                            apps = homeApps,
                            context = this@MainActivity,
                            onOpenDrawer = { currentScreen = "DRAWER" },
                            onOpenSettings = { currentScreen = "SETTINGS" }
                        )
                        "DRAWER" -> AtraAppList(
                            apps = myApps,
                            context = this@MainActivity,
                            onBackToHome = { currentScreen = "HOME" }
                        )
                        "SETTINGS" -> AtraSettingsScreen(
                            onBack = { currentScreen = "HOME" }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StandardizedAppIcon(app: AppInfo, modifier: Modifier = Modifier) {
    val colorFilter = remember(app.isNativeMonochrome) {
        if (app.isNativeMonochrome) {
            ColorFilter.tint(Color.Black)
        } else {
            val matrix = ColorMatrix().apply {
                setToSaturation(0f)
                val invertMatrix = ColorMatrix(
                    floatArrayOf(
                        -1f, 0f, 0f, 0f, 255f,
                        0f, -1f, 0f, 0f, 255f,
                        0f, 0f, -1f, 0f, 255f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                timesAssign(invertMatrix)
            }
            ColorFilter.colorMatrix(matrix)
        }
    }
    Box(
        modifier = modifier.size(48.dp),
        contentAlignment = Alignment.Center
    ) {
        val iconModifier = remember(app.isNativeMonochrome, app.isAdaptive) {
            if (app.isNativeMonochrome) {
                Modifier
                    .fillMaxSize()
                    .padding(if (app.isAdaptive) 0.dp else 8.dp)
            } else {
                Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
            }
        }

        Image(
            painter = rememberDrawablePainter(drawable = app.icon),
            contentDescription = null,
            modifier = iconModifier,
            colorFilter = colorFilter
        )
    }
}

@Composable
fun AtraAppList(apps: List<AppInfo>, context: Context, onBackToHome: () -> Unit) {
    BackHandler {
        onBackToHome()
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 48.dp)
    ) {
        item {
            Text(
                text = "[VOLTAR]",
                fontFamily = FontFamily.Monospace,
                fontSize = 18.sp,
                color = Color.Gray,
                modifier = Modifier
                    .clickable { onBackToHome() }
                    .padding(vertical = 16.dp)
            )
        }
        items(apps) { app ->
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
                StandardizedAppIcon(app = app)

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = app.name,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 22.sp,
                    color = Color.Black,
                )
            }
        }
    }
}

@Composable
fun AtraHomeScreen(
    apps: List<AppInfo>,
    context: Context,
    onOpenDrawer: () -> Unit,
    onOpenSettings: () -> Unit
) {
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }
    var batteryLevel by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            val now = LocalDateTime.now()
            currentTime = now.format(DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)).uppercase()
            currentDate = now.format(DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.ENGLISH))

            val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            batteryLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

            delay(10000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 48.dp)
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
                color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = currentDate,
                fontFamily = FontFamily.Monospace,
                fontSize = 22.sp,
                color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BAT. $batteryLevel", fontFamily = FontFamily.Monospace,
                    fontSize = 22.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    imageVector = Icons.Default.BatteryFull,
                    contentDescription = "Bateria",
                    tint = Color.Black,
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
                    StandardizedAppIcon(app = app)

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = app.name,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 22.sp,
                        color = Color.Black,
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
                tint = Color.Black,
                modifier = Modifier
                    .align(Alignment.Center)
                    .clickable { onOpenDrawer() }
                    .padding(16.dp)
                    .size(42.dp)
            )
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Configuration",
                tint = Color.Black,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable { onOpenSettings() }
                    .padding(16.dp)
                    .size(42.dp)
            )
        }
    }
}

@Composable
fun AtraSettingsScreen(onBack: () -> Unit) {
    var selectedTheme by remember { mutableStateOf("LIGHT") }
    var selectedLocale by remember { mutableStateOf("ENGLISH") }

    BackHandler() { onBack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 48.dp)
    ) {
        Text(
            text = "[BACK]",
            fontFamily = FontFamily.Monospace,
            fontSize = 18.sp,
            color = Color.Gray,
            modifier = Modifier
                .clickable { onBack() }
                .padding(vertical = 16.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "SETTINGS",
            fontFamily = FontFamily.Monospace,
            fontSize = 32.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        SettingsSection(label = "THEME") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SettingsOption("LIGHT", selectedTheme == "LIGHT") { selectedTheme = "LIGHT" }
                SettingsOption("DARK", selectedTheme == "DARK") { selectedTheme = "DARK" }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        SettingsSection(label = "LOCALE") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingsOption("ENGLISH", selectedLocale == "ENGLISH") { selectedLocale = "ENGLISH" }
                SettingsOption("BRAZILIAN PORTUGUESE", selectedLocale == "BRAZILIAN PORTUGUESE") {
                    selectedLocale = "BRAZILIAN PORTUGUESE"
                }
            }
        }
    }
}

@Composable
fun SettingsSection(label: String, content: @Composable () -> Unit) {
    Column {
        Text (
            text = label,
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

@Composable
fun SettingsOption(text: String, isSelected: Boolean, onCLick: () -> Unit) {
    Text (
        text = if (isSelected) "> $text" else "  $text",
        fontFamily = FontFamily.Monospace,
        fontSize = 20.sp,
        color = if (isSelected) Color.Black else Color.LightGray,
        modifier = Modifier
            .clickable { onCLick() }
            .padding(vertical = 4.dp)
    )
}

fun getInstalledApps(context: Context): List<AppInfo> {
    val packageManager = context.packageManager
    val appList = mutableListOf<AppInfo>()

    val intent = Intent(Intent.ACTION_MAIN, null).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }

    val allApps: List<ResolveInfo> = packageManager.queryIntentActivities(intent, 0)

    for (resolveInfo in allApps) {
        val icon = resolveInfo.loadIcon(packageManager)
        var isNativeMonochrome = false
        var isAdaptive = false
        
        val finalIcon = if (icon is AdaptiveIconDrawable) {
            isAdaptive = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && icon.monochrome != null) {
                isNativeMonochrome = true
                icon.monochrome!!
            } else {
                // Use the foreground layer to match the scale/behavior of the monochrome layer
                icon.foreground
            }
        } else {
            icon
        }

        val name = resolveInfo.loadLabel(packageManager).toString()
        val packageName = resolveInfo.activityInfo.packageName
        appList.add(AppInfo(name, packageName, finalIcon, isNativeMonochrome, isAdaptive))
    }

    return appList.sortedBy { it.name.lowercase() }
}
