package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.*
import com.example.ui.theme.*
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProfileDialog(
    profileToEdit: ProxyProfile?,
    onDismiss: () -> Unit,
    onSaveProfile: (ProxyProfile) -> Unit
) {
    var name by remember { mutableStateOf(profileToEdit?.name ?: "New Xray Node") }
    var serverAddress by remember { mutableStateOf(profileToEdit?.serverAddress ?: "node.asterisk.net") }
    var portText by remember { mutableStateOf(profileToEdit?.port?.toString() ?: "443") }
    var selectedProtocol by remember { mutableStateOf(profileToEdit?.protocol ?: ProxyProtocol.VLESS) }
    var selectedTransport by remember { mutableStateOf(profileToEdit?.transport ?: TransportMethod.GRPC) }
    var selectedSecurity by remember { mutableStateOf(profileToEdit?.security ?: SecurityType.REALITY) }
    var sni by remember { mutableStateOf(profileToEdit?.sni ?: "gateway.cloudflare.com") }
    var path by remember { mutableStateOf(profileToEdit?.path ?: "") }
    var countryCode by remember { mutableStateOf(profileToEdit?.countryCode ?: "US") }
    var importUrl by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                text = if (profileToEdit == null) "Add Server Node" else "Edit Node Configuration",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = OnSurfaceText
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .testTag("add_edit_profile_dialog"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (profileToEdit == null) {
                    OutlinedTextField(
                        value = importUrl,
                        onValueChange = {
                            importUrl = it
                            if (it.startsWith("vless://") || it.startsWith("hysteria2://") || it.startsWith("ss://")) {
                                name = "Imported Node"
                                if (it.contains("vless")) selectedProtocol = ProxyProtocol.VLESS
                                if (it.contains("hysteria2")) selectedProtocol = ProxyProtocol.HYSTERIA2
                                if (it.contains("ss")) selectedProtocol = ProxyProtocol.SHADOWSOCKS
                            }
                        },
                        label = { Text("Paste Config URL (vless://, hysteria2://, ss://)") },
                        placeholder = { Text("vless://uuid@host:port?security=reality...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = BorderOutline,
                            focusedTextColor = OnSurfaceText,
                            unfocusedTextColor = OnSurfaceText
                        )
                    )

                    HorizontalDivider(color = BorderOutline)
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Profile Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryCyan,
                        unfocusedBorderColor = BorderOutline,
                        focusedTextColor = OnSurfaceText,
                        unfocusedTextColor = OnSurfaceText
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = serverAddress,
                        onValueChange = { serverAddress = it },
                        label = { Text("Server Host / IP") },
                        modifier = Modifier.weight(2f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = BorderOutline,
                            focusedTextColor = OnSurfaceText,
                            unfocusedTextColor = OnSurfaceText
                        )
                    )

                    OutlinedTextField(
                        value = portText,
                        onValueChange = { portText = it },
                        label = { Text("Port") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = BorderOutline,
                            focusedTextColor = OnSurfaceText,
                            unfocusedTextColor = OnSurfaceText
                        )
                    )
                }

                // Protocol Selector Dropdown
                Text(text = "Protocol", style = MaterialTheme.typography.labelMedium, color = OnSurfaceSubtext)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ProxyProtocol.values().take(4).forEach { proto ->
                        FilterChip(
                            selected = selectedProtocol == proto,
                            onClick = { selectedProtocol = proto },
                            label = { Text(proto.displayName, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(proto.badgeColorHex),
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = sni,
                    onValueChange = { sni = it },
                    label = { Text("SNI / Server Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryCyan,
                        unfocusedBorderColor = BorderOutline,
                        focusedTextColor = OnSurfaceText,
                        unfocusedTextColor = OnSurfaceText
                    )
                )

                OutlinedTextField(
                    value = countryCode,
                    onValueChange = { countryCode = it.take(2).uppercase() },
                    label = { Text("Country Code (2 letters, e.g. US, JP)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryCyan,
                        unfocusedBorderColor = BorderOutline,
                        focusedTextColor = OnSurfaceText,
                        unfocusedTextColor = OnSurfaceText
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val profile = ProxyProfile(
                        id = profileToEdit?.id ?: "prof_${UUID.randomUUID().toString().take(6)}",
                        name = name.ifBlank { "Xray Node" },
                        serverAddress = serverAddress.ifBlank { "127.0.0.1" },
                        port = portText.toIntOrNull() ?: 443,
                        protocol = selectedProtocol,
                        transport = selectedTransport,
                        security = selectedSecurity,
                        sni = sni,
                        path = path,
                        countryCode = countryCode.ifBlank { "US" },
                        pingMs = profileToEdit?.pingMs,
                        pingState = profileToEdit?.pingState ?: PingState.UNTESTED,
                        isFavorite = profileToEdit?.isFavorite ?: false
                    )
                    onSaveProfile(profile)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan, contentColor = Color.Black)
            ) {
                Text("Save Configuration", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = OnSurfaceSubtext)
            }
        }
    )
}
