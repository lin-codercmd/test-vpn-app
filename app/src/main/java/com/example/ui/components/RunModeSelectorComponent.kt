package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.RunMode
import com.example.ui.theme.*

@Composable
fun RunModeSelectorComponent(
    currentRunMode: RunMode,
    onRunModeChange: (RunMode) -> Unit,
    modifier: Modifier = Modifier
) {
    var showInfoDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("run_mode_selector"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = SecondaryIndigo,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "CORE RUN MODE",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = OnSurfaceSubtext
                )
            }

            IconButton(
                onClick = { showInfoDialog = true },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Run Mode Info",
                    tint = OnSurfaceSubtext,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Run Mode Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RunMode.values().forEach { mode ->
                val isSelected = mode == currentRunMode

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) PrimaryCyan else BorderOutline.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { onRunModeChange(mode) },
                    color = if (isSelected) PrimaryCyan.copy(alpha = 0.15f) else DarkSurfaceCard
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = mode.title,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) PrimaryCyan else OnSurfaceText,
                            maxLines = 1
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (mode.requiresRoot) AccentAmber.copy(alpha = 0.2f) else SecondaryIndigo.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = mode.badge,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (mode.requiresRoot) AccentAmber else Color(0xFFA5B4FC),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            containerColor = DarkSurface,
            title = {
                Text(
                    text = "AsteriskNG Execution Modes",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = OnSurfaceText
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RunMode.values().forEach { mode ->
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = mode.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = PrimaryCyan
                                )
                                Text(
                                    text = "[${mode.badge}]",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (mode.requiresRoot) AccentAmber else SecondaryIndigo
                                )
                            }
                            Text(
                                text = mode.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceSubtext
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("Got It", color = PrimaryCyan)
                }
            }
        )
    }
}
