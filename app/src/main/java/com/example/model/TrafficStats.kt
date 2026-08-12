package com.example.model

data class TrafficStats(
    val rxBytesPerSec: Long = 0,
    val txBytesPerSec: Long = 0,
    val totalRxBytes: Long = 0,
    val totalTxBytes: Long = 0,
    val activeConnections: Int = 0,
    val uptimeSeconds: Long = 0,
    val daemonStatus: String = "asteriskd: RUNNING"
) {
    fun formattedRxRate(): String = formatBytesRate(rxBytesPerSec)
    fun formattedTxRate(): String = formatBytesRate(txBytesPerSec)
    fun formattedTotalRx(): String = formatBytes(totalRxBytes)
    fun formattedTotalTx(): String = formatBytes(totalTxBytes)

    private fun formatBytesRate(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 * 1024 -> String.format("%.2f GB/s", bytes / (1024.0 * 1024.0 * 1024.0))
            bytes >= 1024 * 1024 -> String.format("%.1f MB/s", bytes / (1024.0 * 1024.0))
            bytes >= 1024 -> String.format("%.0f KB/s", bytes / 1024.0)
            else -> "$bytes B/s"
        }
    }

    private fun formatBytes(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 * 1024 -> String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0))
            bytes >= 1024 * 1024 -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
            bytes >= 1024 -> String.format("%.0f KB", bytes / 1024.0)
            else -> "$bytes B"
        }
    }
}
