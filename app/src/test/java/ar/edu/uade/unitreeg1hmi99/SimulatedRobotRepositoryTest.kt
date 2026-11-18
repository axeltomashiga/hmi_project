package ar.edu.uade.unitreeg1hmi99

import ar.edu.uade.unitreeg1hmi99.data.local.dao.EventLogDao
import ar.edu.uade.unitreeg1hmi99.data.local.dao.MissionHistoryDao
import ar.edu.uade.unitreeg1hmi99.data.local.entity.EventLogEntity
import ar.edu.uade.unitreeg1hmi99.data.local.entity.MissionHistoryEntity
import ar.edu.uade.unitreeg1hmi99.data.model.BuildingMap
import ar.edu.uade.unitreeg1hmi99.data.model.CommandStatus
import ar.edu.uade.unitreeg1hmi99.data.model.CommandType
import ar.edu.uade.unitreeg1hmi99.data.model.OperationalState
import ar.edu.uade.unitreeg1hmi99.data.repository.SimulatedRobotRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.hypot

/** Tests de la cinemática y las misiones del simulador (sin reloj real ni base de datos). */
class SimulatedRobotRepositoryTest {

    private class FakeMissionDao : MissionHistoryDao {
        override fun getAllMissions(): Flow<List<MissionHistoryEntity>> = emptyFlow()
        override fun insertMission(mission: MissionHistoryEntity): Long = 1L
        override fun clearHistory(): Int = 0
    }

    private class FakeEventDao : EventLogDao {
        override fun getAllEvents(): Flow<List<EventLogEntity>> = emptyFlow()
        override fun insertEvent(event: EventLogEntity): Long = 1L
        override fun clearEvents(): Int = 0
    }

    private lateinit var repo: SimulatedRobotRepository

    // Máximo desplazamiento por tick: 0.85 m/s * 0.2 s * factor de tiempo 2 (+ tolerancia)
    private val maxStepPerTick = 0.85f * 0.2f * 2f + 0.001f

    @Before
    fun setUp() {
        // Scope ya cancelado: el ticker de fondo no corre y los tests avanzan el tiempo a mano
        repo = SimulatedRobotRepository(
            FakeMissionDao(),
            FakeEventDao(),
            CoroutineScope(Job().apply { cancel() })
        )
    }

    private fun send(type: CommandType, missionId: String = "MIS-A") = runBlocking {
        repo.sendCommand(type, mapOf("missionId" to missionId, "missionName" to missionId))
    }

    private fun runUntilStandby(maxTicks: Int = 3000) {
        var n = 0
        while (repo.telemetry.value.operationalState != OperationalState.STANDBY && n++ < maxTicks) {
            repo.advanceTicks(1)
        }
    }

    @Test
    fun startMission_movesContinuouslyWithoutTeleporting() {
        assertEquals(CommandStatus.CONFIRMED, send(CommandType.START_MISSION).status)
        var prevX = repo.telemetry.value.posX
        var prevY = repo.telemetry.value.posY
        var moved = 0f
        repeat(200) {
            repo.advanceTicks(1)
            val t = repo.telemetry.value
            val step = hypot(t.posX - prevX, t.posY - prevY)
            assertTrue("Salto de $step m en un tick", step <= maxStepPerTick)
            moved += step
            prevX = t.posX
            prevY = t.posY
        }
        assertTrue("El robot casi no se movió: $moved m", moved > 10f)
    }

    @Test
    fun heading_pointsTowardTheNextWaypoint() {
        send(CommandType.START_MISSION)
        repo.advanceTicks(3)
        val t = repo.telemetry.value
        val first = BuildingMap.default.waypoints[0]
        val expected = Math.toDegrees(atan2((first.y - t.posY).toDouble(), (first.x - t.posX).toDouble())).toFloat()
        val expectedNorm = ((expected % 360f) + 360f) % 360f
        assertTrue("heading=${t.headingDeg} esperado=$expectedNorm", abs(t.headingDeg - expectedNorm) < 1f)
    }

    @Test
    fun progress_neverDecreasesWhilePatrolling_andMissionCompletes() {
        send(CommandType.START_MISSION)
        var last = 0f
        var ticks = 0
        while (repo.telemetry.value.operationalState == OperationalState.PATROLLING && ticks++ < 3000) {
            repo.advanceTicks(1)
            val p = repo.telemetry.value.missionProgress
            assertTrue("El progreso retrocedió: $last -> $p", p >= last - 0.001f)
            last = p
        }
        val t = repo.telemetry.value
        val finalPoint = BuildingMap.default.waypoints.last()
        assertEquals(OperationalState.STANDBY, t.operationalState)
        assertEquals(100f, t.missionProgress, 0.001f)
        assertEquals(8, t.waypointsReached)
        assertEquals(finalPoint.x, t.posX, 0.001f)
        assertEquals(finalPoint.y, t.posY, 0.001f)
    }

