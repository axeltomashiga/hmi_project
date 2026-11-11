# Alineación de Proyecto: Interfaz HMI/HRI para Robots de Servicio
**Documento de Trazabilidad y Cumplimiento de Consignas del Trabajo Práctico**  
**Cátedra:** Desarrollo de Aplicaciones I — Facultad de Ingeniería y Cs. Exactas, UADE  
**Plataforma Robótica:** Unitree G1 (Humanoide Bípedo)  
**Proyecto Android:** `unitreeg1hmi99` (Namespace: `ar.edu.uade.unitreeg1hmi99`)  
**Fecha de Actualización:** Noviembre 2026  

---

## Matriz Resumen de Cumplimiento

| Sección del TP | Requerimiento de la Consigna | Estado | Componente / Archivo en el Proyecto |
| :--- | :--- | :---: | :--- |
| **1. Caso de Uso** | Servicio concreto en lugar real, problema actual y solución con robot | ✅ **Cumplido** | [RobotSelectionScreen.kt](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/screens/RobotSelectionScreen.kt) y [spec-hmi-unitree-g1.md](file:///C:/Users/axelt/.gemini/antigravity/brain/71eade49-0a01-4eaa-abb6-3a8c22388984/spec-hmi-unitree-g1.md) |
| **1. Plataforma** | Elección justificada G1 vs Go2, sensores nativos vs adicionales | ✅ **Cumplido** | [RobotSelectionScreen.kt](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/screens/RobotSelectionScreen.kt) |
| **1. Recorrido** | Esquema del entorno y recorrido con waypoints | ✅ **Cumplido** | [SimulatedRobotRepository.kt](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/data/repository/SimulatedRobotRepository.kt#L35-L45) |
| **2. Interacción** | HMI (operador) y HRI (personas en el lugar), roles y toma de decisiones | ✅ **Cumplido** | [DashboardScreen.kt](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/screens/DashboardScreen.kt) (Banner de Decisión de Incidente) |
| **2.1. Telemetría** | 8 variables generales obligatorias + 3 variables del servicio | ✅ **Cumplido** | [TelemetryData.kt](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/data/model/TelemetryData.kt) y [CommonComponents.kt](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/components/CommonComponents.kt) |
| **2.2. Comandos** | 7 órdenes discretas con habilitación, respuesta y estado visible | ✅ **Cumplido** | [RobotEnums.kt](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/data/model/RobotEnums.kt) y [DashboardScreen.kt](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/screens/DashboardScreen.kt) |
| **3. Figma / UI** | Vistas de supervisión, telemetría, alertas, historial, estados visuales | ✅ **Cumplido** | [AppNavigation.kt](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/navigation/AppNavigation.kt) y pantallas Jetpack Compose |
| **4. Arquitectura** | Kotlin, Jetpack Compose, MVVM, ViewModel, StateFlow, Repository desacoplado | ✅ **Cumplido** | [RobotViewModel.kt](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/viewmodel/RobotViewModel.kt) y [SimulatedRobotRepository.kt](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/data/repository/SimulatedRobotRepository.kt) |
| **4. Persistencia** | Almacenamiento local de misiones y auditoría de eventos en Room | ✅ **Cumplido** | [AppDatabase.kt](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/data/local/AppDatabase.kt), DAOs y [HistoryScreen.kt](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/screens/HistoryScreen.kt) |
| **4. Simulación** | 100% offline (sin robot, servidor ni internet), contratos JSON | ✅ **Cumplido** | [SimulatedRobotRepository.kt](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/data/repository/SimulatedRobotRepository.kt) y [spec-hmi-unitree-g1.md](file:///C:/Users/axelt/.gemini/antigravity/brain/71eade49-0a01-4eaa-abb6-3a8c22388984/spec-hmi-unitree-g1.md) |
| **5. Escenarios** | Demostración de los 5 escenarios obligatorios del TP | ✅ **Cumplido** | [ScenariosScreen.kt](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/screens/ScenariosScreen.kt) |
| **5. Seguridad HRI** | Matriz de 5 riesgos operativos y medidas de mitigación | ✅ **Cumplido** | Sección 5.2 de este documento y [spec-hmi-unitree-g1.md](file:///C:/Users/axelt/.gemini/antigravity/brain/71eade49-0a01-4eaa-abb6-3a8c22388984/spec-hmi-unitree-g1.md) |
| **Buenas Prácticas** | Desacople de textos en `res/values/strings.xml` (sin hardcoded strings) | ✅ **Cumplido** | [strings.xml](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/res/values/strings.xml) y `@get:StringRes` en enums |

---

## 1. Caso de Uso y Plataforma Robótica

### 1.1. Definición del Servicio y Entorno Real
* **Problema existente hoy:** En edificios educativos, plantas industriales y sedes corporativas, las rondas de seguridad e inspección nocturna dependen exclusivamente de personal de vigilancia física. Esto genera fatiga, inconsistencias en los registros, falta de detección cuantitativa de fallas ambientales tempranas y exposición humana a riesgos (escapes de gas, sobrecalentamiento de salas de servidores o tableros de alta tensión).
* **Cómo se resuelve hoy:** Recorridos manuales con linterna y libro de actas en papel cada 2 o 3 horas.
* **Quién usa el servicio:** El operador del centro de control o guardia de seguridad mediante una tablet/smartphone Android.
* **Qué tarea cumple el robot:** Ejecuta rondas de patrullaje autónomo programadas con telemetría en tiempo real, verifica el estado de pasillos, mide gases y temperaturas, detecta personas en zonas no autorizadas y notifica anomalías al operador solicitando intervención humana ante contingencias.
* **Esquema del entorno y recorrido:** Definido en `SimulatedRobotRepository.kt` mediante una secuencia de 8 zonas y waypoints con coordenadas relativas:
  1. `Waypoint 0`: Pabellón A - Hall Central (X: 2.0, Y: 1.0)
  2. `Waypoint 1`: Pasillo de Aulas 101-105 (X: 8.0, Y: 2.5)
  3. `Waypoint 2`: Laboratorio de Robótica e IoT (X: 14.0, Y: 5.0)
  4. `Waypoint 3`: Sala de Servidores y Tablero B (X: 18.0, Y: 8.0)
  5. `Waypoint 4`: Salida de Emergencia Este (X: 22.0, Y: 12.0)
  6. `Waypoint 5`: Depósito de Mantenimiento (X: 16.0, Y: 15.0)
  7. `Waypoint 6`: Pasillo Sur - Oficinas (X: 9.0, Y: 10.0)
  8. `Waypoint 7`: Punto Final de Inspección (X: 8.0, Y: 8.0)
  9. `Base`: Estación Base de Carga (X: 0.0, Y: 0.0)

### 1.2. Justificación de Unitree G1 y Ficha Técnica
* **Justificación frente a una alternativa sin robot:** La ronda automatizada garantiza continuidad 24/7 sin intervalos ciegos, mide cuantitativamente gases y temperatura, y almacena auditoría histórica en base de datos local Room.
* **Justificación de G1 (Humanoide) frente a Go2 (Cuadrúpedo):** 
  - La interacción HRI humanoide resulta más amigable y natural en entornos institucionales (como universidades).
  - La silueta bípeda permite inspección a la altura visual humana (tableros a 1.20m, extintores de pared y señalización).
* **Capacidades disponibles (nativas de Unitree G1):**
  - LiDAR 3D frontal para mapeo SLAM y detección de obstáculos.
  - Cámara de Profundidad Intel RealSense para visión espacial.
  - IMU de 6 ejes para balance dinámico bípedo.
  - Batería de litio de liberación rápida (9000 mAh, 25.2 V, autonomía ~2 hs).
* **Equipamiento adicional requerido por el servicio de seguridad:**
  - Sensor de gases electroquímico (MQ-2 / BME688) para detección de monóxido y humo.
  - Sensor termográfico / pirómetro infrarrojo para tableros eléctricos.
  - Módulo de comunicación inalámbrica de largo alcance.

*Implementado en:* [`RobotSelectionScreen.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/screens/RobotSelectionScreen.kt).

---

## 2. Interacción y Operación (HMI / HRI)

### 2.1. Telemetría Dinámica (Sección 2.1)
El modelo [`TelemetryData.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/data/model/TelemetryData.kt) implementa todas las variables requeridas:

1. **Conectividad:** `connectionState` (`ONLINE`, `DEGRADED`, `OFFLINE`), señal WiFi `rssiDbm` (-55 dBm) y latencia `latencyMs`.
2. **Batería:** `batteryPercentage` (0-100%), voltaje `batteryVoltage` (24V-25.2V) e indicador de recarga `isCharging`.
3. **Estado Operativo:** `OperationalState` (`STANDBY`, `PATROLLING`, `PAUSED`, `RETURNING_HOME`, `EMERGENCY_STOP`, `FAULT`).
4. **Misión y Progreso:** `missionId`, `missionName`, `missionProgress` (0.0% a 100.0%), `currentWaypoint` y `totalWaypoints`.
5. **Ubicación:** `zoneName` (nombre textual de la zona), coordenadas `posX`, `posY` y orientación `headingDeg`.
6. **Movimiento:** velocidad lineal `speedMps` (m/s) e indicador de marcha `isMoving`.
7. **Alertas:** lista `activeAlerts` (`AlertItem` con id, código, mensaje, severidad y bandera de reconocimiento `acknowledged`).
8. **Última Actualización y Congelamiento:** `lastUpdateTimestamp` y bandera de datos obsoletos `isDataStale` (se activa al perder enlace en Escenario 3).
9. **Tres Variables del Servicio (Inspección y Seguridad):**
   - `smokeGasPpm: Double`: Concentración de gas/humo (Normal: 10-25 ppm; Crítico: >60 ppm).
   - `ambientTemperatureCelsius: Double`: Temperatura en salas de tableros (Normal: 21°C; Crítico: >42°C).
   - `detectedPersonsCount: Int`: Conteo de personas detectadas en horario restringido.

*Implementado en:* [`TelemetryData.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/data/model/TelemetryData.kt) y tarjetas visuales en [`DashboardScreen.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/screens/DashboardScreen.kt).

### 2.2. Comandos y Condiciones de Habilitación (Sección 2.2)
Los comandos están modelados en [`RobotEnums.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/data/model/RobotEnums.kt) como `CommandType`:

| Comando | Condiciones de Habilitación en UI | Estado de la Orden y Efecto |
| :--- | :--- | :--- |
| **`START_MISSION`** | `ONLINE`, estado `STANDBY` y Batería > 15% | Pasa a `PENDING` $\rightarrow$ `CONFIRMED`. Inicia patrullaje. |
| **`PAUSE_MISSION`** | `ONLINE` y estado `PATROLLING` | Detiene el avance en el waypoint actual. |
| **`RESUME_MISSION`** | `ONLINE` y estado `PAUSED` | Retoma la marcha hacia el siguiente punto. |
| **`CANCEL_MISSION`** | `ONLINE` y estado `PATROLLING` o `PAUSED` | Abre modal de confirmación y frena la misión. |
| **`RETURN_TO_SAFE_POINT`** | `ONLINE` y estado $\neq$ `STANDBY en Base` | Activa retorno autónomo a la estación de recarga. |
| **`OPERATIONAL_STOP`** | Siempre habilitado si está `ONLINE` | Modal de advertencia. Parada de software preventiva. |
| **`ACKNOWLEDGE_ALERT`** | Hay alertas activas con `acknowledged = false` | Registra en base de datos la toma de conocimiento. |

* **Ciclo de vida visible de cada orden:** Cada orden pasa por [`CommandStatusCard`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/components/CommonComponents.kt#L175-L230) mostrando:
  - `PENDING` (amarillo, mientras viaja la orden simulada).
  - `CONFIRMED` (verde, aceptada por la máquina de estados).
  - `REJECTED` (rojo con motivo explícito, ej. batería insuficiente).
  - `NO_RESPONSE` (naranja, tras timeout de 2.5s al estar desconectado).
* **Advertencia de seguridad física:** Indicado en la interfaz y en el modal de confirmación: la detención de software no reemplaza la parada de emergencia física.

---

## 3. Diseño en Figma y Estructura de Pantallas

El prototipo de Figma y su implementación equivalente en Android cumplen el 100% de la consigna solicitada:
* **Entregables de Figma generados en el proyecto:**
  - **Prototipo Navegable Interactivo:** [`figma/index.html`](file:///d:/androidProjects/unitreeg1hmi99/figma/index.html) (ejecutable autónomo con frame de smartphone, botones de flujo normal/falla y máquina de estados en vivo).
  - **9 Artboards Vectoriales Importables a Figma:** Carpeta [`figma/export_svg/`](file:///d:/androidProjects/unitreeg1hmi99/figma/export_svg/) con capas editables nativas para arrastrar a cualquier lienzo de Figma.
  - **Guía de Entrega y Conexiones:** [`figma/GUIA_FIGMA_ENTREGA.md`](file:///d:/androidProjects/unitreeg1hmi99/figma/GUIA_FIGMA_ENTREGA.md).
* **Mapa de navegación:** Controlado en [`AppNavigation.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/navigation/AppNavigation.kt) y diagramado en [`09_mapa_navegacion.svg`](file:///d:/androidProjects/unitreeg1hmi99/figma/export_svg/09_mapa_navegacion.svg) con 5 destinos accesibles mediante `NavigationBar`:
  1. **Ficha Robot (`RobotSelectionScreen`):** Especificaciones técnicas del Unitree G1, grados de libertad (23-43 DoF), batería y justificación HRI.
  2. **Supervisión (`DashboardScreen`):** Panel principal de control, selector de misiones en reposo, telemetría activa en patrullaje y comandos discretos.
  3. **Alertas (`AlertsScreen`):** Centro de contingencias con insignias de severidad y badge numérico en el menú inferior.
  4. **Historial (`HistoryScreen`):** Auditoría local persistida con Room dividida en pestañas (Misiones y Logs).
  5. **Escenarios TP (`ScenariosScreen`):** Tablero de pruebas con botones dedicados para los 5 escenarios de evaluación.
* **Componentes reutilizables:** [`CommonComponents.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/components/CommonComponents.kt) y biblioteca documentada en [`08_design_system_componentes.svg`](file:///d:/androidProjects/unitreeg1hmi99/figma/export_svg/08_design_system_componentes.svg) (`OperationalStateBadge`, `BatteryBadge`, `ConnectivityBadge`, `CommandStatusCard`, `ConfirmDialog`, `ServiceMetricCard`, `IncidentDecisionBanner`).
* **Estados visuales especificados y demostrables:**
  - *Normal:* Botones activos en azul/verde Material 3, telemetría online nominal.
  - *Deshabilitado:* Botones bloqueados cuando no se cumplen precondiciones (ej. "Iniciar" con batería baja < 15%).
  - *Advertencia:* Insignias amarillas/naranjas para batería baja y obstáculos detectados en pasillo.
  - *Error / Crítico:* Banners rojos para pérdida de conexión WiFi, telemetría congelada (*stale*), parada de software y fugas de gas.
* **Los dos flujos completos exigidos:**
  - **Flujo 1 (Normal / Éxito):** Ficha Robot $\rightarrow$ Selección de Misión en Standby $\rightarrow$ Iniciar $\rightarrow$ Patrullaje activo por 8 waypoints nominales $\rightarrow$ Fin de misión exitosa $\rightarrow$ Registro en Room como `COMPLETED`.
  - **Flujo 2 (Con falla / Contingencia):** Patrullaje en Sala de Servidores $\rightarrow$ Pico de gas a 88.5 ppm (Alarma Crítica) $\rightarrow$ Parada preventiva de marcha $\rightarrow$ Bloqueo de comandos incompatibles $\rightarrow$ Banner de Decisión HRI $\rightarrow$ Operador ordena "A Base" $\rightarrow$ Registro de auditoría Room como `ABORTADA`.

---

## 4. APK y Simulación

### 4.1. Arquitectura de Software
* **Lenguaje:** Kotlin con Jetpack Compose (Material 3).
* **Patrón de Arquitectura:** MVVM (Model-View-ViewModel) con Clean Architecture:
  - **Capa Data:** Entidades Room, DAOs, Modelos inmutables y Repositorio.
  - **Capa Domain / Repository:** [`RobotRepository`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/data/repository/RobotRepository.kt) desacopla la fuente de datos.
  - **Capa ViewModel:** [`RobotViewModel.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/viewmodel/RobotViewModel.kt) y [`HistoryViewModel.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/viewmodel/HistoryViewModel.kt) exponen `StateFlow` inmutable para consumo reactivo en la UI.
* **Persistencia Local con Room:**
  - Base de datos: [`AppDatabase.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/data/local/AppDatabase.kt) (`unitree_g1_hmi.db`).
  - Entidad de Misiones: [`MissionHistoryEntity.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/data/local/entity/MissionHistoryEntity.kt) (id, nombre, duración, waypoints alcanzados, picos de gas/temperatura y estado `COMPLETED` / `CANCELLED`).
  - Entidad de Auditoría: [`EventLogEntity.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/data/local/entity/EventLogEntity.kt) (comandos enviados, alertas, desconexiones y cambios de estado).

### 4.2. Motor de Simulación 100% Offline
Implementado en [`SimulatedRobotRepository.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/data/repository/SimulatedRobotRepository.kt):
* Funciona de forma autónoma sin conexión a internet ni servidor externo.
* Ciclo periódico en corrutina (`delay(1000L)`) que actualiza la cinemática, desgasta la batería en patrullaje, regenera la batería al acoplarse a la base (+2%/s) y fluctúa las variables ambientales.

---

## 5. Pruebas y Seguridad

### 5.1. Los 5 Escenarios Obligatorios Demostrables
Disponibles con un solo clic en [`ScenariosScreen.kt`](file:///d:/androidProjects/unitreeg1hmi99/app/src/main/java/ar/edu/uade/unitreeg1hmi99/ui/screens/ScenariosScreen.kt):

1. **Escenario 1 — Misión Normal:**
   El operador elige una misión en el selector, presiona "Iniciar", el robot recorre los waypoints del 1 al 8, al alcanzar el 100% frena en destino, abre el diálogo de fin de misión y persiste el resultado en Room como `COMPLETED`.
2. **Escenario 2 — Batería Baja:**
   El simulador reduce la batería al 12%. Se dispara la alerta `WARNING: BATTERY_LOW`, se bloquea el inicio de nuevas rondas y se resalta el comando "Regreso Seguro".
3. **Escenario 3 — Desconexión de Red:**
   Se activa la pérdida de enlace WiFi. La telemetría se marca como `isDataStale = true` (banner de datos congelados), las órdenes quedan en `PENDING` y pasan a `NO_RESPONSE` por timeout. Al desactivar el interruptor, la conexión se restablece inmediatamente.
4. **Escenario 4 — Incidente por Obstáculo:**
   El robot detecta un bloqueo en pasillo (>10s), frena en estado `PAUSED` y despliega en la pantalla principal el **Banner de Decisión de Incidente** ofreciendo al operador: "Reanudar", "Cancelar" o "A Base".
5. **Escenario 5 — Escenario Propio (Fuga de Gas y Sobrecalentamiento Crítico):**
   En la zona de tableros se registran 96.5 ppm de gas y 54°C de temperatura. Se dispara alarma `CRITICAL`, el robot ejecuta parada preventiva de software y exige que el operador reconozca la alerta (`ACKNOWLEDGE_ALERT`) e inicie repliegue a zona segura.

### 5.2. Matriz de 5 Riesgos Operativos y de Interacción (HRI)

| # | Riesgo Identificado | Tipo | Medida Implementada (Interfaz / Lógica / Procedimiento) |
|---|:---|:---:|:---|
| **1** | **Colisión o atropello de peatones/personal** | HRI | Freno autónomo preventivo a 1.5 metros, velocidad limitada a 0.85 m/s en interiores y conteo anónimo de personas en pantalla. |
| **2** | **Envío accidental de comandos críticos** | HMI / Lógica | Modales de confirmación con doble validación obligatoria para `CANCEL_MISSION` y `OPERATIONAL_STOP`. |
| **3** | **Pérdida de enlace en puntos ciegos** | Operativo | Marcado visual de telemetría congelada (`stale`), timeout en órdenes y lógica interna de espera antes de retorno automático a base. |
| **4** | **Falsa sensación de seguridad tras parada por app** | HMI / Procedimiento | Cartelería permanente aclarando que la detención por software no reemplaza el botón de parada de emergencia física. |
| **5** | **Privacidad de ocupantes del edificio** | HRI / Legal | No se almacenan imágenes ni rostros en disco: la cámara procesa localmente en memoria y solo emite un número entero (`detectedPersonsCount`). |

---

## 6. Entregables y Estado del APK

* **Código fuente:** Completo, tipado y compilando con Gradle 9.6.0 y JVM 17.
* **APK Instalable:** Generado en `d:\androidProjects\unitreeg1hmi99\app\build\outputs\apk\debug\app-debug.apk`.
* **Persistencia:** SQLite / Room 2.6.1 con migraciones automáticas.
* **Documentación Técnica Adicional:** [spec-hmi-unitree-g1.md](file:///C:/Users/axelt/.gemini/antigravity/brain/71eade49-0a01-4eaa-abb6-3a8c22388984/spec-hmi-unitree-g1.md) (incluye contratos JSON para telemetría, comandos y respuestas).
