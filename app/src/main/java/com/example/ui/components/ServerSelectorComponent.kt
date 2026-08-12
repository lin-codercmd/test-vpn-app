package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PingState
import com.example.model.ProxyProfile
import com.example.model.ProxyProtocol
import com.example.ui.theme.*

/**
 * 1. Server Selector Component
 * Displays active server info card on dashboard with quick latency test,
 * protocol & encryption chips, and opens modern Sliding Bottom Sheet profile picker.
 */
@Composable
fun ServerSelectorCard(
    selectedProfile: ProxyProfile?,
    allProfiles: List<ProxyProfile>,
    onSelectProfile: (String) -> Unit,
    onOpenPicker: () -> Unit,
    onTestPing: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        PrimaryCyan.copy(alpha = 0.5f),
                        SecondaryIndigo.copy(alpha = 0.3f),
                        BorderOutline.copy(alpha = 0.4f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable { onOpenPicker() }
            .testTag("server_selector_card"),
        color = DarkSurfaceCard,
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Active Node Label & Change Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(PrimaryCyan)
                    )
                    Text(
                        text = "ACTIVE SERVER NODE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = OnSurfaceSubtext
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = PrimaryCyan.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, PrimaryCyan.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Switch Server",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = PrimaryCyan
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = "Switch Server",
                            tint = PrimaryCyan,
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }
            }

            if (selectedProfile != null) {
                // Profile Main Row: Flag / Country Tag, Name, Ping Meter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Country Code Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurfaceVariant,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = selectedProfile.countryCode,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black
                                    ),
                                    color = PrimaryCyan
                                )
                            }
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = selectedProfile.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = OnSurfaceText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${selectedProfile.serverAddress}:${selectedProfile.port}",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceSubtext,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Real-time Ping Latency Badge with refresh action
                    PingLatencyBadge(
                        pingMs = selectedProfile.pingMs,
                        pingState = selectedProfile.pingState,
                        onRefreshPing = { onTestPing(selectedProfile.id) }
                    )
                }

                HorizontalDivider(color = BorderOutline.copy(alpha = 0.5f))

                // Transport & Encryption Quick-Action Indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Protocol Tag
                        ProtocolBadge(protocol = selectedProfile.protocol)

                        // Transport Tag
                        ChipTag(
                            text = selectedProfile.transport.code,
                            backgroundColor = SecondaryIndigo.copy(alpha = 0.2f),
                            textColor = Color(0xFFA5B4FC)
                        )

                        // Security Tag
                        ChipTag(
                            text = selectedProfile.security.code,
                            backgroundColor = TertiaryEmerald.copy(alpha = 0.2f),
                            textColor = Color(0xFF6EE7B7)
                        )
                    }

                    Text(
                        text = "${allProfiles.size} available",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceSubtext
                    )
                }
            } else {
                Text(
                    text = "No Server Selected. Tap to select a profile.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceSubtext
                )
            }
        }
    }
}

/**
 * Sliding Bottom Sheet Profile Picker
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerSelectorBottomSheet(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    profiles: List<ProxyProfile>,
    selectedProfileId: String,
    onSelectProfile: (String) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedFilter: ProxyProtocol?,
    onFilterSelect: (ProxyProtocol?) -> Unit,
    onTestPing: (String) -> Unit,
    onTestAllPings: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onEditProfile: (ProxyProfile) -> Unit,
    onDeleteProfile: (String) -> Unit,
    onAddProfileClick: () -> Unit
) {
    if (isOpen) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = DarkSurface,
            scrimColor = Color.Black.copy(alpha = 0.7f),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight(0.88f)
                    .padding(horizontal = 18.dp)
                    .testTag("server_selector_bottom_sheet"),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Title & Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Server Profiles",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = OnSurfaceText
                        )
                        Text(
                            text = "Select, test latency, or add Xray nodes",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceSubtext
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = onTestAllPings,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(DarkSurfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = "Test All Pings",
                                tint = PrimaryCyan
                            )
                        }

                        IconButton(
                            onClick = onAddProfileClick,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(PrimaryCyan)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Profile",
                                tint = Color.Black
                            )
                        }
                    }
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("server_search_input"),
                    placeholder = { Text("Search by name, host, or location...", color = OnSurfaceSubtext) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryCyan) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = OnSurfaceSubtext)
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryCyan,
                        unfocusedBorderColor = BorderOutline,
                        focusedContainerColor = DarkSurfaceVariant.copy(alpha = 0.5f),
                        unfocusedContainerColor = DarkSurfaceVariant.copy(alpha = 0.5f),
                        focusedTextColor = OnSurfaceText,
                        unfocusedTextColor = OnSurfaceText
                    ),
                    singleLine = true
                )

                // Protocol Filter Pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedFilter == null,
                            onClick = { onFilterSelect(null) },
                            label = { Text("All (${profiles.size})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryCyan,
                                selectedLabelColor = Color.Black,
                                containerColor = DarkSurfaceVariant,
                                labelColor = OnSurfaceSubtext
                            )
                        )
                    }
                    items(ProxyProtocol.values()) { protocol ->
                        FilterChip(
                            selected = selectedFilter == protocol,
                            onClick = { onFilterSelect(if (selectedFilter == protocol) null else protocol) },
                            label = { Text(protocol.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(protocol.badgeColorHex),
                                selectedLabelColor = Color.Black,
                                containerColor = DarkSurfaceVariant,
                                labelColor = OnSurfaceSubtext
                            )
                        )
                    }
                }

                HorizontalDivider(color = BorderOutline.copy(alpha = 0.4f))

                // Profile List Cards
                if (profiles.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Dns,
                                contentDescription = null,
                                tint = OnSurfaceSubtext,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "No server nodes match your filter.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceSubtext
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(profiles, key = { it.id }) { profile ->
                            ServerProfileItemCard(
                                profile = profile,
                                isSelected = profile.id == selectedProfileId,
                                onSelect = {
                                    onSelectProfile(profile.id)
                                    onDismiss()
                                },
                                onTestPing = { onTestPing(profile.id) },
                                onToggleFavorite = { onToggleFavorite(profile.id) },
                                onEdit = { onEditProfile(profile) },
                                onDelete = { onDeleteProfile(profile.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Individual Server Profile Card Item in Picker
 */
