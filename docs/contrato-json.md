# Contrato JSON propuesto: telemetría, comandos y respuestas

> **Aviso:** este contrato es una **propuesta del grupo** para desacoplar la app del robot.
> **No es un protocolo oficial de Unitree** ni representa la API real del G1.

La app no serializa JSON en tiempo de ejecución: usa los modelos Kotlin puros de
`domain/model` a través de `domain/repository/RobotRepository`. Este documento describe el mensaje
equivalente que una implementación real del Repository (por ejemplo sobre WebSocket
o MQTT) recibiría y enviaría. `SimulatedRobotRepository` produce los mismos datos
sin red.

Convenciones: `timestamp` en milisegundos epoch UTC; distancias en metros; velocidad en m/s;
los enums se envían como string en MAYÚSCULAS.

---

## 1. Telemetría (robot → app, periódica, ~1 Hz)

Modelo Kotlin: `TelemetryData`.

```json
{
  "type": "telemetry",
  "robotId": "Unitree-G1-Alpha",
  "timestamp": 1731950000000,
  "connection": { "state": "ONLINE", "rssiDbm": -55, "latencyMs": 28 },
  "battery": { "percentage": 82, "voltage": 24.9, "isCharging": false },
  "operationalState": "PATROLLING",
  "mission": {
    "id": "MIS-A",
    "name": "Ronda Integral del Edificio",
    "progress": 37.5,
    "currentWaypoint": 3,
    "totalWaypoints": 8,
    "waypointsReached": 2
  },
  "location": {
    "zone": "Laboratorio de Robótica e IoT",
    "x": 14.0,
    "y": 5.0,
    "headingDeg": 90.0
  },
  "motion": { "speedMps": 0.85, "isMoving": true },
  "alerts": [],
  "lastUpdateTimestamp": 1731950000000,
  "service": {
    "smokeGasPpm": 14.2,
    "ambientTemperatureCelsius": 21.5,
    "detectedPersonsCount": 0
  }
}
```

### Campos

| Campo JSON | Campo Kotlin (`TelemetryData`) | Tipo | Valores / notas |
|---|---|---|---|
| `connection.state` | `connectionState` | enum | `ONLINE`, `DEGRADED`, `OFFLINE` |
| `connection.rssiDbm` | `rssiDbm` | int | dBm (ej. -55) |
| `connection.latencyMs` | `latencyMs` | long | ms |
| `battery.percentage` | `batteryPercentage` | int | 0–100. Baja si ≤ 15 |
| `battery.voltage` | `batteryVoltage` | float | V (24.0–25.2) |
| `battery.isCharging` | `isCharging` | bool | `true` en base y en `STANDBY` |
| `operationalState` | `operationalState` | enum | `STANDBY`, `PATROLLING`, `PAUSED`, `RETURNING_HOME`, `EMERGENCY_STOP`, `FAULT` |
| `mission.id` / `name` | `missionId` / `missionName` | string? | `null` sin misión |
| `mission.progress` | `missionProgress` | float | 0.0–100.0 |
| `mission.currentWaypoint` / `totalWaypoints` | `currentWaypoint` / `totalWaypoints` | int | `currentWaypoint` es el índice 1-based del waypoint en curso |
| `mission.waypointsReached` | `waypointsReached` | int | Waypoints ya alcanzados de la ruta. Lo usa el mapa para marcar visitados |
| `location.zone` | `zoneName` | string | |
| `location.x` / `y` | `posX` / `posY` | float | m, origen en la Estación Base (0,0) |
| `location.headingDeg` | `headingDeg` | float | 0–360. 0° apunta a +X y crece hacia +Y (eje Y hacia abajo, convención de pantalla) |
| `motion.speedMps` | `speedMps` | float | m/s |
| `motion.isMoving` | `isMoving` | bool | |
| `alerts[]` | `activeAlerts` | array | ver sección 4 |
| `lastUpdateTimestamp` | `lastUpdateTimestamp` | long | base para decidir si los datos son antiguos |
| `service.smokeGasPpm` | `smokeGasPpm` | double | ppm. Normal < 30, crítico > 60 |
| `service.ambientTemperatureCelsius` | `ambientTemperatureCelsius` | double | °C. Sobrecalentamiento > 42 |
| `service.detectedPersonsCount` | `detectedPersonsCount` | int | solo el conteo, no se transmiten imágenes |

**Datos antiguos (stale):** `isDataStale` no viaja en el mensaje. Lo calcula la app
cuando no llegan mensajes nuevos. Mientras dura la desconexión, la UI muestra el último
`lastUpdateTimestamp` recibido con la marca de datos obsoletos.

---

## 2. Comando (app → robot)

Modelo Kotlin: `CommandType` + parámetros (`Map<String, String>`).
`commandId` lo genera la app (`CMD-` + 8 hex) y sirve para correlacionar la respuesta.

```json
{
  "type": "command",
  "commandId": "CMD-3F9A21BC",
  "command": "START_MISSION",
  "params": { "missionId": "MIS-A", "missionName": "Ronda Integral del Edificio" },
  "timestamp": 1731950000000
}
```

