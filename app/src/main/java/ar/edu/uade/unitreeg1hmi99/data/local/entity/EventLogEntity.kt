package ar.edu.uade.unitreeg1hmi99.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_logs")
data class EventLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: String, // COMMAND, ALERT, STATE_CHANGE, DISCONNECTION, SAFETY
    val title: String,
    val details: String,
    val severity: String = "INFO" // INFO, WARNING, CRITICAL
)
