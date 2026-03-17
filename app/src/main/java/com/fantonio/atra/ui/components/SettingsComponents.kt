package com.fantonio.atra.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsComponents(label: String, content: @Composable () -> Unit ) {
    Column {
        Text(
            text = label,
            fontFamily = FontFamily.Monospace,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

@Composable
fun SettingsOptions(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Text(
        text = if (isSelected) "> $text" else "  $text",
        fontFamily = FontFamily.Monospace,
        fontSize = 20.sp,
        color = if (isSelected) MaterialTheme.colorScheme.onBackground else
            MaterialTheme.colorScheme.outline,
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 4.dp)
    )
}