package com.example.data

import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class AsteriskCoreRepository {

    private val initialProfiles = listOf(
        ProxyProfile(
            id = "prof_1",
            name = "US-West HighSpeed • REALITY",
            serverAddress = "us-west.xray.asterisk.net",
            port = 443,
            protocol = ProxyProtocol.VLESS,
            transport = TransportMethod.GRPC,
            security = SecurityType.REALITY,
            sni = "gateway.cloudflare.com",
            countryCode = "US",
            pingMs = 38,
            pingState = PingState.SUCCESS,
            isFavorite = true
        ),
        ProxyProfile(
            id = "prof_2",
            name = "JP-Tokyo Hysteria2 Ultra",
            serverAddress = "tokyo-hy2.asterisk.net",
            port = 8443,
            protocol = ProxyProtocol.HYSTERIA2,
            transport = TransportMethod.QUIC,
            security = SecurityType.TLS,
            sni = "jp.apple.com",
            countryCode = "JP",
            pingMs = 52,
            pingState = PingState.SUCCESS,
            isFavorite = true
        ),
        ProxyProfile(
            id = "prof_3",
            name = "SG-Singapore Shadowsocks 2022",
            serverAddress = "sg-ss.asterisk.net",
            port = 9001,
            protocol = ProxyProtocol.SHADOWSOCKS,
            transport = TransportMethod.TCP,
            security = SecurityType.CHACHA20,
            countryCode = "SG",
            pingMs = 74,
            pingState = PingState.SUCCESS
        ),
        ProxyProfile(
            id = "prof_4",
            name = "DE-Frankfurt Trojan WebSocket",
            serverAddress = "de-trojan.asterisk.net",
            port = 443,
            protocol = ProxyProtocol.TROJAN,
            transport = TransportMethod.WEBSOCKET,
            security = SecurityType.TLS,
            path = "/trojan-ws",
            countryCode = "DE",
            pingMs = 128,
            pingState = PingState.SUCCESS
        ),
        ProxyProfile(
            id = "prof_5",
            name = "HK-HongKong TUIC v5 QUIC",
            serverAddress = "hk-tuic.asterisk.net",
            port = 8080,
            protocol = ProxyProtocol.TUIC,
            transport = TransportMethod.QUIC,
            security = SecurityType.TLS,
            countryCode = "HK",
            pingMs = 45,
            pingState = PingState.SUCCESS,
            isFavorite = true
        ),
        ProxyProfile(
            id = "prof_6",
            name = "NL-Amsterdam VMess CDN",
            serverAddress = "nl-vmess.asterisk.net",
            port = 443,
            protocol = ProxyProtocol.VMESS,
            transport = TransportMethod.WEBSOCKET,
            security = SecurityType.TLS,
            path = "/vmess-path",
            countryCode = "NL",
            pingMs = 185,
            pingState = PingState.SUCCESS
        ),
        ProxyProfile(
            id = "prof_7",
            name = "US-East Backup WireGuard",
            serverAddress = "us-east.wireguard.asterisk.net",
            port = 51820,
            protocol = ProxyProtocol.WIREGUARD,
            transport = TransportMethod.TCP,
            security = SecurityType.NONE,
            countryCode = "US",
            pingMs = null,
            pingState = PingState.UNTESTED
        )
    )

    private val _profiles = MutableStateFlow(initialProfiles)
    val profiles = _profiles.asStateFlow()

    private val _selectedProfileId = MutableStateFlow("prof_1")
    val selectedProfileId = _selectedProfileId.asStateFlow()

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState = _connectionState.asStateFlow()

    private val _currentRunMode = MutableStateFlow(RunMode.VPN_SERVICE)
    val currentRunMode = _currentRunMode.asStateFlow()

    private val _logs = MutableStateFlow<List<String>>(
        listOf(
            "[asteriskd] Daemon initialized v2.4.1 (libsu root ready)",
            "[xray-core] Xray 1.8.8 (Xray, Community Server) started",
            "[asteriskd] Selected run mode: VPN Service",
            "[asteriskd] Loaded 7 server profiles successfully"
        )
    )
    val logs = _logs.asStateFlow()

    fun selectProfile(id: String) {
        _selectedProfileId.value = id
        appendLog("[asteriskd] Selected profile changed -> ID: $id")
    }

    fun setRunMode(mode: RunMode) {
        _currentRunMode.value = mode
        appendLog("[asteriskd] Active run mode changed -> ${mode.title} (${mode.badge})")
    }

    suspend fun toggleConnection() {
        when (_connectionState.value) {
            ConnectionState.DISCONNECTED -> {
                _connectionState.value = ConnectionState.CONNECTING
                val activeProf = getSelectedProfile()
                appendLog("[asteriskd] Starting daemon connection via ${_currentRunMode.value.title}...")
                appendLog("[xray-core] Outbound configuration parsed for: ${activeProf?.name ?: "Unknown"}")

                delay(1200) // Realistic connection negotiation handshake

                _connectionState.value = ConnectionState.CONNECTED
                appendLog("[xray-core] Inbound tun0 connected. Handshake completed in 312ms.")
                appendLog("[asteriskd] Routing active: ALL TRAFFIC -> ${activeProf?.serverAddress}:${activeProf?.port}")
            }
            ConnectionState.CONNECTED -> {
                _connectionState.value = ConnectionState.DISCONNECTING
                appendLog("[asteriskd] Terminating daemon socket & clearing routes...")

                delay(800)

                _connectionState.value = ConnectionState.DISCONNECTED
                appendLog("[asteriskd] Disconnected cleanly.")
            }
            else -> {}
        }
    }

    suspend fun measurePingForProfile(profileId: String) {
        // Update ping state to CHECKING
        _profiles.value = _profiles.value.map {
            if (it.id == profileId) it.copy(pingState = PingState.CHECKING) else it
        }

        delay(Random.nextLong(300, 800)) // Latency check simulation

        val isSuccess = Random.nextFloat() > 0.10f
        val pingMs = if (isSuccess) Random.nextLong(18, 190) else null

        _profiles.value = _profiles.value.map {
            if (it.id == profileId) {
                it.copy(
                    pingMs = pingMs,
                    pingState = if (isSuccess) PingState.SUCCESS else PingState.TIMEOUT
                )
            } else it
        }
    }

    suspend fun testAllPings() {
        appendLog("[asteriskd] Initiating parallel TCP/HTTP latency measurement for all profiles...")
        // Set all to checking
        _profiles.value = _profiles.value.map { it.copy(pingState = PingState.CHECKING) }

        _profiles.value.forEach { profile ->
            delay(150)
            val isSuccess = Random.nextFloat() > 0.08f
            val pingMs = if (isSuccess) Random.nextLong(20, 210) else null
            _profiles.value = _profiles.value.map {
                if (it.id == profile.id) {
                    it.copy(
                        pingMs = pingMs,
                        pingState = if (isSuccess) PingState.SUCCESS else PingState.TIMEOUT
                    )
                } else it
            }
        }
        appendLog("[asteriskd] Ping test batch completed.")
    }

    fun toggleFavorite(profileId: String) {
        _profiles.value = _profiles.value.map {
            if (it.id == profileId) it.copy(isFavorite = !it.isFavorite) else it
        }
    }

    fun addProfile(profile: ProxyProfile) {
        _profiles.value = _profiles.value + profile
        appendLog("[asteriskd] Added new profile: ${profile.name} (${profile.protocol.displayName})")
    }

    fun updateProfile(profile: ProxyProfile) {
        _profiles.value = _profiles.value.map { if (it.id == profile.id) profile else it }
        appendLog("[asteriskd] Updated profile: ${profile.name}")
    }

    fun deleteProfile(profileId: String) {
        val target = _profiles.value.find { it.id == profileId }
        _profiles.value = _profiles.value.filter { it.id != profileId }
        if (_selectedProfileId.value == profileId && _profiles.value.isNotEmpty()) {
            _selectedProfileId.value = _profiles.value.first().id
        }
        appendLog("[asteriskd] Removed profile: ${target?.name ?: profileId}")
    }

    fun getSelectedProfile(): ProxyProfile? {
        return _profiles.value.find { it.id == _selectedProfileId.value }
    }

    private fun appendLog(msg: String) {
        val currentList = _logs.value.toMutableList()
        currentList.add(msg)
        if (currentList.size > 100) {
            currentList.removeAt(0)
        }
        _logs.value = currentList
    }

    // Traffic Flow Generator
    fun getTrafficFlow(): Flow<TrafficStats> = flow {
        var totalRx = 142050000L
        var totalTx = 28400000L
        var seconds = 0L

        while (true) {
            if (_connectionState.value == ConnectionState.CONNECTED) {
                val rx = Random.nextLong(200_000, 8_500_000)
                val tx = Random.nextLong(20_000, 1_200_000)
                totalRx += rx
                totalTx += tx
                seconds++
                emit(
                    TrafficStats(
                        rxBytesPerSec = rx,
                        txBytesPerSec = tx,
                        totalRxBytes = totalRx,
                        totalTxBytes = totalTx,
                        activeConnections = Random.nextInt(4, 28),
                        uptimeSeconds = seconds,
                        daemonStatus = "asteriskd: RUNNING (PID ${Random.nextInt(1000, 9999)})"
                    )
                )
            } else {
                emit(
                    TrafficStats(
                        rxBytesPerSec = 0,
                        txBytesPerSec = 0,
                        totalRxBytes = totalRx,
                        totalTxBytes = totalTx,
                        activeConnections = 0,
                        uptimeSeconds = 0,
                        daemonStatus = "asteriskd: STANDBY"
                    )
                )
            }
            delay(1000)
        }
    }
}
