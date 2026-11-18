package ar.edu.uade.unitreeg1hmi99.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.uade.unitreeg1hmi99.R
import ar.edu.uade.unitreeg1hmi99.data.model.FloorMap
import ar.edu.uade.unitreeg1hmi99.data.model.MapWaypoint
import ar.edu.uade.unitreeg1hmi99.data.model.OperationalState
import ar.edu.uade.unitreeg1hmi99.data.model.TelemetryData
import ar.edu.uade.unitreeg1hmi99.data.model.ZoneKind
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusDanger
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusDangerBg
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusDangerBorder
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusInfo
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusReturn
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusSuccess
import ar.edu.uade.unitreeg1hmi99.ui.theme.StatusWarning
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

private val VisitedColor = StatusSuccess
private val NextColor = Color(0xFFF59E0B)
private val PendingColor = Color(0xFF94A3B8)
private val BaseColor = StatusInfo
private val DangerColor = StatusDanger
private val WarningColor = StatusWarning
private val StaleColor = Color(0xFF64748B)
private val PersonColor = Color(0xFFBE185D)

private const val VISION_RADIUS_M = 3.5f
private const val VISION_HALF_ANGLE_DEG = 35f
private const val TRAIL_MAX_POINTS = 80
private const val TRAIL_MIN_STEP_M = 0.25f

private fun zoneColor(kind: ZoneKind): Color = when (kind) {
    ZoneKind.COMMON -> Color(0xFF94A3B8)
    ZoneKind.CLASSROOM -> Color(0xFF38BDF8)
    ZoneKind.TECHNICAL -> Color(0xFFA855F7)
    ZoneKind.EXIT -> Color(0xFF4ADE80)
    ZoneKind.STORAGE -> Color(0xFFFB923C)
    ZoneKind.OFFICE -> Color(0xFF2DD4BF)
    ZoneKind.BASE -> Color(0xFF3B82F6)
}

private fun robotColor(state: OperationalState, stale: Boolean): Color = when {
    stale -> StaleColor
    state == OperationalState.PATROLLING -> StatusSuccess
    state == OperationalState.STANDBY -> StatusInfo
    state == OperationalState.PAUSED -> StatusWarning
    state == OperationalState.RETURNING_HOME -> StatusReturn
    state == OperationalState.EMERGENCY_STOP -> DangerColor
    else -> Color(0xFFC2410C)
}

