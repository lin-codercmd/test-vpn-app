package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConnectionState
import com.example.ui.theme.*

/**
 * 2. Connection Button Component
 * Prominent, central Morphing FAB / Capsule Connection Button with fluid
 * Material 3 state animations, pulsing glow rings, transforming icons,
 * and exact ViewModel binding.
 */
@Composable
fun MorphingConnectionButton(
    connectionState: ConnectionState,
    onToggleConnection: () -> Unit,
    activeRunModeTitle: String,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Smooth Button Press Scale Animation
    val scaleAnim by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "press_scale"
    )

    // Pulsing Ring Animation for Connecting & Connected States
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_rings")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_scale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    val spinnerRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(1000, easing = LinearEasing)),
        label = "spinner_rotation"
    )

    // State Colors
    val primaryGlowColor = when (connectionState) {
        ConnectionState.DISCONNECTED -> BorderOutline
        ConnectionState.CONNECTING -> AccentAmber
        ConnectionState.CONNECTED -> PrimaryCyan
        ConnectionState.DISCONNECTING -> AccentRed
    }

    val buttonBackgroundBrush = when (connectionState) {
        ConnectionState.DISCONNECTED -> Brush.radialGradient(
            colors = listOf(DarkSurfaceVariant, DarkSurfaceCard)
        )
        ConnectionState.CONNECTING -> Brush.radialGradient(
            colors = listOf(AccentAmber.copy(alpha = 0.35f), DarkSurfaceCard)
        )
        ConnectionState.CONNECTED -> Brush.radialGradient(
            colors = listOf(PrimaryCyan.copy(alpha = 0.35f), SecondaryIndigo.copy(alpha = 0.25f), DarkSurface)
        )
        ConnectionState.DISCONNECTING -> Brush.radialGradient(
            colors = listOf(AccentRed.copy(alpha = 0.35f), DarkSurfaceCard)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scaleAnim)
            .testTag("morphing_connection_button"),
        contentAlignment = Alignment.Center
    ) {
        // Outer Glowing Pulsing Rings (Active when Connecting or Connected)
        if (connectionState == ConnectionState.CONNECTED || connectionState == ConnectionState.CONNECTING) {
            Canvas(
                modifier = Modifier
                    .size(170.dp)
                    .scale(pulseScale)
            ) {
                drawCircle(
                    color = primaryGlowColor.copy(alpha = pulseAlpha),
                    style = Stroke(width = 3.dp.toPx())
                )
            }
        }

        // Main Morphing Capsule Container
        Surface(
            modifier = Modifier
                .width(220.dp)
                .height(110.dp)
                .clip(RoundedCornerShape(55.dp))
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            primaryGlowColor,
                            primaryGlowColor.copy(alpha = 0.3f),
                            primaryGlowColor
                        )
                    ),
                    shape = RoundedCornerShape(55.dp)
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(bounded = true, color = primaryGlowColor),
                    onClick = onToggleConnection
                ),
            color = Color.Transparent,
            shape = RoundedCornerShape(55.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(buttonBackgroundBrush),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Transforming Icon (Power / Rocket / Shield / Sync)
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(primaryGlowColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(
                            targetState = connectionState,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(300)) + scaleIn() togetherWith
                                        fadeOut(animationSpec = tween(300)) + scaleOut()
                            },
                            label = "icon_transition"
                        ) { state ->
                            when (state) {
                                ConnectionState.DISCONNECTED -> {
                                    Icon(
                                        imageVector = Icons.Default.PowerSettingsNew,
                                        contentDescription = "Tap to Connect",
                                        tint = OnSurfaceSubtext,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                ConnectionState.CONNECTING -> {
                                    Icon(
                                        imageVector = Icons.Default.Sync,
                                        contentDescription = "Connecting...",
                                        tint = AccentAmber,
                                        modifier = Modifier
                                            .size(28.dp)
                                            .rotate(spinnerRotation)
                                    )
                                }
                                ConnectionState.CONNECTED -> {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = "Connected",
                                        tint = PrimaryCyan,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                ConnectionState.DISCONNECTING -> {
                                    Icon(
                                        imageVector = Icons.Default.RocketLaunch,
                                        contentDescription = "Disconnecting...",
                                        tint = AccentRed,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Morphing Label Animation
                    AnimatedContent(
                        targetState = connectionState,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(250))
                        },
                        label = "label_transition"
                    ) { state ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val labelText = when (state) {
                                ConnectionState.DISCONNECTED -> "TAP TO CONNECT"
                                ConnectionState.CONNECTING -> "CONNECTING..."
                                ConnectionState.CONNECTED -> "SECURE & CONNECTED"
                                ConnectionState.DISCONNECTING -> "DISCONNECTING..."
                            }

                            Text(
                                text = labelText,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.1.sp
                                ),
                                color = when (state) {
                                    ConnectionState.DISCONNECTED -> OnSurfaceText
                                    ConnectionState.CONNECTING -> AccentAmber
                                    ConnectionState.CONNECTED -> PrimaryCyan
                                    ConnectionState.DISCONNECTING -> AccentRed
                                }
                            )

                            Text(
                                text = activeRunModeTitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceSubtext
                            )
                        }
                    }
                }
            }
        }
    }
}
