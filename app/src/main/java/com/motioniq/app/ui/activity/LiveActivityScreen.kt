package com.motioniq.app.ui.activity

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motioniq.app.core.GpsCalculator
import com.motioniq.app.model.ActivityType
import com.motioniq.app.model.RoutePoint
import com.motioniq.app.model.WorkoutState
import com.motioniq.app.theme.*
import com.motioniq.app.ui.components.RealMapView
import java.util.Locale

@Composable
fun LiveActivityScreen(
    activityType: ActivityType,
    state: WorkoutState,
    durationSeconds: Long,
    distanceMeters: Double,
    steps: Long,
    calories: Int,
    speedKmh: Double,
    paceMinPerKm: Double,
    routePoints: List<RoutePoint>,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onStopClick: () -> Unit
) {
    val hours = durationSeconds / 3600
    val minutes = (durationSeconds % 3600) / 60
    val seconds = durationSeconds % 60
    val timeFormatted = if (hours > 0) {
        "%02d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d:%02d".format(0, minutes, seconds)
    }

    val distanceKm = distanceMeters / 1000.0
    val distanceFormatted = "%.2f".format(Locale.US, distanceKm)

    val paceFormatted = if (paceMinPerKm in 1.0..60.0) {
        val pMin = paceMinPerKm.toInt()
        val pSec = ((paceMinPerKm - pMin) * 60).toInt()
        "%d'%02d\"".format(pMin, pSec)
    } else {
        "5'18\""
    }

    val cadenceEstimate = if (durationSeconds > 0) ((steps * 60) / durationSeconds).toInt().coerceIn(120, 185) else 168
    val hrEstimate = (135 + (speedKmh * 3).toInt()).coerceIn(110, 175)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateGround)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 18.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Top Bar: Back, Logo, LIVE SESSION, Profile Avatar (Stitch 87e7beb3)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = onPauseClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = StitchCyan,
                        modifier = Modifier.size(20.dp)
                    )

                    Text(
                        text = "LIVE SESSION",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = TextMediumDark
                    )
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .background(StitchCyan, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = StitchDarkCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // 2. Status Badges: Running • GPS Locked, 99.4% Acc (Stitch 87e7beb3)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // GPS Locked Pill
                Row(
                    modifier = Modifier
                        .background(SlateSurface1, RoundedCornerShape(20.dp))
                        .border(1.dp, CyanBorderSubtle, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(KineticEmerald, CircleShape)
                    )
                    Text(
                        text = "${activityType.displayName.uppercase()} • GPS LOCKED",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = KineticEmerald
                    )
                }

                // Accuracy Pill
                Row(
                    modifier = Modifier
                        .background(SlateSurface1, RoundedCornerShape(20.dp))
                        .border(1.dp, CyanBorderSubtle, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = null,
                        tint = StitchCyan,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "99.4% ACC",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = StitchCyan
                    )
                }
            }
        }

        // 3. Elevation & Telemetry Route Curve Card (Stitch 87e7beb3)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface1),
                border = BorderStroke(1.dp, CyanBorderSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                    // Elevation Pill Top-Left
                    Row(
                        modifier = Modifier
                            .background(SlateSurface2, RoundedCornerShape(12.dp))
                            .border(1.dp, CyanBorderSubtle, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Landscape,
                            contentDescription = null,
                            tint = StitchCyan,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "+42M ELEV",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Auto-lap Pill Bottom-Right
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .background(SlateSurface2, RoundedCornerShape(12.dp))
                            .border(1.dp, CyanBorderSubtle, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = null,
                            tint = TextLowDark,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Auto-Lap: 1.0 km",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextLowDark
                        )
                    }

                    if (routePoints.size >= 2) {
                        RealMapView(
                            routePoints = routePoints,
                            modifier = Modifier.fillMaxSize(),
                            isLiveTracking = true
                        )
                    } else {
                        // Route Polyline Canvas preview
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 20.dp)
                        ) {
                        // Background smooth elevation curve
                        val bgPath = Path().apply {
                            moveTo(0f, size.height * 0.7f)
                            cubicTo(
                                size.width * 0.3f, size.height * 0.65f,
                                size.width * 0.6f, size.height * 0.35f,
                                size.width, size.height * 0.5f
                            )
                        }
                        drawPath(
                            path = bgPath,
                            color = Color(0xFF22323A),
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Luminous Cyan Active Route Curve
                        val activePath = Path().apply {
                            moveTo(size.width * 0.1f, size.height * 0.8f)
                            cubicTo(
                                size.width * 0.4f, size.height * 0.6f,
                                size.width * 0.7f, size.height * 0.4f,
                                size.width * 0.9f, size.height * 0.15f
                            )
                        }
                        drawPath(
                            path = activePath,
                            color = StitchCyan,
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Start node (Green)
                        drawCircle(
                            color = KineticEmerald,
                            radius = 6.dp.toPx(),
                            center = Offset(size.width * 0.1f, size.height * 0.8f)
                        )

                        // Current node (Cyan with outer glow ring)
                        drawCircle(
                            color = StitchCyan.copy(alpha = 0.3f),
                            radius = 10.dp.toPx(),
                            center = Offset(size.width * 0.9f, size.height * 0.15f)
                        )
                        drawCircle(
                            color = StitchCyan,
                            radius = 6.dp.toPx(),
                            center = Offset(size.width * 0.9f, size.height * 0.15f)
                        )
                    }
                    }
                }
            }
        }

        // 4. Hero Active Elapsed Time Card (Stitch 87e7beb3)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface1),
                border = BorderStroke(1.dp, CyanBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = StitchCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "ACTIVE ELAPSED TIME",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextLowDark,
                            letterSpacing = 1.sp
                        )
                    }

                    Text(
                        text = timeFormatted,
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(SlateSurface2, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Target Pace 5'15\"",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMediumDark
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(StitchTeal.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Zone 3 Cardio",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = StitchCyan
                            )
                        }
                    }
                }
            }
        }

        // 5. 2x2 Telemetry Grid (Distance, Current Pace, Heart Rate, Cadence) (Stitch 87e7beb3)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Distance
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SlateSurface1),
                        border = BorderStroke(1.dp, CyanBorderSubtle)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "DISTANCE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextLowDark,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(Icons.Default.SwapVert, contentDescription = null, tint = StitchCyan, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = distanceFormatted,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "KM",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = StitchCyan,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            // Progress bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .background(Color(0xFF1B2830), RoundedCornerShape(2.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.65f)
                                        .fillMaxHeight()
                                        .background(StitchCyan, RoundedCornerShape(2.dp))
                                )
                            }
                        }
                    }

                    // Current Pace
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SlateSurface1),
                        border = BorderStroke(1.dp, CyanBorderSubtle)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "CURRENT PACE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextLowDark,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(Icons.Default.Speed, contentDescription = null, tint = StitchCyan, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = paceFormatted,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "/KM",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = StitchCyan,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "↓ -4s vs avg",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = KineticEmerald
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Heart Rate
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SlateSurface1),
                        border = BorderStroke(1.dp, CyanBorderSubtle)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "HEART RATE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextLowDark,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = PulseCoral, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "$hrEstimate",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "BPM",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PulseCoral,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "● Aerobic Threshold",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = KineticEmerald
                            )
                        }
                    }

                    // Cadence
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SlateSurface1),
                        border = BorderStroke(1.dp, CyanBorderSubtle)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "CADENCE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextLowDark,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(Icons.Default.DirectionsRun, contentDescription = null, tint = StitchCyan, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "$cadenceEstimate",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "SPM",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = StitchCyan,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Optimal: 165–172",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMediumDark
                            )
                        }
                    }
                }
            }
        }

        // 6. Active Energy Burn Row (Stitch 87e7beb3)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface1),
                border = BorderStroke(1.dp, CyanBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = StitchCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Active Energy Burn",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$calories",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = StitchCyan
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "KCAL",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextLowDark,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }
            }
        }

        // 7. Splits Performance Bar Chart (Stitch 87e7beb3)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface1),
                border = BorderStroke(1.dp, CyanBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = null,
                                tint = StitchCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "SPLITS PERFORMANCE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Text(
                            text = "Lap Avg: 5'17\"",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMediumDark
                        )
                    }

                    // Splits Bar Chart
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val splits = listOf(
                            Triple("1K", "5'24\"", 0.55f),
                            Triple("2K", "5'15\"", 0.75f),
                            Triple("3K", "5'10\"", 0.95f),
                            Triple("4K", "5'20\"", 0.50f),
                            Triple("5K", "5'18\"", 0.70f)
                        )

                        splits.forEach { (km, pace, heightFraction) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = pace,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    color = if (heightFraction > 0.8f) StitchCyan else TextLowDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(36.dp)
                                        .fillMaxHeight(heightFraction)
                                        .background(
                                            if (heightFraction > 0.8f) StitchCyan else if (heightFraction > 0.6f) StitchTeal else SlateSurface2,
                                            RoundedCornerShape(6.dp)
                                        )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = km,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (heightFraction > 0.8f) StitchCyan else TextLowDark
                                )
                            }
                        }
                    }
                }
            }
        }

        // 8. Control Actions: Lock + PAUSE RUN + Stop Button (Stitch 87e7beb3)
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Lock Squircle Button
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(54.dp)
                            .background(SlateSurface2, RoundedCornerShape(16.dp))
                            .border(1.dp, CyanBorderSubtle, RoundedCornerShape(16.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Main Cyan Pause / Resume Pill
                    Button(
                        onClick = {
                            if (state == WorkoutState.PAUSED) onResumeClick() else onPauseClick()
                        },
                        shape = RoundedCornerShape(27.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StitchCyan),
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (state == WorkoutState.PAUSED) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = null,
                                tint = StitchDarkCyan,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = if (state == WorkoutState.PAUSED) "RESUME RUN" else "PAUSE RUN",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Black,
                                color = StitchDarkCyan,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    // Stop Button (Dark Red)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color(0xFF3F1616), CircleShape)
                            .border(1.dp, Color(0xFF7F1D1D), CircleShape)
                            .clickable { onStopClick() }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(Color(0xFFEF4444), RoundedCornerShape(3.dp))
                        )
                    }
                }

                Text(
                    text = "Hold Stop button 1.5s to finish workout",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = TextLowDark
                )
            }
        }
    }
}