/**
 * Mapa 2D del edificio con la ruta de la ronda, el avance y el robot en tiempo real.
 * Soporta colapsar y abrir de forma fluida.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PatrolMap(
    floorMap: FloorMap,
    route: List<MapWaypoint>,
    reached: Int,
    telemetry: TelemetryData,
    modifier: Modifier = Modifier,
    isCollapsed: Boolean = false,
    onToggleCollapse: (() -> Unit)? = null
) {
    val state = telemetry.operationalState
    val stale = telemetry.isDataStale

    // Posición y orientación suavizadas entre ticks del simulador
    val smooth = tween<Float>(durationMillis = 220, easing = LinearEasing)
    val robotX by animateFloatAsState(telemetry.posX, smooth, label = "robotX")
    val robotY by animateFloatAsState(telemetry.posY, smooth, label = "robotY")

    // El heading se "desenrolla" para que animar de 350° a 10° gire 20° y no 340°
    var lastHeading by remember { mutableFloatStateOf(telemetry.headingDeg) }
    var unwrappedHeading by remember { mutableFloatStateOf(telemetry.headingDeg) }
    LaunchedEffect(telemetry.headingDeg) {
        val delta = ((telemetry.headingDeg - lastHeading + 540f) % 360f) - 180f
        unwrappedHeading += delta
        lastHeading = telemetry.headingDeg
    }
    val heading by animateFloatAsState(unwrappedHeading, smooth, label = "heading")

    // Rastro de las últimas posiciones
    val trail = remember { mutableStateListOf<Offset>() }
    LaunchedEffect(telemetry.posX, telemetry.posY) {
        val p = Offset(telemetry.posX, telemetry.posY)
        if (trail.isEmpty() || (p - trail.last()).getDistance() > TRAIL_MIN_STEP_M) {
            trail.add(p)
            if (trail.size > TRAIL_MAX_POINTS) trail.removeAt(0)
        }
    }
    val missionJustStarted = state == OperationalState.PATROLLING && reached == 0 && telemetry.missionProgress < 1f
    LaunchedEffect(missionJustStarted, telemetry.isCharging) {
        if (missionJustStarted || telemetry.isCharging) trail.clear()
    }

    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1100, easing = LinearEasing), RepeatMode.Restart),
        label = "pulseValue"
    )

    val textMeasurer = rememberTextMeasurer()
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurface = MaterialTheme.colorScheme.onSurface
    val outline = MaterialTheme.colorScheme.outline

    val unackAlerts = telemetry.activeAlerts.filter { !it.acknowledged }
    val hasGas = unackAlerts.any { it.code == "GAS_LEAK" }
    val hasObstacle = unackAlerts.any { it.code == "OBSTACLE_BLOCKED" }
    val hasLowBattery = unackAlerts.any { it.code == "BATTERY_LOW" }
    val total = route.size

    // Waypoint tocado por el operador (-1 = ninguno)
    var selectedRaw by remember { mutableIntStateOf(-1) }
    val selected = selectedRaw.takeIf { it in route.indices } ?: -1

    val description = stringResource(
        R.string.map_content_description, telemetry.zoneName, reached.coerceAtMost(total), total
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header del Mapa 2D (clickeable para colapsar/abrir)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (onToggleCollapse != null) {
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onToggleCollapse() }
                        } else {
                            Modifier
                        }
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.map_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = telemetry.zoneName,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = stringResource(R.string.map_progress_format, reached.coerceAtMost(total), total),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    // Botón para Colapsar / Abrir el mapa
                    if (onToggleCollapse != null) {
                        Surface(
                            onClick = onToggleCollapse,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = if (isCollapsed) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                                contentDescription = if (isCollapsed) "Abrir mapa" else "Colapsar mapa",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(16.dp)
                            )
                        }
                    }
                }
            }

            // Contenido colapsable del Mapa 2D
            AnimatedVisibility(
                visible = !isCollapsed,
                enter = expandVertically(animationSpec = tween(250)) + fadeIn(animationSpec = tween(200)),
                exit = shrinkVertically(animationSpec = tween(250)) + fadeOut(animationSpec = tween(150))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Lienzo 2D interactivo
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(floorMap.width / floorMap.height)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                            .border(
                                width = 1.dp,
                                color = if (hasGas) StatusDanger else MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .semantics { contentDescription = description }
                            .pointerInput(route, floorMap) {
                                detectTapGestures { tap ->
                                    val scale = size.width / floorMap.width
                                    val hitRadius = 24.dp.toPx()
                                    val nearest = route.withIndex().minByOrNull { (_, w) ->
                                        (Offset((w.x - floorMap.minX) * scale, (w.y - floorMap.minY) * scale) - tap).getDistance()
                                    }
                                    val hit = nearest?.takeIf { (_, w) ->
                                        (Offset((w.x - floorMap.minX) * scale, (w.y - floorMap.minY) * scale) - tap).getDistance() <= hitRadius
                                    }
                                    selectedRaw = if (hit == null || hit.index == selectedRaw) -1 else hit.index
                                }
                            }
                    ) {
                        val s = size.width / floorMap.width
                        fun px(x: Float, y: Float) = Offset((x - floorMap.minX) * s, (y - floorMap.minY) * s)
                        fun px(p: Offset) = px(p.x, p.y)
                        val dpPx = 1.dp.toPx()

                        drawGrid(floorMap, s, onSurface.copy(alpha = 0.06f), dpPx)
                        drawZones(floorMap, s, textMeasurer, onSurface, outline, dpPx, hasGas, telemetry, pulse)

                        // Ruta: planificada (punteada) y recorrida (sólida)
                        val pathPoints = listOf(floorMap.base) + route
                        val planned = Path().apply {
                            pathPoints.forEachIndexed { i, w ->
                                val o = px(w.x, w.y)
                                if (i == 0) moveTo(o.x, o.y) else lineTo(o.x, o.y)
                            }
                        }
                        drawPath(
                            planned,
                            color = onSurface.copy(alpha = 0.35f),
                            style = Stroke(width = 2 * dpPx, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8 * dpPx, 6 * dpPx)))
                        )
                        val done = Path().apply {
                            val visited = pathPoints.take(1 + reached.coerceIn(0, total))
                            visited.forEachIndexed { i, w ->
                                val o = px(w.x, w.y)
                                if (i == 0) moveTo(o.x, o.y) else lineTo(o.x, o.y)
                            }
                            val onPatrol = state == OperationalState.PATROLLING || state == OperationalState.PAUSED
                            if (onPatrol && reached < total) {
                                val r = px(robotX, robotY)
                                lineTo(r.x, r.y)
                            }
                        }
                        drawPath(done, color = VisitedColor, style = Stroke(width = 3 * dpPx))

                        // Estación base
                        val baseOffset = px(floorMap.base.x, floorMap.base.y)
                        if (hasLowBattery) {
                            drawCircle(
                                WarningColor.copy(alpha = 1f - pulse),
                                radius = (12 + 14 * pulse) * dpPx,
                                center = baseOffset,
                                style = Stroke(2 * dpPx)
                            )
                        }
                        drawRoundRect(
                            BaseColor,
                            topLeft = baseOffset - Offset(8 * dpPx, 8 * dpPx),
                            size = Size(16 * dpPx, 16 * dpPx),
                            cornerRadius = CornerRadius(4 * dpPx)
                        )
                        drawCenteredText(textMeasurer, "B", baseOffset, Color.White, 10.sp.toPx())

                        // Waypoints numerados
                        route.forEachIndexed { i, w ->
                            val c = px(w.x, w.y)
                            val color = when {
                                i < reached -> VisitedColor
                                i == reached -> NextColor
                                else -> PendingColor
                            }
                            if (i == reached && reached < total) {
                                drawCircle(NextColor.copy(alpha = 0.35f), radius = 14 * dpPx, center = c)
                            }
                            if (i == selected) {
                                drawCircle(BaseColor, radius = 17 * dpPx, center = c, style = Stroke(2.5f * dpPx))
                            }
                            drawCircle(color, radius = 9.5f * dpPx, center = c)
                            drawCircle(surfaceColor, radius = 9.5f * dpPx, center = c, style = Stroke(1.8f * dpPx))
                            drawCenteredText(textMeasurer, "${i + 1}", c, Color.White, 10.sp.toPx())
                        }

                        // Rastro
                        trail.forEachIndexed { i, p ->
                            drawCircle(
                                robotColor(state, stale).copy(alpha = 0.45f * (i + 1) / trail.size),
                                radius = 2.2f * dpPx,
                                center = px(p)
                            )
                        }

                        // Obstáculo delante del robot
                        if (hasObstacle) {
                            val rad = Math.toRadians(telemetry.headingDeg.toDouble())
                            val o = px(telemetry.posX + 1.6f * cos(rad).toFloat(), telemetry.posY + 1.6f * sin(rad).toFloat())
                            drawCircle(WarningColor.copy(alpha = 0.25f + 0.25f * pulse), radius = 13 * dpPx, center = o)
                            val k = 6 * dpPx
                            drawLine(WarningColor, o - Offset(k, k), o + Offset(k, k), strokeWidth = 3 * dpPx)
                            drawLine(WarningColor, o - Offset(k, -k), o + Offset(k, -k), strokeWidth = 3 * dpPx)
                        }

                        // Personas detectadas por la cámara: aparecen delante del robot (solo conteo, sin imágenes)
                        if (telemetry.detectedPersonsCount > 0 && !stale) {
                            for (k in 0 until telemetry.detectedPersonsCount.coerceAtMost(4)) {
                                val offsetDeg = (k - (telemetry.detectedPersonsCount - 1) / 2f) * 28f
                                val rad = Math.toRadians((telemetry.headingDeg + offsetDeg).toDouble())
                                val p = px(telemetry.posX + 2.3f * cos(rad).toFloat(), telemetry.posY + 2.3f * sin(rad).toFloat())
                                drawCircle(PersonColor, radius = 6 * dpPx, center = p)
                                drawCircle(Color.White, radius = 6 * dpPx, center = p, style = Stroke(1.5f * dpPx))
                            }
                        }

                        drawRobot(
                            center = px(robotX, robotY),
                            headingDeg = heading,
                            color = robotColor(state, stale),
                            stale = stale,
                            showCone = !stale && (state == OperationalState.PATROLLING || state == OperationalState.PAUSED),
                            coneAlpha = if (state == OperationalState.PAUSED) 0.12f else 0.22f,
                            coneRadius = VISION_RADIUS_M * s,
                            alertRing = if (telemetry.hasCriticalAlert) pulse else null,
                            dpPx = dpPx
                        )

                        drawScaleBar(floorMap, s, textMeasurer, onSurface, dpPx)
                    }

                    // Barra informativa del waypoint seleccionado o guía interactiva
                    if (selected >= 0) {
                        val w = route[selected]
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = stringResource(
                                        R.string.map_waypoint_info,
                                        selected + 1,
                                        w.name,
                                        hypot(w.x - telemetry.posX, w.y - telemetry.posY)
                                    ),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TouchApp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Toque un waypoint en el plano para consultar zona y distancia al robot",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                    }

                    if (stale) {
                        Surface(
                            color = StatusDangerBg,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, StatusDangerBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = StatusDanger,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = stringResource(R.string.map_stale_position),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = StatusDanger,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Leyenda compacta en píldoras
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        LegendDot(VisitedColor, stringResource(R.string.map_legend_visited))
                        LegendDot(NextColor, stringResource(R.string.map_legend_next))
                        LegendDot(PendingColor, stringResource(R.string.map_legend_pending))
                        LegendDot(robotColor(state, stale), stringResource(R.string.map_legend_robot))
                        LegendDot(WarningColor, stringResource(R.string.map_legend_obstacle))
                        LegendDot(PersonColor, stringResource(R.string.map_legend_person))
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        shape = RoundedCornerShape(50)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(Modifier.width(5.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun DrawScope.drawGrid(map: FloorMap, s: Float, color: Color, dpPx: Float) {
    var gx = kotlin.math.ceil(map.minX / 5f) * 5f
    while (gx <= map.maxX) {
        val x = (gx - map.minX) * s
        drawLine(color, Offset(x, 0f), Offset(x, size.height), strokeWidth = dpPx)
        gx += 5f
    }
    var gy = kotlin.math.ceil(map.minY / 5f) * 5f
    while (gy <= map.maxY) {
        val y = (gy - map.minY) * s
        drawLine(color, Offset(0f, y), Offset(size.width, y), strokeWidth = dpPx)
        gy += 5f
    }
}

private fun DrawScope.drawZones(
    map: FloorMap,
    s: Float,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    labelColor: Color,
    outline: Color,
    dpPx: Float,
    hasGas: Boolean,
    telemetry: TelemetryData,
    pulse: Float
) {
    // La zona donde el robot detectó gas se resalta en rojo
    val gasZone = if (hasGas) map.zoneAt(telemetry.posX, telemetry.posY) else null
    map.zones.forEach { z ->
        val tl = Offset((z.left - map.minX) * s, (z.top - map.minY) * s)
        val sz = Size((z.right - z.left) * s, (z.bottom - z.top) * s)
        val isGas = z == gasZone
        drawRoundRect(
            color = if (isGas) DangerColor.copy(alpha = 0.25f + 0.2f * pulse) else zoneColor(z.kind).copy(alpha = 0.22f),
            topLeft = tl,
            size = sz,
            cornerRadius = CornerRadius(6 * dpPx)
        )
        drawRoundRect(
            color = if (isGas) DangerColor else outline.copy(alpha = 0.55f),
            topLeft = tl,
            size = sz,
            cornerRadius = CornerRadius(6 * dpPx),
            style = Stroke(width = (if (isGas) 2.5f else 1.5f) * dpPx)
        )
        val layout = textMeasurer.measure(
            text = z.shortName,
            style = TextStyle(
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = labelColor.copy(alpha = 0.8f)
            ),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            constraints = Constraints(maxWidth = (sz.width - 6 * dpPx).toInt().coerceAtLeast(1))
        )
        drawText(layout, topLeft = tl + Offset(4 * dpPx, 3 * dpPx))
    }
}

private fun DrawScope.drawCenteredText(
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    text: String,
    center: Offset,
    color: Color,
    fontSizePx: Float
) {
    val layout = textMeasurer.measure(
        text = text,
        style = TextStyle(fontSize = fontSizePx.toSp(), color = color, fontWeight = FontWeight.Bold)
    )
    drawText(layout, topLeft = center - Offset(layout.size.width / 2f, layout.size.height / 2f))
}

private fun DrawScope.drawRobot(
    center: Offset,
    headingDeg: Float,
    color: Color,
    stale: Boolean,
    showCone: Boolean,
    coneAlpha: Float,
    coneRadius: Float,
    alertRing: Float?,
    dpPx: Float
) {
    if (showCone) {
        val cone = Path().apply {
            moveTo(center.x, center.y)
            arcTo(
                Rect(center.x - coneRadius, center.y - coneRadius, center.x + coneRadius, center.y + coneRadius),
                startAngleDegrees = headingDeg - VISION_HALF_ANGLE_DEG,
                sweepAngleDegrees = 2 * VISION_HALF_ANGLE_DEG,
                forceMoveTo = false
            )
            close()
        }
        drawPath(cone, color = color.copy(alpha = coneAlpha))
    }
    if (alertRing != null) {
        drawCircle(
            DangerColor.copy(alpha = 1f - alertRing),
            radius = (10 + 18 * alertRing) * dpPx,
            center = center,
            style = Stroke(2.5f * dpPx)
        )
    }
    if (stale) {
        drawCircle(
            StaleColor,
            radius = 16 * dpPx,
            center = center,
            style = Stroke(1.5f * dpPx, pathEffect = PathEffect.dashPathEffect(floatArrayOf(5 * dpPx, 4 * dpPx)))
        )
    }
    drawCircle(color, radius = 8.5f * dpPx, center = center)
    drawCircle(Color.White, radius = 8.5f * dpPx, center = center, style = Stroke(2 * dpPx))

    // Flecha de orientación
    val rad = Math.toRadians(headingDeg.toDouble())
    val dir = Offset(cos(rad).toFloat(), sin(rad).toFloat())
    val normal = Offset(-dir.y, dir.x)
    val tip = center + dir * (15 * dpPx)
    val baseCenter = center + dir * (7 * dpPx)
    val arrow = Path().apply {
        moveTo(tip.x, tip.y)
        lineTo(baseCenter.x + normal.x * 5 * dpPx, baseCenter.y + normal.y * 5 * dpPx)
        lineTo(baseCenter.x - normal.x * 5 * dpPx, baseCenter.y - normal.y * 5 * dpPx)
        close()
    }
    drawPath(arrow, color = color)
    drawPath(arrow, color = Color.White, style = Stroke(1.2f * dpPx))
}

private fun DrawScope.drawScaleBar(
    map: FloorMap,
    s: Float,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    color: Color,
    dpPx: Float
) {
    val start = Offset(8 * dpPx, size.height - 10 * dpPx)
    val end = start + Offset(5f * s, 0f)
    drawLine(color.copy(alpha = 0.7f), start, end, strokeWidth = 2 * dpPx)
    drawLine(color.copy(alpha = 0.7f), start - Offset(0f, 3 * dpPx), start + Offset(0f, 3 * dpPx), strokeWidth = 2 * dpPx)
    drawLine(color.copy(alpha = 0.7f), end - Offset(0f, 3 * dpPx), end + Offset(0f, 3 * dpPx), strokeWidth = 2 * dpPx)
    val layout = textMeasurer.measure("5 m", TextStyle(fontSize = 9.sp, color = color.copy(alpha = 0.7f)))
    drawText(layout, topLeft = Offset(end.x + 4 * dpPx, start.y - layout.size.height / 2f))
}
