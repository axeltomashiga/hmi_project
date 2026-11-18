package ar.edu.uade.unitreeg1hmi99.data.model

/** Punto de interés del recorrido (coordenadas en metros, origen en la Estación Base). */
data class MapWaypoint(val name: String, val x: Float, val y: Float)

enum class ZoneKind { COMMON, CLASSROOM, TECHNICAL, EXIT, STORAGE, OFFICE, BASE }

/** Sala o sector rectangular del plano. */
data class MapZone(
    val name: String,
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
    val kind: ZoneKind
) {
    fun contains(x: Float, y: Float): Boolean = x in left..right && y in top..bottom
}

/**
 * Plano 2D del edificio. Eje X hacia la derecha, eje Y hacia abajo (convención de pantalla);
 * el heading 0° apunta a +X y crece hacia +Y.
 */
data class FloorMap(
    val minX: Float,
    val minY: Float,
    val maxX: Float,
    val maxY: Float,
    val zones: List<MapZone>,
    val base: MapWaypoint,
    val waypoints: List<MapWaypoint>,
    /** Ruta de cada misión como índices sobre [waypoints]. */
    val missionRoutes: Map<String, List<Int>>
) {
    val width: Float get() = maxX - minX
    val height: Float get() = maxY - minY

    /** Waypoints de la misión indicada; si no existe, recorre todos. */
    fun routeFor(missionId: String?): List<MapWaypoint> =
        missionRoutes[missionId]?.map { waypoints[it] } ?: waypoints

    fun zoneAt(x: Float, y: Float): MapZone? = zones.firstOrNull { it.contains(x, y) }
}

/** Plano del Pabellón A usado por el simulador (esquema del entorno del TP). */
object BuildingMap {
    val default: FloorMap = FloorMap(
        minX = -2f, minY = -2f, maxX = 25f, maxY = 18f,
        zones = listOf(
            MapZone("Hall Central / Base", -2f, -2f, 4f, 3f, ZoneKind.BASE),
            MapZone("Pasillo de Aulas 101-105", 4f, 0.5f, 11f, 4.5f, ZoneKind.CLASSROOM),
            MapZone("Laboratorio de Robótica e IoT", 11f, 2.5f, 16.5f, 7.5f, ZoneKind.TECHNICAL),
            MapZone("Sala de Servidores y Tablero B", 16.5f, 5.5f, 20.5f, 10.5f, ZoneKind.TECHNICAL),
            MapZone("Salida de Emergencia Este", 20.5f, 9.5f, 25f, 14.5f, ZoneKind.EXIT),
            MapZone("Depósito de Mantenimiento", 13.5f, 12.5f, 20.5f, 17.5f, ZoneKind.STORAGE),
            MapZone("Pasillo Sur - Oficinas", 6f, 8.6f, 13.5f, 13.6f, ZoneKind.OFFICE),
            MapZone("Punto Final de Inspección", 5f, 5.5f, 10.5f, 8.6f, ZoneKind.COMMON)
        ),
        base = MapWaypoint("Estación Base de Carga", 0f, 0f),
        waypoints = listOf(
            MapWaypoint("Pabellón A - Hall Central", 2f, 1f),
            MapWaypoint("Pasillo de Aulas 101-105", 8f, 2.5f),
            MapWaypoint("Laboratorio de Robótica e IoT", 14f, 5f),
            MapWaypoint("Sala de Servidores y Tablero B", 18f, 8f),
            MapWaypoint("Salida de Emergencia Este", 22f, 12f),
            MapWaypoint("Depósito de Mantenimiento", 16f, 15f),
            MapWaypoint("Pasillo Sur - Oficinas", 9f, 10f),
            MapWaypoint("Punto Final de Inspección", 8f, 8f)
        ),
        missionRoutes = mapOf(
            "MIS-A" to listOf(0, 1, 2, 3, 4, 5, 6, 7),   // Ronda integral: 8 waypoints
            "MIS-B" to listOf(2, 3, 4, 5, 7),            // Tableros y salas técnicas: 5 waypoints
            "MIS-C" to listOf(0, 2, 3, 4)                // Accesos y salida de emergencia: 4 waypoints
        )
    )
}
