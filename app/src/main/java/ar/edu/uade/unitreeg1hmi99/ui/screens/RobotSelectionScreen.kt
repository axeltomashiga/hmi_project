package ar.edu.uade.unitreeg1hmi99.ui.screens

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
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RobotSelectionScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ficha Técnica: Unitree G1",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
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
            // Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessibilityNew,
                        contentDescription = "Robot Humanoide",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Unitree G1 (Humanoid)",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Plataforma Robótica de Servicio e Inspección",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Especificaciones de Hardware
            Text(
                text = "Especificaciones y Movilidad",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SpecItem("Altura / Peso", "127 cm / ~35 kg (Estructura compacta bípeda)")
                    SpecItem("Grados de Libertad (DOF)", "23 a 43 DOF (Piernas bípedas + brazos manipuladores)")
                    SpecItem("Velocidad Máxima", "~2.0 m/s (Caminata/Trote dinámico)")
                    SpecItem("Autonomía Nominal", "Aprox. 2 horas de operación continua")
                    SpecItem("Batería", "Batería de Litio de liberación rápida 9000mAh (25.2V)")
                }
            }

            // Sensores Incorporados vs Equipamiento Adicional (Punto 1 de la Consigna)
            Text(
                text = "Sensores y Capacidades (Consigna Punto 1)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Capacidades Nativas Disponibles:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Text(
                        text = "• LiDAR 3D frontal (Mapeo y detección de obstáculos SLAM)\n" +
                                "• Cámara de Profundidad Intel RealSense / Visión estéreo\n" +
                                "• Unidad IMU de 6 ejes (Estabilidad bípeda y balance)\n" +
                                "• Altavoces y micrófonos para interacción HRI con personas",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Equipamiento Adicional Requerido para el Servicio:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Text(
                        text = "• Sensor electroquímico de gases / CO2 / humo (MQ-2 / BME688) para inspección ambiental.\n" +
                                "• Sensor infrarrojo térmico o pirómetro sin contacto para detección de sobrecalentamiento en tableros.\n" +
                                "• Módulo de radio de largo alcance WiFi Mesh para evitar zonas ciegas en subsuelos.",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }
            }

            // Justificación HRI del formato Humanoide
            Text(
                text = "Justificación HRI (Human-Robot Interaction)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "El formato humanoide bípedo del G1 fue elegido frente al cuadrúpedo Go2 debido a que en edificios e instalaciones educativas:\n" +
                                "1. Permite inspección a la altura visual humana (tableros, extintores, señalética).\n" +
                                "2. Genera una aceptación social HRI superior y menos intimidante en presencia de docentes, alumnos o personal de maestranza.\n" +
                                "3. Puede interactuar con botones o manijas en etapas posteriores.",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

@Composable
fun SpecItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color.Gray)
        Text(text = value, fontWeight = FontWeight.Medium, fontSize = 12.sp)
    }
}
