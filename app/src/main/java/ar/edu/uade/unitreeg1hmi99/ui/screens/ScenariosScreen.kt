package ar.edu.uade.unitreeg1hmi99.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Co2
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.uade.unitreeg1hmi99.ui.viewmodel.RobotViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenariosScreen(
    viewModel: RobotViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Panel de Pruebas: 5 Escenarios",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                actions = {
                    OutlinedButton(
                        onClick = { viewModel.resetSimulation() },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset", fontSize = 12.sp)
                    }
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
            Text(
                text = "Demostración de Escenarios Requeridos",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "Presione cada escenario para simularlo en la telemetría, observar la respuesta en la HMI y verificar la persistencia en Room.",
                fontSize = 12.sp,
                color = Color.DarkGray
            )

            // Escenario 1: Misión Normal
            ScenarioCard(
                title = "1. Misión Normal",
                description = "Inicia el patrullaje autónomo a través de los 8 waypoints del edificio, actualiza el progreso porcentual y finaliza en base.",
                icon = Icons.Default.PlayCircle,
                badgeColor = Color(0xFF2E7D32),
                actionLabel = "Lanzar Misión Normal",
                onAction = { viewModel.runNormalMissionScenario() }
            )

            // Escenario 2: Batería Baja
            ScenarioCard(
                title = "2. Batería Baja (< 15%)",
                description = "Fuerza la batería al 12%. Emite alerta WARNING, bloquea el inicio de nuevas rondas y resalta el comando 'Regreso Seguro'.",
                icon = Icons.Default.BatteryAlert,
                badgeColor = Color(0xFFF57C00),
                actionLabel = "Simular Batería al 12%",
                onAction = { viewModel.triggerLowBatteryScenario() }
            )

            // Escenario 3: Desconexión y Timeout
            ScenarioToggleCard(
                title = "3. Desconexión de Red",
                description = "Corta la comunicación. Los datos de telemetría se congelan y marcan como antiguos (stale). Los comandos pasan a PENDING y luego a NO_RESPONSE.",
                icon = Icons.Default.SignalWifiOff,
                badgeColor = Color(0xFFC62828),
                isActive = state.isForcedOffline,
                activeText = "Desconectado (OFFLINE)",
                inactiveText = "Conectado (ONLINE)",
                onToggle = { viewModel.toggleDisconnectionScenario() }
            )

            // Escenario 4: Incidente por Obstáculo
            ScenarioCard(
                title = "4. Incidente: Obstáculo en Ruta",
                description = "El robot detecta un bloqueo prolongado en el pasillo (>10s), frena y pasa a estado PAUSED, exigiendo decisión humana (reanudar o cancelar).",
                icon = Icons.Default.Warning,
                badgeColor = Color(0xFFF57F17),
                actionLabel = "Simular Obstáculo",
                onAction = { viewModel.triggerObstacleScenario() }
            )

            // Escenario 5: Escenario Propio (Gas y Sobrecalentamiento)
            ScenarioCard(
                title = "5. Escenario Propio: Fuga de Gas / Fuego",
                description = "Detección crítica en tablero eléctrico (Gas: 96.5 ppm, Temp: 54°C). Activa alarma CRITICAL, freno preventivo y solicitud de protocolo de emergencia.",
                icon = Icons.Default.Co2,
                badgeColor = Color(0xFFB71C1C),
                actionLabel = "Simular Fuga de Gas Crítica",
                onAction = { viewModel.triggerGasOverheatScenario() }
            )

            // Prueba adicional: Rechazo de Comandos
            ScenarioToggleCard(
                title = "Prueba Extra: Forzar Rechazo de Órdenes",
                description = "Simula que el robot no puede aceptar órdenes por políticas de seguridad internas (retorna estado visible REJECTED).",
                icon = Icons.Default.Block,
                badgeColor = Color(0xFF6A1B9A),
                isActive = state.isForcedRejection,
                activeText = "Rechazos Activos",
                inactiveText = "Comportamiento Normal",
                onToggle = { viewModel.toggleCommandRejectionScenario() }
            )
        }
    }
}

@Composable
fun ScenarioCard(
    title: String,
    description: String,
    icon: ImageVector,
    badgeColor: Color,
    actionLabel: String,
    onAction: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = badgeColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(actionLabel, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun ScenarioToggleCard(
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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = badgeColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
                Switch(
                    checked = isActive,
                    onCheckedChange = { onToggle() }
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Estado: ${if (isActive) activeText else inactiveText}",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (isActive) badgeColor else Color.Gray
            )
        }
    }
}
