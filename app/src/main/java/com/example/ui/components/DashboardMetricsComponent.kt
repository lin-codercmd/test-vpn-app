package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.ConnectionState
import com.example.model.TrafficStats
import com.example.ui.theme.*

@Composable
fun DashboardMetricsComponent(
    trafficStats: TrafficStats,
    connectionState: ConnectionState,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, BorderOutline.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .testTag("dashboard_metrics"),
        color = DarkSurfaceCard,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Live Speed Gauge Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Download Speed Card
                SpeedMeterCard(
                    title = "DOWNLOAD",
                    speedText = if (connectionState == ConnectionState.CONNECTED) trafficStats.formattedRxRate() else "0.0 B/s",
                    totalText = "Total: ${trafficStats.formattedTotalRx()}",
                    icon = Icons.Default.ArrowDownward,
                    accentColor = PrimaryCyan,
                    modifier = Modifier.weight(1f)
                )

                // Upload Speed Card
                SpeedMeterCard(
                    title = "UPLOAD",
                    speedText = if (connectionState == ConnectionState.CONNECTED) trafficStats.formattedTxRate() else "0.0 B/s",
                    totalText = "Total: ${trafficStats.formattedTotalTx()}",
                    icon = Icons.Default.ArrowUpward,
                    accentColor = TertiaryEmerald,
                    modifier = Modifier.weight(1f)
                )
            }

            // Connection Stats Footer: Sockets & Daemon PID
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
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = null,
                        tint = OnSurfaceSubtext,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (connectionState == ConnectionState.CONNECTED)
                            "${trafficStats.activeConnections} Active Sockets" else "0 Sockets",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceSubtext
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (connectionState == ConnectionState.CONNECTED) PrimaryCyan else OnSurfaceSubtext
                            )
                    )
                    Text(
                        text = trafficStats.daemonStatus,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = if (connectionState == ConnectionState.CONNECTED) PrimaryCyan else OnSurfaceSubtext
                    )
                }
            }
        }
    }
}

@Composable
private fun SpeedMeterCard(
    title: String,
    speedText: String,
    totalText: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = DarkSurfaceVariant.copy(alpha = 0.6f),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, accentColor.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = OnSurfaceSubtext
                )
                Text(
                    text = speedText,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = OnSurfaceText
                )
                Text(
                    text = totalText,
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceSubtext
                )
            }
        }
    }
}
