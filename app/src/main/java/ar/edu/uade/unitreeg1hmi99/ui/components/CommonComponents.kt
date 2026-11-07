package ar.edu.uade.unitreeg1hmi99.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.uade.unitreeg1hmi99.data.model.CommandRecord
import ar.edu.uade.unitreeg1hmi99.data.model.CommandStatus
import ar.edu.uade.unitreeg1hmi99.data.model.ConnectionState
import ar.edu.uade.unitreeg1hmi99.data.model.OperationalState

@Composable
fun OperationalStateBadge(
    state: OperationalState,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (state) {
        OperationalState.PATROLLING -> Color(0xFF2E7D32) to Color.White
        OperationalState.STANDBY -> Color(0xFF1565C0) to Color.White
        OperationalState.PAUSED -> Color(0xFFF57F17) to Color.Black
        OperationalState.RETURNING_HOME -> Color(0xFF6A1B9A) to Color.White
        OperationalState.EMERGENCY_STOP -> Color(0xFFC62828) to Color.White
        OperationalState.FAULT -> Color(0xFFD84315) to Color.White
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(state.displayNameRes),
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
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
    val (color, icon) = when {
        isCharging -> Color(0xFF2E7D32) to Icons.Default.BatteryChargingFull
        isLow -> Color(0xFFD32F2F) to Icons.Default.BatteryAlert
        percentage < 30 -> Color(0xFFF57C00) to Icons.Default.BatteryAlert
        else -> Color(0xFF388E3C) to Icons.Default.BatteryFull
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Batería",
            tint = color,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$percentage% (${String.format("%.1f", voltage)}V)",
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
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
    val (color, icon, text) = when {
        connectionState == ConnectionState.OFFLINE || isStale ->
            Triple(Color(0xFFC62828), Icons.Default.SignalWifiOff, "OFFLINE (Datos congelados)")
        connectionState == ConnectionState.DEGRADED ->
            Triple(Color(0xFFF57C00), Icons.Default.SignalWifiBad, "Inestable ($rssiDbm dBm)")
        else ->
            Triple(Color(0xFF2E7D32), Icons.Default.SignalWifi4Bar, "Online ($latencyMs ms)")
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Conectividad",
            tint = color,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun CommandStatusCard(
    command: CommandRecord?,
    modifier: Modifier = Modifier
) {
    if (command == null) return

    val (bgColor, icon, statusColor) = when (command.status) {
        CommandStatus.PENDING -> Triple(Color(0xFFFFF9C4), Icons.Default.HourglassTop, Color(0xFFF57F17))
        CommandStatus.CONFIRMED -> Triple(Color(0xFFE8F5E9), Icons.Default.CheckCircle, Color(0xFF2E7D32))
        CommandStatus.REJECTED -> Triple(Color(0xFFFFEBEE), Icons.Default.Error, Color(0xFFC62828))
        CommandStatus.NO_RESPONSE -> Triple(Color(0xFFFFE0B2), Icons.Default.Warning, Color(0xFFE65100))
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = stringResource(command.status.labelRes),
                tint = statusColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(command.type.labelRes),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = stringResource(command.status.labelRes),
                        color = statusColor,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }
                if (command.message.isNotBlank()) {
                    Text(
                        text = command.message,
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
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
        title = { Text(text = title, fontWeight = FontWeight.Bold) },
        text = { Text(text = message) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = confirmColor)
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Volver")
            }
        }
    )
}
