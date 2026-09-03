package com.motioniq.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.model.RoutePoint

@Composable
fun RouteMapCanvas(
    routePoints: List<RoutePoint>,
    modifier: Modifier = Modifier,
    isLiveTracking: Boolean = false
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant
    val gridColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(surfaceColor)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Draw subtle coordinate grid lines
            val step = 40.dp.toPx()
            var x = 0f
            while (x < width) {
                drawLine(gridColor, Offset(x, 0f), Offset(x, height), strokeWidth = 1.dp.toPx())
                x += step
            }
            var y = 0f
            while (y < height) {
                drawLine(gridColor, Offset(0f, y), Offset(width, y), strokeWidth = 1.dp.toPx())
                y += step
            }

            if (routePoints.size >= 2) {
                val minLat = routePoints.minOf { it.latitude }
                val maxLat = routePoints.maxOf { it.latitude }
                val minLng = routePoints.minOf { it.longitude }
                val maxLng = routePoints.maxOf { it.longitude }

                val latSpan = (maxLat - minLat).coerceAtLeast(0.0001)
                val lngSpan = (maxLng - minLng).coerceAtLeast(0.0001)

                val padding = 45.dp.toPx()
                val drawWidth = width - (padding * 2)
                val drawHeight = height - (padding * 2)

                fun project(p: RoutePoint): Offset {
                    val px = padding + (((p.longitude - minLng) / lngSpan) * drawWidth).toFloat()
                    val py = padding + ((1.0 - ((p.latitude - minLat) / latSpan)) * drawHeight).toFloat()
                    return Offset(px, py)
                }

                // Draw Path Polyline
                val path = Path()
                val first = project(routePoints.first())
                path.moveTo(first.x, first.y)

                for (i in 1 until routePoints.size) {
                    val pt = project(routePoints[i])
                    path.lineTo(pt.x, pt.y)
                }

                // Path shadow / glow
                drawPath(
                    path = path,
                    color = primaryColor.copy(alpha = 0.35f),
                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // Main route line
                drawPath(
                    path = path,
                    color = primaryColor,
                    style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // Start Marker (Green circle 🟢)
                val startPoint = project(routePoints.first())
                drawCircle(color = Color.White, radius = 9.dp.toPx(), center = startPoint)
                drawCircle(color = Color(0xFF4CAF50), radius = 7.dp.toPx(), center = startPoint)

                // End Marker / Current Location (Red circle 🔴 or pulsing Blue circle)
                val endPoint = project(routePoints.last())
                if (isLiveTracking) {
                    drawCircle(color = primaryColor.copy(alpha = 0.3f), radius = 16.dp.toPx(), center = endPoint)
                    drawCircle(color = Color.White, radius = 9.dp.toPx(), center = endPoint)
                    drawCircle(color = primaryColor, radius = 6.dp.toPx(), center = endPoint)
                } else {
                    drawCircle(color = Color.White, radius = 9.dp.toPx(), center = endPoint)
                    drawCircle(color = Color(0xFFE53935), radius = 7.dp.toPx(), center = endPoint)
                }
            }
        }

        // Top-left status badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(12.dp)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(if (isLiveTracking) Color(0xFF4CAF50) else Color(0xFF2196F3), RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isLiveTracking) "GPS LIVE TRACKING" else "ROUTE MAP",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
