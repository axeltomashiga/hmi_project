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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Co2
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.uade.unitreeg1hmi99.ui.components.OperationalStateBadge
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusDanger
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusDangerDark
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusReturn
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusSuccess
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusWarning
import ar.edu.uade.unitreeg1hmi99.ui.viewmodel.RobotViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenariosScreen(
    viewModel: RobotViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val telemetry = state.telemetry
    val scrollState = rememberScrollState()

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
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Science,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Panel de Pruebas: 5 Escenarios",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Simulación 100% Offline · Evaluación TP",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    actions = {
                        OutlinedButton(
                            onClick = { viewModel.resetSimulation() },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.padding(end = 10.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Monitor compacto de telemetría en vivo para feedback instantáneo al activar escenarios
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ESTADO EN VIVO DEL SIMULADOR",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Presione cualquier escenario y observe el cambio inmediato",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        OperationalStateBadge(state = telemetry.operationalState)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        LiveStatusMiniPill(
                            label = "Batería",
                            value = "${telemetry.batteryPercentage}%",
                            isAlert = telemetry.batteryPercentage <= 15,
                            modifier = Modifier.weight(1f)
                        )
                        LiveStatusMiniPill(
                            label = "Enlace",
                            value = if (state.isForcedOffline) "OFFLINE" else "ONLINE",
                            isAlert = state.isForcedOffline,
                            modifier = Modifier.weight(1f)
                        )
                        LiveStatusMiniPill(
                            label = "Gas / Temp",
                            value = "${telemetry.smokeGasPpm.toInt()}ppm · ${telemetry.ambientTemperatureCelsius.toInt()}°",
                            isAlert = telemetry.smokeGasPpm > 50 || telemetry.ambientTemperatureCelsius > 40,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Escenario 1: Misión Normal
            ScenarioCard(
                tag = "ESCENARIO 1 · FLUJO NOMINAL",
                title = "1. Misión Normal",
                description = "Inicia el patrullaje autónomo a través de los 8 waypoints del edificio, actualiza el progreso porcentual y finaliza en base.",
                icon = Icons.Default.PlayCircle,
                badgeColor = StatusSuccess,
                actionLabel = "Lanzar Misión Normal",
                onAction = { viewModel.runNormalMissionScenario() }
            )

            // Escenario 2: Batería Baja
            ScenarioCard(
                tag = "ESCENARIO 2 · ENERGÍA CRÍTICA",
                title = "2. Batería Baja (< 15%)",
                description = "Fuerza la batería al 12%. Emite alerta WARNING, bloquea el inicio de nuevas rondas y resalta el comando 'Regreso Seguro'.",
                icon = Icons.Default.BatteryAlert,
                badgeColor = StatusWarning,
                actionLabel = "Simular Batería al 12%",
                onAction = { viewModel.triggerLowBatteryScenario() }
            )

            // Escenario 3: Desconexión y Timeout
            ScenarioToggleCard(
                tag = "ESCENARIO 3 · PÉRDIDA DE ENLACE",
                title = "3. Desconexión de Red",
                description = "Corta la comunicación. Los datos de telemetría se congelan y marcan como antiguos (stale). Los comandos pasan a PENDING y luego a NO_RESPONSE.",
                icon = Icons.Default.SignalWifiOff,
                badgeColor = StatusDanger,
                isActive = state.isForcedOffline,
                activeText = "Desconectado (OFFLINE · Stale)",
                inactiveText = "Conectado (ONLINE Nominal)",
                onToggle = { viewModel.toggleDisconnectionScenario() }
            )

            // Escenario 4: Incidente por Obstáculo
            ScenarioCard(
                tag = "ESCENARIO 4 · INTERACCIÓN HRI",
                title = "4. Incidente: Obstáculo en Ruta",
                description = "El robot detecta un bloqueo prolongado en el pasillo (>10s), frena y pasa a estado PAUSED, exigiendo decisión humana (reanudar o cancelar).",
                icon = Icons.Default.Warning,
                badgeColor = StatusWarning,
                actionLabel = "Simular Obstáculo",
                onAction = { viewModel.triggerObstacleScenario() }
            )

            // Escenario 5: Escenario Propio (Gas y Sobrecalentamiento)
            ScenarioCard(
                tag = "ESCENARIO 5 · CONTINGENCIA DEL SERVICIO",
                title = "5. Escenario Propio: Fuga de Gas / Fuego",
                description = "Detección crítica en tablero eléctrico (Gas: 96.5 ppm, Temp: 54°C). Activa alarma CRITICAL, freno preventivo y solicitud de protocolo de emergencia.",
                icon = Icons.Default.Co2,
                badgeColor = StatusDangerDark,
                actionLabel = "Simular Fuga de Gas Crítica",
                onAction = { viewModel.triggerGasOverheatScenario() }
            )

            // Prueba adicional: Rechazo de Comandos
            ScenarioToggleCard(
                tag = "PRUEBA ADICIONAL · VALIDACIÓN HMI",
                title = "Prueba Extra: Forzar Rechazo de Órdenes",
                description = "Simula que el robot no puede aceptar órdenes por políticas de seguridad internas (retorna estado visible REJECTED).",
                icon = Icons.Default.Block,
                badgeColor = StatusReturn,
                isActive = state.isForcedRejection,
                activeText = "Rechazos Activos (REJECTED)",
                inactiveText = "Comportamiento Normal",
                onToggle = { viewModel.toggleCommandRejectionScenario() }
            )
        }
    }
}

@Composable
private fun LiveStatusMiniPill(
    label: String,
    value: String,
    isAlert: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isAlert) StatusDanger.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isAlert) StatusDanger else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = if (isAlert) StatusDanger else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isAlert) StatusDanger else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ScenarioCard(
    tag: String = "ESCENARIO TP",
    title: String,
    description: String,
    icon: ImageVector,
    badgeColor: Color,
    actionLabel: String,
    onAction: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(badgeColor.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = badgeColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Surface(
                        color = badgeColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = tag,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = badgeColor),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(actionLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ScenarioToggleCard(
    tag: String = "ESCENARIO CONMUTABLE",
    title: String,
    description: String,
    icon: ImageVector,
    badgeColor: Color,
    isActive: Boolean,
    activeText: String,
    inactiveText: String,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) badgeColor.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (isActive) 1.5.dp else 1.dp,
            color = if (isActive) badgeColor else MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(badgeColor.copy(alpha = 0.14f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Surface(
                            color = badgeColor.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = tag,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = badgeColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Switch(
                    checked = isActive,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = badgeColor
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                color = if (isActive) badgeColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isActive) badgeColor else StatusSuccess)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Estado actual: ${if (isActive) activeText else inactiveText}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = if (isActive) badgeColor else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