@Composable
fun ServerProfileItemCard(
    profile: ProxyProfile,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onTestPing: () -> Unit,
    onToggleFavorite: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) PrimaryCyan else BorderOutline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onSelect() }
            .testTag("profile_item_${profile.id}"),
        color = if (isSelected) DarkSurfaceVariant else DarkSurfaceCard,
        tonalElevation = if (isSelected) 8.dp else 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Radio / Active Indicator
                RadioButton(
                    selected = isSelected,
                    onClick = onSelect,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = PrimaryCyan,
                        unselectedColor = OnSurfaceSubtext
                    )
                )

                // Country Tag
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DarkSurface,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = profile.countryCode,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryCyan
                        )
                    }
                }

                // Profile Details
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = profile.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = OnSurfaceText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (profile.isFavorite) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Favorite",
                                tint = AccentAmber,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProtocolBadge(protocol = profile.protocol)
                        Text(
                            text = "${profile.transport.code} • ${profile.security.code}",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceSubtext
                        )
                    }
                }
            }

            // Right side: Ping meter, Favorite star, More Menu
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PingLatencyBadge(
                    pingMs = profile.pingMs,
                    pingState = profile.pingState,
                    onRefreshPing = onTestPing
                )

                IconButton(onClick = onToggleFavorite, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (profile.isFavorite) Icons.Default.Star else Icons.Outlined.StarOutline,
                        contentDescription = "Favorite toggle",
                        tint = if (profile.isFavorite) AccentAmber else OnSurfaceSubtext,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = OnSurfaceSubtext,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(DarkSurfaceVariant)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit Configuration", color = OnSurfaceText) },
                            onClick = {
                                showMenu = false
                                onEdit()
                            },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = PrimaryCyan) }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Profile", color = AccentRed) },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = AccentRed) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Protocol badge chip with distinct protocol brand color
 */
@Composable
fun ProtocolBadge(protocol: ProxyProtocol) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(protocol.badgeColorHex).copy(alpha = 0.2f),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(protocol.badgeColorHex).copy(alpha = 0.5f))
    ) {
        Text(
            text = protocol.displayName,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = Color(protocol.badgeColorHex),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

/**
 * Generic Chip Tag for transport/security
 */
@Composable
fun ChipTag(text: String, backgroundColor: Color, textColor: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = textColor,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
        )
    }
}

/**
 * Latency ping indicator with dynamic colors & refresh animation
 */
@Composable
fun PingLatencyBadge(
    pingMs: Long?,
    pingState: PingState,
    onRefreshPing: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ping_spinner")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(900, easing = LinearEasing)),
        label = "rotation"
    )

    val (color, text) = when (pingState) {
        PingState.CHECKING -> Pair(PrimaryCyan, "...")
        PingState.SUCCESS -> {
            val ms = pingMs ?: 0L
            val colorCode = when {
                ms < 60 -> PingGreen
                ms < 150 -> PingAmber
                else -> PingRed
            }
            Pair(colorCode, "${ms}ms")
        }
        PingState.TIMEOUT -> Pair(PingRed, "Timeout")
        PingState.ERROR -> Pair(PingRed, "Error")
        PingState.UNTESTED -> Pair(PingOffline, "Test")
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, color.copy(alpha = 0.4f)),
        modifier = Modifier.clickable { onRefreshPing() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (pingState == PingState.CHECKING) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Checking ping",
                    tint = PrimaryCyan,
                    modifier = Modifier
                        .size(12.dp)
                        .rotate(rotation)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }

            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
    }
}
