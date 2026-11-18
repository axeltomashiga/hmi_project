package ar.edu.uade.unitreeg1hmi99.data.local

import android.content.Context
import ar.edu.uade.unitreeg1hmi99.data.model.AlertItem
import ar.edu.uade.unitreeg1hmi99.data.model.AlertSeverity
import ar.edu.uade.unitreeg1hmi99.data.model.CommandRecord
import ar.edu.uade.unitreeg1hmi99.data.model.CommandStatus
import ar.edu.uade.unitreeg1hmi99.data.model.CommandType
import ar.edu.uade.unitreeg1hmi99.data.model.ConnectionState
import ar.edu.uade.unitreeg1hmi99.data.model.MapWaypoint
import ar.edu.uade.unitreeg1hmi99.data.model.OperationalState
import ar.edu.uade.unitreeg1hmi99.data.model.TelemetryData
import org.json.JSONArray
import org.json.JSONObject

/** Instantánea persistible del estado completo del simulador y la misión en curso. */
data class SimulationSnapshot(
    val telemetry: TelemetryData,
    val lastCommand: CommandRecord?,
    val isForcedOffline: Boolean,
    val isForcedRejection: Boolean,
    val missionStartTime: Long,
    val maxRecordedGas: Double,
    val maxRecordedTemp: Double,
    val routeQueue: List<MapWaypoint>,
    val currentRoute: List<MapWaypoint>,
    val pathTotalDistance: Float,
    val returnStartProgress: Float,
    val reachedCount: Int,
    val dwellRemainingSec: Float,
    val batteryAccum: Float,
    val tickCounter: Long
)

interface SimulationStateStore {
    fun load(): SimulationSnapshot?
    fun save(snapshot: SimulationSnapshot, sync: Boolean = false)
    fun clear()
}

/**
 * Almacena la instantánea de simulación en SharedPreferences como JSON para sobrevivir
 * cierres de la app y reinicios del proceso sin alterar el esquema Room de historial.
 */
class SharedPrefsSimulationStateStore(context: Context) : SimulationStateStore {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun load(): SimulationSnapshot? {
        val raw = prefs.getString(KEY_SNAPSHOT, null) ?: return null
        return try {
            decode(JSONObject(raw))
        } catch (_: Exception) {
            null
        }
    }

    override fun save(snapshot: SimulationSnapshot, sync: Boolean) {
        val json = encode(snapshot).toString()
        val editor = prefs.edit().putString(KEY_SNAPSHOT, json)
        if (sync) {
            editor.commit()
        } else {
            editor.apply()
        }
    }

    override fun clear() {
        prefs.edit().remove(KEY_SNAPSHOT).apply()
    }

