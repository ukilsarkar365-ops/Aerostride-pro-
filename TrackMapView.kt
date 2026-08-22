package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.LatLngPoint
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TrackMapView(
    pathPoints: List<LatLngPoint>,
    currentSpeedKmh: Double,
    isTracking: Boolean,
    isSimulating: Boolean,
    modifier: Modifier = Modifier
) {
    var zoomLevel by remember { mutableStateOf(1.0f) }
    var autoCenter by remember { mutableStateOf(true) }

    // Pulse animation for runner dot
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkBg)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val center = Offset(canvasWidth / 2f, canvasHeight / 2f)

            // Draw athletic dark radar background grid
            drawRadarGrid(center, canvasWidth, canvasHeight)

            if (pathPoints.isNotEmpty()) {
                // Determine bounding box
                val minLat = pathPoints.minOf { it.latitude }
                val maxLat = pathPoints.maxOf { it.latitude }
                val minLon = pathPoints.minOf { it.longitude }
                val maxLon = pathPoints.maxOf { it.longitude }

                val latSpan = (maxLat - minLat).coerceAtLeast(0.0004)
                val lonSpan = (maxLon - minLon).coerceAtLeast(0.0004)

                val padding = 70f
                val drawW = (canvasWidth - padding * 2) * zoomLevel
                val drawH = (canvasHeight - padding * 2) * zoomLevel

                fun toCanvasOffset(pt: LatLngPoint): Offset {
                    val normalizedX = ((pt.longitude - minLon) / lonSpan).toFloat()
                    val normalizedY = 1.0f - ((pt.latitude - minLat) / latSpan).toFloat()

                    val x = (center.x - drawW / 2f) + (normalizedX * drawW)
                    val y = (center.y - drawH / 2f) + (normalizedY * drawH)
                    return Offset(x, y)
                }

                val screenPoints = pathPoints.map { toCanvasOffset(it) }

                // Draw Path Polyline with glow
                if (screenPoints.size >= 2) {
                    val path = Path().apply {
                        moveTo(screenPoints.first().x, screenPoints.first().y)
                        for (i in 1 until screenPoints.size) {
                            lineTo(screenPoints[i].x, screenPoints[i].y)
                        }
                    }

                    // Outer glowing trail
                    drawPath(
                        path = path,
                        color = NeonGreen.copy(alpha = 0.25f),
                        style = Stroke(width = 12f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                    // Inner bright polyline
                    drawPath(
                        path = path,
                        color = NeonGreen,
                        style = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }

                // Start Marker
                val startPt = screenPoints.first()
                drawCircle(
                    color = Color.White,
                    radius = 7f,
                    center = startPt
                )
                drawCircle(
                    color = NeonYellow,
                    radius = 4.5f,
                    center = startPt
                )

                // Current Runner Marker
                val currentPt = screenPoints.last()

                if (isTracking) {
                    // Pulsing radar ring
                    drawCircle(
                        color = NeonCyan.copy(alpha = pulseAlpha),
                        radius = pulseRadius * zoomLevel.coerceIn(0.8f, 2.0f),
                        center = currentPt
                    )
                }

                // Solid runner dot
                drawCircle(
                    color = Color(0xFF090A0F),
                    radius = 9f,
                    center = currentPt
                )
                drawCircle(
                    color = if (isTracking) NeonGreen else NeonCyan,
                    radius = 6.5f,
                    center = currentPt
                )
            } else {
                // Empty state: draw simulated track silhouette in center
                drawSampleTrackGuide(center)
            }
        }

        // Compass & GPS status indicator (Top Left)
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CardBg.copy(alpha = 0.85f))
                .border(0.5.dp, BorderDark, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Explore,
                contentDescription = "Compass",
                tint = NeonCyan,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (isSimulating) "RADAR LIVE (SIM)" else "GPS LIVE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isTracking) NeonGreen else MutedText
            )
        }

        // Zoom Controls & Recenter (Top Right)
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            IconButton(
                onClick = { zoomLevel = (zoomLevel * 1.25f).coerceAtMost(3.0f) },
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(CardBg.copy(alpha = 0.85f))
                    .border(0.5.dp, BorderDark, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Zoom In",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = { zoomLevel = (zoomLevel / 1.25f).coerceAtLeast(0.5f) },
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(CardBg.copy(alpha = 0.85f))
                    .border(0.5.dp, BorderDark, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Zoom Out",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = {
                    zoomLevel = 1.0f
                    autoCenter = true
                },
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(if (autoCenter) Color(0xFF003814) else CardBg.copy(alpha = 0.85f))
                    .border(0.5.dp, BorderDark, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Center",
                    tint = if (autoCenter) NeonGreen else Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Current speed HUD badge on bottom right of map
        if (currentSpeedKmh > 0.5) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CardBg.copy(alpha = 0.9f))
                    .border(0.5.dp, BorderDark, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${String.format("%.1f", currentSpeedKmh)} km/h",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = NeonGreen
                )
            }
        }
    }
}

private fun DrawScope.drawRadarGrid(center: Offset, width: Float, height: Float) {
    val gridColor = Color(0xFF141926)
    val radarLineColor = Color(0xFF1B2335)

    // Subtle Cartesian Grid
    val step = 45f
    var x = 0f
    while (x < width) {
        drawLine(
            color = gridColor,
            start = Offset(x, 0f),
            end = Offset(x, height),
            strokeWidth = 1f
        )
        x += step
    }

    var y = 0f
    while (y < height) {
        drawLine(
            color = gridColor,
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = 1f
        )
        y += step
    }

    // Concentric Radar Rings
    val maxRadius = kotlin.math.min(width, height) * 0.45f
    for (r in listOf(maxRadius * 0.3f, maxRadius * 0.6f, maxRadius * 0.9f)) {
        drawCircle(
            color = radarLineColor,
            radius = r,
            center = center,
            style = Stroke(width = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))
        )
    }

    // Crosshairs
    drawLine(
        color = radarLineColor,
        start = Offset(center.x - maxRadius, center.y),
        end = Offset(center.x + maxRadius, center.y),
        strokeWidth = 1f
    )
    drawLine(
        color = radarLineColor,
        start = Offset(center.x, center.y - maxRadius),
        end = Offset(center.x, center.y + maxRadius),
        strokeWidth = 1f
    )
}

private fun DrawScope.drawSampleTrackGuide(center: Offset) {
    // Draw an athletic oval 400m visual guide outline
    val w = 180f
    val h = 100f
    val trackRect = Path().apply {
        addRoundRect(
            androidx.compose.ui.geometry.RoundRect(
                left = center.x - w / 2f,
                top = center.y - h / 2f,
                right = center.x + w / 2f,
                bottom = center.y + h / 2f,
                radiusX = h / 2f,
                radiusY = h / 2f
            )
        )
    }

    drawPath(
        path = trackRect,
        color = Color(0xFF1E2638),
        style = Stroke(width = 3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f)))
    )
}
