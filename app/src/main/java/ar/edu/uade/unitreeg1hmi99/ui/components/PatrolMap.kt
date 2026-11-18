package ar.edu.uade.unitreeg1hmi99.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
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
import kotlin.math.cos
import kotlin.math.sin

private val VisitedColor = Color(0xFF2E7D32)
private val NextColor = Color(0xFFF9A825)
private val PendingColor = Color(0xFF90A4AE)
private val BaseColor = Color(0xFF1565C0)
private val DangerColor = Color(0xFFC62828)
private val WarningColor = Color(0xFFEF6C00)
private val StaleColor = Color(0xFF757575)

private const val VISION_RADIUS_M = 3.5f
private const val VISION_HALF_ANGLE_DEG = 35f
private const val TRAIL_MAX_POINTS = 80
private const val TRAIL_MIN_STEP_M = 0.25f

private fun zoneColor(kind: ZoneKind): Color = when (kind) {
    ZoneKind.COMMON -> Color(0xFF90A4AE)
    ZoneKind.CLASSROOM -> Color(0xFF64B5F6)
    ZoneKind.TECHNICAL -> Color(0xFFBA68C8)
    ZoneKind.EXIT -> Color(0xFF81C784)
    ZoneKind.STORAGE -> Color(0xFFFFB74D)
    ZoneKind.OFFICE -> Color(0xFF4DB6AC)
    ZoneKind.BASE -> Color(0xFF29B6F6)
}

private fun robotColor(state: OperationalState, stale: Boolean): Color = when {
    stale -> StaleColor
    state == OperationalState.PATROLLING -> Color(0xFF2E7D32)
    state == OperationalState.STANDBY -> Color(0xFF1565C0)
    state == OperationalState.PAUSED -> Color(0xFFF57F17)
    state == OperationalState.RETURNING_HOME -> Color(0xFF6A1B9A)
    state == OperationalState.EMERGENCY_STOP -> DangerColor
    else -> Color(0xFFD84315)
}

/**
 * Mapa 2D del edificio con la ruta de la ronda, el avance y el robot en tiempo real.
 * Solo observa estado: no conoce al simulador.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PatrolMap(
    floorMap: FloorMap,
    route: List<MapWaypoint>,
    reached: Int,
    telemetry: TelemetryData,
    modifier: Modifier = Modifier
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
    val description = stringResource(
        R.string.map_content_description, telemetry.zoneName, reached.coerceAtMost(total), total
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.map_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = stringResource(R.string.map_progress_format, reached.coerceAtMost(total), total),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(floorMap.width / floorMap.height)
                    .clip(MaterialTheme.shapes.medium)
                    .background(surfaceColor)
                    .semantics { contentDescription = description }
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
                    cornerRadius = CornerRadius(3 * dpPx)
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
                    drawCircle(color, radius = 9 * dpPx, center = c)
                    drawCircle(Color.White, radius = 9 * dpPx, center = c, style = Stroke(1.5f * dpPx))
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

            if (stale) {
                Text(
                    text = stringResource(R.string.map_stale_position),
                    style = MaterialTheme.typography.labelMedium,
                    color = StaleColor,
                    fontWeight = FontWeight.Bold
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                LegendDot(VisitedColor, stringResource(R.string.map_legend_visited))
                LegendDot(NextColor, stringResource(R.string.map_legend_next))
                LegendDot(PendingColor, stringResource(R.string.map_legend_pending))
                LegendDot(robotColor(state, stale), stringResource(R.string.map_legend_robot))
                LegendDot(WarningColor, stringResource(R.string.map_legend_obstacle))
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
            cornerRadius = CornerRadius(5 * dpPx)
        )
        drawRoundRect(
            color = if (isGas) DangerColor else outline.copy(alpha = 0.55f),
            topLeft = tl,
            size = sz,
            cornerRadius = CornerRadius(5 * dpPx),
            style = Stroke(width = (if (isGas) 2.5f else 1.5f) * dpPx)
        )
        val layout = textMeasurer.measure(
            text = z.shortName,
            style = TextStyle(fontSize = 9.sp, color = labelColor.copy(alpha = 0.75f)),
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
    drawCircle(color, radius = 8 * dpPx, center = center)
    drawCircle(Color.White, radius = 8 * dpPx, center = center, style = Stroke(2 * dpPx))

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
