package com.example.model

enum class RunMode(
    val id: String,
    val title: String,
    val badge: String,
    val description: String,
    val requiresRoot: Boolean
) {
    VPN_SERVICE(
        id = "vpn",
        title = "VPN Service",
        badge = "Standard",
        description = "Standard Android VpnService tun route for non-rooted devices.",
        requiresRoot = false
    ),
    TPROXY(
        id = "tproxy",
        title = "TPROXY",
        badge = "Root",
        description = "Kernel iptables/nftables transparent proxy redirect for ultra low overhead.",
        requiresRoot = true
    ),
    TUN2SOCKS(
        id = "tun2socks",
        title = "TUN2SOCKS",
        badge = "Userland",
        description = "Lightweight user-space packet parsing and SOCKS5 forwarding.",
        requiresRoot = false
    ),
    BPF2SOCKS(
        id = "bpf2socks",
        title = "BPF2SOCKS",
        badge = "eBPF Root",
        description = "Direct socket-level eBPF sk_msg kernel redirection for maximum throughput.",
        requiresRoot = true
    )
}
