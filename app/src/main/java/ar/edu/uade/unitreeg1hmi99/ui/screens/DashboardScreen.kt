package ar.edu.uade.unitreeg1hmi99.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Co2
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WrongLocation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.uade.unitreeg1hmi99.R
import ar.edu.uade.unitreeg1hmi99.data.model.CommandType
import ar.edu.uade.unitreeg1hmi99.data.model.ConnectionState
import ar.edu.uade.unitreeg1hmi99.data.model.OperationalState
import ar.edu.uade.unitreeg1hmi99.ui.components.BatteryBadge
import ar.edu.uade.unitreeg1hmi99.ui.components.CommandStatusCard
import ar.edu.uade.unitreeg1hmi99.ui.components.ConfirmDialog
import ar.edu.uade.unitreeg1hmi99.ui.components.ConnectivityBadge
import ar.edu.uade.unitreeg1hmi99.ui.components.OperationalStateBadge
import ar.edu.uade.unitreeg1hmi99.ui.components.PatrolMap
import ar.edu.uade.unitreeg1hmi99.ui.components.SectionHeader
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusDanger
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusDangerBg
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusDangerBorder
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusDangerContent
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusDangerDark
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusReturn
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusReturnBg
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusReturnBorder
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusReturnContent
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusSuccess
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusSuccessBg
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusSuccessBorder
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusSuccessContent
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusWarning
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusWarningBg
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusWarningContent
import ar.edu.uade.unitreeg1hmi99.ui.viewmodel.RobotViewModel
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: RobotViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val telemetry = state.telemetry
    val scrollState = rememberScrollState()

    // Estado colapsable del Mapa 2D
    var isMapCollapsed by remember { mutableStateOf(false) }

    // Estado del Modal Inferior de Comandos (expandible con swipe hacia arriba, ocultable con swipe hacia abajo)
    var isCommandModalExpanded by remember { mutableStateOf(false) }

    // Condiciones de habilitación de comandos
    val isOnline = telemetry.connectionState != ConnectionState.OFFLINE
    val canStart = isOnline && telemetry.operationalState == OperationalState.STANDBY && telemetry.batteryPercentage > 15
    val canPause = isOnline && telemetry.operationalState == OperationalState.PATROLLING
    val canResume = isOnline && telemetry.operationalState == OperationalState.PAUSED
    val canCancel = isOnline && (telemetry.operationalState == OperationalState.PATROLLING || telemetry.operationalState == OperationalState.PAUSED)
    val canReturnSafe = isOnline && (telemetry.operationalState != OperationalState.STANDBY || !telemetry.zoneName.contains("Base"))

    val commandContextHint = when {
        !isOnline -> "Enlace OFFLINE: órdenes bloqueadas"
        telemetry.batteryPercentage <= 15 -> "Batería crítica (≤15%)"
        telemetry.operationalState == OperationalState.STANDBY -> "Listo para iniciar misión"
        telemetry.operationalState == OperationalState.PATROLLING -> "Patrullaje activo"
        telemetry.operationalState == OperationalState.PAUSED -> "Misión en pausa"
        telemetry.operationalState == OperationalState.RETURNING_HOME -> "Retornando a base"
        else -> "Control manual activo"
    }

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
                                    imageVector = Icons.Default.PrecisionManufacturing,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Supervisión HMI",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = state.selectedRobot,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    },
                    actions = {
                        OperationalStateBadge(
                            state = telemetry.operationalState,
                            modifier = Modifier.padding(end = 10.dp)
                        )
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Contenido principal scrolleable de Supervisión
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Barra de Estado Rápido Simétrica (Misma Altura Exacta): Conexión WiFi y Batería
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ConnectivityBadge(
                        connectionState = telemetry.connectionState,
                        rssiDbm = telemetry.rssiDbm,
                        latencyMs = telemetry.latencyMs,
                        isStale = telemetry.isDataStale,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                    BatteryBadge(
                        percentage = telemetry.batteryPercentage,
                        voltage = telemetry.batteryVoltage,
                        isCharging = telemetry.isCharging,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }

                // 2. Banner de Advertencia por Datos Congelados (Desconexión)
                AnimatedVisibility(visible = telemetry.isDataStale || telemetry.connectionState == ConnectionState.OFFLINE) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = StatusDangerBg),
                        border = BorderStroke(1.5.dp, StatusDangerBorder),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(StatusDanger.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Desconexión",
                                    tint = StatusDanger,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "ENLACE INTERRUMPIDO · DATOS CONGELADOS",
                                    color = StatusDangerContent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Telemetría congelada por pérdida de enlace. Las órdenes permanecerán pendientes o pasarán a Sin Respuesta.",
                                    color = StatusDangerContent.copy(alpha = 0.9f),
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                // 2b. Banner de Recarga Dinámica en Estación Base
                AnimatedVisibility(visible = telemetry.isCharging) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = StatusSuccessBg),
                        border = BorderStroke(1.dp, StatusSuccessBorder),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(StatusSuccess.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BatteryChargingFull,
                                    contentDescription = "Cargando Batería",
                                    tint = StatusSuccess,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Acoplado en Estación Base · Recarga Activa",
                                    color = StatusSuccessContent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Regenerando batería (+2%/s) · Nivel actual: ${telemetry.batteryPercentage}%",
                                    color = StatusSuccessContent.copy(alpha = 0.85f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // 3. Banner de Alertas Críticas e Incidentes que Exigen Decisión del Operador
                val pendingIncidents = telemetry.activeAlerts.filter { !it.acknowledged }
                AnimatedVisibility(
                    visible = pendingIncidents.isNotEmpty() ||
                        (telemetry.operationalState == OperationalState.PAUSED && telemetry.activeAlerts.isNotEmpty())
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = StatusWarningBg),
                        border = BorderStroke(1.5.dp, StatusWarning),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(StatusWarning.copy(alpha = 0.18f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationImportant,
                                        contentDescription = "Incidente Detectado",
                                        tint = StatusWarningContent,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Surface(
                                        color = StatusWarning,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "INCIDENTE DETECTADO · DECISIÓN REQUERIDA",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = pendingIncidents.firstOrNull()?.message
                                            ?: "El robot detuvo su marcha preventivamente. Seleccione una acción:",
                                        color = StatusWarningContent,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "¿Qué acción operativa desea ejecutar?",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusWarningContent
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Botones de Decisión ante Incidentes
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.acknowledgeAllAlerts()
                                        viewModel.sendCommand(CommandType.RESUME_MISSION)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Reanudar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        viewModel.acknowledgeAllAlerts()
                                        viewModel.setCancelDialogVisible(true)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusDanger),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                ) {
                                    Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Cancelar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        viewModel.acknowledgeAllAlerts()
                                        viewModel.sendCommand(CommandType.RETURN_TO_SAFE_POINT)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusReturn),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                ) {
                                    Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("A Base", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // 3b. Mapa 2D de patrullaje colapsable
                PatrolMap(
                    floorMap = state.floorMap,
                    route = state.plannedRoute,
                    reached = state.routeReached,
                    telemetry = telemetry,
                    isCollapsed = isMapCollapsed,
                    onToggleCollapse = { isMapCollapsed = !isMapCollapsed }
                )

                // 4. Selector de Misión de Inspección (cuando el robot está en STANDBY)
                if (telemetry.operationalState == OperationalState.STANDBY) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SectionHeader(
                                title = "Seleccionar Misión de Inspección",
                                icon = Icons.Default.Route,
                                trailingText = "Est. ${state.selectedMission.estimatedDuration}"
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                state.availableMissions.forEach { mission ->
                                    val isSelected = state.selectedMission.id == mission.id
                                    Card(
                                        onClick = { viewModel.selectMission(mission) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) {
                                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                                            } else {
                                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                            }
                                        ),
                                        border = if (isSelected) {
                                            BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                                        } else {
                                            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Icon(
                                                imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                                contentDescription = null,
                                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Surface(
                                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                                        shape = RoundedCornerShape(4.dp)
                                                    ) {
                                                        Text(
                                                            text = mission.id,
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = mission.name,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                                        fontSize = 13.sp,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = mission.description,
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column(horizontalAlignment = Alignment.End) {
                                                Surface(
                                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                                    shape = RoundedCornerShape(50)
                                                ) {
                                                    Text(
                                                        text = "${mission.waypointsCount} pts",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = mission.estimatedDuration,
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. Progreso de Operación: Regreso a Base Dedicado o Misión Activa
                if (telemetry.operationalState == OperationalState.RETURNING_HOME) {
                    val returnProgress = (100f - telemetry.missionProgress).coerceIn(0f, 100f)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = StatusReturnBg),
                        border = BorderStroke(1.5.dp, StatusReturnBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(StatusReturn.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.WrongLocation,
                                            contentDescription = "Regreso a Base",
                                            tint = StatusReturn,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = stringResource(R.string.title_returning_home),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = StatusReturnContent
                                        )
                                        Text(
                                            text = stringResource(R.string.desc_returning_home),
                                            fontSize = 11.sp,
                                            color = StatusReturnContent.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                                Surface(
                                    color = StatusReturn,
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = "${returnProgress.toInt()}%",
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            LinearProgressIndicator(
                                progress = { returnProgress / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = StatusReturn,
                                trackColor = StatusReturn.copy(alpha = 0.2f)
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "Ubicación",
                                        tint = StatusReturn,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = telemetry.zoneName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = StatusReturnContent
                                    )
                                }
                                Text(
                                    text = stringResource(R.string.destination_base),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusReturnContent
                                )
                            }
                        }
                    }
                } else {
                    // Tarjeta de Misión Activa y Progreso
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "PROGRESO DE RONDA",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = telemetry.missionName ?: "Sin Misión en Curso (En Espera)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = "${telemetry.missionProgress.toInt()}%",
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = { telemetry.missionProgress / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "Zona",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = telemetry.zoneName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Route,
                                            contentDescription = "Waypoint",
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Punto ${telemetry.currentWaypoint} / ${telemetry.totalWaypoints}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 6. Las 3 Variables Específicas del Servicio (Seguridad e Inspección)
                SectionHeader(
                    title = "Variables de Servicio (Inspección)",
                    icon = Icons.Default.Sensors,
                    trailingText = "3 Sensores"
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isGasHigh = telemetry.smokeGasPpm > 50.0
                    ServiceMetricCard(
                        title = "Gas / Humo",
                        value = "${telemetry.smokeGasPpm} ppm",
                        subtitle = if (isGasHigh) "Peligro Fuga" else "Normal (<25)",
                        icon = Icons.Default.Co2,
                        isWarning = isGasHigh,
                        modifier = Modifier.weight(1f)
                    )

                    val isTempHigh = telemetry.ambientTemperatureCelsius > 40.0
                    ServiceMetricCard(
                        title = "Temperatura",
                        value = "${telemetry.ambientTemperatureCelsius} °C",
                        subtitle = if (isTempHigh) "Sobrecalent." else "Estable (21°)",
                        icon = Icons.Default.Thermostat,
                        isWarning = isTempHigh,
                        modifier = Modifier.weight(1f)
                    )

                    ServiceMetricCard(
                        title = "Personas",
                        value = "${telemetry.detectedPersonsCount}",
                        subtitle = if (telemetry.detectedPersonsCount > 0) "En área restr." else "Sin presencia",
                        icon = Icons.Default.People,
                        isWarning = telemetry.detectedPersonsCount > 0,
                        modifier = Modifier.weight(1f)
                    )
                }

                // 7. Métricas Cinemáticas y Espaciales
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KinematicInfoCard(
                        icon = Icons.Default.Speed,
                        label = "Velocidad",
                        value = "${String.format(Locale.US, "%.2f", telemetry.speedMps)} m/s",
                        detail = if (telemetry.isMoving) "En marcha" else "Detenido",
                        isActive = telemetry.isMoving,
                        modifier = Modifier.weight(1f)
                    )
                    KinematicInfoCard(
                        icon = Icons.Default.Explore,
                        label = "Orientación",
                        value = "${telemetry.headingDeg.toInt()}°",
                        detail = cardinalDirection(telemetry.headingDeg),
                        isActive = false,
                        modifier = Modifier.weight(1f)
                    )
                    KinematicInfoCard(
                        icon = Icons.Default.MyLocation,
                        label = "Coordenadas",
                        value = "${String.format(Locale.US, "%.1f", telemetry.posX)}, ${String.format(Locale.US, "%.1f", telemetry.posY)}m",
                        detail = "Plano 2D",
                        isActive = false,
                        modifier = Modifier.weight(1f)
                    )
                }

                // 8. Estado de la Última Orden Emitida
                SectionHeader(
                    title = "Estado de Última Orden",
                    icon = Icons.Default.Terminal
                )
                CommandStatusCard(command = state.lastCommand)

                // Espacio inferior extra para que el botón flotante fijo y el modal inferior colapsado nunca tapen contenido
                Spacer(modifier = Modifier.height(124.dp))
            }

            // Botón con Posición Fixed: Acceso rápido únicamente para Abrir y Colapsar el Mapa
            FixedMapQuickToggleButton(
                isMapCollapsed = isMapCollapsed,
                onToggleCollapse = { isMapCollapsed = !isMapCollapsed },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 78.dp)
            )

            // Scrim semitransparente detrás del Modal de Comandos cuando está expandido
            AnimatedVisibility(
                visible = isCommandModalExpanded,
                enter = fadeIn(animationSpec = tween(200)),
                exit = fadeOut(animationSpec = tween(200)),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.38f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            isCommandModalExpanded = false
                        }
                )
            }

            // Modal Inferior de Comandos de Operación (Pegado sin espacio a la barra inferior)
            SwipeableCommandsBottomModal(
                isExpanded = isCommandModalExpanded,
                onExpandChange = { isCommandModalExpanded = it },
                commandContextHint = commandContextHint,
                canStart = canStart,
                canPause = canPause,
                canResume = canResume,
                canCancel = canCancel,
                canReturnSafe = canReturnSafe,
                lastCommand = state.lastCommand,
                onStart = { viewModel.sendCommand(CommandType.START_MISSION) },
                onPause = { viewModel.sendCommand(CommandType.PAUSE_MISSION) },
                onResume = { viewModel.sendCommand(CommandType.RESUME_MISSION) },
                onCancel = { viewModel.setCancelDialogVisible(true) },
                onReturnSafe = { viewModel.sendCommand(CommandType.RETURN_TO_SAFE_POINT) },
                onEmergencyStop = { viewModel.setEmergencyDialogVisible(true) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    // Modal de confirmación para Detención Operativa
    if (state.showEmergencyConfirmDialog) {
        ConfirmDialog(
            title = "Confirmar Detención Operativa",
            message = "¿Desea detener inmediatamente todos los actuadores del Unitree G1 por software? Nota: no reemplaza la parada de emergencia física.",
            confirmText = "Detener Robot",
            confirmColor = StatusDangerDark,
            onConfirm = {
                viewModel.sendCommand(CommandType.OPERATIONAL_STOP)
                viewModel.setEmergencyDialogVisible(false)
            },
            onDismiss = { viewModel.setEmergencyDialogVisible(false) }
        )
    }

    // Modal de confirmación para Cancelar Misión
    if (state.showCancelConfirmDialog) {
        ConfirmDialog(
            title = "Cancelar Misión en Curso",
            message = "¿Está seguro de abortar la ronda de inspección actual? El robot quedará en reposo en su posición actual.",
            confirmText = "Sí, Cancelar",
            confirmColor = StatusDanger,
            onConfirm = {
                viewModel.sendCommand(CommandType.CANCEL_MISSION)
                viewModel.setCancelDialogVisible(false)
            },
            onDismiss = { viewModel.setCancelDialogVisible(false) }
        )
    }

    // Modal de Fin de Misión: Elegir entre Hacer Otra Misión o Regresar a Base a Cargar Batería
    val completedMission = telemetry.justCompletedMissionName
    if (completedMission != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissMissionCompleted() },
            shape = RoundedCornerShape(20.dp),
            icon = {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(StatusSuccessBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = null,
                        tint = StatusSuccess,
                        modifier = Modifier.size(30.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "¡Misión Finalizada con Éxito!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "El robot completó la ronda '$completedMission' y se encuentra en espera.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "• Ubicación final: ${telemetry.zoneName}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "• Batería restante: ${telemetry.batteryPercentage}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "• Sensores ambientales: Valores normales",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StatusSuccess
                            )
                        }
                    }
                    Text(
                        text = "¿Qué desea hacer a continuación?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.dismissMissionCompleted()
                        viewModel.sendCommand(CommandType.RETURN_TO_SAFE_POINT)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusReturn),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.BatteryChargingFull, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Regresar a Base y Cargar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        viewModel.dismissMissionCompleted()
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hacer Otra Misión", fontSize = 12.sp)
                }
            }
        )
    }
}

/**
 * Botón Flotante Fixed de acceso rápido únicamente para Abrir y Colapsar el Mapa.
 */
@Composable
private fun FixedMapQuickToggleButton(
    isMapCollapsed: Boolean,
    onToggleCollapse: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onToggleCollapse,
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shadowElevation = 6.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.25f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Map,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isMapCollapsed) "Abrir Mapa" else "Colapsar Mapa",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = if (isMapCollapsed) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                contentDescription = if (isMapCollapsed) "Abrir Mapa" else "Colapsar Mapa",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * Modal Inferior Deslizable (Bottom Sheet Modal) para los Comandos de Operación (HMI).
 * Soporta swipe hacia arriba para expandirse y swipe hacia abajo para ocultarse,
 * apoyado directamente sobre la barra de navegación inferior sin espacios.
 */
@Composable
private fun SwipeableCommandsBottomModal(
    isExpanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    commandContextHint: String,
    canStart: Boolean,
    canPause: Boolean,
    canResume: Boolean,
    canCancel: Boolean,
    canReturnSafe: Boolean,
    lastCommand: ar.edu.uade.unitreeg1hmi99.data.model.CommandRecord?,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit,
    onReturnSafe: () -> Unit,
    onEmergencyStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    var dragDeltaY by remember { mutableFloatStateOf(0f) }

    // Desplazamiento solo hacia abajo al arrastrar para cerrar, evitando levantar el borde inferior de la barra
    val visualOffsetPx = when {
        isExpanded && dragDeltaY > 0f -> (dragDeltaY * 0.35f).coerceAtMost(120f)
        else -> 0f
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .offset { IntOffset(0, visualOffsetPx.roundToInt()) }
            .pointerInput(isExpanded) {
                detectVerticalDragGestures(
                    onDragStart = { dragDeltaY = 0f },
                    onVerticalDrag = { change, dragAmount ->
                        change.consume()
                        dragDeltaY += dragAmount
                        if (!isExpanded && dragDeltaY < -36.dp.toPx()) {
                            onExpandChange(true)
                            dragDeltaY = 0f
                        } else if (isExpanded && dragDeltaY > 36.dp.toPx()) {
                            onExpandChange(false)
                            dragDeltaY = 0f
                        }
                    },
                    onDragEnd = {
                        if (!isExpanded && dragDeltaY < -20.dp.toPx()) {
                            onExpandChange(true)
                        } else if (isExpanded && dragDeltaY > 20.dp.toPx()) {
                            onExpandChange(false)
                        }
                        dragDeltaY = 0f
                    },
                    onDragCancel = { dragDeltaY = 0f }
                )
            },
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shadowElevation = 14.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.22f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Barra de agarre (Drag Handle) + Cabecera del Modal de Comandos
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onExpandChange(!isExpanded) }
                    .padding(bottom = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(42.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f))
                )
                Spacer(modifier = Modifier.height(8.dp))

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
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Gamepad,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Comandos de Operación (HMI)",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isExpanded) {
                                    "Deslice hacia abajo ↓ para ocultar · $commandContextHint"
                                } else {
                                    "Deslice hacia arriba ↑ para desplegar · $commandContextHint"
                                },
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(50)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isExpanded) "Ocultar" else "Abrir",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                                contentDescription = if (isExpanded) "Ocultar comandos" else "Desplegar comandos",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Contenido Expandible del Modal de Comandos
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(240)) + fadeIn(animationSpec = tween(200)),
                exit = shrinkVertically(animationSpec = tween(220)) + fadeOut(animationSpec = tween(150))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    // Fila 1: Iniciar / Pausar / Reanudar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onStart,
                            enabled = canStart,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Iniciar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onPause,
                            enabled = canPause,
                            colors = ButtonDefaults.buttonColors(containerColor = StatusWarning),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Icon(Icons.Default.Pause, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pausar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onResume,
                            enabled = canResume,
                            colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reanudar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Fila 2: Cancelar Misión / Retorno a Punto Seguro
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onCancel,
                            enabled = canCancel,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusDanger),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (canCancel) StatusDanger else MaterialTheme.colorScheme.outlineVariant
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cancelar Misión", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onReturnSafe,
                            enabled = canReturnSafe,
                            colors = ButtonDefaults.buttonColors(containerColor = StatusReturn),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Icon(Icons.Default.WrongLocation, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Regreso Seguro", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Fila 3: Detención Operativa (Destacada)
                    Button(
                        onClick = onEmergencyStop,
                        colors = ButtonDefaults.buttonColors(containerColor = StatusDangerDark),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(Icons.Default.PanTool, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DETENCIÓN OPERATIVA (SOFTWARE)",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }

                    // Aviso permanente de Seguridad HRI (Consigna 2.2 / 5.2)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Seguridad HRI: la detención por software no reemplaza la parada de emergencia física.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Feedback inmediato de la última orden dentro del mismo modal
                    if (lastCommand != null) {
                        CommandStatusCard(command = lastCommand)
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    isWarning: Boolean,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isWarning) StatusDangerBg else MaterialTheme.colorScheme.surface
    val borderColor = if (isWarning) StatusDangerBorder else MaterialTheme.colorScheme.outlineVariant
    val tintColor = if (isWarning) StatusDanger else MaterialTheme.colorScheme.primary
    val valueColor = if (isWarning) StatusDangerContent else MaterialTheme.colorScheme.onSurface

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isWarning) 2.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(tintColor.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = tintColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Surface(
                    color = if (isWarning) StatusDanger else StatusSuccessBg,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = if (isWarning) "ALERTA" else "OK",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isWarning) Color.White else StatusSuccess,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (isWarning) StatusDangerContent.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = valueColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                fontWeight = if (isWarning) FontWeight.Bold else FontWeight.Normal,
                color = if (isWarning) StatusDanger else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun KinematicInfoCard(
    icon: ImageVector,
    label: String,
    value: String,
    detail: String,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(16.dp),
                    tint = if (isActive) StatusSuccess else MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = detail,
                fontSize = 10.sp,
                color = if (isActive) StatusSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}

private fun cardinalDirection(deg: Float): String {
    val normalized = ((deg % 360f) + 360f) % 360f
    return when {
        normalized < 22.5f || normalized >= 337.5f -> "Rumbo Este (E)"
        normalized < 67.5f -> "Rumbo SE"
        normalized < 112.5f -> "Rumbo Sur (S)"
        normalized < 157.5f -> "Rumbo SO"
        normalized < 202.5f -> "Rumbo Oeste (O)"
        normalized < 247.5f -> "Rumbo NO"
        normalized < 292.5f -> "Rumbo Norte (N)"
        else -> "Rumbo NE"
    }
}
