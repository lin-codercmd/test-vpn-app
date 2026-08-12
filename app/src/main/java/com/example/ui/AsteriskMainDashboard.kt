package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.ConnectionState
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsteriskMainDashboard(
    viewModel: MainViewModel
) {
    val selectedProfile by viewModel.selectedProfile.collectAsStateWithLifecycle()
    val allProfiles by viewModel.profiles.collectAsStateWithLifecycle()
    val filteredProfiles by viewModel.filteredProfiles.collectAsStateWithLifecycle()
    val selectedProfileId by viewModel.selectedProfileId.collectAsStateWithLifecycle()
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle()
    val currentRunMode by viewModel.currentRunMode.collectAsStateWithLifecycle()
    val trafficStats by viewModel.trafficStats.collectAsStateWithLifecycle()
    val logs by viewModel.logs.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val protocolFilter by viewModel.protocolFilter.collectAsStateWithLifecycle()

    val isBottomSheetOpen by viewModel.isBottomSheetOpen.collectAsStateWithLifecycle()
    val isLogsSheetOpen by viewModel.isLogsSheetOpen.collectAsStateWithLifecycle()
    val isAddEditDialogOpen by viewModel.isAddEditProfileDialogOpen.collectAsStateWithLifecycle()
    val profileToEdit by viewModel.profileToEdit.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("asterisk_main_dashboard"),
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PrimaryCyan.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.4f))
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "NG",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                                    color = PrimaryCyan
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "AsteriskNG",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                ),
                                color = OnSurfaceText
                            )
                            Text(
                                text = "Xray-core Proxy Daemon",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceSubtext
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.setShowLogsSheet(true) },
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = "Core Daemon Logs",
                            tint = PrimaryCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = OnSurfaceText
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Spacer(modifier = Modifier.height(2.dp))

            // 1. Server Selector Component (Active profile card with quick switch)
            ServerSelectorCard(
                selectedProfile = selectedProfile,
                allProfiles = allProfiles,
                onSelectProfile = { viewModel.selectProfile(it) },
                onOpenPicker = { viewModel.setShowBottomSheet(true) },
                onTestPing = { viewModel.measurePing(it) }
            )

            // 2. Connection Button Component (Morphing FAB Capsule with fluid M3 animations)
            MorphingConnectionButton(
                connectionState = connectionState,
                onToggleConnection = { viewModel.toggleConnection() },
                activeRunModeTitle = "${currentRunMode.title} (${currentRunMode.badge})"
            )

            // Live Metrics Speed Gauges
            DashboardMetricsComponent(
                trafficStats = trafficStats,
                connectionState = connectionState
            )

            // Core Run Mode Selector Bar
            RunModeSelectorComponent(
                currentRunMode = currentRunMode,
                onRunModeChange = { viewModel.setRunMode(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // 1. Sliding Bottom Sheet Server Selector Picker
    ServerSelectorBottomSheet(
        isOpen = isBottomSheetOpen,
        onDismiss = { viewModel.setShowBottomSheet(false) },
        profiles = filteredProfiles,
        selectedProfileId = selectedProfileId,
        onSelectProfile = { viewModel.selectProfile(it) },
        searchQuery = searchQuery,
        onSearchQueryChange = { viewModel.setSearchQuery(it) },
        selectedFilter = protocolFilter,
        onFilterSelect = { viewModel.setProtocolFilter(it) },
        onTestPing = { viewModel.measurePing(it) },
        onTestAllPings = { viewModel.testAllPings() },
        onToggleFavorite = { viewModel.toggleFavorite(it) },
        onEditProfile = { viewModel.openEditProfileDialog(it) },
        onDeleteProfile = { viewModel.deleteProfile(it) },
        onAddProfileClick = { viewModel.openAddProfileDialog() }
    )

    // Daemon Logs Sheet
    DaemonLogsSheet(
        isOpen = isLogsSheetOpen,
        onDismiss = { viewModel.setShowLogsSheet(false) },
        logs = logs
    )

    // Add/Edit Profile Dialog
    if (isAddEditDialogOpen) {
        AddEditProfileDialog(
            profileToEdit = profileToEdit,
            onDismiss = { viewModel.closeAddEditProfileDialog() },
            onSaveProfile = {
                if (profileToEdit == null) {
                    viewModel.addProfile(it)
                } else {
                    viewModel.updateProfile(it)
                }
            }
        )
    }
}