    private fun encode(snapshot: SimulationSnapshot): JSONObject {
        val t = snapshot.telemetry
        val telemetryJson = JSONObject().apply {
            put("timestamp", t.timestamp)
            put("robotId", t.robotId)
            put("connectionState", t.connectionState.name)
            put("rssiDbm", t.rssiDbm)
            put("latencyMs", t.latencyMs)
            put("batteryPercentage", t.batteryPercentage)
            put("batteryVoltage", t.batteryVoltage.toDouble())
            put("isCharging", t.isCharging)
            put("operationalState", t.operationalState.name)
            put("missionId", t.missionId ?: JSONObject.NULL)
            put("missionName", t.missionName ?: JSONObject.NULL)
            put("missionProgress", t.missionProgress.toDouble())
            put("currentWaypoint", t.currentWaypoint)
            put("totalWaypoints", t.totalWaypoints)
            put("waypointsReached", t.waypointsReached)
            put("zoneName", t.zoneName)
            put("posX", t.posX.toDouble())
            put("posY", t.posY.toDouble())
            put("headingDeg", t.headingDeg.toDouble())
            put("speedMps", t.speedMps.toDouble())
            put("isMoving", t.isMoving)
            put("activeAlerts", encodeAlerts(t.activeAlerts))
            put("lastUpdateTimestamp", t.lastUpdateTimestamp)
            put("isDataStale", t.isDataStale)
            put("smokeGasPpm", t.smokeGasPpm)
            put("ambientTemperatureCelsius", t.ambientTemperatureCelsius)
            put("detectedPersonsCount", t.detectedPersonsCount)
            put("justCompletedMissionName", t.justCompletedMissionName ?: JSONObject.NULL)
        }

        val cmdJson = snapshot.lastCommand?.let { cmd ->
            JSONObject().apply {
                put("id", cmd.id)
                put("type", cmd.type.name)
                put("status", cmd.status.name)
                put("timestamp", cmd.timestamp)
                put("message", cmd.message)
            }
        }

        return JSONObject().apply {
            put("telemetry", telemetryJson)
            put("lastCommand", cmdJson ?: JSONObject.NULL)
            put("isForcedOffline", snapshot.isForcedOffline)
            put("isForcedRejection", snapshot.isForcedRejection)
            put("missionStartTime", snapshot.missionStartTime)
            put("maxRecordedGas", snapshot.maxRecordedGas)
            put("maxRecordedTemp", snapshot.maxRecordedTemp)
            put("routeQueue", encodeWaypoints(snapshot.routeQueue))
            put("currentRoute", encodeWaypoints(snapshot.currentRoute))
            put("pathTotalDistance", snapshot.pathTotalDistance.toDouble())
            put("returnStartProgress", snapshot.returnStartProgress.toDouble())
            put("reachedCount", snapshot.reachedCount)
            put("dwellRemainingSec", snapshot.dwellRemainingSec.toDouble())
            put("batteryAccum", snapshot.batteryAccum.toDouble())
            put("tickCounter", snapshot.tickCounter)
        }
    }

    private fun decode(root: JSONObject): SimulationSnapshot {
        val tJson = root.getJSONObject("telemetry")
        val telemetry = TelemetryData(
            timestamp = tJson.getLong("timestamp"),
            robotId = tJson.getString("robotId"),
            connectionState = enumOrDefault(tJson.optString("connectionState"), ConnectionState.ONLINE),
            rssiDbm = tJson.getInt("rssiDbm"),
            latencyMs = tJson.getLong("latencyMs"),
            batteryPercentage = tJson.getInt("batteryPercentage"),
            batteryVoltage = tJson.getDouble("batteryVoltage").toFloat(),
            isCharging = tJson.getBoolean("isCharging"),
            operationalState = enumOrDefault(tJson.optString("operationalState"), OperationalState.STANDBY),
            missionId = tJson.optStringOrNull("missionId"),
            missionName = tJson.optStringOrNull("missionName"),
            missionProgress = tJson.getDouble("missionProgress").toFloat(),
            currentWaypoint = tJson.getInt("currentWaypoint"),
            totalWaypoints = tJson.getInt("totalWaypoints"),
            waypointsReached = tJson.getInt("waypointsReached"),
            zoneName = tJson.getString("zoneName"),
            posX = tJson.getDouble("posX").toFloat(),
            posY = tJson.getDouble("posY").toFloat(),
            headingDeg = tJson.getDouble("headingDeg").toFloat(),
            speedMps = tJson.getDouble("speedMps").toFloat(),
            isMoving = tJson.getBoolean("isMoving"),
            activeAlerts = decodeAlerts(tJson.optJSONArray("activeAlerts")),
            lastUpdateTimestamp = tJson.getLong("lastUpdateTimestamp"),
            isDataStale = tJson.getBoolean("isDataStale"),
            smokeGasPpm = tJson.getDouble("smokeGasPpm"),
            ambientTemperatureCelsius = tJson.getDouble("ambientTemperatureCelsius"),
            detectedPersonsCount = tJson.getInt("detectedPersonsCount"),
            justCompletedMissionName = tJson.optStringOrNull("justCompletedMissionName")
        )

        val lastCommand = if (root.isNull("lastCommand")) {
            null
        } else {
            val cJson = root.getJSONObject("lastCommand")
            CommandRecord(
                id = cJson.getString("id"),
                type = enumOrDefault(cJson.optString("type"), CommandType.START_MISSION),
                status = enumOrDefault(cJson.optString("status"), CommandStatus.CONFIRMED),
                timestamp = cJson.getLong("timestamp"),
                message = cJson.optString("message", "")
            )
        }

        return SimulationSnapshot(
            telemetry = telemetry,
            lastCommand = lastCommand,
            isForcedOffline = root.optBoolean("isForcedOffline", false),
            isForcedRejection = root.optBoolean("isForcedRejection", false),
            missionStartTime = root.optLong("missionStartTime", 0L),
            maxRecordedGas = root.optDouble("maxRecordedGas", 14.2),
            maxRecordedTemp = root.optDouble("maxRecordedTemp", 21.5),
            routeQueue = decodeWaypoints(root.optJSONArray("routeQueue")),
            currentRoute = decodeWaypoints(root.optJSONArray("currentRoute")),
            pathTotalDistance = root.optDouble("pathTotalDistance", 0.0).toFloat(),
            returnStartProgress = root.optDouble("returnStartProgress", 0.0).toFloat(),
            reachedCount = root.optInt("reachedCount", 0),
            dwellRemainingSec = root.optDouble("dwellRemainingSec", 0.0).toFloat(),
            batteryAccum = root.optDouble("batteryAccum", 0.0).toFloat(),
            tickCounter = root.optLong("tickCounter", 0L)
        )
    }

