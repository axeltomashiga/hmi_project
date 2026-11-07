package ar.edu.uade.unitreeg1hmi99.data.model

data class TelemetryData(
    val timestamp: Long = System.currentTimeMillis(),
    val robotId: String = "Unitree-G1-Alpha",
    // 1. Conectividad
    val connectionState: ConnectionState = ConnectionState.ONLINE,
    val rssiDbm: Int = -55,
    val latencyMs: Long = 28,
    // 2. Batería
    val batteryPercentage: Int = 92,
    val batteryVoltage: Float = 25.1f,
    val isCharging: Boolean = false,
    // 3. Estado Operativo
    val operationalState: OperationalState = OperationalState.STANDBY,
    // 4. Misión y Progreso
    val missionId: String? = null,
    val missionName: String? = null,
    val missionProgress: Float = 0f,
    val currentWaypoint: Int = 0,
    val totalWaypoints: Int = 0,
    // 5. Ubicación
    val zoneName: String = "Estación Base - Pabellón A",
    val posX: Float = 0f,
    val posY: Float = 0f,
    val headingDeg: Float = 0f,
    // 6. Movimiento
    val speedMps: Float = 0f,
    val isMoving: Boolean = false,
    // 7. Alertas Activas
    val activeAlerts: List<AlertItem> = emptyList(),
    // 8. Última actualización
    val lastUpdateTimestamp: Long = System.currentTimeMillis(),
    val isDataStale: Boolean = false,
    // 9. Tres variables vinculadas con el servicio (Inspección y Seguridad de Edificios)
    val smokeGasPpm: Double = 14.2,                 // Normal < 30 ppm, Peligro > 60 ppm
    val ambientTemperatureCelsius: Double = 21.5,   // Normal 18-28 °C, Sobrecalentamiento > 42 °C
    val detectedPersonsCount: Int = 0,              // Conteo de personas en horario no laboral
    // 10. Evento de fin de misión para decisión del operador
    val justCompletedMissionName: String? = null
) {
    val isBatteryLow: Boolean
        get() = batteryPercentage <= 15

    val hasCriticalAlert: Boolean
        get() = activeAlerts.any { it.severity == AlertSeverity.CRITICAL && !it.acknowledged }

    val unacknowledgedAlertsCount: Int
        get() = activeAlerts.count { !it.acknowledged }
}
