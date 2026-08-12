package com.example.model

enum class ProxyProtocol(val displayName: String, val badgeColorHex: Long) {
    VLESS("VLESS", 0xFF00E5FF),
    HYSTERIA2("Hysteria 2", 0xFFA855F7),
    SHADOWSOCKS("Shadowsocks", 0xFF10B981),
    VMESS("VMess", 0xFF3B82F6),
    TROJAN("Trojan", 0xFFF59E0B),
    TUIC("TUIC v5", 0xFFEC4899),
    WIREGUARD("WireGuard", 0xFF6366F1)
}

enum class TransportMethod(val code: String) {
    TCP("TCP"),
    WEBSOCKET("WS"),
    GRPC("gRPC"),
    QUIC("QUIC"),
    HTTP2("H2")
}

enum class SecurityType(val code: String) {
    NONE("None"),
    TLS("TLS"),
    REALITY("REALITY"),
    CHACHA20("ChaCha20"),
    AES_128_GCM("AES-128-GCM")
}

enum class PingState {
    UNTESTED,
    CHECKING,
    SUCCESS,
    TIMEOUT,
    ERROR
}

data class ProxyProfile(
    val id: String,
    val name: String,
    val serverAddress: String,
    val port: Int,
    val protocol: ProxyProtocol,
    val transport: TransportMethod = TransportMethod.TCP,
    val security: SecurityType = SecurityType.REALITY,
    val encryption: String = "auto",
    val uuidOrPassword: String = "",
    val sni: String = "",
    val path: String = "",
    val countryCode: String = "US",
    val pingMs: Long? = null,
    val pingState: PingState = PingState.UNTESTED,
    val isFavorite: Boolean = false,
    val isAutoRoute: Boolean = true
)
