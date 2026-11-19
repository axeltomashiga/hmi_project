package ar.edu.uade.unitreeg1hmi99.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.SignalWifi4Bar
import androidx.compose.material.icons.filled.SignalWifiBad
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.uade.unitreeg1hmi99.domain.model.CommandRecord
import ar.edu.uade.unitreeg1hmi99.domain.model.CommandStatus
import ar.edu.uade.unitreeg1hmi99.domain.model.ConnectionState
import ar.edu.uade.unitreeg1hmi99.domain.model.OperationalState
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusDanger
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusDangerBg
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusDangerBorder
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusDangerContent
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusInfo
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusReturn
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusSuccess
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusSuccessBg
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusSuccessBorder
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusSuccessContent
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusWarning
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusWarningBg
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusWarningBorder
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusWarningContent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OperationalStateBadge(
    state: OperationalState,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, isAnimated) = when (state) {
        OperationalState.PATROLLING -> Triple(StatusSuccess, Color.White, true)
        OperationalState.STANDBY -> Triple(StatusInfo, Color.White, false)
        OperationalState.PAUSED -> Triple(StatusWarning, Color.White, false)
        OperationalState.RETURNING_HOME -> Triple(StatusReturn, Color.White, true)
        OperationalState.EMERGENCY_STOP -> Triple(StatusDanger, Color.White, true)
        OperationalState.FAULT -> Triple(Color(0xFFC2410C), Color.White, true)
    }

    val pulseAlpha by rememberInfiniteTransition(label = "stateBadgePulse").animateFloat(
        initialValue = 1f,
        targetValue = if (isAnimated) 0.35f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(50),
        shadowElevation = 2.dp,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .alpha(pulseAlpha)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(state.displayNameRes),
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun BatteryBadge(
    percentage: Int,
    voltage: Float,
    isCharging: Boolean,
    modifier: Modifier = Modifier
) {
    val isLow = percentage <= 15
    val (accentColor, bgColor, borderColor, icon) = when {
        isCharging -> Quadruple(StatusSuccess, StatusSuccessBg, StatusSuccessBorder, Icons.Default.BatteryChargingFull)
        isLow -> Quadruple(StatusDanger, StatusDangerBg, StatusDangerBorder, Icons.Default.BatteryAlert)
        percentage < 30 -> Quadruple(StatusWarning, StatusWarningBg, StatusWarningBorder, Icons.Default.BatteryAlert)
        else -> Quadruple(StatusSuccess, StatusSuccessBg, StatusSuccessBorder, Icons.Default.BatteryFull)
    }

    val footerText = when {
        isCharging -> "Cargando (+2%/s)"
        isLow -> "Nivel crítico"
        percentage < 30 -> "Nivel bajo"
        else -> "Autonomía OK"
    }

    Surface(
        color = bgColor.copy(alpha = 0.7f),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier.height(82.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "Batería",
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Batería",
                            fontSize = 10.sp,
                            lineHeight = 12.sp,
                            color = accentColor.copy(alpha = 0.85f),
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                        Text(
                            text = "$percentage%",
                            color = accentColor,
                            fontSize = 14.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1
                        )
                    }
                }
                Surface(
                    color = accentColor.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${String.format(Locale.US, "%.1f", voltage)}V",
                        color = accentColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        maxLines = 1
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LinearProgressIndicator(
                    progress = { (percentage.coerceIn(0, 100)) / 100f },
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = accentColor,
                    trackColor = accentColor.copy(alpha = 0.2f)
                )
                Text(
                    text = footerText,
                    color = accentColor.copy(alpha = 0.9f),
                    fontSize = 9.sp,
                    lineHeight = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun ConnectivityBadge(
    connectionState: ConnectionState,
    rssiDbm: Int,
    latencyMs: Long,
    isStale: Boolean,
    modifier: Modifier = Modifier
) {
    val isOffline = connectionState == ConnectionState.OFFLINE || isStale
    val (accentColor, bgColor, borderColor, icon) = when {
        isOffline ->
            Quadruple(StatusDanger, StatusDangerBg, StatusDangerBorder, Icons.Default.SignalWifiOff)
        connectionState == ConnectionState.DEGRADED ->
            Quadruple(StatusWarning, StatusWarningBg, StatusWarningBorder, Icons.Default.SignalWifiBad)
        else ->
            Quadruple(StatusSuccess, StatusSuccessBg, StatusSuccessBorder, Icons.Default.SignalWifi4Bar)
    }

    val statusTitle = when {
        isOffline -> "OFFLINE"
        connectionState == ConnectionState.DEGRADED -> "Inestable"
        else -> "En Línea"
    }

    val badgePillText = when {
        isOffline -> "Stale"
        else -> "${latencyMs}ms"
    }

    val signalProgress = when {
        isOffline -> 0f
        connectionState == ConnectionState.DEGRADED -> 0.45f
        else -> ((rssiDbm + 100).coerceIn(15, 70)) / 70f
    }

    val footerText = when {
        isOffline -> "Congelado"
        else -> "$rssiDbm dBm"
    }

    Surface(
        color = bgColor.copy(alpha = 0.7f),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier.height(82.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "Conectividad",
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Enlace WiFi",
                            fontSize = 10.sp,
                            lineHeight = 12.sp,
                            color = accentColor.copy(alpha = 0.85f),
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                        Text(
                            text = statusTitle,
                            color = accentColor,
                            fontSize = 14.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1
                        )
                    }
                }
                Surface(
                    color = accentColor.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = badgePillText,
                        color = accentColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        maxLines = 1
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LinearProgressIndicator(
                    progress = { signalProgress },
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = accentColor,
                    trackColor = accentColor.copy(alpha = 0.2f)
                )
                Text(
                    text = footerText,
                    color = accentColor.copy(alpha = 0.9f),
                    fontSize = 9.sp,
                    lineHeight = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun CommandStatusCard(
    command: CommandRecord?,
    modifier: Modifier = Modifier
) {
    val timeFormatter = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    if (command == null) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Canal de Comandos Listo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Ninguna orden emitida aún en esta sesión.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        return
    }

    val (bgColor, borderColor, icon, statusColor, contentColor) = when (command.status) {
        CommandStatus.PENDING -> Quintuple(
            StatusWarningBg,
            StatusWarningBorder,
            Icons.Default.HourglassTop,
            StatusWarning,
            StatusWarningContent
        )
        CommandStatus.CONFIRMED -> Quintuple(
            StatusSuccessBg,
            StatusSuccessBorder,
            Icons.Default.CheckCircle,
            StatusSuccess,
            StatusSuccessContent
        )
        CommandStatus.REJECTED -> Quintuple(
            StatusDangerBg,
            StatusDangerBorder,
            Icons.Default.Error,
            StatusDanger,
            StatusDangerContent
        )
        CommandStatus.NO_RESPONSE -> Quintuple(
            StatusWarningBg,
            StatusWarningBorder,
            Icons.Default.Warning,
            Color(0xFFC2410C),
            StatusWarningContent
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(statusColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = stringResource(command.status.labelRes),
                    tint = statusColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(command.type.labelRes),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = contentColor
                    )
                    Surface(
                        color = statusColor,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = stringResource(command.status.labelRes).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                if (command.message.isNotBlank()) {
                    Text(
                        text = command.message,
                        fontSize = 11.sp,
                        color = contentColor.copy(alpha = 0.85f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Text(
                    text = "ID: ${command.id} · Emitida: ${timeFormatter.format(Date(command.timestamp))}",
                    fontSize = 10.sp,
                    color = contentColor.copy(alpha = 0.65f),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    icon: ImageVector? = null,
    trailingText: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        if (trailingText != null) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = trailingText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    confirmColor: Color = MaterialTheme.colorScheme.primary,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        icon = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(confirmColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = confirmColor,
                    modifier = Modifier.size(26.dp)
                )
            }
        },
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            Text(
                text = message,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = confirmColor),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(confirmText, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Volver")
            }
        }
    )
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

private data class Quintuple<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)