    private fun encodeAlerts(alerts: List<AlertItem>): JSONArray = JSONArray().apply {
        alerts.forEach { alert ->
            put(
                JSONObject().apply {
                    put("id", alert.id)
                    put("code", alert.code)
                    put("title", alert.title)
                    put("message", alert.message)
                    put("severity", alert.severity.name)
                    put("timestamp", alert.timestamp)
                    put("acknowledged", alert.acknowledged)
                }
            )
        }
    }

    private fun decodeAlerts(array: JSONArray?): List<AlertItem> {
        if (array == null) return emptyList()
        return buildList(array.length()) {
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                add(
                    AlertItem(
                        id = item.getString("id"),
                        code = item.getString("code"),
                        title = item.getString("title"),
                        message = item.getString("message"),
                        severity = enumOrDefault(item.optString("severity"), AlertSeverity.INFO),
                        timestamp = item.getLong("timestamp"),
                        acknowledged = item.getBoolean("acknowledged")
                    )
                )
            }
        }
    }

    private fun encodeWaypoints(waypoints: List<MapWaypoint>): JSONArray = JSONArray().apply {
        waypoints.forEach { wp ->
            put(
                JSONObject().apply {
                    put("name", wp.name)
                    put("x", wp.x.toDouble())
                    put("y", wp.y.toDouble())
                }
            )
        }
    }

    private fun decodeWaypoints(array: JSONArray?): List<MapWaypoint> {
        if (array == null) return emptyList()
        return buildList(array.length()) {
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                add(
                    MapWaypoint(
                        name = item.getString("name"),
                        x = item.getDouble("x").toFloat(),
                        y = item.getDouble("y").toFloat()
                    )
                )
            }
        }
    }

    private fun JSONObject.optStringOrNull(key: String): String? =
        if (!has(key) || isNull(key)) null else getString(key)

    private inline fun <reified T : Enum<T>> enumOrDefault(name: String, default: T): T =
        try {
            enumValueOf<T>(name)
        } catch (_: IllegalArgumentException) {
            default
        }

    private companion object {
        const val PREFS_NAME = "unitree_g1_sim_state"
        const val KEY_SNAPSHOT = "simulation_snapshot_v1"
    }
}
