package ar.edu.uade.unitreeg1hmi99.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mission_history")
data class MissionHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val missionId: String,
    val missionName: String,
    val startTime: Long,
    val endTime: Long,
    val status: String, // COMPLETED, CANCELLED, STOPPED, FAILED
    val waypointsCompleted: Int,
    val totalWaypoints: Int,
    val maxGasPpm: Double,
    val maxTempCelsius: Double,
    val incidentsCount: Int,
    val notes: String = ""
)
