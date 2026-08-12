package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AsteriskCoreRepository
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: AsteriskCoreRepository = AsteriskCoreRepository()
) : ViewModel() {

    val profiles: StateFlow<List<ProxyProfile>> = repository.profiles
    val selectedProfileId: StateFlow<String> = repository.selectedProfileId
    val connectionState: StateFlow<ConnectionState> = repository.connectionState
    val currentRunMode: StateFlow<RunMode> = repository.currentRunMode
    val logs: StateFlow<List<String>> = repository.logs

    val trafficStats: StateFlow<TrafficStats> = repository.getTrafficFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TrafficStats()
        )

    val selectedProfile: StateFlow<ProxyProfile?> = combine(profiles, selectedProfileId) { list, id ->
        list.find { it.id == id } ?: list.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.getSelectedProfile())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _protocolFilter = MutableStateFlow<ProxyProtocol?>(null)
    val protocolFilter = _protocolFilter.asStateFlow()

    private val _isBottomSheetOpen = MutableStateFlow(false)
    val isBottomSheetOpen = _isBottomSheetOpen.asStateFlow()

    private val _isLogsSheetOpen = MutableStateFlow(false)
    val isLogsSheetOpen = _isLogsSheetOpen.asStateFlow()

    private val _isAddEditProfileDialogOpen = MutableStateFlow(false)
    val isAddEditProfileDialogOpen = _isAddEditProfileDialogOpen.asStateFlow()

    private val _profileToEdit = MutableStateFlow<ProxyProfile?>(null)
    val profileToEdit = _profileToEdit.asStateFlow()

    val filteredProfiles: StateFlow<List<ProxyProfile>> = combine(
        profiles,
        _searchQuery,
        _protocolFilter
    ) { list, query, filter ->
        list.filter { profile ->
            val matchesQuery = query.isEmpty() ||
                    profile.name.contains(query, ignoreCase = true) ||
                    profile.serverAddress.contains(query, ignoreCase = true) ||
                    profile.countryCode.contains(query, ignoreCase = true)

            val matchesFilter = filter == null || profile.protocol == filter

            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectProfile(id: String) {
        repository.selectProfile(id)
    }

    fun setRunMode(mode: RunMode) {
        repository.setRunMode(mode)
    }

    fun toggleConnection() {
        viewModelScope.launch {
            repository.toggleConnection()
        }
    }

    fun measurePing(profileId: String) {
        viewModelScope.launch {
            repository.measurePingForProfile(profileId)
        }
    }

    fun testAllPings() {
        viewModelScope.launch {
            repository.testAllPings()
        }
    }

    fun toggleFavorite(profileId: String) {
        repository.toggleFavorite(profileId)
    }

    fun addProfile(profile: ProxyProfile) {
        repository.addProfile(profile)
    }

    fun updateProfile(profile: ProxyProfile) {
        repository.updateProfile(profile)
    }

    fun deleteProfile(profileId: String) {
        repository.deleteProfile(profileId)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setProtocolFilter(filter: ProxyProtocol?) {
        _protocolFilter.value = filter
    }

    fun setShowBottomSheet(show: Boolean) {
        _isBottomSheetOpen.value = show
    }

    fun setShowLogsSheet(show: Boolean) {
        _isLogsSheetOpen.value = show
    }

    fun openAddProfileDialog() {
        _profileToEdit.value = null
        _isAddEditProfileDialogOpen.value = true
    }

    fun openEditProfileDialog(profile: ProxyProfile) {
        _profileToEdit.value = profile
        _isAddEditProfileDialogOpen.value = true
    }

    fun closeAddEditProfileDialog() {
        _isAddEditProfileDialogOpen.value = false
        _profileToEdit.value = null
    }
}
