package ar.edu.uade.unitreeg1hmi99.data.mapper

import ar.edu.uade.unitreeg1hmi99.data.local.entity.EventLogEntity
import ar.edu.uade.unitreeg1hmi99.data.local.entity.MissionHistoryEntity
import ar.edu.uade.unitreeg1hmi99.domain.model.EventLogRecord
import ar.edu.uade.unitreeg1hmi99.domain.model.MissionRecord

fun MissionHistoryEntity.toDomain(): MissionRecord = MissionRecord(
    id = id,
    missionId = missionId,
    missionName = missionName,
    startTime = startTime,
    endTime = endTime,
    status = status,
    waypointsCompleted = waypointsCompleted,
    totalWaypoints = totalWaypoints,
    maxGasPpm = maxGasPpm,
    maxTempCelsius = maxTempCelsius,
    incidentsCount = incidentsCount,
    notes = notes
)

fun EventLogEntity.toDomain(): EventLogRecord = EventLogRecord(
    id = id,
    timestamp = timestamp,
    eventType = eventType,
    title = title,
    details = details,
    severity = severity
)
