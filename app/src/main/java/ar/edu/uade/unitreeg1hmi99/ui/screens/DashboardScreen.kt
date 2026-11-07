package ar.edu.uade.unitreeg1hmi99.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Co2
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WrongLocation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import ar.edu.uade.unitreeg1hmi99.ui.viewmodel.RobotViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: RobotViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val telemetry = state.telemetry
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "HMI Unitree G1 - Supervisión",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = state.selectedRobot,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    OperationalStateBadge(
                        state = telemetry.operationalState,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Barra de Estado Rápido: Conexión y Batería
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ConnectivityBadge(
                    connectionState = telemetry.connectionState,
                    rssiDbm = telemetry.rssiDbm,
                    latencyMs = telemetry.latencyMs,
                    isStale = telemetry.isDataStale
                )
                BatteryBadge(
                    percentage = telemetry.batteryPercentage,
                    voltage = telemetry.batteryVoltage,
                    isCharging = telemetry.isCharging
                )
            }

            // 2. Banner de Advertencia por Datos Congelados (Desconexión)
            AnimatedVisibility(visible = telemetry.isDataStale || telemetry.connectionState == ConnectionState.OFFLINE) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Desconexión",
                            tint = Color(0xFFC62828)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ATENCIÓN: Telemetría congelada por pérdida de enlace. Las órdenes permanecerán pendientes o serán rechazadas.",
                            color = Color(0xFFB71C1C),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 2b. Banner de Recarga Dinámica en Estación Base
            AnimatedVisibility(visible = telemetry.isCharging) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.BatteryChargingFull,
                            contentDescription = "Cargando Batería",
                            tint = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Robot en Estación Base: regenerando batería (+2%/s) - Nivel: ${telemetry.batteryPercentage}%",
                            color = Color(0xFF1B5E20),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 3. Banner de Alertas Críticas e Incidentes que Exigen Decisión del Operador
            val pendingIncidents = telemetry.activeAlerts.filter { !it.acknowledged }
            AnimatedVisibility(visible = pendingIncidents.isNotEmpty() || telemetry.operationalState == OperationalState.PAUSED && telemetry.activeAlerts.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE0B2)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationImportant,
                                contentDescription = "Incidente Detectado",
                                tint = Color(0xFFE65100),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "INCIDENTE DETECTADO - INTERVENCIÓN REQUERIDA",
                                    color = Color(0xFFBF360C),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = pendingIncidents.firstOrNull()?.message ?: "El robot detuvo su marcha preventivamente. Seleccione una acción:",
                                    color = Color.Black,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "¿Qué decisión desea tomar como operador?",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFBF360C)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        // Botones de Decisión ante Incidentes
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.acknowledgeAllAlerts()
                                    viewModel.sendCommand(CommandType.RESUME_MISSION)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                modifier = Modifier.weight(1f).height(36.dp)
                            ) {
                                Text("Reanudar", fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    viewModel.acknowledgeAllAlerts()
                                    viewModel.setCancelDialogVisible(true)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                                modifier = Modifier.weight(1f).height(36.dp)
                            ) {
                                Text("Cancelar", fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    viewModel.acknowledgeAllAlerts()
                                    viewModel.sendCommand(CommandType.RETURN_TO_SAFE_POINT)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
                                modifier = Modifier.weight(1f).height(36.dp)
                            ) {
                                Text("A Base", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // 4. Selector de Misión de Inspección (Paso 2: Operador selecciona la misión cuando el robot está en reposo)
            if (telemetry.operationalState == OperationalState.STANDBY) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Seleccionar Misión de Inspección",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${state.selectedMission.estimatedDuration}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        state.availableMissions.forEach { mission ->
                            val isSelected = state.selectedMission.id == mission.id
                            Card(
                                onClick = { viewModel.selectMission(mission) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                                ),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = mission.name,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = mission.description,
                                            fontSize = 11.sp,
                                            color = Color.DarkGray
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${mission.waypointsCount} pts",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Progreso de Operación: Regreso a Base Dedicado o Misión Activa
            if (telemetry.operationalState == OperationalState.RETURNING_HOME) {
                // Cartel específico para el Regreso Autónomo a la Estación Base
                val returnProgress = (100f - telemetry.missionProgress).coerceIn(0f, 100f)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF8E24AA)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.WrongLocation,
                                    contentDescription = "Regreso a Base",
                                    tint = Color(0xFF6A1B9A),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = stringResource(R.string.title_returning_home),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color(0xFF4A148C)
                                    )
                                    Text(
                                        text = stringResource(R.string.desc_returning_home),
                                        fontSize = 11.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                            Text(
                                text = "${returnProgress.toInt()}%",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF6A1B9A),
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { returnProgress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = Color(0xFF8E24AA),
                            trackColor = Color(0xFFE1BEE7)
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
                                    tint = Color(0xFF6A1B9A),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = telemetry.zoneName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = stringResource(R.string.destination_base),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF4A148C)
                            )
                        }
                    }
                }
            } else {
                // Cartel de Misión Activa y Progreso
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = telemetry.missionName ?: "Sin Misión en Curso",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "${telemetry.missionProgress.toInt()}%",
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { telemetry.missionProgress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
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
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Route,
                                    contentDescription = "Waypoint",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Punto ${telemetry.currentWaypoint} / ${telemetry.totalWaypoints}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // 5. Las 3 Variables Específicas del Servicio (Seguridad e Inspección)
            Text(
                text = "Variables de Servicio (Inspección Ambiental)",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Variable 1: Gas / Humo
                val isGasHigh = telemetry.smokeGasPpm > 50.0
                ServiceMetricCard(
                    title = "Gas / Humo",
                    value = "${telemetry.smokeGasPpm} ppm",
                    subtitle = if (isGasHigh) "Peligro Fuga" else "Normal (<25)",
                    icon = Icons.Default.Co2,
                    isWarning = isGasHigh,
                    modifier = Modifier.weight(1f)
                )

                // Variable 2: Temperatura Ambiental
                val isTempHigh = telemetry.ambientTemperatureCelsius > 40.0
                ServiceMetricCard(
                    title = "Temperatura",
                    value = "${telemetry.ambientTemperatureCelsius} °C",
                    subtitle = if (isTempHigh) "Sobrecalentamiento" else "Estable (21°)",
                    icon = Icons.Default.Thermostat,
                    isWarning = isTempHigh,
                    modifier = Modifier.weight(1f)
                )

                // Variable 3: Personas Detectadas
                ServiceMetricCard(
                    title = "Personas",
                    value = "${telemetry.detectedPersonsCount}",
                    subtitle = "Área restringida",
                    icon = Icons.Default.People,
                    isWarning = telemetry.detectedPersonsCount > 0,
                    modifier = Modifier.weight(1f)
                )
            }

            // 6. Métricas Cinemáticas Secundarias
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Velocidad",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("Velocidad", fontSize = 11.sp, color = Color.Gray)
                            Text("${String.format("%.2f", telemetry.speedMps)} m/s", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
                Card(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Orientación",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("Orientación", fontSize = 11.sp, color = Color.Gray)
                            Text("${telemetry.headingDeg.toInt()}°", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // 7. Panel de Comandos de Operación (Órdenes Discretas)
            Text(
                text = "Comandos de Operación (HMI)",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            // Fila 1: Iniciar / Pausar / Reanudar
            val isOnline = telemetry.connectionState != ConnectionState.OFFLINE
            val canStart = isOnline && telemetry.operationalState == OperationalState.STANDBY && telemetry.batteryPercentage > 15
            val canPause = isOnline && telemetry.operationalState == OperationalState.PATROLLING
            val canResume = isOnline && telemetry.operationalState == OperationalState.PAUSED
            val canCancel = isOnline && (telemetry.operationalState == OperationalState.PATROLLING || telemetry.operationalState == OperationalState.PAUSED)
            val canReturnSafe = isOnline && (telemetry.operationalState != OperationalState.STANDBY || !telemetry.zoneName.contains("Base"))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.sendCommand(CommandType.START_MISSION) },
                    enabled = canStart,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Iniciar", fontSize = 12.sp)
                }

                Button(
                    onClick = { viewModel.sendCommand(CommandType.PAUSE_MISSION) },
                    enabled = canPause,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57F17)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Pause, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pausar", fontSize = 12.sp)
                }

                Button(
                    onClick = { viewModel.sendCommand(CommandType.RESUME_MISSION) },
                    enabled = canResume,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reanudar", fontSize = 12.sp)
                }
            }

            // Fila 2: Cancelar Misión / Retorno a Punto Seguro
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.setCancelDialogVisible(true) },
                    enabled = canCancel,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cancelar Misión", fontSize = 11.sp)
                }

                Button(
                    onClick = { viewModel.sendCommand(CommandType.RETURN_TO_SAFE_POINT) },
                    enabled = canReturnSafe,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.WrongLocation, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Regreso Seguro", fontSize = 11.sp)
                }
            }

            // Fila 3: Detención Operativa (Destacada)
            Button(
                onClick = { viewModel.setEmergencyDialogVisible(true) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.PanTool, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("DETENCIÓN OPERATIVA (SOFTWARE)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            // 8. Estado de la Última Orden Emitida
            Text(
                text = "Estado de Última Orden",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            CommandStatusCard(command = state.lastCommand)
        }
    }

    // Modal de confirmación para Detención Operativa
    if (state.showEmergencyConfirmDialog) {
        ConfirmDialog(
            title = "Confirmar Detención Operativa",
            message = "¿Desea detener inmediatamente todos los actuadores del Unitree G1 por software? Nota: no reemplaza la parada de emergencia física.",
            confirmText = "Detener Robot",
            confirmColor = Color(0xFFB71C1C),
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
            confirmColor = Color(0xFFC62828),
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
            icon = {
                Icon(
                    imageVector = Icons.Default.DoneAll,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "¡Misión Finalizada con Éxito!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "El robot completó la ronda '$completedMission' y se encuentra en espera.",
                        fontSize = 13.sp
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("• Ubicación final: ${telemetry.zoneName}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text("• Batería restante: ${telemetry.batteryPercentage}%", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text("• Sensores ambientales: Valores normales", fontSize = 12.sp, color = Color(0xFF2E7D32))
                        }
                    }
                    Text(
                        text = "¿Qué desea hacer a continuación?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.dismissMissionCompleted()
                        viewModel.sendCommand(CommandType.RETURN_TO_SAFE_POINT)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
                ) {
                    Icon(Icons.Default.BatteryChargingFull, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Regresar a Base y Cargar", fontSize = 12.sp)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        viewModel.dismissMissionCompleted()
                    }
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hacer Otra Misión", fontSize = 12.sp)
                }
            }
        )
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
    val containerColor = if (isWarning) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surfaceVariant
    val tintColor = if (isWarning) Color(0xFFC62828) else MaterialTheme.colorScheme.primary

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = tintColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                color = Color.DarkGray
            )
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (isWarning) Color(0xFFB71C1C) else Color.Unspecified
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = if (isWarning) Color(0xFFC62828) else Color.Gray,
                maxLines = 1
            )
        }
    }
}
