# Alineación de Proyecto: Interfaz HMI/HRI para Robots de Servicio
**Documento de Trazabilidad y Cumplimiento de Consignas del Trabajo Práctico**  
**Cátedra:** Desarrollo de Aplicaciones I — Facultad de Ingeniería y Cs. Exactas, UADE  
**Plataforma Robótica:** Unitree G1 (Humanoide Bípedo)  
**Proyecto Android:** `unitreeg1hmi99` (Namespace: `ar.edu.uade.unitreeg1hmi99`)  
**Rama:** `master`  
**Fecha de Actualización:** Noviembre 2026  

---

## Matriz Resumen de Cumplimiento

| Sección del TP | Requerimiento de la Consigna | Estado | Componente / Archivo en el Proyecto |
| :--- | :--- | :---: | :--- |
| **1. Caso de Uso** | Servicio concreto en lugar real, problema actual y solución con robot | ✅ **Cumplido** | [`RobotSelectionScreen.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/screens/RobotSelectionScreen.kt) y [`mapa-2d.md`](file:///d:/androidProjects/unitreeg1hmi99/docs/mapa-2d.md) |
| **1. Plataforma** | Elección justificada G1 vs Go2, sensores nativos vs adicionales | ✅ **Cumplido** | [`RobotSelectionScreen.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/screens/RobotSelectionScreen.kt) |
| **1. Recorrido y Mapa 2D** | Esquema del entorno, plano 2D interactivo y rutas por misión con waypoints | ✅ **Cumplido** | [`MapModel.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/domain/model/MapModel.kt), [`PatrolMap.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/components/PatrolMap.kt) y [`mapa-2d.md`](file:///d:/androidProjects/unitreeg1hmi99/docs/mapa-2d.md) |
| **2. Interacción** | HMI (operador) y HRI (personas en el lugar), roles y toma de decisiones | ✅ **Cumplido** | [`DashboardScreen.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/screens/DashboardScreen.kt) (Banner de Decisión de Incidente, Mapa 2D y Modal Inferior de Comandos) |
| **2.1. Telemetría** | 8 variables generales obligatorias + 3 variables del servicio + métricas cinemáticas | ✅ **Cumplido** | [`TelemetryData.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/domain/model/TelemetryData.kt), [`CommonComponents.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/components/CommonComponents.kt) y [`DashboardScreen.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/screens/DashboardScreen.kt) |
| **2.2. Comandos** | 7 órdenes discretas con habilitación, respuesta y estado visible en modal deslizable | ✅ **Cumplido** | [`RobotEnums.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/domain/model/RobotEnums.kt), [`ValidateRobotCommandUseCase.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/domain/usecase/ValidateRobotCommandUseCase.kt) y [`DashboardScreen.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/screens/DashboardScreen.kt) (`SwipeableCommandsBottomModal`) |
| **3. Penpot / Figma / UI** | Vistas de supervisión, mapa 2D, telemetría, alertas, historial y paleta industrial ISA-101 | ✅ **Cumplido** | [`AppNavigation.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/navigation/AppNavigation.kt), [`Color.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/theme/Color.kt), [`Theme.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/theme/Theme.kt) y prototipo en Penpot / `../figma` |
| **4. Arquitectura** | Kotlin, Jetpack Compose, MVVM, Clean Architecture (`domain`/`data`/`ui`), StateFlow | ✅ **Cumplido** | [`RobotViewModel.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/viewmodel/RobotViewModel.kt), [`RobotRepository.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/domain/repository/RobotRepository.kt), [`ValidateRobotCommandUseCase.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/domain/usecase/ValidateRobotCommandUseCase.kt) y [`SimulatedRobotRepository.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/data/repository/SimulatedRobotRepository.kt) |
| **4. Persistencia** | Historial y auditoría en Room + persistencia de estado de misión activa entre cierres | ✅ **Cumplido** | [`AppDatabase.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/data/local/AppDatabase.kt), [`AuditMappers.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/data/mapper/AuditMappers.kt), [`SimulationStateStore.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/data/local/SimulationStateStore.kt) y [`HistoryScreen.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/screens/HistoryScreen.kt) |
| **4. Simulación y Contratos** | 100% offline con cinemática continua (200 ms), guardia 360° y contratos JSON | ✅ **Cumplido** | [`SimulatedRobotRepository.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/data/repository/SimulatedRobotRepository.kt), [`contrato-json.md`](file:///d:/androidProjects/unitreeg1hmi99/docs/contrato-json.md) y [`SimulatedRobotRepositoryTest.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/test/java/ar/edu/uade/unitreeg1hmi99/SimulatedRobotRepositoryTest.kt) |
| **5. Escenarios** | Demostración de los 5 escenarios obligatorios del TP + monitor en vivo y prueba extra | ✅ **Cumplido** | [`ScenariosScreen.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/screens/ScenariosScreen.kt) |
| **5. Seguridad HRI** | Matriz de 5 riesgos operativos y medidas de mitigación | ✅ **Cumplido** | Sección 5.2 de este documento |
| **Buenas Prácticas** | Desacople de textos en `res/values/strings.xml` y mapeo `@StringRes` en capa UI | ✅ **Cumplido** | [`strings.xml`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/res/values/strings.xml) y [`RobotUiMappers.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/components/RobotUiMappers.kt) |

---

## 1. Caso de Uso y Plataforma Robótica

### 1.1. Definición del Servicio, Entorno Real y Plano 2D
* **Problema existente hoy:** En edificios educativos, plantas industriales y sedes corporativas, las rondas de seguridad e inspección nocturna dependen exclusivamente de personal de vigilancia física. Esto genera fatiga, inconsistencias en los registros, falta de detección cuantitativa de fallas ambientales tempranas y exposición humana a riesgos (escapes de gas, sobrecalentamiento de salas de servidores o tableros de alta tensión).
* **Cómo se resuelve hoy:** Recorridos manuales con linterna y libro de actas en papel cada 2 o 3 horas.
* **Quién usa el servicio:** El operador del centro de control o guardia de seguridad mediante una tablet/smartphone Android.
* **Qué tarea cumple el robot:** Ejecuta rondas de patrullaje autónomo programadas con telemetría en tiempo real, verifica el estado de pasillos, se detiene en cada punto de control para ejecutar un barrido de guardia de 360°, mide gases y temperaturas, detecta personas en zonas restringidas y notifica anomalías al operador solicitando intervención humana ante contingencias.
* **Esquema del entorno y modelo de plano 2D (`MapModel.kt`):** Definido en [`MapModel.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/domain/model/MapModel.kt) mediante el objeto `BuildingMap.default` (`FloorMap` de `-2..25 m` en X y `-2..18 m` en Y), que modela las **8 zonas rectangulares del Pabellón A**, la **Estación Base de Carga** y los **8 waypoints** con coordenadas en metros:
  - `Base`: Estación Base de Carga `(X: 0.0, Y: 0.0)` — Zona: *Hall Central / Base* (`ZoneKind.BASE`)
  - `Waypoint 0 (Punto 1)`: Pabellón A - Hall Central `(X: 2.0, Y: 1.0)` — Zona: *Hall Central / Base*
  - `Waypoint 1 (Punto 2)`: Pasillo de Aulas 101-105 `(X: 8.0, Y: 2.5)` — Zona: *Aulas 101-105* (`ZoneKind.CLASSROOM`)
  - `Waypoint 2 (Punto 3)`: Laboratorio de Robótica e IoT `(X: 14.0, Y: 5.0)` — Zona: *Lab. Robótica* (`ZoneKind.TECHNICAL`)
  - `Waypoint 3 (Punto 4)`: Sala de Servidores y Tablero B `(X: 18.0, Y: 8.0)` — Zona: *Servidores* (`ZoneKind.TECHNICAL`)
  - `Waypoint 4 (Punto 5)`: Salida de Emergencia Este `(X: 22.0, Y: 12.0)` — Zona: *Salida Este* (`ZoneKind.EXIT`)
  - `Waypoint 5 (Punto 6)`: Depósito de Mantenimiento `(X: 16.0, Y: 15.0)` — Zona: *Depósito* (`ZoneKind.STORAGE`)
  - `Waypoint 6 (Punto 7)`: Pasillo Sur - Oficinas `(X: 9.0, Y: 10.0)` — Zona: *Oficinas* (`ZoneKind.OFFICE`)
  - `Waypoint 7 (Punto 8)`: Punto Final de Inspección `(X: 8.0, Y: 8.0)` — Zona: *Punto Final* (`ZoneKind.COMMON`)

* **Rutas diferenciadas por plantilla de misión:**

| ID Misión | Nombre de la Misión | Índices de Waypoints (`0..7`) | Cantidad | Duración Est. |
| :--- | :--- | :--- | :---: | :---: |
| **`MIS-A`** | Ronda Integral del Edificio | `0, 1, 2, 3, 4, 5, 6, 7` | 8 pts | 12 min |
| **`MIS-B`** | Inspección Técnica de Tableros | `2, 3, 4, 5, 7` | 5 pts | 8 min |
| **`MIS-C`** | Patrullaje Perimetral y Accesos | `0, 2, 3, 4` | 4 pts | 6 min |

### 1.2. Justificación de Unitree G1 y Ficha Técnica
* **Justificación frente a una alternativa sin robot:** La ronda automatizada garantiza continuidad 24/7 sin intervalos ciegos, mide cuantitativamente gases y temperatura, y almacena auditoría histórica en base de datos local Room.
* **Justificación de G1 (Humanoide) frente a Go2 (Cuadrúpedo):**
  1. **Inspección a altura visual humana (1.27 m):** Permite lectura directa de tableros eléctricos, extintores de pared y señalética sin puntos ciegos bajos.
  2. **Aceptación social HRI superior:** Genera una presencia amigable y natural ante docentes, alumnos o personal de guardia.
  3. **Capacidad de manipulación bípeda (23–43 DOF):** Sus brazos manipuladores permiten interactuar con botones de emergencia, puertas o manijas en etapas futuras.
* **Capacidades disponibles (nativas de Unitree G1):**
  - LiDAR 3D frontal para mapeo SLAM y detección de obstáculos.
  - Cámara de Profundidad Intel RealSense / visión estéreo.
  - Unidad IMU de 6 ejes para balance dinámico bípedo.
  - Altavoces y micrófonos para interacción HRI con personas.
  - Batería de litio de liberación rápida (9000 mAh, 25.2 V, autonomía ~2 hs).
* **Equipamiento adicional requerido por el servicio de seguridad:**
  - Sensor electroquímico de gases / $\text{CO}_2$ / humo (MQ-2 / BME688) para inspección ambiental.
  - Sensor infrarrojo térmico o pirómetro sin contacto para detección de sobrecalentamiento en tableros.
  - Módulo de radio de largo alcance WiFi Mesh para evitar zonas ciegas en subsuelos.

*Implementado en:* [`RobotSelectionScreen.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/screens/RobotSelectionScreen.kt).

---

## 2. Interacción y Operación (HMI / HRI)

### 2.1. Telemetría Dinámica y Mapa 2D en Tiempo Real (Sección 2.1)
El modelo [`TelemetryData.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/domain/model/TelemetryData.kt) implementa todas las variables requeridas:

1. **Conectividad:** `connectionState` (`ONLINE`, `DEGRADED`, `OFFLINE`), señal WiFi `rssiDbm` (-55 dBm) y latencia `latencyMs` (~24–33 ms), renderizados en [`ConnectivityBadge`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/components/CommonComponents.kt#L254-L385) con barra de intensidad y píldora de latencia o `"Stale"`.
2. **Batería:** `batteryPercentage` (0–100%), voltaje `batteryVoltage` (24.0V–25.2V) e indicador de recarga `isCharging`, renderizados en [`BatteryBadge`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/components/CommonComponents.kt#L138-L251) con barra de progreso y estado (`Cargando (+2%/s)`, `Autonomía OK`, `Nivel bajo`, `Nivel crítico`).
3. **Estado Operativo:** `OperationalState` (`STANDBY`, `PATROLLING`, `PAUSED`, `RETURNING_HOME`, `EMERGENCY_STOP`, `FAULT`), exhibido en la barra superior mediante [`OperationalStateBadge`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/components/CommonComponents.kt#L84-L135) con indicador luminoso pulsante.
4. **Misión y Progreso:** `missionId`, `missionName`, `missionProgress` (0.0% a 100.0%), `currentWaypoint`, `totalWaypoints` y `waypointsReached` (cantidad de puntos de la ruta ya visitados).
5. **Ubicación y Plano 2D Interactivo (`PatrolMap.kt`):** `zoneName`, coordenadas `posX`, `posY` y orientación `headingDeg`. Además de las tarjetas de progreso y métricas espaciales (`KinematicInfoCard`), el componente [`PatrolMap`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/components/PatrolMap.kt) dibuja en tiempo real:
   - Zonas coloreadas del Pabellón A, grilla cada 5 metros y barra de escala.
   - Ruta planificada (trazo punteado) y tramo recorrido (trazo sólido verde).
   - Waypoints numerados diferenciados por estado: visitado (verde), siguiente objetivo (ámbar con halo), pendiente (gris). Al tocar cualquier waypoint en el lienzo se muestra su nombre de zona y la distancia exacta al robot.
   - Posición suavizada del robot con flecha de orientación (`headingDeg`), cono de visión (que gira 360° durante la pausa de guardia en cada waypoint) y estela de rastro.
   - Superposiciones visuales de contingencias: zona resaltada en rojo pulsante ante fuga de gas (`GAS_LEAK`), marcador `X` delante del robot ante obstáculo (`OBSTACLE_BLOCKED`), anillo de advertencia en la estación base ante batería baja (`BATTERY_LOW`), marcadores de personas detectadas delante del robot y estilo gris punteado con leyenda *"Última posición conocida — sin enlace"* ante desconexión (`isDataStale`).
   - Capacidad de **colapsar/expandir** el mapa desde su cabecera o mediante el botón flotante fijo [`FixedMapQuickToggleButton`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/screens/DashboardScreen.kt#L1069-L1106) (`"Colapsar Mapa"` / `"Abrir Mapa"`).
6. **Movimiento y Cinemática:** velocidad lineal `speedMps` (m/s), indicador de marcha `isMoving`, orientación en grados con rumbo cardinal (`Rumbo Este (E)`, `Rumbo SE`, etc.) y coordenadas `(X, Y)` en metros.
7. **Alertas:** lista `activeAlerts` (`AlertItem` con id, código, título, mensaje, severidad y bandera `acknowledged`).
8. **Última Actualización y Congelamiento:** `lastUpdateTimestamp` y bandera de datos obsoletos `isDataStale` (se activa al perder enlace en Escenario 3).
9. **Tres Variables del Servicio (Inspección y Seguridad):**
   - `smokeGasPpm: Double`: Concentración de gas/humo (Normal: 12–15 ppm; Peligro/Alerta: >50 ppm).
   - `ambientTemperatureCelsius: Double`: Temperatura en salas de tableros (Normal: 21–22.5 °C; Sobrecalentamiento: >40 °C).
   - `detectedPersonsCount: Int`: Conteo anónimo de personas detectadas por la guardia al inspeccionar zonas restringidas (Oficinas: 2, Depósito: 1).

*Implementado en:* [`TelemetryData.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/domain/model/TelemetryData.kt), [`PatrolMap.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/components/PatrolMap.kt) y [`DashboardScreen.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/screens/DashboardScreen.kt).

### 2.2. Comandos, Modal Inferior Deslizable y Condiciones de Habilitación (Sección 2.2)
Los comandos están modelados en [`RobotEnums.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/domain/model/RobotEnums.kt) como `CommandType`, sus reglas de negocio y habilitación se evalúan en [`ValidateRobotCommandUseCase.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/domain/usecase/ValidateRobotCommandUseCase.kt), y se operan desde el **Modal Inferior Deslizable** ([`SwipeableCommandsBottomModal`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/screens/DashboardScreen.kt#L1101-L1400)), acoplado sobre la barra de navegación inferior con soporte de gesto vertical (*swipe up/down*) y toque directo:

| Comando | Condiciones de Habilitación en UI | Estado de la Orden y Efecto |
| :--- | :--- | :--- |
| **`START_MISSION`** | `ONLINE`, estado `STANDBY` y Batería > 15% | Pasa a `PENDING` $\rightarrow$ `CONFIRMED`. Inicia patrullaje sobre la ruta de la misión seleccionada (`MIS-A`, `MIS-B` o `MIS-C`). |
| **`PAUSE_MISSION`** | `ONLINE` y estado `PATROLLING` | Congela la posición del robot en el tramo o waypoint actual (`PAUSED`). |
| **`RESUME_MISSION`** | `ONLINE` y estado `PAUSED` | Retoma la marcha continua hacia el siguiente waypoint pendiente. |
| **`CANCEL_MISSION`** | `ONLINE` y estado `PATROLLING` o `PAUSED` | Abre modal de confirmación, frena la misión y persiste el registro en Room como `CANCELLED`. |
| **`RETURN_TO_SAFE_POINT`** | `ONLINE` y estado $\neq$ (`STANDBY` en Base) | Activa retorno autónomo (`RETURNING_HOME`) desandando los waypoints visitados en orden inverso hasta la Estación Base `(0, 0)`. |
| **`OPERATIONAL_STOP`** | Siempre habilitado en el panel | Abre modal de doble validación. Ejecuta parada preventiva de software (`EMERGENCY_STOP`). |
| **`ACKNOWLEDGE_ALERT`** | Hay alertas activas con `acknowledged = false` | Marca alertas como reconocidas y registra el evento en Room. |

* **Ciclo de vida visible de cada orden:** Cada comando actualiza [`CommandStatusCard`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/components/CommonComponents.kt#L388-L541) (visible tanto en el cuerpo de Supervisión como dentro del modal deslizable expandido):
  - *Sin órdenes previas:* Tarjeta neutra *"Canal de Comandos Listo — Ninguna orden emitida aún en esta sesión"*.
  - `PENDING` (ámbar, mientras viaja la orden simulada durante 450 ms).
  - `CONFIRMED` (verde, aceptada y aplicada por la máquina de estados).
  - `REJECTED` (rojo con motivo explícito, ej. batería insuficiente o modo de rechazo forzado).
  - `NO_RESPONSE` (naranja oscuro, tras timeout de 1950 ms al estar `OFFLINE` o si la app se cerró mientras la orden estaba `PENDING`).
* **Advertencia de seguridad física:** Visible permanentemente dentro del modal de comandos y en el diálogo de confirmación de parada operativa: *"Seguridad HRI: la detención por software no reemplaza la parada de emergencia física."*

---

## 3. Diseño en Penpot / Figma y Estructura de Pantallas

El diseño de interfaz en Penpot (y su paquete equivalente en `../figma`) refleja fielmente la interfaz actualizada de la rama `master`:
* **Sistema de Diseño Industrial HMI (Inspirado en ISA-101):**
  - Implementado en [`Color.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/theme/Color.kt), [`Theme.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/theme/Theme.kt) y [`Type.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/theme/Type.kt) con `dynamicColor = false` para preservar el significado semántico de cada color operativo:
    - **Brand Primary (Deep Cobalt):** `#1E40AF` / Container `#DBEAFE` / OnContainer `#1E3A8A`
    - **Secondary (Cyber Teal):** `#0F766E` / Container `#CCFBF1`
    - **Nominal / Success (`PATROLLING` / `ONLINE`):** `#15803D` / Bg `#DCFCE7` / Border `#86EFAC`
    - **Standby / Info (`STANDBY`):** `#1D4ED8` / Bg `#DBEAFE` / Border `#93C5FD`
    - **Warning (`PAUSED` / `DEGRADED` / Batería Baja):** `#D97706` / Bg `#FEF3C7` / Border `#FCD34D`
    - **Critical / Danger (`EMERGENCY_STOP` / `OFFLINE` / Fuga Gas):** `#DC2626` (Dark `#991B1B`) / Bg `#FEE2E2` / Border `#FCA5A5`
    - **Return / Safe Point (`RETURNING_HOME`):** `#6D28D9` / Bg `#F3E8FF` / Border `#D8B4FE`
    - **Neutral / Stale / Surface:** Background `#F4F6FA`, Surface `#FFFFFF`, SurfaceVariant `#E8EEF5`, Muted `#64748B`.
* **Mapa de navegación:** Controlado en [`AppNavigation.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/navigation/AppNavigation.kt) mediante `Scaffold` + `NavigationBar` con transición `Crossfade` entre 5 destinos:
  1. **Supervisión (`DashboardScreen`):** TopAppBar con `OperationalStateBadge`, fila simétrica de estado rápido (`ConnectivityBadge` y `BatteryBadge`), banners condicionales (Desconexión/Stale, Recarga Activa en Base, Decisión HRI ante Incidentes), **Mapa 2D de Patrullaje colapsable (`PatrolMap`)** con botón flotante fijo (`FixedMapQuickToggleButton`), selector de 3 misiones en Standby (`MIS-A`, `MIS-B`, `MIS-C`), tarjeta de Progreso de Ronda (o tarjeta violeta de Regreso a Base), 3 tarjetas de Variables de Servicio (`ServiceMetricCard`), 3 tarjetas de Métricas Cinemáticas (`KinematicInfoCard`), tarjeta de Última Orden (`CommandStatusCard`) y **Modal Inferior Deslizable de Comandos (`SwipeableCommandsBottomModal`)**.
  2. **Alertas (`AlertsScreen`):** TopAppBar con contador de alertas y botón `"Reconocer Todas"`, vista nominal con escudo verde y 3 chips (`Gases < 25 ppm`, `Temp ~21 °C`, `SLAM Activo`) cuando no hay contingencias, o lista activa con 3 tarjetas resumen (`Críticas`, `Advertencias`, `Sin Reconocer`) y tarjetas `AlertItemCard` con reconocimiento individual.
  3. **Historial (`HistoryScreen`):** Auditoría local persistida en Room con botón de limpieza (`DeleteSweep`), `PrimaryTabRow` (`Misiones` y `Logs de Eventos`), 3 chips de resumen (`Completadas`, `Canceladas`, `Logs Room`), tarjetas `MissionCard` con píldoras de métricas (`pts`, `ppm`, `°C`) y tarjetas `EventLogCard`.
  4. **Escenarios (`ScenariosScreen`):** Panel de evaluación del TP con botón `"Reset"`, tarjeta **Monitor de Estado en Vivo del Simulador** (`OperationalStateBadge` + 3 `LiveStatusMiniPill`s de Batería, Enlace y Gas/Temp), tarjetas de acción para los 5 escenarios obligatorios y switch adicional para rechazo forzado de órdenes.
  5. **Unitree G1 (`RobotSelectionScreen`):** Ficha técnica del humanoide con tarjeta Hero (`127 cm · 35 kg`, `23–43 DOF`, `9000 mAh`), tabla de especificaciones de hardware, comparativa de sensores nativos vs adicionales del servicio y las 3 razones numeradas de justificación HRI (`G1 vs Go2`).

---

## 4. APK, Persistencia Dual y Simulación

### 4.1. Arquitectura de Software y Persistencia Dual
* **Lenguaje y UI:** Kotlin con Jetpack Compose (Material 3) y configuración `enableEdgeToEdge()` en [`MainActivity.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/MainActivity.kt).
* **Patrón de Arquitectura:** MVVM (Model-View-ViewModel) con Clean Architecture de 3 capas (`domain`, `data`, `ui`) y Flujo Unidireccional de Datos (UDF):
  - **Capa Domain (`domain/`):** 100% Kotlin puro sin dependencias de Android ni Room. Incluye modelos inmutables ([`TelemetryData`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/domain/model/TelemetryData.kt), [`FloorMap`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/domain/model/MapModel.kt), [`RobotEnums`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/domain/model/RobotEnums.kt), [`AuditModels`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/domain/model/AuditModels.kt)), el contrato [`RobotRepository`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/domain/repository/RobotRepository.kt) y el caso de uso [`ValidateRobotCommandUseCase`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/domain/usecase/ValidateRobotCommandUseCase.kt).
  - **Capa Data (`data/`):** Entidades y DAOs de Room, mappers hacia dominio ([`AuditMappers.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/data/mapper/AuditMappers.kt)), almacén de estado activo [`SimulationStateStore`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/data/local/SimulationStateStore.kt) y [`SimulatedRobotRepository`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/data/repository/SimulatedRobotRepository.kt) (Singleton).
  - **Capa UI / ViewModel (`ui/`):** [`RobotViewModel.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/viewmodel/RobotViewModel.kt) y [`HistoryViewModel.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/viewmodel/HistoryViewModel.kt) exponen `StateFlow` inmutable observado con `collectAsStateWithLifecycle()`, y [`RobotUiMappers.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/components/RobotUiMappers.kt) mapea los enums de dominio a recursos `@StringRes`.
* **Persistencia Local de Historial y Auditoría (Room SQLite):**
  - Base de datos: [`AppDatabase.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/data/local/AppDatabase.kt) (`unitree_g1_hmi.db`).
  - Entidad de Misiones: [`MissionHistoryEntity.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/data/local/entity/MissionHistoryEntity.kt) (`COMPLETED` / `CANCELLED`, duración, waypoints alcanzados, picos de gas/temperatura y notas).
  - Entidad de Auditoría: [`EventLogEntity.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/data/local/entity/EventLogEntity.kt) (`SYSTEM`, `COMMAND`, `ALERT`, `SAFETY`, `DISCONNECTION`, `MISSION`).
* **Persistencia de Estado de Misión Activa (`SimulationStateStore.kt`):**
  - Implementada en [`SimulationStateStore.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/data/local/SimulationStateStore.kt) mediante `SharedPrefsSimulationStateStore` (`SharedPreferences` `unitree_g1_sim_state`).
  - Serializa en JSON una instantánea completa (`SimulationSnapshot`) con la telemetría actual, la última orden, las banderas de escenario (`isForcedOffline`, `isForcedRejection`), la cola de waypoints pendientes (`routeQueue`), la ruta activa (`currentRoute`), distancias, progreso de retorno, pausa de guardia restante (`dwellRemainingSec`) y acumulador de batería.
  - Se guarda automáticamente en cada transición de estado, cada segundo (slow tick) y de forma síncrona en `MainActivity.onStop()`. Al reabrir la app, `SimulatedRobotRepository` restaura la misión en el punto exacto donde estaba; si había un comando en `PENDING` al cerrarse la app, pasa a `NO_RESPONSE`.

### 4.2. Motor de Simulación Cinemática 100% Offline
Implementado en [`SimulatedRobotRepository.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/data/repository/SimulatedRobotRepository.kt):
* Funciona de forma autónoma sin conexión a internet ni servidor externo.
* **Reloj de doble frecuencia:**
  - **Tick rápido cada `200 ms` (`5 Hz`, `TIME_SCALE = 2f`):** actualiza la cinemática continua (`posX`, `posY`, `headingDeg`, `speedMps = 0.85 m/s` en patrullaje y `0.70 m/s` en retorno), calcula el progreso real por distancia restante sobre la polilínea de la ruta y gestiona el acumulador fraccional de batería (`-0.3%/s` en patrullaje, `+2.0%/s` cargando en base).
  - **Pausa de guardia y barrido 360°:** al alcanzar cada waypoint intermedio, el robot se detiene durante `DWELL_SECONDS = 3f` (simulados) y gira sobre su eje a `120°/s` para inspeccionar la sala, detectando de forma determinista personas en zonas restringidas (2 en *Oficinas*, 1 en *Depósito*).
  - **Tick lento cada `1000 ms` (`1 Hz`):** actualiza las fluctuaciones ambientales (`smokeGasPpm`, `ambientTemperatureCelsius`) y métricas de enlace (`rssiDbm`, `latencyMs`).
* **Retorno a base por backtracking:** al recibir `RETURN_TO_SAFE_POINT`, construye la ruta inversa a través de los waypoints ya visitados (`currentRoute.take(reachedCount).reversed() + baseStation`) hasta acoplarse en `(0.0, 0.0)`.
* **Tests unitarios:** [`SimulatedRobotRepositoryTest.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/test/java/ar/edu/uade/unitreeg1hmi99/SimulatedRobotRepositoryTest.kt) valida movimiento continuo sin saltos, orientación hacia el siguiente waypoint, progreso monótono, rutas por plantilla (`MIS-A`, `MIS-B`, `MIS-C`), pausa de guardia con barrido, pausa/reanudación, retorno a base, detección de personas y restauración de `SimulationSnapshot`.

---

## 5. Pruebas y Seguridad

### 5.1. Los 5 Escenarios Obligatorios Demostrables
Disponibles con un solo clic en [`ScenariosScreen.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/screens/ScenariosScreen.kt) junto con un monitor de estado en vivo:

1. **Escenario 1 — Misión Normal:**
   El operador elige una misión (`MIS-A`, `MIS-B` o `MIS-C`), presiona "Iniciar", el robot recorre continuamente los waypoints en el mapa 2D deteniéndose a barrer 360° en cada punto, al alcanzar el 100% frena en destino, abre el diálogo de fin de misión y persiste el resultado en Room como `COMPLETED`.
2. **Escenario 2 — Batería Baja:**
   El simulador reduce la batería al 12%. Se dispara la alerta `WARNING: BATTERY_LOW`, el mapa 2D dibuja un anillo pulsante en la Estación Base, se bloquea el inicio de nuevas rondas y se habilita el comando "Regreso Seguro".
3. **Escenario 3 — Desconexión de Red:**
   Se activa la pérdida de enlace WiFi. La telemetría se marca como `isDataStale = true` (banner rojo de datos congelados y robot en gris con borde punteado en el mapa 2D), las órdenes quedan en `PENDING` y pasan a `NO_RESPONSE` por timeout. Al desactivar el interruptor, la conexión se restablece inmediatamente.
4. **Escenario 4 — Incidente por Obstáculo:**
   El robot detecta un bloqueo en pasillo (>10s), frena en estado `PAUSED`, dibuja el marcador de obstáculo delante del robot en el mapa 2D y despliega en la pantalla principal el **Banner de Decisión de Incidente** ofreciendo al operador: "Reanudar", "Cancelar" o "A Base".
5. **Escenario 5 — Escenario Propio (Fuga de Gas y Sobrecalentamiento Crítico):**
   En la zona de tableros se registran 96.5 ppm de gas y 54.0°C de temperatura. Se dispara alarma `CRITICAL: GAS_LEAK`, la zona afectada titila en rojo en el mapa 2D, el robot ejecuta parada preventiva y exige que el operador tome una decisión (reconocer alerta e iniciar repliegue a base).

### 5.2. Matriz de 5 Riesgos Operativos y de Interacción (HRI)

| # | Riesgo Identificado | Tipo | Medida Implementada (Interfaz / Lógica / Procedimiento) |
|---|:---|:---:|:---|
| **1** | **Colisión o atropello de peatones/personal** | HRI | Freno autónomo preventivo, velocidad limitada a 0.85 m/s en interiores, visualización de posición en el Mapa 2D y conteo anónimo de personas en pantalla. |
| **2** | **Envío accidental de comandos críticos** | HMI / Lógica | Modal deslizable dedicado y diálogos de confirmación (`ConfirmDialog`) con doble validación obligatoria para `CANCEL_MISSION` y `OPERATIONAL_STOP`. |
| **3** | **Pérdida de enlace en puntos ciegos** | Operativo | Marcado visual de telemetría congelada (`stale`) en tarjetas y Mapa 2D ("Última posición conocida"), timeout `NO_RESPONSE` en órdenes y persistencia local de estado. |
| **4** | **Falsa sensación de seguridad tras parada por app** | HMI / Procedimiento | Cartelería permanente en el modal de comandos y en el diálogo de confirmación aclarando que la detención por software no reemplaza el botón de parada de emergencia física. |
| **5** | **Privacidad de ocupantes del edificio** | HRI / Legal | No se almacenan imágenes ni rostros en disco: la cámara procesa localmente en memoria durante la guardia y solo emite un número entero (`detectedPersonsCount`). |

---

## 6. Entregables y Documentación Técnica

* **Código fuente:** Completo, tipado y compilando con Gradle y JVM 17.
* **APK Instalable:** Generado en `d:\androidProjects\unitreeg1hmi99\app\build\outputs\apk\debug\app-debug.apk`.
* **Persistencia Dual:** SQLite / Room 2.6.1 (`unitree_g1_hmi.db`) + `SharedPrefsSimulationStateStore` (`unitree_g1_sim_state`).
* **Documentación de Arquitectura y Contratos:**
  - [`arquitectura.md`](file:///d:/androidProjects/unitreeg1hmi99/arquitectura.md): Diagramas de capas, flujo UDF, máquina de estados, código del repositorio simulado, persistencia de estado activo y ejemplos JSON.
  - [`contrato-json.md`](file:///d:/androidProjects/unitreeg1hmi99/docs/contrato-json.md): Especificación detallada del contrato JSON propuesto (telemetría, comandos, respuestas y alertas).
  - [`mapa-2d.md`](file:///d:/androidProjects/unitreeg1hmi99/docs/mapa-2d.md): Documentación del módulo de visualización del patrullaje en 2D (`FloorMap` y `PatrolMap`).