    @Test
    fun missionRoutes_matchTheirTemplates() {
        listOf("MIS-A" to 8, "MIS-B" to 5, "MIS-C" to 4).forEach { (id, expected) ->
            setUp()
            send(CommandType.START_MISSION, id)
            assertEquals(expected, repo.telemetry.value.totalWaypoints)
            runUntilStandby()
            assertEquals("Waypoints alcanzados en $id", expected, repo.telemetry.value.waypointsReached)
        }
    }

    @Test
    fun guard_stopsAndScansAtEachWaypoint() {
        send(CommandType.START_MISSION)
        var sawDwell = false
        var headingChangedWhileStopped = false
        var prevHeading = repo.telemetry.value.headingDeg
        repeat(400) {
            repo.advanceTicks(1)
            val t = repo.telemetry.value
            if (t.operationalState == OperationalState.PATROLLING && !t.isMoving) {
                sawDwell = true
                if (abs(t.headingDeg - prevHeading) > 1f) headingChangedWhileStopped = true
            }
            prevHeading = t.headingDeg
        }
        assertTrue("Nunca se detuvo a inspeccionar", sawDwell)
        assertTrue("No barrió el entorno al detenerse", headingChangedWhileStopped)
    }

    @Test
    fun pause_freezesPosition_andResumeContinues() {
        send(CommandType.START_MISSION)
        repo.advanceTicks(10)
        send(CommandType.PAUSE_MISSION)
        val paused = repo.telemetry.value
        repo.advanceTicks(20)
        assertEquals(paused.posX, repo.telemetry.value.posX, 0f)
        assertEquals(paused.posY, repo.telemetry.value.posY, 0f)
        assertFalse(repo.telemetry.value.isMoving)

        send(CommandType.RESUME_MISSION)
        repo.advanceTicks(10)
        val moved = hypot(repo.telemetry.value.posX - paused.posX, repo.telemetry.value.posY - paused.posY)
        assertTrue("No retomó la marcha", moved > 0.5f)
    }

    @Test
    fun returnToSafePoint_walksBackToBaseAndCharges() {
        send(CommandType.START_MISSION)
        repo.advanceTicks(60)
        send(CommandType.RETURN_TO_SAFE_POINT)
        assertEquals(OperationalState.RETURNING_HOME, repo.telemetry.value.operationalState)
        var prevX = repo.telemetry.value.posX
        var prevY = repo.telemetry.value.posY
        var ticks = 0
        while (repo.telemetry.value.operationalState == OperationalState.RETURNING_HOME && ticks++ < 3000) {
            repo.advanceTicks(1)
            val t = repo.telemetry.value
            assertTrue("Salto al volver a base", hypot(t.posX - prevX, t.posY - prevY) <= maxStepPerTick)
            prevX = t.posX
            prevY = t.posY
        }
        val t = repo.telemetry.value
        assertEquals(OperationalState.STANDBY, t.operationalState)
        assertEquals(0f, t.posX, 0.001f)
        assertEquals(0f, t.posY, 0.001f)
        repo.advanceTicks(1)
        assertTrue(repo.telemetry.value.isCharging)
    }

    @Test
    fun returnToSafePoint_isRejectedWhenAlreadyAtBase() {
        assertEquals(CommandStatus.REJECTED, send(CommandType.RETURN_TO_SAFE_POINT).status)
    }

    @Test
    fun personsAreDetectedOnlyWhileInspectingRestrictedAreas() {
        send(CommandType.START_MISSION)
        var maxPersons = 0
        var nonZeroWhileMoving = false
        repeat(1500) {
            repo.advanceTicks(1)
            val t = repo.telemetry.value
            maxPersons = maxOf(maxPersons, t.detectedPersonsCount)
            if (t.detectedPersonsCount > 0 && t.isMoving) nonZeroWhileMoving = true
        }
        assertTrue("No detectó personas en toda la ronda", maxPersons > 0)
        assertFalse(nonZeroWhileMoving)
    }

    @Test
    fun routes_visitEveryWaypointOfTheFloorMap() {
        val map = BuildingMap.default
        map.missionRoutes.values.forEach { ids -> assertTrue(ids.all { it in map.waypoints.indices }) }
        map.waypoints.forEach { w ->
            assertTrue("${w.name} fuera de toda zona", map.zoneAt(w.x, w.y) != null)
        }
    }
}
