package ar.edu.uade.unitreeg1hmi99.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.uade.unitreeg1hmi99.domain.model.AlertItem
import ar.edu.uade.unitreeg1hmi99.domain.model.AlertSeverity
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusDanger
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusDangerBg
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusDangerBorder
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusDangerContent
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusInfo
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusInfoBg
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusInfoBorder
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusInfoContent
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusSuccess
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusSuccessBg
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusSuccessBorder
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusSuccessContent
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusWarning
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusWarningBg
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusWarningBorder
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusWarningContent
import ar.edu.uade.unitreeg1hmi99.ui.viewmodel.RobotViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    viewModel: RobotViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val alerts = state.telemetry.activeAlerts
    val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    val pendingCount = alerts.count { !it.acknowledged }
    val criticalCount = alerts.count { it.severity == AlertSeverity.CRITICAL }
    val warningCount = alerts.count { it.severity == AlertSeverity.WARNING }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (pendingCount > 0) StatusDangerBg else MaterialTheme.colorScheme.primaryContainer
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = if (pendingCount > 0) StatusDanger else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Centro de Alertas (${alerts.size})",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = if (pendingCount > 0) "$pendingCount pendiente(s) de reconocimiento" else "Monitoreo de contingencias HRI",
                                    fontSize = 11.sp,
                                    color = if (pendingCount > 0) StatusDanger else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    actions = {
                        if (pendingCount > 0) {
                            Button(
                                onClick = { viewModel.acknowledgeAllAlerts() },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.padding(end = 10.dp)
                            ) {
                                Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Reconocer Todas", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        if (alerts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(StatusSuccessBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = "Sin alertas",
                                modifier = Modifier.size(36.dp),
                                tint = StatusSuccess
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Sistema Nominal · Sin Alertas Activas",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Todos los sensores ambientales, batería, enlace WiFi y navegación LiDAR operan dentro de los parámetros seguros.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 17.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            NominalChip("Gases < 25 ppm")
                            NominalChip("Temp ~21 °C")
                            NominalChip("SLAM Activo")
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AlertSummaryStatCard(
                            label = "Críticas",
                            count = criticalCount,
                            color = StatusDanger,
                            bgColor = StatusDangerBg,
                            borderColor = StatusDangerBorder,
                            modifier = Modifier.weight(1f)
                        )
                        AlertSummaryStatCard(
                            label = "Advertencias",
                            count = warningCount,
                            color = StatusWarning,
                            bgColor = StatusWarningBg,
                            borderColor = StatusWarningBorder,
                            modifier = Modifier.weight(1f)
                        )
                        AlertSummaryStatCard(
                            label = "Sin Reconocer",
                            count = pendingCount,
                            color = if (pendingCount > 0) StatusDanger else StatusSuccess,
                            bgColor = if (pendingCount > 0) StatusDangerBg else StatusSuccessBg,
                            borderColor = if (pendingCount > 0) StatusDangerBorder else StatusSuccessBorder,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                items(alerts, key = { it.id }) { alert ->
                    AlertItemCard(
                        alert = alert,
                        formattedTime = timeFormat.format(Date(alert.timestamp)),
                        onAcknowledge = { viewModel.acknowledgeAlert(alert.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NominalChip(text: String) {
    Surface(
        color = StatusSuccessBg,
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, StatusSuccessBorder)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = StatusSuccess,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = StatusSuccessContent
            )
        }
    }
}

@Composable
private fun AlertSummaryStatCard(
    label: String,
    count: Int,
    color: Color,
    bgColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = bgColor.copy(alpha = 0.7f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$count",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = color
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = color.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun AlertItemCard(
    alert: AlertItem,
    formattedTime: String,
    onAcknowledge: () -> Unit
) {
    val (bgColor, borderColor, icon, tintColor, contentColor) = when (alert.severity) {
        AlertSeverity.CRITICAL -> AlertStyle(
            StatusDangerBg,
            StatusDangerBorder,
            Icons.Default.Error,
            StatusDanger,
            StatusDangerContent
        )
        AlertSeverity.WARNING -> AlertStyle(
            StatusWarningBg,
            StatusWarningBorder,
            Icons.Default.Warning,
            StatusWarning,
            StatusWarningContent
        )
        AlertSeverity.INFO -> AlertStyle(
            StatusInfoBg,
            StatusInfoBorder,
            Icons.Default.Info,
            StatusInfo,
            StatusInfoContent
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(1.2.dp, borderColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (alert.acknowledged) 0.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(tintColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = alert.severity.name,
                            tint = tintColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = alert.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = contentColor
                        )
                        Text(
                            text = "Código: ${alert.code}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = contentColor.copy(alpha = 0.7f)
                        )
                    }
                }

                Surface(
                    color = tintColor,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = alert.severity.name,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = alert.message,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = contentColor.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = contentColor.copy(alpha = 0.65f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Registrada: $formattedTime",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = contentColor.copy(alpha = 0.75f)
                    )
                }

                if (alert.acknowledged) {
                    Surface(
                        color = StatusSuccessBg,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, StatusSuccessBorder)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Reconocida",
                                tint = StatusSuccess,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Reconocida",
                                color = StatusSuccessContent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = onAcknowledge,
                        colors = ButtonDefaults.buttonColors(containerColor = tintColor),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reconocer", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private data class AlertStyle(
    val bgColor: Color,
    val borderColor: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val tintColor: Color,
    val contentColor: Color
)