| `command` | `params` | Condición de habilitación |
|---|---|---|
| `START_MISSION` | `missionId`, `missionName` | `ONLINE`, estado `STANDBY`, batería > 15 % |
| `PAUSE_MISSION` | – | `ONLINE`, estado `PATROLLING` |
| `RESUME_MISSION` | – | `ONLINE`, estado `PAUSED` |
| `CANCEL_MISSION` | – | `ONLINE`, estado `PATROLLING` o `PAUSED`. Requiere confirmación en la UI |
| `RETURN_TO_SAFE_POINT` | – | `ONLINE`, distinto de `STANDBY` en la base |
| `OPERATIONAL_STOP` | – | `ONLINE`. Requiere confirmación en la UI |
| `ACKNOWLEDGE_ALERT` | – | existe al menos una alerta sin reconocer |

> `OPERATIONAL_STOP` es una detención por software simulada. **No reemplaza la parada de emergencia física.**

---

## 3. Respuesta a comando (robot → app)

Modelo Kotlin: `CommandRecord` con `CommandStatus`.
Una orden **enviada** no equivale a una orden **ejecutada**: solo `CONFIRMED` indica
que el robot la aceptó y aplicó el efecto.

### 3.1 `PENDING`: orden enviada, sin respuesta todavía

```json
{
  "type": "commandResponse",
  "commandId": "CMD-3F9A21BC",
  "status": "PENDING",
  "message": "Enviando comando al robot...",
  "timestamp": 1731950000010
}
```

### 3.2 `CONFIRMED`: aceptada y ejecutada

```json
{
  "type": "commandResponse",
  "commandId": "CMD-3F9A21BC",
  "status": "CONFIRMED",
  "message": "Orden ejecutada con éxito.",
  "timestamp": 1731950000460
}
```

### 3.3 `REJECTED`: el robot recibió la orden y la rechazó

```json
{
  "type": "commandResponse",
  "commandId": "CMD-7B20E4D1",
  "status": "REJECTED",
  "message": "Batería insuficiente (12%).",
  "timestamp": 1731950001460
}
```

Motivos de rechazo que produce el simulador:

| Situación | `message` |
|---|---|
| `START_MISSION` fuera de `STANDBY` | `El robot no está en espera (Estado actual: <ESTADO>).` |
| `START_MISSION` con batería ≤ 15 % | `Batería insuficiente (<n>%).` |
| `PAUSE_MISSION` sin patrullaje | `No hay una misión en patrullaje para pausar.` |
| `RESUME_MISSION` sin pausa | `El robot no se encuentra en pausa.` |
| `CANCEL_MISSION` sin misión | `No hay misión activa para cancelar.` |
| `RETURN_TO_SAFE_POINT` ya en base | `El robot ya se encuentra en la base segura.` |
| `ACKNOWLEDGE_ALERT` sin alertas | `No existen alertas pendientes de reconocimiento.` |
| Rechazo forzado (modo de prueba) | `Orden rechazada por el robot: modo de simulación de rechazos activo.` |

### 3.4 `NO_RESPONSE`: timeout

Este estado **no lo envía el robot**: lo asigna la app cuando vence el tiempo de espera
(en el simulador, unos 2 s con el enlace caído). Se muestra como un registro local con el
mismo `commandId`:

```json
{
  "type": "commandResponse",
  "commandId": "CMD-91C4AA02",
  "status": "NO_RESPONSE",
  "message": "Sin respuesta del robot. Verifique el enlace de comunicaciones.",
  "timestamp": 1731950002000,
  "origin": "local-timeout"
}
```

La orden queda con resultado desconocido: pudo o no haberse ejecutado. El operador
debe verificar la telemetría al recuperar el enlace, y no asumir que fue o no aplicada.

---

## 4. Alerta (dentro de `alerts[]`)

Modelo Kotlin: `AlertItem`.

```json
{
  "id": "b3c1c7f0-5d0e-4a39-9b43-0f6f8a4d2f11",
  "code": "GAS_LEAK",
  "title": "Peligro: Fuga de Gas / Sobrecalentamiento",
  "message": "Lectura anómala en tablero eléctrico (96.5 ppm, 54.0°C). Freno preventivo.",
  "severity": "CRITICAL",
  "timestamp": 1731950003000,
  "acknowledged": false
}
```

| Campo | Tipo | Valores |
|---|---|---|
| `severity` | enum | `INFO`, `WARNING`, `CRITICAL` |
| `acknowledged` | bool | pasa a `true` al reconocerla (comando `ACKNOWLEDGE_ALERT` o reconocimiento individual) |
| `code` | string | `BATTERY_LOW` (WARNING), `OBSTACLE_BLOCKED` (WARNING), `GAS_LEAK` (CRITICAL) |

---

## 5. Eventos de auditoría (persistidos localmente en Room)

No viajan por el contrato. Los genera la app, los persiste en Room mediante `EventLogEntity`
(`eventType`: `SYSTEM`, `COMMAND`, `ALERT`, `SAFETY`, `DISCONNECTION`, `MISSION`) y
`MissionHistoryEntity` (estado `COMPLETED` o `CANCELLED`), y los expone hacia la capa de
presentación mapeados a los modelos de dominio `EventLogRecord` y `MissionRecord` (`AuditMappers.kt`).

---

## 6. Secuencia de ejemplo: comando rechazado

```
App                                   Robot
 |-- command START_MISSION (CMD-7B2) -->|
 |<-- commandResponse PENDING ----------|   (UI: "pendiente")
 |<-- commandResponse REJECTED ---------|   (UI: "rechazada: batería insuficiente")
```

Con el enlace caído:

```
App                                   Robot
 |-- command START_MISSION (CMD-91C) -->X   (no llega / sin respuesta)
 |   ... timeout local ...
 |   estado local NO_RESPONSE             (UI: "sin respuesta")
```
